package com.gooya.cooolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.gooya.cooolweather.db.CooolWeatherDB;
import com.gooya.cooolweather.model.City;
import com.gooya.cooolweather.model.County;
import com.gooya.cooolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public synchronized static boolean handleProvincesResponse(CooolWeatherDB cooolWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String provinceName = jsonObject.getString("name");
                    String provinceCode = jsonObject.getString("id");
                    Province province = new Province();
                    province.setProvinceName(provinceName);
                    province.setProvinceCode(provinceCode);
                    cooolWeatherDB.saveProvince(province);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCitiesResponse(CooolWeatherDB cooolWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String cityName = jsonObject.getString("name");
                    String cityCode = jsonObject.getString("id");
                    City city = new City();
                    city.setCityName(cityName);
                    city.setCityCode(cityCode);
                    city.setProvinceId(provinceId);
                    cooolWeatherDB.saveCity(city);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountiesResponse(CooolWeatherDB cooolWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String countyName = jsonObject.getString("name");
                    String countyCode = jsonObject.getString("id");
                    County county = new County();
                    county.setCountyCode(countyCode);
                    county.setCountyName(countyName);
                    county.setCityId(cityId);
                    cooolWeatherDB.saveCounty(county);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
