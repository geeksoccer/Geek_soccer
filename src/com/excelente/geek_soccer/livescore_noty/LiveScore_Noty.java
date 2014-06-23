package com.excelente.geek_soccer.livescore_noty;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;

public class LiveScore_Noty {
	
	public static void StartLiveScoreChk(Context mContext){
		ControllParameter data = ControllParameter.getInstance(mContext);
		if(data.socket_LiveScore==null){
			Live_score_Loader(mContext);
		}
	}
	
	public static void Live_score_Loader(final Context mContext) {

		Runnable runnable = new Runnable() {
			public void run() {
				final ControllParameter data = ControllParameter.getInstance(mContext);
				try {
					data.socket_LiveScore = new SocketIO("http://183.90.171.209:5070");
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
											String Home  = json_dt.getString("ht");
											String Home_img  = json_dt.getString("hl");
											String away  = json_dt.getString("at");
											String away_img  = json_dt.getString("al");
											String link = json_dt.getString("lk");
											String Time = "";//json_dt.getString("tp");
											
											if(json_dt.getString("ty").equals("playing")){
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
											
											String stat = "[no]";
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
											if (away.contains(ControllParameter.TeamSelect)
													|| Home.contains(ControllParameter.TeamSelect)) {
												data.Match_list_c_JSON.add(new JSONObject().put("League"
														, "[0]" + "Your Team in " +League.substring(League.lastIndexOf("]") + 1)));								
												JSONObject j_data = new JSONObject();
												j_data.put("League", "[0]" + "Your Team in " +League.substring(League.lastIndexOf("]") + 1));
												j_data.put("Time", "[0]" + Time);
												j_data.put("stat", stat);
												j_data.put("Home", Home);
												j_data.put("score", score);
												NotifyLiveScore(mContext, Home, score, away, Time);
												data.OldScore = score;
												data.OldTime = Time;
												j_data.put("Away", away);
												j_data.put("Home_img", Home_img);
												j_data.put("Away_img", away_img);
												j_data.put("link", link);
												j_data.put("score_ag", score_ag);
												data.Match_list_c_JSON.add(j_data);
											}
											if(!Time.contains("FT")){
												data.liveScore_ChkHavePlaying = true;
											}
											JSONObject j_data = new JSONObject();
											j_data.put("League", League);
											j_data.put("Time", "[1]" + Time);
											j_data.put("stat", stat);
											j_data.put("Home", Home);
											j_data.put("score", score);
											j_data.put("Away", away);
											j_data.put("Home_img", Home_img);
											j_data.put("Away_img", away_img);
											j_data.put("link", link);
											j_data.put("score_ag", score_ag);
											data.Match_list_c_JSON.add(j_data);
										}
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
		            		Collections.sort(data.Match_list_c_JSON, new Comparator<JSONObject>() {
								@Override
								public int compare(JSONObject s1, JSONObject s2) {
									try {
										return s1.getString("League").compareToIgnoreCase(s2.getString("League"));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return 0;
								}
							});
		            		if(data.liveScore_ChkHavePlaying){
		            			data.liveScore_ChkHavePlaying = false;
		            		}
		                }
		            }
		        });
				//socket.emit("adduser", Name_Send);
			}
		};

		new Thread(runnable).start();
	}
	
	public static void NotifyLiveScore(Context mContext, final String Home, final String newScore, final String Away, final String Time) {
		if(SessionManager.getSetting( mContext, SessionManager.setting_notify_livescore)==null){
			SessionManager.setSetting(mContext, SessionManager.setting_notify_livescore, "true");
		}
		if(SessionManager.getSetting( mContext, SessionManager.setting_notify_livescore).equals("true")){
			ControllParameter data = ControllParameter.getInstance(mContext);
			if(!data.OldTime.equals("FT")){
				if(!Time.equals("FT")
						|| !data.OldTime.equals("")){
					if((!newScore.equals(data.OldScore) && !data.OldScore.equals(""))
							|| (!Time.equals(data.OldTime) && ((Time.equals("HT")) || Time.equals("FT")))
							|| data.OldTime.equals("")
							|| (!Time.equals(data.OldTime) && ((data.OldTime.equals("HT")))) ){
						NotificationManager mNotifyManager = (NotificationManager) mContext
								.getSystemService(Context.NOTIFICATION_SERVICE);
						android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
						mBuilder.setContentTitle(Home + " " + newScore + " " + Away)
								.setContentText("Time: "+Time)
								.setSmallIcon(R.drawable.notify_livescore)
								.setDefaults(Notification.DEFAULT_ALL);
						mNotifyManager.notify(0, mBuilder.build());
					}
				}
				
			}
		}		
	}
}
