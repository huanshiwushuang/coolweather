package com.guohao.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.model.City;
import com.guohao.model.County;
import com.guohao.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.AvoidXfermode.Mode;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class ParseResponse {
	public synchronized static Boolean parseProvince(CoolWeatherDB coolWeatherDB, String provinceData) {
		if (!TextUtils.isEmpty(provinceData)) {
			String[] provinceAllData = provinceData.split(",");
			if (provinceAllData != null && provinceAllData.length > 0) {
				for (String string : provinceAllData) {
					String[] provinceOne = string.split("\\|");
					Province province = new Province();
					province.setProvinceCode(provinceOne[0]);
					province.setProvinceName(provinceOne[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	public synchronized static Boolean parseCity(CoolWeatherDB coolWeatherDB, String cityData, int provinceId) {
		if (!TextUtils.isEmpty(cityData)) {
			String[] cityAllData = cityData.split(",");
			if (cityAllData != null && cityAllData.length > 0) {
				for (String string : cityAllData) {
					String[] cityOne = string.split("\\|");
					City city = new City();
					city.setCityCode(cityOne[0]);
					city.setCityName(cityOne[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static Boolean parseCounty(CoolWeatherDB coolWeatherDB, String countyData, int cityId) {	
		if (!TextUtils.isEmpty(countyData)) {
			String[] countyAllData = countyData.split(",");
			if (countyAllData != null && countyAllData.length > 0) {
				for (String string : countyAllData) {
					String[] countyOne = string.split("\\|");
					County county = new County();
					county.setCountyCode(countyOne[0]);
					county.setCountyName(countyOne[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	public synchronized static String parseWeatherCode(String weatherCodeData) {
		String[] strings = weatherCodeData.split("\\|");
		return strings[1];
	}
	public static String parseWeatherInfo(Context context, String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonObject2 = jsonObject.getJSONObject("weatherinfo");
			String city = jsonObject2.getString("city");
			String cityid = jsonObject2.getString("cityid");
			String temp1 = jsonObject2.getString("temp1");
			if (temp1.equals("暂无预报")) {
				return "2";
			}
			String temp2 = jsonObject2.getString("temp2");
			String weather = jsonObject2.getString("weather");
			String ptime = jsonObject2.getString("ptime");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putString("city", city);
			editor.putString("cityid", cityid);
			editor.putString("releaseTime", "今天"+ptime+"发布");
			editor.putString("time", format.format(new Date()));
			editor.putString("weather", weather);
			editor.putString("temperature", temp2+"~"+temp1);
			editor.commit();
			return "1";
		} catch (JSONException e) {
			e.printStackTrace();
			return "0";
		}
	}
}
