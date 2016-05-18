package com.guohao.util;

import com.guohao.model.City;
import com.guohao.model.County;
import com.guohao.model.Province;

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
}
