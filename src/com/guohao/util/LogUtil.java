package com.guohao.util;

import android.util.Log;

public class LogUtil {
	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
	private static final int NOTHING = 6;
	private static final int now = VERBOSE;
	
	public static void v(String key, String values) {
		if (now <= VERBOSE) {
			Log.v(key, values);
		}
	}
	public static void d(String key, String values) {
		if (now <= DEBUG) {
			Log.d(key, values);
		}
	}
	public static void i(String key, String values) {
		if (now <= INFO) {
			Log.i(key, values);
		}
	}
	public static void w(String key, String values) {
		if (now <= WARN) {
			Log.w(key, values);
		}
	}
	public static void e(String key, String values) {
		if (now <= ERROR) {
			Log.e(key, values);
		}
	}
}
