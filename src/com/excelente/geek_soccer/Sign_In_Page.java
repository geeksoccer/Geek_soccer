package com.excelente.geek_soccer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.authen.AuthenGoogleAccount;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
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

public class Sign_In_Page extends Activity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, AuthenGoogleAccount.OnConnectGoogleAccount{
	
	private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static final String MEMBER_SIGN_IN_URL = "http://183.90.171.209/gs_member/member_sign_in.php"; 
	private static final String MEMBER_SIGN_UP_URL = "http://183.90.171.209/gs_member/member_sign_up.php";
	private static final String MEMBER_TOKEN_URL = "http://183.90.171.209/gs_member/member_token.php";
	
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    
    private SignInButton signInGoogleButton;
	private ProgressBar signProgressbar;
	private NotificationManager mNotification;

	private Button signInGoogleAccountButton;

	private AccountManager mAccountManager;

	private AuthenGoogleAccount authenGoogleAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SessionManager.setLangApp(getApplicationContext());
		cancelNotify();
		
		/*MemberModel mem = new MemberModel();
		mem.setUid(14);
		mem.setTeamId(1);
		mem.setRole(2);
		mem.setNickname("ched");
		mem.setToken("1234");
		SessionManager.setMember(getApplicationContext(), mem);
		gotoMainPage(SessionManager.getMember());*/
		
		if(SessionManager.hasMember(this)){
			gotoMainPage(SessionManager.getMember(this));
		}else{
			setContentView(R.layout.sign_in_page);
			initView();
			doCheckToken(); 
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		cancelNotify();
	}

	private void cancelNotify() {
		mNotification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		mNotification.cancel(0);
		mNotification.cancel(1); 
		mNotification.cancel(3); 
	}

	private void initView() {
		mPlusClient = new PlusClient.Builder(this, this, this)
        	.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
        	.build();
		 
		signInGoogleButton = (SignInButton)findViewById(R.id.sign_in_google_button); 
		signInGoogleButton.setOnClickListener(this);
		
		signInGoogleAccountButton = (Button)findViewById(R.id.sign_in_google_account_button); 
		signInGoogleAccountButton.setOnClickListener(this);
		
		signProgressbar = (ProgressBar)findViewById(R.id.sign_progressbar); 
		
		mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setCancelable(false);
	}
	
