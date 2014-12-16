package com.excelente.geek_soccer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

public class HttpConnectUtils {
	
	public static final String CHARSET_ENCODE_UTF8 = "UTF-8";
	public static final String MIMETYPE_HTML = "text/html";
	public static int timeoutConnection = 10*1000;
	public static int timeoutSocket = 10*1000;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	private static void setStrictMode(){
		if (android.os.Build.VERSION.SDK_INT > 9) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy); 
	    }
	}
	
public static String getStrHttpGetConnect(String url){
		
		String result = "";

	    HttpGet httpget = new HttpGet(url);
	    HttpParams httpParameters = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	    
	    HttpClient httpclient = new DefaultHttpClient(httpParameters);

	    try {
	    	
	    	HttpResponse response = httpclient.execute(httpget);

	        HttpEntity entity = response.getEntity();
	        
	        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
		        if (entity != null) {
	
		            InputStream instream = entity.getContent();
		            result = convertStreamToString(instream);
		            
		            instream.close();
		        }
	        }

	        return result;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return result;
	    }
	}
	
	public static String getStrHttpPostConnect(String url,List<NameValuePair> params) {

		String result = "";
		
		HttpPost httpPost = new HttpPost(url);
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		    
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				if (entity != null) {
					
					InputStream content = entity.getContent();
					result = convertStreamToString(content);
					
					content.close();
				}
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
