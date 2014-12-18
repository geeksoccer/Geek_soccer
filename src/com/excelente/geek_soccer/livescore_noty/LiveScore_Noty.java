package com.excelente.geek_soccer.livescore_noty;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.JSONParser;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.Sign_In_Page;

public class LiveScore_Noty {
	
	static Context mContextG;
	
	public static void StartLiveScoreChk(final Context mContext){
		mContextG = mContext;
		final ControllParameter data = ControllParameter.getInstance(mContext);
		if(data.socket_LiveScore==null){
			TimerTask newsTask = new TimerTask() {
				
				@Override 
				public void run() {
					if(data.socket_LiveScore==null){
						new Live_score_1stLoader().execute();
					}
				}

			};
			Calendar c_t = Calendar.getInstance();
			Timer timer = new Timer();
			timer.schedule(newsTask, c_t.getTime(), 5*60*1000);
		}
	}
	
	public static class Live_score_1stLoader extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				final ControllParameter data = ControllParameter.getInstance(mContextG);
				JSONParser jParser = new JSONParser();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("_k",
						"592ffdb05020001222c7d024479b028d"));
				params.add(new BasicNameValuePair("_t", "get-livescore"));
				params.add(new BasicNameValuePair("_u", String
						.valueOf(SessionManager.getMember(mContextG)
								.getUid())));
				params.add(new BasicNameValuePair("_d", "c"));

				JSONObject json_ob = jParser
						.makeHttpRequest(
								ControllParameter.GET_LIVESCORE_URL,
								"POST", params);

				if (json_ob != null) {
					Boolean continueUpdate = false;
					JSONArray json_itArr = json_ob.getJSONArray("it");
					for(int i=0; i<json_itArr.length(); i++){
						JSONObject json_it = json_itArr.getJSONObject(i);
						String League = json_it.getString("ln");
						if(League.contains("UEFA Champions League")){
							League = "[1]"+League;
						}else if(League.contains("Premier League")){
							League = "[2]"+League;
						}else if(League.contains("Primera División")||League.contains("Primera DivisiÃ³n")){
							League = "[3]"+League;
						}else if(League.contains("Bundesliga")){
							League = "[4]"+League;
						}else if(League.contains("Serie A")){
							League = "[5]"+League;
						}else if(League.contains("Ligue 1")){
							League = "[6]"+League;
						}
						data.Match_list_c_JSON.add(new JSONObject().put("League", League));
						JSONArray json_dtArr = json_it.getJSONArray("dt");
						
						for(int j=0; j<json_dtArr.length(); j++){
							JSONObject json_dt = json_dtArr.getJSONObject(j);
							String id = json_dt.getString("id");
							String Home  = json_dt.getString("ht");
							String away  = json_dt.getString("at");
							String Time = "";//json_dt.getString("tp");
							
							if(json_dt.getString("ty").equals("playing")){
								continueUpdate = true;
								Time = json_dt.getString("pr");
							}else if(json_dt.getString("ty").equals("played")){
								Time = "FT";
							}else if(json_dt.getString("ty").equals("postponed")){
								Time = json_dt.getString("ty");
							}else if(json_dt.getString("ty").equals("fixture")){
								Time = json_dt.getString("tp");
							}else{
								Time = json_dt.getString("tp");
							}

							String score = json_dt.getString("sc");
							String score_ag = json_dt.getString("ag");
							if(score.equals("")){
								score = "vs";
							}
							if(score_ag==null || score_ag.length()<5){
								score_ag="";
							}else{
								if(!score.equals("vs")){
									String AG[] = score_ag.replaceAll(" ", "").split("-");
									int Ag_home = Integer.parseInt(AG[0]);
									int Ag_away = Integer.parseInt(AG[1]);
									String SC[] = score.replaceAll(" ", "").split("-");
									int Sc_home = Integer.parseInt(SC[0]);
									int SC_away = Integer.parseInt(SC[1]);
									score_ag = String.valueOf(Ag_home+Sc_home)+ " - " + String.valueOf(Ag_away+SC_away);
								}
							}
							if (SessionManager.chkFavContain(mContextG, id)
									||away.contains(ControllParameter.TeamSelect)
									|| Home.contains(ControllParameter.TeamSelect)) {
								NotifyLiveScore(mContextG, id, Home, score, away, Time, data);
								data.OldScoreH.put(id, score);
								data.OldTimeH.put(id, Time);
							}
						}
					}
					
					if(!continueUpdate){
						Log.d("TEST", "continueUpdate::NO");
					}else{
						Log.d("TEST", "continueUpdate::YES");
						Live_score_Loader(mContextG);
						return "continueUpdate";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String msg) {
			if(msg!=null){
				Live_score_Loader(mContextG);
			}
		}
	}
	
	public static void Live_score_Loader(final Context mContext) {

		Runnable runnable = new Runnable() {
			public void run() {
				final ControllParameter data = ControllParameter.getInstance(mContext);
				try {
					data.socket_LiveScore = new SocketIO("http://"+ControllParameter.SERVER_URL+":5070");
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				data.socket_LiveScore.connect(new IOCallback() {
		            @Override
		            public void onMessage(JSONObject json, IOAcknowledge ack) {
		                try {
		                	Log.d("TEST","test::Server said:" + json.toString(2));
		                } catch (JSONException e) {
		                    e.printStackTrace();
		                }
		            }

		            @Override
		            public void onMessage(String data, IOAcknowledge ack) {
		            	Log.d("TEST","test::Server said: " + data);
		            }

		            @Override
		            public void onError(SocketIOException socketIOException) {
		            	Log.d("TEST","test::an Error occured");
		                socketIOException.printStackTrace();
		            }

		            @Override
		            public void onDisconnect() {
		            	
		            	data.liveScore_on = false;
		            	Log.d("TEST", "chat_on::"+data.liveScore_on);
		            }

		            @Override
		            public void onConnect() {
		            	
		            	data.liveScore_on = true;
		            	Log.d("TEST", "chat_on::"+data.liveScore_on);
		            }

		            @Override
		            public void on(String event, IOAcknowledge ack, Object... args) {
		            	
		            	if (event.equals("update-score")) {
		            		data.Match_list_c_JSON.clear();
		            		
		            		for (Object object : args) {
		                		try {
									JSONObject json_ob = new JSONObject(object.toString());
									Boolean continueUpdate = false;
									JSONArray json_itArr = json_ob.getJSONArray("it");
									for(int i=0; i<json_itArr.length(); i++){
										JSONObject json_it = json_itArr.getJSONObject(i);
										String League = json_it.getString("ln");
										if(League.contains("UEFA Champions League")){
											League = "[1]"+League;
										}else if(League.contains("Premier League")){
											League = "[2]"+League;
										}else if(League.contains("Primera División")||League.contains("Primera DivisiÃ³n")){
											League = "[3]"+League;
										}else if(League.contains("Bundesliga")){
											League = "[4]"+League;
										}else if(League.contains("Serie A")){
											League = "[5]"+League;
										}else if(League.contains("Ligue 1")){
											League = "[6]"+League;
										}
										data.Match_list_c_JSON.add(new JSONObject().put("League", League));
										JSONArray json_dtArr = json_it.getJSONArray("dt");
										
										for(int j=0; j<json_dtArr.length(); j++){
											JSONObject json_dt = json_dtArr.getJSONObject(j);
											String id = json_dt.getString("id");
											String Home  = json_dt.getString("ht");
											String away  = json_dt.getString("at");
											String Time = "";//json_dt.getString("tp");
											
											if(json_dt.getString("ty").equals("playing")){
												continueUpdate = true;
												Time = json_dt.getString("pr");
											}else if(json_dt.getString("ty").equals("played")){
												Time = "FT";
											}else if(json_dt.getString("ty").equals("postponed")){
												Time = json_dt.getString("ty");
											}else if(json_dt.getString("ty").equals("fixture")){
												Time = json_dt.getString("tp");
											}else{
												Time = json_dt.getString("tp");
											}

											String score = json_dt.getString("sc");
											String score_ag = json_dt.getString("ag");
											if(score.equals("")){
												score = "vs";
											}
											if(score_ag==null || score_ag.length()<5){
												score_ag="";
											}else{
												if(!score.equals("vs")){
													String AG[] = score_ag.replaceAll(" ", "").split("-");
													int Ag_home = Integer.parseInt(AG[0]);
													int Ag_away = Integer.parseInt(AG[1]);
													String SC[] = score.replaceAll(" ", "").split("-");
													int Sc_home = Integer.parseInt(SC[0]);
													int SC_away = Integer.parseInt(SC[1]);
													score_ag = String.valueOf(Ag_home+Sc_home)+ " - " + String.valueOf(Ag_away+SC_away);
												}
											}
											if (SessionManager.chkFavContain(mContext, id)
													||away.contains(ControllParameter.TeamSelect)
													|| Home.contains(ControllParameter.TeamSelect)) {
												NotifyLiveScore(mContext, id, Home, score, away, Time, data);
												data.OldScoreH.put(id, score);
												data.OldTimeH.put(id, Time);
											}
										}
									}
									
									if(!continueUpdate){
										Log.d("TEST", "continueUpdate::NO");
										data.socket_LiveScore.disconnect();
										data.socket_LiveScore = null;
									}else{
										Log.d("TEST", "continueUpdate::YES");
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
		                }
		            }
		        });
				//socket.emit("adduser", Name_Send);
			}
		};

		new Thread(runnable).start();
	}
	
	public static void NotifyLiveScore(Context mContext, String id, final String Home
			, final String newScore, final String Away, final String Time, ControllParameter data) {
		if(SessionManager.getSetting( mContext, SessionManager.setting_notify_livescore)==null){
			SessionManager.setSetting(mContext, SessionManager.setting_notify_livescore, "true");
		}
		if(SessionManager.getSetting( mContext, SessionManager.setting_notify_livescore).equals("true")){
			if(Time.contains(":")){
				Calendar c_t = Calendar.getInstance();
				int ChkNotyB15B = ChkNotyB15(Time, c_t);
				Boolean ChkNotyB180B = ChkNotyB180(Time, c_t);
				String msg = "at: "+Time;
				if(ChkNotyB15B>0){
					msg = mContext.getResources().getString(R.string.alert_match_nearby);
					msg = msg + ChkNotyB15B + mContext.getResources().getString(R.string.alert_match_nearby_minute);
					NotifyLiveEvent(mContext, id, Home, newScore, Away, msg);
				}
				if(ChkNotyB180B){
					NotifyLiveEvent(mContext, id, Home, newScore, Away, msg);
				}
			}else{
				if(data.OldTimeH.get(id)!=null){
					if(!data.OldTimeH.get(id).equals("FT")){
						if((!newScore.equals(data.OldScoreH.get(id)))
								|| (!Time.equals(data.OldTimeH.get(id)) 
										&& (Time.equals("HT")
												|| Time.equals("FT") 
												|| data.OldTimeH.get(id).equals("HT")
												|| data.OldTimeH.get(id).contains(":"))) ){
							NotifyLiveEvent(mContext, id, Home, newScore, Away, "Time: "+Time);
						}
					}else{
						SessionManager.delFavTeam(mContext, id);
					}
				}
			}
		}
	}
	
	public static void NotifyLiveEvent(Context mContext, String id, final String Home, final String newScore, final String Away, final String msg){
		Intent nextToMain = new Intent(mContext, Sign_In_Page.class);
		nextToMain.putExtra("NOTIFY_INTENT", 4600);
		PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, nextToMain, 0);
		NotificationManager mNotifyManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentTitle(Home + " " + newScore + " " + Away)
				.setContentText(msg)
				.setSmallIcon(R.drawable.notify_livescore)
				.setContentIntent(pIntent)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL);
		mNotifyManager.notify(Integer.parseInt(id), mBuilder.build());
	}
	
	public static int ChkNotyB15(String Time, Calendar c){
		if(Time.contains(":")){
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			int minuteConclude = (hour*60)+minute;
			String timeArr[] = Time.split(":");
			int hourMatch = Integer.parseInt(timeArr[0]);
			int minuteMatch = Integer.parseInt(timeArr[1]);
			int minuteConcludeMatch = (hourMatch*60)+minuteMatch;
			int minuteConcludeMatchS = minuteConcludeMatch-15;
			if(Math.abs(minuteConcludeMatchS-minuteConclude)<=3){
				return minuteConcludeMatch-minuteConclude;
			}else{
				return -1;
			}
		}
		return -1;
	}
	
	public static Boolean ChkNotyB180(String Time, Calendar c){
		if(Time.contains(":")){
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			int minuteConclude = (hour*60)+minute;
			String timeArr[] = Time.split(":");
			int hourMatch = Integer.parseInt(timeArr[0]);
			int minuteMatch = Integer.parseInt(timeArr[1]);
			int minuteConcludeMatch = (hourMatch*60)+minuteMatch;
			minuteConcludeMatch -= 180;
			if(Math.abs(minuteConcludeMatch-minuteConclude)<=5){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
}
