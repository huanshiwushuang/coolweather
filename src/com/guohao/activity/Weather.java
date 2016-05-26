package com.guohao.activity;

import com.guohao.coolweather.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.TextView;

public class Weather extends Activity {
	private TextView showCountyName,releaseTime,showTime,weatherMsg,temperature;
	
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
	}
	private void showWeather() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		showCountyName.setText(sharedPreferences.getString("city", ""));
		releaseTime.setText(sharedPreferences.getString("releaseTime", ""));
		showTime.setText(sharedPreferences.getString("time", ""));
		weatherMsg.setText(sharedPreferences.getString("weather", ""));
		temperature.setText(sharedPreferences.getString("temperature", ""));
	}
	
	
	
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, Weather.class);
		context.startActivity(intent);
	}
}
