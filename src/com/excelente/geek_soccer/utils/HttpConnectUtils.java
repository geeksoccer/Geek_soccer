package com.excelente.geek_soccer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

public class HttpConnectUtils {
	
	public static final String CHARSET_ENCODE_UTF8 = "UTF-8";
	public static final String MIMETYPE_HTML = "text/html";
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	private static void setStrictMode(){
		if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy); 
	    }
	}
	
	public static String getStrHttpGetConnect(String url){
		setStrictMode();
		
		String result = "";

	    HttpClient httpclient = new DefaultHttpClient();

	    HttpGet httpget = new HttpGet(url); 

	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);

	        HttpEntity entity = response.getEntity();
	        
	        if (entity != null) {

	            InputStream instream = entity.getContent();
	            result = convertStreamToString(instream);
	            
	            instream.close();
	        }

	        return result;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return result;
	    }
	}
	
	public static String getStrHttpPostConnect(String url,List<NameValuePair> params) {

		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				
				InputStream content = entity.getContent();
				result = convertStreamToString(content);
				
				content.close();
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		
	}
	
	private static String convertStreamToString(InputStream is) {
		
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    return sb.toString();
	}
}
