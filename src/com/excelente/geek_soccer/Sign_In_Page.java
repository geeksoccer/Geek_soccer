package com.excelente.geek_soccer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Sign_In_Page extends Activity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{
	
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static final String MEMBER_SIGN_IN_URL = "http://183.90.171.209/gs_member/member_sign_in.php"; 
	private static final String MEMBER_SIGN_UP_URL = "http://183.90.171.209/gs_member/member_sign_up.php";
	private static final String MEMBER_TOKEN_URL = "http://183.90.171.209/gs_member/member_token.php";
	private static final String MEMBER_SIGN_OUT_URL = "http://183.90.171.209/gs_member/member_sign_out.php";

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    
    private Button signInGoogleButton;
	private ProgressBar signProgressbar;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(MemberSession.hasMember()){
			gotoMainPage(MemberSession.getMember());
		}else{
			setContentView(R.layout.sign_in_page);
			initView();
			doCheckToken();
		}
	}

	private void initView() {
		mPlusClient = new PlusClient.Builder(this, this, this)
        	.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
        	.build();
		
		signInGoogleButton = (Button)findViewById(R.id.sign_in_google_button); 
		signInGoogleButton.setOnClickListener(this);
		
		signProgressbar = (ProgressBar)findViewById(R.id.sign_progressbar); 
		
		mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setCancelable(false);
	}
	
	private void doCheckToken() {
		try {
			if(NetworkUtils.isNetworkAvailable(this)){ 
				new doSignTokenTask().execute().get(5000, TimeUnit.MILLISECONDS);
			}else{
				Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace(); 
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
        //mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {

		if (mConnectionProgressDialog.isShowing()) {

			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
	      
	      mConnectionResult = result;
	}

	@Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    	
    	MemberModel member = new MemberModel();
    	
    	member.setUid(0);
    	member.setUser(mPlusClient.getAccountName());
    	member.setPass("");
    	member.setTypeLogin("google_plus");
    	
    	new doSignInTask().execute(member);
    	
    }

    @Override
    public void onDisconnected() {
        Log.e("SignIn", "disconnected");
    }

	@Override
	public void onClick(View v) { 
		if(NetworkUtils.isNetworkAvailable(Sign_In_Page.this)){ 
			if (v.getId() == R.id.sign_in_google_button && !mPlusClient.isConnected()) {
		        if (mConnectionResult == null) {
		        	mConnectionProgressDialog.setMessage("Signing in...");
		            mConnectionProgressDialog.show();
		            mPlusClient.connect();
		        } else {
		            try {
		                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
		            } catch (SendIntentException e) {
		                mConnectionResult = null;
		                mPlusClient.connect();
		            }
		        }
		    }
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(Sign_In_Page.this), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void gotoMainPage(MemberModel memberSignedIn) {
        Intent intent = new Intent(Sign_In_Page.this, MainActivity.class);
        startActivity(intent);
        finish();
	}
	
	private void doSelectTeam() {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_In_Page.this);
		dialog.setTitle("Select Favorite Team");
		
		CharSequence[] teams = new CharSequence[]{"Arsenal", "Chelsea","Liverpool", "Manchester United", "Others"};
		dialog.setSingleChoiceItems(teams, 0, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						doRegister(0);
						break;
					case 1:
						doRegister(1);
						break;
					case 2:
						doRegister(2);
						break;
					case 3:
						doRegister(3);
						break;
					case 4:
						doRegister(4);
						break;
				}
				dialog.dismiss();
			}
			
		});
		
		dialog.setCancelable(false);
		dialog.create();
		dialog.show();
	}

	private void doRegister(int teamId) {
		MemberModel member = new MemberModel();
    	
    	member.setUser(mPlusClient.getAccountName());
    	member.setPass("");
    	member.setTypeLogin("google_plus");
    	member.setBirthday(mPlusClient.getCurrentPerson().getBirthday());
    	member.setGender(mPlusClient.getCurrentPerson().getGender());
    	member.setNickname(mPlusClient.getCurrentPerson().getDisplayName());
    	member.setPhoto(mPlusClient.getCurrentPerson().getImage().getUrl());
    	member.setEmail(mPlusClient.getAccountName());
    	member.setTeamId(teamId+1);
    	member.setRole(2);
    	
    	if(NetworkUtils.isNetworkAvailable(this)){ 
    		new doSignUpTask().execute(member);
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
		}
	}
	
	class doSignInTask extends AsyncTask<MemberModel, Void, MemberModel>{ 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mConnectionProgressDialog.setMessage("Signing in...");
			mConnectionProgressDialog.show();
		}
		
		@Override
		protected MemberModel doInBackground(MemberModel... params) {
			MemberModel member = params[0];
			
			List<NameValuePair> memberParam = new ArrayList<NameValuePair>();
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_USER, member.getUser()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_PASS, member.getPass()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TYPE_LOGIN, member.getTypeLogin()));
			
			try {
				String scope = "oauth2:" + Scopes.PLUS_LOGIN;
				String token = GoogleAuthUtil.getToken(getApplicationContext(), member.getUser(), scope);
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, token));
			} catch (UserRecoverableAuthException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				e.printStackTrace();
			}
			
			String dev_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID, dev_id));
			
			String memberStr = HttpConnectUtils.getStrHttpPostConnect(MEMBER_SIGN_IN_URL, memberParam);
			
			if(memberStr.trim().equals("member not yet")){ 
				return member;
			}else if(memberStr.trim().equals("this member using on any device")){
				return null;
			}
			MemberModel memberSignedIn = MemberModel.convertMemberJSONToList(memberStr);
			
			return memberSignedIn;
		}
		
		@Override
		protected void onPostExecute(MemberModel memberSignedIn) {
			super.onPostExecute(memberSignedIn);
			mConnectionProgressDialog.dismiss();
			
			if(memberSignedIn == null){
				Toast.makeText(getApplicationContext(), "this member using on any device", Toast.LENGTH_SHORT).show();
				mPlusClient.disconnect();
			}else if(memberSignedIn.getUid() < 1 && memberSignedIn.getTypeLogin().equals("google_plus")){
				doSelectTeam();
			}else{
				gotoMainPage(memberSignedIn);
				MemberSession.setMember(Sign_In_Page.this, memberSignedIn); 
			}
		}
		
	}
	
	class doSignUpTask extends AsyncTask<MemberModel, Void, MemberModel>{ 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mConnectionProgressDialog.setMessage("Signing up...");
			mConnectionProgressDialog.show();
		}
		
		@Override
		protected MemberModel doInBackground(MemberModel... params) {
			MemberModel member = params[0];
			
			List<NameValuePair> memberParam = new ArrayList<NameValuePair>();
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_USER, member.getUser()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_PASS, member.getPass()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_BIRTHDAY, member.getBirthday()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_GENDER, String.valueOf(member.getGender())));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_NICKNAME, member.getNickname()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_PHOTO, member.getPhoto()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_EMAIL, member.getEmail()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TEAM_ID, String.valueOf(member.getTeamId())));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TYPE_LOGIN, member.getTypeLogin()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_ROLE, String.valueOf(member.getRole())));
			
			try {
				String scope = "oauth2:" + Scopes.PLUS_LOGIN;
				String token = GoogleAuthUtil.getToken(getApplicationContext(), member.getUser(), scope);
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, token));
			} catch (UserRecoverableAuthException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				e.printStackTrace();
			}
			
			String dev_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID, dev_id));
			
			String memberStr = HttpConnectUtils.getStrHttpPostConnect(MEMBER_SIGN_UP_URL, memberParam);
			if(memberStr.trim().equals("no parameter")){ 
				return member;
			}
			
			MemberModel memberSignedUp = MemberModel.convertMemberJSONToList(memberStr);
			
			return memberSignedUp;
		}
		
		@Override
		protected void onPostExecute(MemberModel memberSignedUp) {
			super.onPostExecute(memberSignedUp);
			
			mConnectionProgressDialog.dismiss();
			
			if(memberSignedUp.getUid() > 0){
				gotoMainPage(memberSignedUp);
				MemberSession.setMember(Sign_In_Page.this, memberSignedUp); 
			}else{
				Toast.makeText(getApplicationContext(), "Signed Up Fail", Toast.LENGTH_SHORT).show();
				mPlusClient.disconnect();
			}
		}
		
	}
	
	class doSignTokenTask extends AsyncTask<Void, Void, MemberModel>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			signProgressbar.setVisibility(View.VISIBLE);
			signInGoogleButton.setVisibility(View.GONE);
		}
		
		@Override
		protected MemberModel doInBackground(Void... params) {
			
			SharedPreferences memberFile = getSharedPreferences(MemberSession.MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
			
			if(!memberFile.getString(MemberModel.MEMBER_TOKEN, "").equals("")){
	
				List<NameValuePair> memberParam = new ArrayList<NameValuePair>();
				
				String token = memberFile.getString(MemberModel.MEMBER_TOKEN, "");
	
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, token));
				
				String dev_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID, dev_id));
				
				String memberStr = HttpConnectUtils.getStrHttpPostConnect(MEMBER_TOKEN_URL, memberParam);
				if(memberStr.trim().equals("member not yet")){ 
					return null;
				}
				MemberModel memberSignedIn = MemberModel.convertMemberJSONToList(memberStr);
				return memberSignedIn;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(MemberModel memberToken) {
			super.onPostExecute(memberToken);
			
			MemberSession.setMember(Sign_In_Page.this, memberToken);
			
			if(MemberSession.hasMember()){
				gotoMainPage(memberToken);
			}
			
			signProgressbar.setVisibility(View.GONE);
			signInGoogleButton.setVisibility(View.VISIBLE);
		}
		
	}
	
	class doSignOutTask extends AsyncTask<MemberModel, Void, Boolean>{
		
		Activity mActivity; 
		ProgressDialog mConnectionProgressDialog;
		
		public doSignOutTask(Activity context) {
			mActivity = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mConnectionProgressDialog = new ProgressDialog(mActivity);
	        mConnectionProgressDialog.setCancelable(false);
			mConnectionProgressDialog.setMessage("Signing out...");
			mConnectionProgressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(MemberModel... params) {
			MemberModel member = params[0];
			
			List<NameValuePair> memberParam = new ArrayList<NameValuePair>();

			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_UID, String.valueOf(member.getUid())));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, member.getToken()));
			
			String dev_id = Secure.getString(mActivity.getContentResolver(),Secure.ANDROID_ID);
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID, dev_id));
				
			String memberStr = HttpConnectUtils.getStrHttpPostConnect(MEMBER_SIGN_OUT_URL, memberParam);
				
			if(memberStr.trim().equals("updated token")){ 
				return true;
			}
				
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean memberToken) {
			super.onPostExecute(memberToken);
			
			mConnectionProgressDialog.dismiss();
			
			if(memberToken){
				MemberSession.clearMember(mActivity);
				mActivity.finish();
			}else{
				Toast.makeText(mActivity, "Sign Out Failed", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
}
