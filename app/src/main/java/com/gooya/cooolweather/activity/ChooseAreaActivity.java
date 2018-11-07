package com.gooya.cooolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gooya.cooolweather.R;
import com.gooya.cooolweather.db.CooolWeatherDB;
import com.gooya.cooolweather.model.City;
import com.gooya.cooolweather.model.County;
import com.gooya.cooolweather.model.Province;
import com.gooya.cooolweather.util.HttpCallbackListener;
import com.gooya.cooolweather.util.HttpUtil;
import com.gooya.cooolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;



public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private int currentLevel;
    private TextView titleText;
    private ListView listView;
    private ProgressDialog progressDialog;
    private CooolWeatherDB cooolWeatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false)){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        cooolWeatherDB = CooolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    County county = countyList.get(position);
                    String weatherId = county.getWeatherId();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        provinceList = cooolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer("province");
        }
    }
    private void queryCities(){
        cityList = cooolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer("city");
        }
    }
    private void queryCounties(){
        countyList = cooolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer("county");
        }
    }
    private void queryFromServer(final String type){
        String address="";
        if("province".equals(type)){
            address = "http://guolin.tech/api/china";
        }else if("city".equals(type)){
            address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
        }else if("county".equals(type)){
            address = "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
        }
        showProgressDialog();
        HttpUtil.sendHttpHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(cooolWeatherDB,response);
                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(cooolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(cooolWeatherDB,response,selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else{
            finish();
        }
    }
}
