package com.guohao.activity;

import com.guohao.coolweather.R;
import com.guohao.util.HttpCallbackListener;
import com.guohao.util.HttpUtil;
import com.guohao.util.ParseResponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Weather extends Activity implements OnClickListener {
	private TextView showCountyName,releaseTime,showTime,weatherMsg,temperature;
	private ImageView showHome,refresh;
	private SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		
		initView();
		showWeather();
	}

	private void initView() {
		showCountyName = (TextView) findViewById(R.id.showCountyName);
		releaseTime = (TextView) findViewById(R.id.releaseTime);
		showTime = (TextView) findViewById(R.id.showTime);
		weatherMsg = (TextView) findViewById(R.id.weatherMsg);
		temperature = (TextView) findViewById(R.id.temperature);
		
		showHome = (ImageView) findViewById(R.id.showHome);
		refresh = (ImageView) findViewById(R.id.refresh);
		showHome.setOnClickListener(this);
		refresh.setOnClickListener(this);
	}
	private void showWeather() {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		showCountyName.setText(sharedPreferences.getString("city", ""));
		releaseTime.setText(sharedPreferences.getString("releaseTime", ""));
		showTime.setText(sharedPreferences.getString("time", ""));
		weatherMsg.setText(sharedPreferences.getString("weather", ""));
		temperature.setText(sharedPreferences.getString("temperature", ""));
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, Weather.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.showHome:
			Intent intent = new Intent(Weather.this, ChooseAreaActivity.class);
			intent.putExtra("fromWeather", true);
			startActivity(intent);
			break;
		case R.id.refresh:
			releaseTime.setText("正在更新...");
			String address = "http://www.weather.com.cn/adat/cityinfo/"+sharedPreferences.getString("cityid", "")+".html";
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override
				public void onFinish(final String response) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							String s = ParseResponse.parseWeatherInfo(Weather.this, response);
							if (s.equals("1")) {
								showWeather();
							}else if (s.equals("2")) {
								Toast.makeText(Weather.this, "更新失败，暂无预报", Toast.LENGTH_SHORT).show();
								releaseTime.setText("更新失败");
							}else {
								Toast.makeText(Weather.this, "解析JSON 数据错误", Toast.LENGTH_SHORT).show();
								releaseTime.setText("更新失败");
							}
						}
					});
				}
				
				@Override
				public void onError(Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast.makeText(Weather.this, "更新失败", Toast.LENGTH_SHORT).show();
									releaseTime.setText("更新失败");
								}
							});
						}
					});
				}
			});
			break;
		default:
			break;
		}
	}
}
