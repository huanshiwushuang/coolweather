package com.guohao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;



public class HttpUtil {
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				
				InputStream in = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					//connection.setRequestProperty("Connection", "close");
					connection.setConnectTimeout(30000);
					connection.setReadTimeout(30000);
					in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
						Log.d("guohao", "¹þà¶£¬ÏìÓ¦Âë£º"+connection.getResponseCode()+" : "+connection.getResponseMessage());
						return;
					}
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						listener.onFinish(response.toString());
					}
					in.close();
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
}
