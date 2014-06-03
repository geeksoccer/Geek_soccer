package com.excelente.geek_soccer.receiver;

import java.util.List;

import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.Sign_In_Page;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

public class NetWorkChageReceiver extends BroadcastReceiver{
	
	public static final String NOTIFY_INTENT = "NOTIFY_INTENT";
	public static final int NOTIFY_INTENT_CODE_NEWS = 1000;
	public static final int NOTIFY_INTENT_CODE_HILIGHT = 2000;
	public static final String NOTIFY_CONNECT_FIRST = "NOTIFY_CONNECT_FIRST"; 
	public static final String SHARE_PERFERENCE = "MEMBER_SHAREPREFERENCE";
    
	Context mContext;
	private SharedPreferences sharePre;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		if(NetworkUtils.isNetworkAvailable(context)){
			checkNetwork(context); 
		}
	}

	private void checkNetwork(Context context) {
        if(SessionManager.hasMember(context))
        	checkUpdateNews(context);
	}

	private void checkUpdateNews(Context context) {
		sharePre = mContext.getSharedPreferences(UpdateService.SHARE_PERFERENCE, Context.MODE_PRIVATE);
		if(!sharePre.getBoolean(UpdateService.NOTIFY_CONNECT_FIRST, true) && SessionManager.hasMember(context)){
			
			int newsIdTag0 = sharePre.getInt(NewsModel.NEWS_ID+"tag0", 0);
			int newsIdTag1 = sharePre.getInt(NewsModel.NEWS_ID+"tag1", 0);
			int hilightId = sharePre.getInt(HilightModel.HILIGHT_ID, 0);
			String url = getURLbyTag(SessionManager.getMember(context), newsIdTag0, "tag0");
			if(!SessionManager.getSetting(mContext, SessionManager.setting_notify_team_news).equals("false"))
					new LoadLastNewsTask("tag0").execute(url);
			
			url = getURLbyTag(SessionManager.getMember(context), newsIdTag1, "tag1");
			if(!SessionManager.getSetting(mContext, SessionManager.setting_notify_global_news).equals("false"))
					new LoadLastNewsTask("tag1").execute(url);
			
			if(!SessionManager.getSetting(mContext, SessionManager.setting_notify_hilight).equals("false"))
					new LoadLastHilightTask().execute(getURLHilight(context, hilightId));
			
		}
	}
	
	private String getURLHilight(Context context, int id) { 
		return 	UpdateService.GET_HILIGHT_UPDATE_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=All&member_id=" + SessionManager.getMember(context).getUid();
	}
	
	private String getURLbyTag(MemberModel member, int id, String tag) {
		String url = ""; 
		
		if(tag.equals("tag0")){
			url = UpdateService.GET_NEWS_UPDATE_URL + "?" + NewsModel.NEWS_TEAM_ID + "=" + member.getTeamId() + "&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ member.getUid();
		}else if(tag.equals("tag1")){
			url = UpdateService.GET_NEWS_UPDATE_URL + "?" + NewsModel.NEWS_TEAM_ID + "=0&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ member.getUid();
		}
		
		return url;
	}
	
	public class LoadLastNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		private NotificationManager mNotification;
		private String tag;
		
		public LoadLastNewsTask(String tag) {
			this.tag = tag;
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
			sharePre = mContext.getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			sharePre.edit().putBoolean(UpdateService.NOTIFY_CONNECT_FIRST, true).commit();
			
			Intent nextToMain = new Intent(mContext, Sign_In_Page.class);
			nextToMain.putExtra(NOTIFY_INTENT, NOTIFY_INTENT_CODE_NEWS);
			PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, nextToMain, 0);
				
			mNotification = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
			if(tag.equals("tag0")){
				editSharePre.putInt(NewsModel.NEWS_ID+"tag0", result.get(0).getNewsId());
				editSharePre.commit();
				
				nextToMain.putExtra(NewsModel.NEWS_ID+"tag", 0);
				pIntent = PendingIntent.getActivity(mContext, 0, nextToMain, 0);
				
				Notification notification = new NotificationCompat.Builder(mContext)
				.setContentTitle(mContext.getResources().getString(R.string.team_news)) 
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
				pIntent = PendingIntent.getActivity(mContext, 1, nextToMain, 0);
				
				Notification notification = new NotificationCompat.Builder(mContext)
				.setContentTitle(mContext.getResources().getString(R.string.global_news)) 
				.setContentText(result.get(0).getNewsTopic())
				.setSmallIcon(R.drawable.notify_news)
				.setContentIntent(pIntent)
				.build();
				notification.defaults = Notification.DEFAULT_ALL;
				
				mNotification.cancel(1);
				mNotification.notify(1, notification);
			}
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
			sharePre = mContext.getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			sharePre.edit().putBoolean(UpdateService.NOTIFY_CONNECT_FIRST, true).commit();
			editSharePre.putInt(HilightModel.HILIGHT_ID, result.get(0).getHilightId());
			editSharePre.commit();
			
			Intent nextToMain = new Intent(mContext, Sign_In_Page.class);
			nextToMain.putExtra(NOTIFY_INTENT, NOTIFY_INTENT_CODE_HILIGHT);
			PendingIntent pIntent; 
				
			mNotification = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
			pIntent = PendingIntent.getActivity(mContext, 3, nextToMain, 0);
				
			Notification notification = new NotificationCompat.Builder(mContext)
				.setContentTitle(mContext.getResources().getString(R.string.title_bar_hilight)) 
				.setContentText(result.get(0).getHilightTopic()) 
				.setSmallIcon(R.drawable.notify_hilight)
				.setContentIntent(pIntent)
				.build();
			notification.defaults = Notification.DEFAULT_ALL;
				
			mNotification.cancel(3);
			mNotification.notify(3, notification);
		}

	}

}
