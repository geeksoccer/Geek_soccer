package com.excelente.geek_soccer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.Sign_In_Page;
import com.excelente.geek_soccer.livescore_noty.LiveScore_Noty;
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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

@SuppressLint("CommitPrefEdits")
public class UpdateService extends Service{
	
	public static final String NOTIFY_INTENT = "NOTIFY_INTENT";
	public static final int NOTIFY_INTENT_CODE_NEWS = 1000;
	public static final int NOTIFY_INTENT_CODE_HILIGHT = 2000;
	public static final long NOTIFY_REPEAT = 1*60*60*1000; 
	public static final String NOTIFY_CONNECT_FIRST = "NOTIFY_CONNECT_FIRST"; 
	public static final String SHARE_PERFERENCE = "MEMBER_SHAREPREFERENCE";
	
	public final static String NOTIFY_NEWS_UPDATE = "com.pilarit.chettha.manfindjob.service.NOTIFY_NEWS_UPDATE";
	public final static int NOTIFY_NEWS_UPDATE_VALUE = 1001;
	
	private SharedPreferences sharePre; 
	TimerTask newsTask = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(newsTask == null){
			LiveScore_Noty.StartLiveScoreChk(this);
			if(SessionManager.hasMember(getApplication()))
				runUpdateNews(SessionManager.getMember(getApplication()));
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
				if(NetworkUtils.isNetworkAvailable(getApplicationContext()) && SessionManager.hasMember(getApplication())){
					if(!isForeground(getApplicationContext().getPackageName())){ 
						if(!SessionManager.getSetting(getApplicationContext(), SessionManager.setting_notify_team_news).equals("false"))
							loadLastNewsTask("tag0", getURLbyTag(member, newsIdTag0, "tag0"));
						if(!SessionManager.getSetting(getApplicationContext(), SessionManager.setting_notify_global_news).equals("false"))
							loadLastNewsTask("tag1", getURLbyTag(member, newsIdTag1, "tag1"));
						if(!SessionManager.getSetting(getApplicationContext(), SessionManager.setting_notify_hilight).equals("false"))
							loadLastHilightTask(getURLHilight(hilightId));
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
		return 	ControllParameter.GET_HILIGHT_UPDATE_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=All&member_id=" + SessionManager.getMember(getApplication()).getUid();
	}

	private String getURLbyTag(MemberModel member, int id, String tag) {
		String url = ""; 
		
		if(tag.equals("tag0")){
			url = ControllParameter.GET_NEWS_UPDATE_URL + "?" + NewsModel.NEWS_TEAM_ID + "=" + member.getTeamId() + "&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ member.getUid();
		}else if(tag.equals("tag1")){
			url = ControllParameter.GET_NEWS_UPDATE_URL + "?" + NewsModel.NEWS_TEAM_ID + "=0&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ member.getUid();
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

	public void loadLastNewsTask(String tag, String url){
		
		List<NewsModel> newsList = new ArrayList<NewsModel>();

		String result = HttpConnectUtils.getStrHttpGetConnect(url); 
		
		if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
			newsList = null;
		}else{
			newsList = NewsModel.convertNewsStrToList(result);
		}

		if(newsList!=null && !newsList.isEmpty()){
			setNotifyNews(tag, newsList);
		}

	}
	
	private void setNotifyNews(String tag, List<NewsModel> result) {
		NotificationManager mNotification;
		
		sharePre = getApplicationContext().getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
		Editor editSharePre = sharePre.edit();
			
		Intent nextToMain = new Intent(getApplicationContext(), Sign_In_Page.class);
		nextToMain.putExtra(NOTIFY_INTENT, NOTIFY_INTENT_CODE_NEWS);
		PendingIntent pIntent;
		
		mNotification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		if(tag.equals("tag0")){
			editSharePre.putInt(NewsModel.NEWS_ID+"tag0", result.get(0).getNewsId());
			editSharePre.commit();
			
			nextToMain.putExtra(NewsModel.NEWS_ID+"tag", 0);
			pIntent = PendingIntent.getActivity(getApplicationContext(), 0, nextToMain, 0);
			
			Notification notification = new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle(getResources().getString(R.string.team_news)) 
			.setContentText(result.get(0).getNewsTopic())
			.setSmallIcon(R.drawable.notify_news)
			.setContentIntent(pIntent)
			.build();
			notification.defaults = Notification.DEFAULT_ALL;
			
			mNotification.cancel(0);
			mNotification.notify(0, notification);
		}else{
			editSharePre.putInt(NewsModel.NEWS_ID+"tag1", result.get(0).getNewsId());
			editSharePre.commit();
			
			nextToMain.putExtra(NewsModel.NEWS_ID+"tag", 1);
			pIntent = PendingIntent.getActivity(getApplicationContext(), 1, nextToMain, 0);
			
			Notification notification = new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle(getResources().getString(R.string.global_news)) 
			.setContentText(result.get(0).getNewsTopic())
			.setSmallIcon(R.drawable.notify_news)
			.setContentIntent(pIntent)
			.build();
			notification.defaults = Notification.DEFAULT_ALL;
			
			mNotification.cancel(1);
			mNotification.notify(1, notification);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(newsTask != null)
			newsTask.cancel();
	}
	
	public void loadLastHilightTask(String url){ 
		List<HilightModel> hilightList = new ArrayList<HilightModel>();
			
		String result = HttpConnectUtils.getStrHttpGetConnect(url); 
			
		if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
			hilightList = null;
		}else{
			hilightList = HilightModel.convertHilightStrToList(result);
		}
		
		if(hilightList!=null && !hilightList.isEmpty()){
			setNotifyHilight(hilightList);
		}

	}
			
	private void setNotifyHilight(List<HilightModel> result) { 
		NotificationManager mNotification;
		sharePre = getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
		Editor editSharePre = sharePre.edit();
		editSharePre.putInt(HilightModel.HILIGHT_ID, result.get(0).getHilightId());
		editSharePre.commit();
				
		Intent nextToMain = new Intent(getApplicationContext(), Sign_In_Page.class);
		nextToMain.putExtra(NOTIFY_INTENT, NOTIFY_INTENT_CODE_HILIGHT);
		PendingIntent pIntent; 
					
		mNotification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		pIntent = PendingIntent.getActivity(getApplicationContext(), 3, nextToMain, 0);
					
		Notification notification = new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle(getResources().getString(R.string.title_bar_hilight)) 
			.setContentText(result.get(0).getHilightTopic()) 
			.setSmallIcon(R.drawable.notify_hilight)
			.setContentIntent(pIntent)
			.build();
					
		mNotification.cancel(3);
		mNotification.notify(3, notification);
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

}
