package com.guohao.activity;

import java.util.ArrayList;
import java.util.List;

import com.guohao.coolweather.R;
import com.guohao.model.City;
import com.guohao.model.County;
import com.guohao.model.Province;
import com.guohao.util.CoolWeatherDB;
import com.guohao.util.HttpCallbackListener;
import com.guohao.util.HttpUtil;
import com.guohao.util.LogUtil;
import com.guohao.util.ParseResponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseAreaActivity	extends Activity implements OnItemClickListener {
	//判断当前的 activity 处于 哪个级别
	private static final int LEVEL_PROVINCE = 1;
	private static final int LEVEL_CITY = 2;
	private static final int LEVEL_COUNTY = 3;
	private int currentLevel;
	
	private TextView title;
	private ListView listView;
	private List<String> showName = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private List<Province> provinces;
	private List<City> cities;
	private List<County> counties;
	private ArrayAdapter<String> adapter;
	private ProgressDialog progressDialog;
	private String weatherCode;
	private String address;
	
	//选中的省、市
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area_activity);
		
		initView();
		showProvince();
	}
	
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new ArrayAdapter<String>(ChooseAreaActivity.this, android.R.layout.simple_list_item_1, showName);
		listView.setAdapter(adapter);
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (currentLevel) {
		case LEVEL_PROVINCE:
			selectedProvince = provinces.get(position);
			showCity();
			break;
		case LEVEL_CITY:
			selectedCity = cities.get(position);
			showCounty();
			break;
		case LEVEL_COUNTY:
			selectedCounty = counties.get(position);
			showWeatherInfo();
			break;
		default:
			Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	
	private void showProvince() {
		provinces = coolWeatherDB.loadProvince();
		if (provinces.size() > 0) {
			showName.clear();
			for (Province province : provinces) {
				showName.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else {
			showFromServer(null,LEVEL_PROVINCE);
		}
	}
	
	private void showCity() {
		cities = coolWeatherDB.loadCity(selectedProvince.getId());
		if (cities.size() > 0) {
			showName.clear();
			for (City city : cities) {
				showName.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText("中国."+selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else {
			showFromServer(selectedProvince.getProvinceCode(),LEVEL_CITY);
		}
	}

	private void showCounty() {
		counties = coolWeatherDB.loadCounty(selectedCity.getId());
		if (counties.size() > 0) {
			showName.clear();
			for (County county : counties) {
				showName.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			title.setText("中国."+selectedProvince.getProvinceName()+"."+selectedCity.getCityName());
			listView.setSelection(0);
			currentLevel = LEVEL_COUNTY;
		}else {
			showFromServer(selectedCity.getCityCode(),LEVEL_COUNTY);
		}
	}
	
	
	
	private void showWeatherInfo() {
		
		String countyCode = selectedCounty.getCountyCode();
		address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		if (!TextUtils.isEmpty(countyCode)) {
			showProgressDialog();
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					weatherCode = ParseResponse.parseWeatherCode(response);
					getWeatherData();
				}
				@Override
				public void onError(Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							Toast.makeText(ChooseAreaActivity.this, "加载失败3", Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}
		
		
	}
	
	private void getWeatherData() {
		if (!TextUtils.isEmpty(weatherCode)) {
			address = "http://www.weather.com.cn/adat/cityinfo/"+weatherCode+".html";
			Log.d("guohao", "天气网址："+address);
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override 
				public void onFinish(final String response) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							String s = ParseResponse.parseWeatherInfo(ChooseAreaActivity.this, response, "中国."+selectedProvince.getProvinceName()+"."+selectedCity.getCityName()+".");
							if (s.equals("1")) {
								Weather.actionStart(ChooseAreaActivity.this);
							}else if (s.equals("2")) {
								Toast.makeText(ChooseAreaActivity.this, "暂无预报", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(ChooseAreaActivity.this, "解析JSON 数据错误", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
				
				@Override
				public void onError(final Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							Toast.makeText(ChooseAreaActivity.this, "加载失败8", Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}else {
			closeProgressDialog();
			Toast.makeText(this, "加载失败5", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showFromServer(String code, final int type) {
		address = "http://www.weather.com.cn/data/list3/city.xml";
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Boolean result = false;
				switch (type) {
				case LEVEL_PROVINCE:
					result = ParseResponse.parseProvince(coolWeatherDB, response);
					break;
				case LEVEL_CITY:
					result = ParseResponse.parseCity(coolWeatherDB, response, selectedProvince.getId());
					break;
				case LEVEL_COUNTY:
					result = ParseResponse.parseCounty(coolWeatherDB, response, selectedCity.getId());
					break;
				default:
					break;
				}
				if (result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							
							switch (type) {
							case LEVEL_PROVINCE:
								showProvince();
								break;
							case LEVEL_CITY:
								showCity();
								break;
							case LEVEL_COUNTY:
								showCounty();
								break;
							default:
								break;
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
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			showCity();
			LogUtil.d("guohao", "显示市");
		}else if (currentLevel == LEVEL_CITY) {
			showProvince();
			LogUtil.d("guohao", "显示省");
		}else {
			LogUtil.d("guohao", "销毁");
			finish();
		}
	}
}
