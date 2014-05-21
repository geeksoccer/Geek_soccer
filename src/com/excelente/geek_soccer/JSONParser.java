package com.excelente.geek_soccer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		// Making HTTP request
		try {

			// check for request method
			if (method == "POST") {
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				Log.d("HEEEEEEEE", "ALOHA"+params.toString());
				//data = data.replaceAll("pic", "pic[0]");
				String data = EntityUtils.toString(new UrlEncodedFormEntity(params,"UTF-8"));
				data = data.replaceAll("%5B", "[");
				data = data.replaceAll("%5D","]");
				Log.d("HEEEEEEEE", data);
				StringEntity se = new StringEntity(data, "UTF-8");
		        se.setContentType("application/x-www-form-urlencoded");
		        se.setContentEncoding("UTF-8");
		        httpPost.setEntity(se);
		        
		        HttpParams httpParameters = httpPost.getParams();
		        // Set the timeout in milliseconds until a connection is established.
		        int timeoutConnection = 7500;
		        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		        // Set the default socket timeout (SO_TIMEOUT) 
		        // in milliseconds which is the timeout for waiting for data.
		        int timeoutSocket = 7500;
		        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		        
				//httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				try {
					final int status = httpResponse.getStatusLine().getStatusCode();
			        if (status != HttpStatus.SC_OK) {
			            Log.d("serverStat", "Can't connect to server");
			        }else if(status == HttpStatus.SC_OK){
			        	Log.d("HEEEEEEEE", "Every thing ok");
			        	HttpEntity httpEntity = httpResponse.getEntity();
			        	is = httpEntity.getContent();
			        }
			        ///do your stuff here
			      } catch (IOException e) {
			    	  httpPost.abort();

			    } catch (IllegalStateException e) {
			    	httpPost.abort();

			    } catch (Exception e) {
			    	httpPost.abort();

			    } finally {
			    }

			} else if (method == "GET") {
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				//paramString = paramString.replaceAll("%5B%5D", "[]");
				url += "?" + paramString;
				Log.d("HEEEEEEEE", url);
				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("Content-Type", "application/json");
				httpGet.setHeader("Accept", "JSON");
				HttpParams httpParameters = httpGet.getParams();
		        // Set the timeout in milliseconds until a connection is established.
		        int timeoutConnection = 7500;
		        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		        // Set the default socket timeout (SO_TIMEOUT) 
		        // in milliseconds which is the timeout for waiting for data.
		        int timeoutSocket = 7500;
		        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				//HttpResponse response = client.execute(post);
				try {
					final int status = httpResponse.getStatusLine().getStatusCode();
			        if (status != HttpStatus.SC_OK) {
			            Log.d("serverStat", "Can't connect to server");
			        }else if(status == HttpStatus.SC_OK){
			        	HttpEntity httpEntity = httpResponse.getEntity();
			        	is = httpEntity.getContent();
			        }
			        ///do your stuff here
			      } catch (IOException e) {
			    	  httpGet.abort();

			    } catch (IllegalStateException e) {
			    	httpGet.abort();

			    } catch (Exception e) {
			    	httpGet.abort();

			    } finally {
			    }
		        
			}

		} catch (UnsupportedEncodingException e) {
			//return null;
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			//return null;
			e.printStackTrace();
		} catch (IOException e) {
			//return null;
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
			return null;
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return null;
		}

		// return JSON String
		return jObj;

	}
}