	private void doCheckToken() {
		if(NetworkUtils.isNetworkAvailable(this)){ 
			new doSignTokenTask().execute();
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
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
    
    private String[] getAccountNames() {
		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
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
		    }else if(v.getId() == R.id.sign_in_google_account_button){
		    	signProgressbar.setVisibility(View.VISIBLE);
				signInGoogleAccountButton.setVisibility(View.GONE);
		    	syncInGoogleAccount();
		    }
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(Sign_In_Page.this), Toast.LENGTH_SHORT).show();
		}
	}
	
	 private void syncInGoogleAccount() {
		 String[] accountarrs = getAccountNames();
	    if(accountarrs.length>0){
	    	authenGoogleAccount = (AuthenGoogleAccount) new AuthenGoogleAccount(this, accountarrs[0], SCOPE).execute();
	    	authenGoogleAccount.setOnConnectGoogleAccount(this);
	   	} else {
			Toast.makeText(this, getResources().getString(R.string.sign_google_no_sync), Toast.LENGTH_SHORT).show();
		}
	}
	 
	private void gotoMainPage(MemberModel memberSignedIn) {
        Intent intent = new Intent(Sign_In_Page.this, MainActivity.class);
        intent.putExtra(NewsModel.NEWS_ID+"tag", getIntent().getIntExtra(NewsModel.NEWS_ID+"tag", 0));
        intent.putExtra(UpdateService.NOTIFY_INTENT, getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000));
        startActivity(intent);
        finish();
	}
	
	private void doSelectTeam() {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_In_Page.this);
		dialog.setTitle(getResources().getString(R.string.dailog_favorite_team));
		
		dialog.setSingleChoiceItems(getResources().getStringArray(R.array.team_list), 0, new DialogInterface.OnClickListener() {
			
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
    	
    	member.setUser(authenGoogleAccount.getProfileData().getUser());
    	member.setPass("");
    	member.setTypeLogin("google_plus");
    	member.setBirthday(authenGoogleAccount.getProfileData().getBirthday());
    	member.setGender(authenGoogleAccount.getProfileData().getGender());
    	member.setNickname(authenGoogleAccount.getProfileData().getNickname());
    	member.setPhoto(authenGoogleAccount.getProfileData().getPhoto());
    	member.setEmail(authenGoogleAccount.getProfileData().getEmail());
    	member.setRole(2);
    	member.setTeamId(teamId+1);
    	
    	if(NetworkUtils.isNetworkAvailable(this)){ 
    		new doSignUpTask().execute(member);
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
		}
	}

	public class doSignInTask extends AsyncTask<MemberModel, Void, MemberModel>{ 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			 
			mConnectionProgressDialog.setMessage(getResources().getString(R.string.sign_signing_in));
			mConnectionProgressDialog.show();
			
			signProgressbar.setVisibility(View.VISIBLE);
			signInGoogleAccountButton.setVisibility(View.GONE);
		}
		
		@Override
		protected MemberModel doInBackground(MemberModel... params) {
			MemberModel member = params[0];
			
			List<NameValuePair> memberParam = new ArrayList<NameValuePair>();
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_USER, member.getUser()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_PASS, member.getPass()));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TYPE_LOGIN, member.getTypeLogin()));
			
			try {
				String scope = "oauth2:" + SCOPE;
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
				signProgressbar.setVisibility(View.GONE);
				signInGoogleAccountButton.setVisibility(View.VISIBLE);
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_warning), Toast.LENGTH_SHORT).show();
				mPlusClient.disconnect(); 
			}else if(memberSignedIn.getUid() < 1 && memberSignedIn.getTypeLogin().equals("google_plus")){
				signProgressbar.setVisibility(View.GONE);
				signInGoogleAccountButton.setVisibility(View.VISIBLE);
				doSelectTeam();
			}else{
				SessionManager.setMember(Sign_In_Page.this, memberSignedIn); 
				if(SessionManager.hasMember(Sign_In_Page.this))
					gotoMainPage(memberSignedIn);
				else{
					signProgressbar.setVisibility(View.GONE);
					signInGoogleAccountButton.setVisibility(View.VISIBLE);
				}
			}
		}
		
	}
	
	public class doSignUpTask extends AsyncTask<MemberModel, Void, MemberModel>{ 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mConnectionProgressDialog.setMessage(getResources().getString(R.string.sign_signing_up));
			mConnectionProgressDialog.show(); 
			
			signProgressbar.setVisibility(View.VISIBLE);
			signInGoogleAccountButton.setVisibility(View.GONE);
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
				String scope = "oauth2:" + SCOPE;
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
			
			if(memberSignedUp!=null && memberSignedUp.getUid() > 0){
				gotoMainPage(memberSignedUp);
				SessionManager.setMember(Sign_In_Page.this, memberSignedUp); 
			}else{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				mPlusClient.disconnect(); 
				signProgressbar.setVisibility(View.GONE);
				signInGoogleAccountButton.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	public class doSignTokenTask extends AsyncTask<Void, Void, MemberModel>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			signProgressbar.setVisibility(View.VISIBLE);
			signInGoogleAccountButton.setVisibility(View.GONE);
		}
		
		@Override
		protected MemberModel doInBackground(Void... params) {
			
			SharedPreferences memberFile = getSharedPreferences(SessionManager.MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
			
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
			
			SessionManager.setMember(Sign_In_Page.this, memberToken);
			
			if(SessionManager.hasMember(Sign_In_Page.this)){
				gotoMainPage(memberToken);
			}else{
		    	syncInGoogleAccount();
			}
		}
		
	}

	@Override
	public void onConnect() {
		MemberModel member = new MemberModel();
    	member.setUid(0);
		member.setUser(authenGoogleAccount.getProfileData().getUser());
		member.setPass("");
		member.setTypeLogin("google_plus");
		new doSignInTask().execute(member);
	}

	@Override
	public void onConnectFail() {
		signProgressbar.setVisibility(View.GONE);
		signInGoogleAccountButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onTryAgain() {
		signProgressbar.setVisibility(View.GONE);
		signInGoogleAccountButton.setVisibility(View.VISIBLE);
	}
	
}
