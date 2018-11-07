package com.gooya.cooolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.gooya.cooolweather.db.CooolWeatherDB;
import com.gooya.cooolweather.model.City;
import com.gooya.cooolweather.model.County;
import com.gooya.cooolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                    String weatherId = jsonObject.getString("weather_id");
                    County county = new County();
                    county.setCountyCode(countyCode);
                    county.setCountyName(countyName);
                    county.setCityId(cityId);
                    county.setWeatherId(weatherId);
                    cooolWeatherDB.saveCounty(county);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String cityName = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("basic").getString("city");
            Log.d("yusongyu", cityName);
            String temp1 = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONArray("daily_forecast").getJSONObject(0).getJSONObject("tmp").getString("max");
            String temp2 = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONArray("daily_forecast").getJSONObject(0).getJSONObject("tmp").getString("min");
            String weatherDesp = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONArray("daily_forecast").getJSONObject(0).getJSONObject("cond").getString("txt_d");
            String publishTime = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("basic").getJSONObject("update").getString("loc");
            saveWeatherInfo(context,cityName,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void saveWeatherInfo(Context context,String cityName,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
