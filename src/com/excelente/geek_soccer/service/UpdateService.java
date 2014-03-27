package com.excelente.geek_soccer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.MemberSession;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.Sign_In_Page;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;

@SuppressLint("CommitPrefEdits")
public class UpdateService extends Service{
	
	private static final String MEMBER_TOKEN_URL = "http://183.90.171.209/gs_member/member_token.php";
	public static final String GET_NEWS_UPDATE_URL = "http://183.90.171.209/gs_news/get_news_update.php";
	public static final String GET_HILIGHT_UPDATE_URL = "http://183.90.171.209/gs_hilight/get_hilight_update.php";
	
	public static final String NOTIFY_INTENT = "NOTIFY_INTENT";
	public static final int NOTIFY_INTENT_CODE_NEWS = 1000;
	public static final int NOTIFY_INTENT_CODE_HILIGHT = 2000;
	public static final long NOTIFY_REPEAT = 1*60*60*1000; 
	public static final String NOTIFY_CONNECT_FIRST = "NOTIFY_CONNECT_FIRST"; 
	public static final String SHARE_PERFERENCE = "SHARE_PERFERENCE";
	
	public final static String NOTIFY_NEWS_UPDATE = "com.pilarit.chettha.manfindjob.service.NOTIFY_NEWS_UPDATE";
	public final static int NOTIFY_NEWS_UPDATE_VALUE = 1001;
	
	private Vibrator mVibrator;
	
	int dot = 200; // Length of a Morse Code "dot" in milliseconds
    int dash = 500; // Length of a Morse Code "dash" in milliseconds
    int short_gap = 200; // Length of Gap Between dots/dashes
    int medium_gap = 500; // Length of Gap Between Letters
    int long_gap = 1000; // Length of Gap Between Words
 
    long[] pattern = { 0, dot, short_gap, dot, short_gap, dot};
	
	private SharedPreferences sharePre; 
	TimerTask newsTask = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(newsTask == null){
			if(MemberSession.hasMember())
				runUpdateNews(MemberSession.getMember());
			else{
				if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
					new doSignTokenTask().execute();
				}else{
					runUpdateNews(MemberSession.getMember());
				}
			}
		}
		return Service.START_STICKY;
	}

	private void runUpdateNews(final MemberModel member) { 
		 
		newsTask = new TimerTask() {
			
			@Override 
			public void run() {
				sharePre = getApplicationContext().getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
				int newsIdTag0 = sharePre.getInt(NewsModel.NEWS_ID+"tag0", 0);
				int newsIdTag1 = sharePre.getInt(NewsModel.NEWS_ID+"tag1", 0);
				int hilightId = sharePre.getInt(HilightModel.HILIGHT_ID, 0);
				if(NetworkUtils.isNetworkAvailable(getApplicationContext()) && MemberSession.hasMember()){
					if(!isForeground(getApplicationContext().getPackageName())){ 
						new LoadLastNewsTask("tag0").execute(getURLbyTag(member, newsIdTag0, "tag0"));
						new LoadLastNewsTask("tag1").execute(getURLbyTag(member, newsIdTag1, "tag1")); 
						new LoadLastHilightTask().execute(getURLHilight(hilightId));
					}else{
						//updateMainActivity();
					}
				}else{
					Editor editShare = sharePre.edit();
					editShare.putBoolean(NOTIFY_CONNECT_FIRST, false);
					editShare.commit();
				}
			}

		};
		
		Timer timer = new Timer();
		timer.schedule(newsTask, getTimeUpdate(), NOTIFY_REPEAT);
	}
	
	private String getURLHilight(int id) {
		return 	UpdateService.GET_HILIGHT_UPDATE_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=All&member_id=" + MemberSession.getMember().getUid();
	}

	private String getURLbyTag(MemberModel member, int id, String tag) {
		String url = ""; 
		
		if(tag.equals("tag0")){
			url = GET_NEWS_UPDATE_URL + "?" + NewsModel.NEWS_TEAM_ID + "=" + member.getTeamId() + "&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ member.getUid();
		}else if(tag.equals("tag1")){
			url = GET_NEWS_UPDATE_URL + "?" + NewsModel.NEWS_TEAM_ID + "=0&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ member.getUid();
		}
		
		return url;
	}
	
	/*private void updateMainActivity() {
		Intent nextToMain = new Intent(); 
		nextToMain.setAction(NOTIFY_NEWS_UPDATE);
		nextToMain.putExtra(NOTIFY_NEWS_UPDATE, NOTIFY_NEWS_UPDATE_VALUE);
		sendBroadcast(nextToMain);
	}*/
	
	@SuppressWarnings("deprecation")
	private Date getTimeUpdate() {
		Date now = new Date();
		now.setMinutes(0);
		
		for (int i = 0; i < 24; i++) {
			if(now.getHours() < i){
				now.setHours(i);
				break;
			}else if(i==23){
				now.setHours(0);
				break;
			}
		}
		
		return now;
	}

	public class LoadLastNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		private NotificationManager mNotification;
		String tag;
		
		public LoadLastNewsTask(String string) {
			tag = string;
		}

		@Override
		protected List<NewsModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
		
			List<NewsModel> newsList = NewsModel.convertNewsStrToList(result);
			
			return newsList;
		}

		@Override
		protected void onPostExecute(List<NewsModel> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				setNotify(result);
			}
			
		}

		private void setNotify(List<NewsModel> result) {
			sharePre = getApplicationContext().getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			editSharePre.putInt(NewsModel.NEWS_ID, result.get(0).getNewsId());
			editSharePre.commit();
			
			Intent nextToMain = new Intent(getApplicationContext(), Sign_In_Page.class);
			nextToMain.putExtra(NOTIFY_INTENT, NOTIFY_INTENT_CODE_HILIGHT);
			PendingIntent pIntent;
				
			mNotification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
			if(tag.equals("tag0")){
				nextToMain.putExtra(NewsModel.NEWS_ID+"tag", 0);
				pIntent = PendingIntent.getActivity(getApplicationContext(), 0, nextToMain, 0);
				
				Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setContentTitle(getResources().getString(R.string.team_news)) 
				.setContentText(result.get(0).getNewsTopic())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntent)
				.build();
				
				mNotification.cancel(0);
				mNotification.notify(0, notification);
			}else{
				nextToMain.putExtra(NewsModel.NEWS_ID+"tag", 1);
				pIntent = PendingIntent.getActivity(getApplicationContext(), 0, nextToMain, 0);
				
				Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setContentTitle(getResources().getString(R.string.global_news)) 
				.setContentText(result.get(0).getNewsTopic())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntent)
				.build();
				mNotification.cancel(1);
				mNotification.notify(1, notification);
			}
			
			mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(pattern, -1);
		}

	}
	
