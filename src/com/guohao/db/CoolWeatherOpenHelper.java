package com.guohao.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	//����---ʡ
	private static final String CREATE_PROVINCE = "create table province (" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)";
	//����---��
	private static final String CREATE_CITY = "create table city (" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)";
	//����---��
	private static final String CREATE_COUNTY = "create table county (" +
			"id integer primary key autoincrement," +
			"county_name text," +
			"county_code text," +
			"city_id integer)";

	
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CoolWeatherOpenHelper.CREATE_PROVINCE);
		db.execSQL(CoolWeatherOpenHelper.CREATE_CITY);
		db.execSQL(CoolWeatherOpenHelper.CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}