package com.excelente.geek_soccer.receiver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.MemberSession;
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
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;

public class NetWorkChageReceiver extends BroadcastReceiver{
	
	private static final String MEMBER_TOKEN_URL = "http://183.90.171.209/gs_member/member_token.php";
	public static final String NOTIFY_INTENT = "NOTIFY_INTENT";
	public static final int NOTIFY_INTENT_CODE_NEWS = 1000;
	public static final int NOTIFY_INTENT_CODE_HILIGHT = 2000;
	public static final String NOTIFY_CONNECT_FIRST = "NOTIFY_CONNECT_FIRST"; 
	public static final String SHARE_PERFERENCE = "SHARE_PERFERENCE";
	
	private Vibrator mVibrator;
	
	int dot = 200; // Length of a Morse Code "dot" in milliseconds
    int dash = 500; // Length of a Morse Code "dash" in milliseconds
    int short_gap = 200; // Length of Gap Between dots/dashes
    int medium_gap = 500; // Length of Gap Between Letters
    int long_gap = 1000; // Length of Gap Between Words
 
    long[] pattern = { 0, dot, short_gap, dot, short_gap, dot};
    
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
        if(MemberSession.hasMember())
        	checkUpdateNews(context);
        else
        	new doSignTokenTask().execute();
	}

	private void checkUpdateNews(Context context) {
		sharePre = mContext.getSharedPreferences(UpdateService.SHARE_PERFERENCE, Context.MODE_PRIVATE);
		if(!sharePre.getBoolean(UpdateService.NOTIFY_CONNECT_FIRST, true) && MemberSession.hasMember()){
			int newsIdTag0 = sharePre.getInt(NewsModel.NEWS_ID+"tag0", 0);
			int newsIdTag1 = sharePre.getInt(NewsModel.NEWS_ID+"tag1", 0);
			int hilightId = sharePre.getInt(HilightModel.HILIGHT_ID, 0);
			String url = getURLbyTag(MemberSession.getMember(), newsIdTag0, "tag0");
			new LoadLastNewsTask("tag0").execute(url);
			url = getURLbyTag(MemberSession.getMember(), newsIdTag1, "tag1");
			new LoadLastNewsTask("tag1").execute(url);
			new LoadLastHilightTask().execute(getURLHilight(hilightId));
		}
	}
	
	private String getURLHilight(int id) {
		return 	UpdateService.GET_HILIGHT_UPDATE_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=All&member_id=" + MemberSession.getMember().getUid();
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
				mNotification.cancel(1);
				mNotification.notify(1, notification);
			}
			  
			mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
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
			sharePre = mContext.getSharedPreferences(SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
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
				
			mNotification.cancel(3);
			mNotification.notify(3, notification);
			
			mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(pattern, -1);
		}

	}
	
	class doSignTokenTask extends AsyncTask<Void, Void, MemberModel>{
		
		@Override
		protected MemberModel doInBackground(Void... params) {
			
			SharedPreferences memberFile = mContext.getSharedPreferences(MemberSession.MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
			
			if(!memberFile.getString(MemberModel.MEMBER_TOKEN, "").equals("")){
	
				List<NameValuePair> memberParam = new ArrayList<NameValuePair>();
				
				String token = memberFile.getString(MemberModel.MEMBER_TOKEN, "");
	
				memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, token));
				
				String dev_id = Secure.getString(mContext.getContentResolver(),Secure.ANDROID_ID);
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
			MemberSession.setMember(mContext, memberToken);
			checkUpdateNews(mContext);
		}
		
	}

}
