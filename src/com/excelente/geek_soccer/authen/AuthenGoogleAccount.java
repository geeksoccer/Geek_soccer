package com.excelente.geek_soccer.authen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.excelente.geek_soccer.Sign_In_Page;
import com.excelente.geek_soccer.model.MemberModel;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class AuthenGoogleAccount extends AsyncTask<Void, Void, String> {
	
		public interface OnConnectGoogleAccount{
			public void onConnect();
			public void onConnectFail();
			public void onTryAgain();
		}
		
		private static final String TAG = "TokenInfoTask";
		protected Sign_In_Page mActivity;
		public String GOOGLE_USER_DATA="No_data";
		protected String mScope;
		protected String mEmail;
		public OnConnectGoogleAccount authenGoogleAccount;
		public MemberModel profileData;
		
		public void setOnConnectGoogleAccount(OnConnectGoogleAccount authenGoogleAccount){
			this.authenGoogleAccount = authenGoogleAccount;
		}

		public AuthenGoogleAccount(Sign_In_Page activity, String email, String scope) {
			this.mActivity = activity;
			this.mScope = scope;
			this.mEmail = email;
			
			profileData = null;
			
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				fetchNameFromProfileServer();
				return GOOGLE_USER_DATA;
			} catch (IOException ex) {
				onError("Following Error occured, please try again. " + ex.getMessage(), ex);
				
				return "No_data";
			} catch (JSONException e) {
				return "Bad response";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result.equals("No_data")){
				Toast.makeText(mActivity, "Please try again.", Toast.LENGTH_SHORT).show();
				authenGoogleAccount.onTryAgain(); 
			}else if(result.equals("Bad response")){
				Toast.makeText(mActivity, "Bad response", Toast.LENGTH_SHORT).show();
				authenGoogleAccount.onConnectFail();
			}else{
				//Toast.makeText(mActivity, "Google Account Connected", Toast.LENGTH_SHORT).show();
				setProfileData(result);
				authenGoogleAccount.onConnect();
			}
			Log.e(TAG, result);
		}

		protected void onError(String msg, Exception e) {
			if (e != null) {
				Log.e(TAG, "Exception: ", e);
			}
		}
		
		protected String fetchToken() throws IOException{
			 try {
		          return GoogleAuthUtil.getToken(mActivity, mEmail, "oauth2:" + mScope);
		      } catch (GooglePlayServicesAvailabilityException playEx) {
		      } catch (UserRecoverableAuthException userRecoverableException) {
		    	  mActivity.startActivityForResult(userRecoverableException.getIntent(), 9001);
		      } catch (GoogleAuthException fatalException) {
		      }
		      return null;
		}

		private void fetchNameFromProfileServer() throws IOException, JSONException {
			String token = fetchToken();
			
			URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+ token);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			int sc = con.getResponseCode();
			if (sc == 200) {
				InputStream is = con.getInputStream();
				GOOGLE_USER_DATA = readResponse(is);
				is.close();
				return;
			} else if (sc == 401) {
				GoogleAuthUtil.invalidateToken(mActivity, token);
				onError("Server auth error, please try again.", null);
				return;
			} else {
				onError("Server returned the following error code: " + sc, null);
				return;
			}
		} 

		private String readResponse(InputStream is) throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] data = new byte[2048];
			int len = 0;
			while ((len = is.read(data, 0, data.length)) >= 0) {
				bos.write(data, 0, len);
			}
			return new String(bos.toByteArray(), "UTF-8");
		}

		public MemberModel getProfileData() {
			return profileData;
		}

		public void setProfileData(String result){
			JSONObject jsObj;
			MemberModel member = new MemberModel();
			member.setEmail(mEmail);
			member.setUser(mEmail);
			try {
				jsObj = new JSONObject(result);
				if (jsObj.has("picture")) {
					member.setPhoto(jsObj.getString("picture"));
				}
				if (jsObj.has("name")) {
					member.setNickname(jsObj.getString("name"));
				}
				if (jsObj.has("gender")) {
					member.setGender(jsObj.getString("gender").equalsIgnoreCase("male")?0:1);
				}
				if (jsObj.has("birthday")) {
					member.setBirthday(jsObj.getString("birthday"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			this.profileData = member; 
		}

}
