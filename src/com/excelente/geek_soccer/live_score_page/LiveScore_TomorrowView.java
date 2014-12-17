package com.excelente.geek_soccer.live_score_page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.GetdipSize;
import com.excelente.geek_soccer.JSONParser;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.pic_download.DownLiveScorePic;

public class LiveScore_TomorrowView {
	
	Activity mActivity;
	View myView;
	
	JSONParser jParser = new JSONParser();
	JSONObject products = null;
	private ListView lstView;
	Boolean chk_ani = true;
	int last_ItemView = 0;
	LinearLayout layOutlist;
	Boolean chk_D_Stat = false;
	int chk_loaded = 0;
	private static ControllParameter data;
	String saveModeGet;
	
	ProgressBar progressV;
	
	public View getView(Activity activity){
		mActivity = activity;
		data = ControllParameter.getInstance(activity);
		LayoutInflater factory = LayoutInflater.from(activity);
		myView = factory.inflate(R.layout.livescore_today, null);
		
		saveModeGet = SessionManager.getSetting(mActivity,
				SessionManager.setting_save_mode);
		lstView = new ListView(mActivity);
		lstView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		lstView.setDividerHeight(0);
		lstView.setClipToPadding(false);
		data.imageAdapterLiveScoreTomorrowByView = new ImageAdapter(mActivity.getApplicationContext());
		lstView.setAdapter(data.imageAdapterLiveScoreTomorrowByView);

		lstView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (!data.Match_list_t_JSON.get(position).isNull("score")) {
					Intent Detail_Page = new Intent(mActivity,
							Live_score_Detail_Json.class);
					Detail_Page.putExtra("URL", position);
					Detail_Page.putExtra("TYPE", "t");
					mActivity.startActivity(Detail_Page);
				}

			}
		});
		progressV = (ProgressBar) myView.findViewById(R.id.progressBar);
		if (data.Match_list_t_JSON.size() > 0) {
			layOutlist = (LinearLayout) myView.findViewById(R.id.List_Layout);
			progressV.setVisibility(RelativeLayout.GONE);
			((LinearLayout) layOutlist).addView(lstView);
			chk_ani = false;
			data.imageAdapterLiveScoreTomorrowByView.notifyDataSetChanged();
		} else {
			new Live_score_1stLoader().execute();
		}
		
		return myView;
	}

	public class ImageAdapter extends BaseAdapter {

		private Context mActivity;

		public ImageAdapter(Context context) {
			mActivity = context;
		}

		public int getCount() {
			return data.Match_list_t_JSON.size();// +League_list.size();
		}

		public Object getItem(int position) {
			return null;// URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LinearLayout retval = new LinearLayout(mActivity);
			retval.setOrientation(LinearLayout.VERTICAL);
			retval.setGravity(Gravity.CENTER);
			retval.setMinimumHeight(GetdipSize.dip(mActivity, 35));

			LinearLayout bg = new LinearLayout(mActivity);
			bg.setOrientation(LinearLayout.VERTICAL);
			bg.setGravity(Gravity.CENTER_VERTICAL);

			JSONObject txt_Item = null;
			try {
				if (data.Match_list_t_JSON.size() - 1 >= position) {
					txt_Item = data.Match_list_t_JSON.get(position);
				}

				int colors = Integer.parseInt("000000", 16) + (0xFF000000);
				TextView txt = new TextView(mActivity);
				txt.setTextColor(colors);
				txt.setTypeface(Typeface.DEFAULT_BOLD);
				if (!txt_Item.isNull("score")) {
					bg.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					bg.setBackgroundResource(R.drawable.card_background_white);
					txt.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt.setGravity(Gravity.CENTER);
					// String text_Sprite[] = txt_Item.split("\n");

					LinearLayout layOut_Detail = new LinearLayout(mActivity);
					layOut_Detail.setOrientation(LinearLayout.HORIZONTAL);
					layOut_Detail.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));

					txt.setTextSize(14);
					txt.setText(" " + txt_Item.getString("Time").substring(3));
					txt.setPadding(5, 0, 5, 0);
					txt.setGravity(Gravity.LEFT);

					TextView txt_Home = new TextView(mActivity);
					txt_Home.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));
					txt_Home.setTextSize(16);
					txt_Home.setText(txt_Item.getString("Home"));
					txt_Home.setTextColor(Color.DKGRAY);

					TextView txt_Score = new TextView(mActivity);
					txt_Score.setLayoutParams(new LayoutParams(GetdipSize.dip(mActivity, 50), GetdipSize.dip(mActivity, 25)));
					txt_Score.setTextSize(14);
					txt_Score.setTypeface(Typeface.DEFAULT_BOLD);
					txt_Score.setText(txt_Item.getString("score").replaceAll(
							"&nbsp;", " "));
					txt_Score.setGravity(Gravity.CENTER);
					txt_Score.setBackgroundResource(R.drawable.score_bg_layer);
					if (!txt_Item.getString("score").replaceAll("&nbsp;", " ")
							.equals("vs")) {
						txt_Score.setTextColor(Color.WHITE);
					}

					TextView txt_Away = new TextView(mActivity);
					txt_Away.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));
					txt_Away.setTextSize(16);
					txt_Away.setGravity(Gravity.RIGHT);
					txt_Away.setText(txt_Item.getString("Away"));
					txt_Away.setTextColor(Color.DKGRAY);

					final ImageView image_Home = new ImageView(mActivity);
					image_Home.setLayoutParams(new LayoutParams(GetdipSize.dip(mActivity, 35), GetdipSize.dip(mActivity, 35)));
					final ImageView image_Away = new ImageView(mActivity);
					image_Away.setLayoutParams(new LayoutParams(GetdipSize.dip(mActivity, 35), GetdipSize.dip(mActivity, 35)));
					if (data.get_HomeMap(txt_Item.getString("Home_img")) != null) {
						image_Home.setImageBitmap(data.get_HomeMap(txt_Item
								.getString("Home_img")));
					} else {
						if (saveModeGet.equals("true")) {
							image_Home
									.setImageResource(R.drawable.ic_menu_view);
						} else {
							image_Home.setImageResource(R.drawable.soccer_icon);
						}
						new DownLiveScorePic().startDownload_Home(
								data.Match_list_t_JSON.get(position).getString(
										"Home_img"), image_Home, saveModeGet,
								data);
					}
					if (data.get_AwayMap(txt_Item.getString("Away_img")) != null) {
						image_Away.setImageBitmap(data.get_AwayMap(txt_Item
								.getString("Away_img")));
					} else {
						if (saveModeGet.equals("true")) {
							image_Away
									.setImageResource(R.drawable.ic_menu_view);
						} else {
							image_Away.setImageResource(R.drawable.soccer_icon);
						}
						new DownLiveScorePic().startDownload_Away(
								data.Match_list_t_JSON.get(position).getString(
										"Away_img"), image_Away, saveModeGet,
								data);
					}

					LinearLayout layOut_1 = new LinearLayout(mActivity);
					layOut_1.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));
					layOut_1.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					layOut_1.setOrientation(LinearLayout.HORIZONTAL);

					LinearLayout layOut_2 = new LinearLayout(mActivity);
					layOut_2.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					layOut_2.setGravity(Gravity.CENTER);

					LinearLayout layOut_3 = new LinearLayout(mActivity);
					layOut_3.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));
					layOut_3.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					layOut_3.setOrientation(LinearLayout.HORIZONTAL);

					layOut_1.setPadding(5, 0, 5, 0);
					image_Home.setPadding(5, 0, 5, 0);
					txt_Home.setPadding(5, 0, 5, 0);
					layOut_1.addView(image_Home);
					layOut_1.addView(txt_Home);

					layOut_2.setPadding(5, 0, 5, 0);
					txt_Score.setPadding(5, 0, 5, 0);
					layOut_2.addView(txt_Score);

					layOut_3.setPadding(5, 0, 5, 0);
					txt_Away.setPadding(5, 0, 5, 0);
					image_Away.setPadding(5, 0, 5, 0);
					layOut_3.addView(txt_Away);
					layOut_3.addView(image_Away);

					LinearLayout layOut_time = new LinearLayout(mActivity);
					layOut_time.setOrientation(LinearLayout.HORIZONTAL);
					layOut_time.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					layOut_time.addView(txt);

					if (txt_Item.getString("details").length() > 0) {
						TextView txt_Aggregate = new TextView(mActivity);
						txt_Aggregate
								.setLayoutParams(new LinearLayout.LayoutParams(
										0, LayoutParams.WRAP_CONTENT, 1f));
						txt_Aggregate.setTextSize(14);
						txt_Aggregate.setTextColor(Color.GRAY);
						txt_Aggregate.setGravity(Gravity.RIGHT);
						txt_Aggregate.setText(txt_Item.getString("details") + " ");
						layOut_time.addView(txt_Aggregate);
					}

					layOut_Detail.addView(layOut_1);
					layOut_Detail.addView(layOut_2);
					layOut_Detail.addView(layOut_3);

					bg.addView(layOut_time);
					bg.addView(layOut_Detail);
					if (chk_ani && last_ItemView - 1 < position) {
						layOut_time.setAnimation(AnimationUtils.loadAnimation(
								mActivity, R.drawable.listview_anim));
						layOut_Detail.setAnimation(AnimationUtils
								.loadAnimation(mActivity,
										R.drawable.listview_anim));
					}
					// retval.setBackgroundColor(Color.GRAY);
					// retval.getBackground().setAlpha(200);
				} else {
					retval.setPadding(5, 5, 5, 5);
					bg.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					txt.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					txt.setGravity(Gravity.CENTER);

					txt.setText(txt_Item
							.getString("League")
							.substring(
									txt_Item.getString("League").lastIndexOf(
											"]") + 1).replaceAll("&lrm;", " "));

					txt.setTextSize(24);
					bg.addView(txt);
					if (chk_ani && last_ItemView - 1 < position) {
						txt.setAnimation(AnimationUtils.loadAnimation(mActivity,
								R.drawable.listview_anim));
					}
					bg.setBackgroundColor(Color.DKGRAY);
					bg.getBackground().setAlpha(200);
				}
				retval.addView(bg);
				return retval;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retval;

		}

	}

	class Live_score_1stLoader extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(c_page.this);
			 * pDialog.setMessage("Preparing content data. Please wait...");
			 * pDialog.setIndeterminate(true); pDialog.setCancelable(false);
			 * pDialog.show();
			 */
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("_k",
						"592ffdb05020001222c7d024479b028d"));
				params.add(new BasicNameValuePair("_t", "get-livescore"));
				params.add(new BasicNameValuePair("_u", String
						.valueOf(SessionManager.getMember(
								mActivity).getUid())));
				params.add(new BasicNameValuePair("_d", "t"));

				ControllParameter.jObLiveTomorrow = jParser
						.makeHttpRequest(
								ControllParameter.GET_LIVESCORE_URL,
								"POST", params);

				if (ControllParameter.jObLiveTomorrow != null) {
					data.Match_list_t_JSON.clear();
					JSONObject json_ob = ControllParameter.jObLiveTomorrow;
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
							}else if(SessionManager.chkFavContain(mActivity, id)){
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
										+ mActivity.getResources().getString(
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
			((Activity) mActivity).runOnUiThread(new Runnable() {
				public void run() {
					if (data.Match_list_t_JSON.size() > 0) {
						chk_D_Stat = false;
						layOutlist = (LinearLayout) myView.findViewById(R.id.List_Layout);
						progressV.setVisibility(RelativeLayout.GONE);
						((LinearLayout) layOutlist).addView(lstView);
						chk_ani = false;
						data.imageAdapterLiveScoreTomorrowByView.notifyDataSetChanged();
					} else {
						layOutlist = (LinearLayout) myView.findViewById(R.id.List_Layout);
						progressV.setVisibility(RelativeLayout.GONE);
						final TextView RefreshTag = new TextView(mActivity);
						RefreshTag.setPadding(0, 30, 0, 30);
						RefreshTag.setTextColor(Color.GRAY);
						RefreshTag.setText(mActivity.getResources().getString(R.string.pull_to_refresh_tap_label));
						RefreshTag.setGravity(Gravity.CENTER);
						((LinearLayout) layOutlist).addView(RefreshTag);
						layOutlist
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View arg0) {
										progressV.setVisibility(RelativeLayout.ABOVE);
										((LinearLayout) layOutlist).removeView(RefreshTag);
										new Live_score_1stLoader().execute();
									}
								});
					}
				}
			});
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return true;
	}
}