public class LoadLastHilightTask extends AsyncTask<String, Void, List<HilightModel>>{ 
		
		private NotificationManager mNotification;
		 
		@Override
		protected List<HilightModel> doInBackground(String... params){ 
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
		
			List<HilightModel> hilightList = HilightModel.convertHilightStrToList(result);
			 
			return hilightList;
		}

		@Override
		protected void onPostExecute(List<HilightModel> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				setNotify(result);
			}
		}
		
		private void setNotify(List<HilightModel> result) { 
			sharePre = getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			editSharePre.putInt(HilightModel.HILIGHT_ID, result.get(0).getHilightId());
			editSharePre.commit();
			
			Intent nextToMain = new Intent(getApplicationContext(), Sign_In_Page.class);
			nextToMain.putExtra(NOTIFY_INTENT, NOTIFY_INTENT_CODE_HILIGHT);
			PendingIntent pIntent; 
				
			mNotification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
			pIntent = PendingIntent.getActivity(getApplicationContext(), 0, nextToMain, 0);
				
			Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setContentTitle(getResources().getString(R.string.title_bar_hilight)) 
				.setContentText(result.get(0).getHilightTopic()) 
				.setSmallIcon(R.drawable.game_icon_select)
				.setContentIntent(pIntent)
				.build();
				
			mNotification.cancel(3);
			mNotification.notify(3, notification);
			
			mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(pattern, -1);
		}

	}
	
	public boolean isForeground(String myPackage){
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List< ActivityManager.RunningTaskInfo > runningTaskInfo = manager.getRunningTasks(1); 

		ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
		if(componentInfo.getPackageName().equals(myPackage)) return true;
		
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	class doSignTokenTask extends AsyncTask<Void, Void, MemberModel>{
		
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
			MemberSession.setMember(getApplicationContext(), memberToken);
			runUpdateNews(MemberSession.getMember());
		}
		
	}

}
