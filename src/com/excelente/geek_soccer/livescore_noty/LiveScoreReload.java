package com.excelente.geek_soccer.livescore_noty;

import java.util.Collections;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.JSONParser;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;

public class LiveScoreReload {
	JSONParser jParser = new JSONParser();
	private static ControllParameter data;
	Context mContext;
	
	public interface LiveScoreCallbackClass {
		void LiveScorecallbackReturn();
	}

	public static LiveScoreCallbackClass myCallbackClass;

	public void registerCallback(LiveScoreCallbackClass callbackClass) {
		myCallbackClass = callbackClass;
	}
	
	public void SelectReload(String type, Context mContext){
		this.mContext = mContext;
		data = ControllParameter.getInstance(this.mContext);
		if (type.equals("y")) {
			new ReloadYesterday().execute();
		} else if (type.equals("c")) {
			new ReloadToday().execute();
		} else if (type.equals("t")) {
			new ReloadTomorrow().execute();
		}
	}
	
	class ReloadYesterday extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {

				JSONObject json = ControllParameter.jObLiveYesterday;
				Log.d("TEST", "JSON::"+json);
				if (json != null) {
					data.Match_list_y_JSON.clear();
					JSONObject json_ob = json;
					Boolean ContainFav = false;
					JSONArray json_itArr = json_ob.getJSONArray("it");
					for (int i = 0; i < json_itArr.length(); i++) {
						JSONObject json_it = json_itArr.getJSONObject(i);
						String League = json_it.getString("ln");
						if (League.contains("UEFA Champions League")) {
							League = "[1]" + League;
						} else if (League.contains("Premier League")) {
							League = "[2]" + League;
						} else if (League.contains("Primera División")
								|| League.contains("Primera DivisiÃ³n")) {
							League = "[3]" + League;
						} else if (League.contains("Bundesliga")) {
							League = "[4]" + League;
						} else if (League.contains("Serie A")) {
							League = "[5]" + League;
						} else if (League.contains("Ligue 1")) {
							League = "[6]" + League;
						}
						data.Match_list_y_JSON.add(new JSONObject().put(
								"League", League));
						JSONArray json_dtArr = json_it.getJSONArray("dt");

						int chk_ExistTeam_in = data.Match_list_y_JSON.size();
						for (int j = 0; j < json_dtArr.length(); j++) {
							JSONObject json_dt = json_dtArr.getJSONObject(j);
							String id = json_dt.getString("id");
							String Home = json_dt.getString("ht");
							String Home_img = json_dt.getString("hl");
							String away = json_dt.getString("at");
							String away_img = json_dt.getString("al");
							String link = json_dt.getString("lk");
							String Time = "";// json_dt.getString("tp");

							if (json_dt.getString("ty").equals("playing")) {
								Time = json_dt.getString("pr");
							} else if (json_dt.getString("ty").equals("played")) {
								Time = json_dt.getString("pr");
							} else if (json_dt.getString("ty").equals(
									"postponed")) {
								Time = json_dt.getString("ty");
							} else if (json_dt.getString("ty")
									.equals("fixture")) {
								Time = json_dt.getString("tp");
							} else {
								Time = json_dt.getString("tp");
							}

							String stat = "[no]";
							String score = json_dt.getString("sc");
							String score_ag = json_dt.getString("ag");
							String details = json_dt.getString("details");
							if (score.equals("")) {
								score = "vs";
							}
							if (score_ag == null || score_ag.length() < 5) {
								score_ag = "";
							} else {
								if (!score.equals("vs")) {
									String AG[] = score_ag.replaceAll(" ", "")
											.split("-");
									int Ag_home = Integer.parseInt(AG[0]);
									int Ag_away = Integer.parseInt(AG[1]);
									String SC[] = score.replaceAll(" ", "")
											.split("-");
									int Sc_home = Integer.parseInt(SC[0]);
									int SC_away = Integer.parseInt(SC[1]);
									score_ag = String
											.valueOf(Ag_home + Sc_home)
											+ " - "
											+ String.valueOf(Ag_away + SC_away);
								}
							}
							if (away.contains(ControllParameter.TeamSelect)
									|| Home.contains(ControllParameter.TeamSelect)) {
								data.Match_list_y_JSON
										.add(new JSONObject().put(
												"League",
												"[0]"
														+ "Your Team in "
														+ League.substring(League
																.lastIndexOf("]") + 1)));
								JSONObject j_data = new JSONObject();
								j_data.put(
										"League",
										"[0]"
												+ "Your Team in "
												+ League.substring(League
														.lastIndexOf("]") + 1));
								j_data.put("Time", "[0]" + Time);
								j_data.put("id", id);
								j_data.put("stat", stat);
								j_data.put("Home", Home);
								j_data.put("score", score);
								j_data.put("Away", away);
								j_data.put("Home_img", Home_img);
								j_data.put("Away_img", away_img);
								j_data.put("link", link);
								j_data.put("score_ag", score_ag);
								j_data.put("details", details);
								data.Match_list_y_JSON.add(j_data);
							}else if(SessionManager.chkFavContain(mContext, id)){
								if(!ContainFav){
									ContainFav=true;
									data.Match_list_y_JSON
									.add(new JSONObject().put(
											"League",
											"[1]Match Following"));
								}
								
								JSONObject j_data = new JSONObject();
								j_data.put(
										"League",
										"[1]Match Following");
								j_data.put("Time", "[1]" + Time);
								j_data.put("id", id);
								j_data.put("stat", stat);
								j_data.put("Home", Home);
								j_data.put("score", score);
								j_data.put("Away", away);
								j_data.put("Home_img", Home_img);
								j_data.put("Away_img", away_img);
								j_data.put("link", link);
								j_data.put("score_ag", score_ag);
								j_data.put("details", details);
								data.Match_list_y_JSON
										.add(j_data);
							}
							JSONObject j_data = new JSONObject();
							j_data.put("League", League);
							j_data.put("Time", "[2]" + Time);
							j_data.put("id", id);
							j_data.put("stat", stat);
							j_data.put("Home", Home);
							j_data.put("score", score);
							j_data.put("Away", away);
							j_data.put("Home_img", Home_img);
							j_data.put("Away_img", away_img);
							j_data.put("link", link);
							j_data.put("score_ag", score_ag);
							j_data.put("details", details);
							data.Match_list_y_JSON.add(j_data);
						}
						if (chk_ExistTeam_in == data.Match_list_y_JSON.size()) {
							data.Match_list_y_JSON
									.remove(data.Match_list_y_JSON.size() - 1);
						}
					}

					Collections.sort(data.Match_list_y_JSON,
							new Comparator<JSONObject>() {
								@Override
								public int compare(JSONObject s1, JSONObject s2) {
									try {
										return s1.getString("League")
												.compareToIgnoreCase(
														s2.getString("League"));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return 0;
								}
							});

					if (data.Match_list_y_JSON.size() <= 0) {
						data.Match_list_y_JSON.add(new JSONObject().put(
								"League",
								"[0]"
										+ mContext.getResources().getString(
												R.string.no_match)));
						return "no";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					myCallbackClass.LiveScorecallbackReturn();
				}
			});
		}
	}
	
	public class ReloadToday extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				JSONObject json = ControllParameter.jObLiveToday;
				Log.d("TEST", "JSON::"+json);
				if (json != null) {
					data.Match_list_c_JSON.clear();
					JSONObject json_ob = json;
					Boolean ContainFav = false;
					JSONArray json_itArr = json_ob.getJSONArray("it");
					for (int i = 0; i < json_itArr.length(); i++) {
						JSONObject json_it = json_itArr.getJSONObject(i);
						String League = json_it.getString("ln");
						if (League.contains("UEFA Champions League")) {
							League = "[1]" + League;
						} else if (League.contains("Premier League")) {
							League = "[2]" + League;
						} else if (League.contains("Primera División")
								|| League.contains("Primera DivisiÃ³n")) {
							League = "[3]" + League;
						} else if (League.contains("Bundesliga")) {
							League = "[4]" + League;
						} else if (League.contains("Serie A")) {
							League = "[5]" + League;
						} else if (League.contains("Ligue 1")) {
							League = "[6]" + League;
						}
						data.Match_list_c_JSON.add(new JSONObject().put(
								"League", League));
						JSONArray json_dtArr = json_it.getJSONArray("dt");

						int chk_ExistTeam_in = data.Match_list_c_JSON.size();
						for (int j = 0; j < json_dtArr.length(); j++) {
							JSONObject json_dt = json_dtArr.getJSONObject(j);
							String id = json_dt.getString("id");
							String Home = json_dt.getString("ht");
							String Home_img = json_dt.getString("hl");
							String away = json_dt.getString("at");
							String away_img = json_dt.getString("al");
							String link = json_dt.getString("lk");
							String Time = "";// json_dt.getString("tp");

							if (json_dt.getString("ty").equals("playing")) {
								Time = json_dt.getString("pr");
							} else if (json_dt.getString("ty").equals("played")) {
								Time = json_dt.getString("pr");
							} else if (json_dt.getString("ty").equals(
									"postponed")) {
								Time = json_dt.getString("ty");
							} else if (json_dt.getString("ty")
									.equals("fixture")) {
								Time = json_dt.getString("tp");
							} else {
								Time = json_dt.getString("tp");
							}

							String stat = "[no]";
							String score = json_dt.getString("sc");
							String score_ag = json_dt.getString("ag");
							String details = json_dt.getString("details");
							if (score.equals("")) {
								score = "vs";
							}
							if (score_ag == null || score_ag.length() < 5) {
								score_ag = "";
							} else {
								if (!score.equals("vs")) {
									String AG[] = score_ag.replaceAll(" ", "")
											.split("-");
									int Ag_home = Integer.parseInt(AG[0]);
									int Ag_away = Integer.parseInt(AG[1]);
									String SC[] = score.replaceAll(" ", "")
											.split("-");
									int Sc_home = Integer.parseInt(SC[0]);
									int SC_away = Integer.parseInt(SC[1]);
									score_ag = String
											.valueOf(Ag_home + Sc_home)
											+ " - "
											+ String.valueOf(Ag_away + SC_away);
								}
							}
							if (away.contains(ControllParameter.TeamSelect)
									|| Home.contains(ControllParameter.TeamSelect)) {
								data.Match_list_c_JSON
										.add(new JSONObject().put(
												"League",
												"[0]"
														+ "Your Team in "
														+ League.substring(League
																.lastIndexOf("]") + 1)));
								JSONObject j_data = new JSONObject();
								j_data.put(
										"League",
										"[0]"
												+ "Your Team in "
												+ League.substring(League
														.lastIndexOf("]") + 1));
								j_data.put("Time", "[0]" + Time);
								j_data.put("id", id);
								j_data.put("stat", stat);
								j_data.put("Home", Home);
								j_data.put("score", score);
								j_data.put("Away", away);
								j_data.put("Home_img", Home_img);
								j_data.put("Away_img", away_img);
								j_data.put("link", link);
								j_data.put("score_ag", score_ag);
								j_data.put("details", details);
								data.Match_list_c_JSON.add(j_data);
							}else if(SessionManager.chkFavContain(mContext, id)){
								if(!ContainFav){
									ContainFav=true;
									data.Match_list_c_JSON
									.add(new JSONObject().put(
											"League",
											"[1]Match Following"));
								}
								JSONObject j_data = new JSONObject();
								j_data.put(
										"League",
										"[1]Match Following");
								j_data.put("Time", "[1]" + Time);
								j_data.put("id", id);
								j_data.put("stat", stat);
								j_data.put("Home", Home);
								j_data.put("score", score);
								j_data.put("Away", away);
								j_data.put("Home_img", Home_img);
								j_data.put("Away_img", away_img);
								j_data.put("link", link);
								j_data.put("score_ag", score_ag);
								j_data.put("details", details);
								data.Match_list_c_JSON
										.add(j_data);
							}
							JSONObject j_data = new JSONObject();
							j_data.put("League", League);
							j_data.put("Time", "[2]" + Time);
							j_data.put("id", id);
							j_data.put("stat", stat);
							j_data.put("Home", Home);
							j_data.put("score", score);
							j_data.put("Away", away);
							j_data.put("Home_img", Home_img);
							j_data.put("Away_img", away_img);
							j_data.put("link", link);
							j_data.put("score_ag", score_ag);
							j_data.put("details", details);
							data.Match_list_c_JSON.add(j_data);
						}
						if (chk_ExistTeam_in == data.Match_list_c_JSON.size()) {
							data.Match_list_c_JSON
									.remove(data.Match_list_c_JSON.size() - 1);
						}
					}

					Collections.sort(data.Match_list_c_JSON,
							new Comparator<JSONObject>() {
								@Override
								public int compare(JSONObject s1, JSONObject s2) {
									try {
										return s1.getString("League")
												.compareToIgnoreCase(
														s2.getString("League"));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return 0;
								}
							});
					if (data.Match_list_c_JSON.size() <= 0) {
						data.Match_list_c_JSON.add(new JSONObject().put(
								"League",
								"[0]"
										+ mContext.getResources().getString(
												R.string.no_match)));
						return "no";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					myCallbackClass.LiveScorecallbackReturn();
				}
			});
		}
	}
	
	public class ReloadTomorrow extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				JSONObject json = ControllParameter.jObLiveTomorrow;
				Log.d("TEST", "json_dtArr::" + json);
				if (json != null) {
					data.Match_list_t_JSON.clear();
					JSONObject json_ob = json;
					Boolean ContainFav = false;
					JSONArray json_itArr = json_ob.getJSONArray("it");
					for (int i = 0; i < json_itArr.length(); i++) {
						JSONObject json_it = json_itArr.getJSONObject(i);
						String League = json_it.getString("ln");
						if (League.contains("UEFA Champions League")) {
							League = "[1]" + League;
						} else if (League.contains("Premier League")) {
							League = "[2]" + League;
						} else if (League.contains("Primera División")
								|| League.contains("Primera DivisiÃ³n")) {
							League = "[3]" + League;
						} else if (League.contains("Bundesliga")) {
							League = "[4]" + League;
						} else if (League.contains("Serie A")) {
							League = "[5]" + League;
						} else if (League.contains("Ligue 1")) {
							League = "[6]" + League;
						}
						data.Match_list_t_JSON.add(new JSONObject().put(
								"League", League));
						JSONArray json_dtArr = json_it.getJSONArray("dt");

						int chk_ExistTeam_in = data.Match_list_t_JSON.size();
						for (int j = 0; j < json_dtArr.length(); j++) {
							JSONObject json_dt = json_dtArr.getJSONObject(j);
							String id = json_dt.getString("id");
							String Home = json_dt.getString("ht");
							String Home_img = json_dt.getString("hl");
							String away = json_dt.getString("at");
							String away_img = json_dt.getString("al");
							String link = json_dt.getString("lk");
							String Time = "";// json_dt.getString("tp");
							if (json_dt.getString("ty").equals("playing")) {
								Time = json_dt.getString("pr");
							} else if (json_dt.getString("ty").equals("played")) {
								Time = json_dt.getString("pr");
							} else if (json_dt.getString("ty").equals(
									"postponed")) {
								Time = json_dt.getString("ty");
							} else if (json_dt.getString("ty")
									.equals("fixture")) {
								Time = json_dt.getString("tp");
							} else {
								Time = json_dt.getString("tp");
							}

							String stat = "[no]";
							String score = json_dt.getString("sc");
							String score_ag = json_dt.getString("ag");
							String details = json_dt.getString("details");
							if (score.equals("")) {
								score = "vs";
							}
							if (score_ag == null || score_ag.length() < 5) {
								score_ag = "";
							} else {
								if (!score.equals("vs")) {
									String AG[] = score_ag.replaceAll(" ", "")
											.split("-");
									int Ag_home = Integer.parseInt(AG[0]);
									int Ag_away = Integer.parseInt(AG[1]);
									String SC[] = score.replaceAll(" ", "")
											.split("-");
									int Sc_home = Integer.parseInt(SC[0]);
									int SC_away = Integer.parseInt(SC[1]);
									score_ag = String
											.valueOf(Ag_home + Sc_home)
											+ " - "
											+ String.valueOf(Ag_away + SC_away);
								}
							}
							if (away.contains(ControllParameter.TeamSelect)
									|| Home.contains(ControllParameter.TeamSelect)) {
								data.Match_list_t_JSON
										.add(new JSONObject().put(
												"League",
												"[0]"
														+ "Your Team in "
														+ League.substring(League
																.lastIndexOf("]") + 1)));
								JSONObject j_data = new JSONObject();
								j_data.put(
										"League",
										"[0]"
												+ "Your Team in "
												+ League.substring(League
														.lastIndexOf("]") + 1));
								j_data.put("Time", "[0]" + Time);
								j_data.put("id", id);
								j_data.put("stat", stat);
								j_data.put("Home", Home);
								j_data.put("score", score);
								j_data.put("Away", away);
								j_data.put("Home_img", Home_img);
								j_data.put("Away_img", away_img);
								j_data.put("link", link);
								j_data.put("score_ag", score_ag);
								j_data.put("details", details);
								data.Match_list_t_JSON.add(j_data);
							}else if(SessionManager.chkFavContain(mContext, id)){
								if(!ContainFav){
									ContainFav=true;
									data.Match_list_t_JSON
									.add(new JSONObject().put(
											"League",
											"[1]Match Following"));
								}
								
								JSONObject j_data = new JSONObject();
								j_data.put(
										"League",
										"[1]Match Following");
								j_data.put("Time", "[1]" + Time);
								j_data.put("id", id);
								j_data.put("stat", stat);
								j_data.put("Home", Home);
								j_data.put("score", score);
								j_data.put("Away", away);
								j_data.put("Home_img", Home_img);
								j_data.put("Away_img", away_img);
								j_data.put("link", link);
								j_data.put("score_ag", score_ag);
								j_data.put("details", details);
								data.Match_list_t_JSON
										.add(j_data);
							}
							JSONObject j_data = new JSONObject();
							j_data.put("League", League);
							j_data.put("Time", "[2]" + Time);
							j_data.put("id", id);
							j_data.put("stat", stat);
							j_data.put("Home", Home);
							j_data.put("score", score);
							j_data.put("Away", away);
							j_data.put("Home_img", Home_img);
							j_data.put("Away_img", away_img);
							j_data.put("link", link);
							j_data.put("score_ag", score_ag);
							j_data.put("details", details);
							data.Match_list_t_JSON.add(j_data);
						}
						if (chk_ExistTeam_in == data.Match_list_t_JSON.size()) {
							data.Match_list_t_JSON
									.remove(data.Match_list_t_JSON.size() - 1);
						}
					}

					Collections.sort(data.Match_list_t_JSON,
							new Comparator<JSONObject>() {
								@Override
								public int compare(JSONObject s1, JSONObject s2) {
									try {
										return s1.getString("League")
												.compareToIgnoreCase(
														s2.getString("League"));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return 0;
								}
							});
					if (data.Match_list_t_JSON.size() <= 0) {
						data.Match_list_t_JSON.add(new JSONObject().put(
								"League",
								"[0]"
										+ mContext.getResources().getString(
												R.string.no_match)));
						return "no";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					myCallbackClass.LiveScorecallbackReturn();
				}
			});
		}
	}
}
