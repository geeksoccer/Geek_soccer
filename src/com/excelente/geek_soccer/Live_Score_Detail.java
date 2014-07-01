package com.excelente.geek_soccer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.pic_download.DownLiveScorePic;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.Boast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Live_Score_Detail extends Activity {

	Context mContext;
	String URL = "http://www.goal.com";
	int Detail_positon;
	TextView Time, Score, Home_name, Away_name, Detail;
	ImageView Home_Pic, Away_Pic;
	LinearLayout Up_btn;
	ImageButton Fav_btn;
	JSONObject getValue;
	String get_Time = "";
	String get_Score = "";
	String get_Home_name = "";
	String get_Away_name = "";
	private static ControllParameter data;
	// String player_Detail[];
	ArrayList<String> player_Detail = new ArrayList<String>();
	private ListView lstView;
	private ImageAdapter imageAdapter;
	int position;
	String type;
	TextView txt_Aggregate;
	String link_t = "";
	String Time_t = "";
	String score_t = "";
	Boolean loading = false;
	Boolean FirstLoad = true;
	String id_t = "", Home_img_t = "", Away_img_t = ""
			, Home_name_t = "", Away_name_t = "", score_ag_t = "", detail_t = "";

	JSONParser jParser = new JSONParser();
	JSONObject jsonTagMap;
	List<JSONObject> ListDetail = new ArrayList<JSONObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = ControllParameter.getInstance(this);

		data.fragement_Section_set(0);
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this)
				.getTeamId());
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(R.layout.live_score_detail, null);
		setContentView(myView);
		overridePendingTransition(R.anim.in_trans_left_right,
				R.anim.out_trans_right_left);
		Up_btn = (LinearLayout) myView.findViewById(R.id.Up_btn);
		Home_Pic = (ImageView) myView.findViewById(R.id.Home_Pic);
		Away_Pic = (ImageView) myView.findViewById(R.id.Away_Pic);
		Score = (TextView) myView.findViewById(R.id.Score);
		Home_name = (TextView) myView.findViewById(R.id.Home_name);
		Away_name = (TextView) myView.findViewById(R.id.Away_name);
		Time = (TextView) myView.findViewById(R.id.Time);
		Detail = (TextView) myView.findViewById(R.id.Details);
		txt_Aggregate = (TextView) myView.findViewById(R.id.Score_Aggregate);
		Fav_btn = (ImageButton) myView.findViewById(R.id.Fav_btn);
		data.detailPageOpenning = true;
		mContext = this;
		position = getIntent().getExtras().getInt("URL");
		type = getIntent().getExtras().getString("TYPE");
		if (type.equals("y")) {
			getValue = data.Match_list_y_JSON.get(position);
		} else if (type.equals("c")) {
			getValue = data.Match_list_c_JSON.get(position);
		} else if (type.equals("t")) {
			getValue = data.Match_list_t_JSON.get(position);
		}

		Up_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		try {
			id_t = getValue.getString("id");
			Time_t = getValue.getString("Time").substring(3);
			link_t = getValue.getString("link").replace("/en/", "/th/")
					+ "/live-commentary/main-events";
			Home_img_t = getValue.getString("Home_img");
			Away_img_t = getValue.getString("Away_img");
			Home_name_t = getValue.getString("Home");
			score_t = getValue.getString("score").replaceAll("&nbsp;", " ");
			Away_name_t = getValue.getString("Away");
			score_ag_t = getValue.getString("score_ag");
			detail_t = getValue.getString("details");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Time.setText(Time_t);
		URL += link_t;
		String saveModeGet = SessionManager.getSetting(mContext,
				SessionManager.setting_save_mode);

		if (data.get_HomeMap(Home_img_t) != null) {
			Home_Pic.setImageBitmap(data.get_HomeMap(Home_img_t));
		} else {
			if (saveModeGet.equals("true")) {
				Home_Pic.setImageResource(R.drawable.ic_menu_view);
			} else {
				Home_Pic.setImageResource(R.drawable.soccer_icon);
			}
			new DownLiveScorePic().startDownload_Home(Home_img_t, Home_Pic,
					saveModeGet, data);
		}
		if (data.get_AwayMap(Away_img_t) != null) {
			Away_Pic.setImageBitmap(data.get_AwayMap(Away_img_t));
		} else {
			if (saveModeGet.equals("true")) {
				Away_Pic.setImageResource(R.drawable.ic_menu_view);
			} else {
				Away_Pic.setImageResource(R.drawable.soccer_icon);
			}
			new DownLiveScorePic().startDownload_Away(Away_img_t, Away_Pic,
					saveModeGet, data);
		}

		Score.setText(score_t);
		Home_name.setText(Home_name_t);
		Away_name.setText(Away_name_t);

		if (score_ag_t.length() >= 5) {
			txt_Aggregate.setText("AGGREGATE: " + score_ag_t);
		} else {
			txt_Aggregate.setVisibility(RelativeLayout.GONE);
		}
		
		if(detail_t.length() >= 1){
			Detail.setText(detail_t);
		}else{
			Detail.setVisibility(RelativeLayout.GONE);
		}
		if (type.equals("c")) {
			checkRefreshDetail();
		}

		if (SessionManager.chkFavContain(mContext, id_t)
				|| Away_name_t.contains(ControllParameter.TeamSelect)
				|| Home_name_t.contains(ControllParameter.TeamSelect)) {
			Fav_btn.setImageResource(R.drawable.favorite_icon_full);
		}

		Fav_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SessionManager.chkFavContain(mContext, id_t)) {
					SessionManager.delFavTeam(mContext, id_t);
					Fav_btn.setImageResource(R.drawable.favorite_icon_hole);
				} else {
					SessionManager.addFavTeam(mContext, id_t);
					Fav_btn.setImageResource(R.drawable.favorite_icon_full);
				}
			}
		});

		lstView = new ListView(mContext);
		lstView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		lstView.setClipToPadding(false);
		imageAdapter = new ImageAdapter(mContext.getApplicationContext());
		lstView.setAdapter(imageAdapter);
		lstView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				JSONObject txt_Item = ListDetail.get(position);
				try {
					Boast.makeText(mContext, txt_Item.getString("eventType"),
							Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * String txt_Item = player_Detail.get(position); if
				 * (!txt_Item.equals("NotFoundData")) { String Split_item[] =
				 * txt_Item.replaceAll("&quot;", "\"") .split(":"); String
				 * eventStr = ""; if (Split_item[1].contains("ใบเหลือง")) {
				 * eventStr = "ได้รับใบเหลือง"; } else if
				 * (Split_item[1].contains("Yellow Card")) { eventStr =
				 * "Yellow Card"; } else if (Split_item[1].contains("ใบแดง")) {
				 * eventStr = "ได้รับใบแดง"; } else if
				 * (Split_item[1].contains("Red Card")) { eventStr = "Red Card";
				 * } else if (Split_item[1].contains("Yellow/Red")) { if
				 * (link_t.contains("/en/")) { eventStr = "Yellow/Red"; } else {
				 * eventStr = "ได้รับใบเหลืองใบที่ 2 / ได้รับใบแดง"; } } else if
				 * (Split_item[1].contains("ยิงจุดโทษได้") ||
				 * Split_item[1].contains("Pen SO Goal") ||
				 * Split_item[1].contains("Pen SO Miss")) { if
				 * (link_t.contains("/en/")) { eventStr = "Pen Goal"; } else {
				 * eventStr = "ทำประตูได้จากจุดโทษ"; } } else if
				 * (Split_item[1].contains("ทำเข้าประตูตัวเอง")) { eventStr =
				 * "ทำเข้าประตูตัวเอง"; } else if
				 * (Split_item[1].contains("Own Goal")) { eventStr = "Own Goal";
				 * } else if (Split_item[1].contains("ประตู")) { eventStr =
				 * "ทำประตูได้"; } else if (Split_item[1].contains("Goal")) {
				 * eventStr = "Goal"; } else if
				 * (Split_item[1].contains("แอสซิสต์")) { eventStr =
				 * "จ่ายให้เพื่อนทำประตูได้"; } else if
				 * (Split_item[1].contains("Assist")) { eventStr = "Assist"; }
				 * else if (Split_item[1].equals("เปลี่ยนตัว")) { eventStr =
				 * "เปลี่ยนตัว"; } else if
				 * (Split_item[1].equals("Substitution")) { eventStr =
				 * "Substitution"; } JSONObject txt_Item =
				 * ListDetail.get(position); Boast.makeText(mContext, eventStr,
				 * Toast.LENGTH_LONG) .show(); }
				 */
			}
		});
		new Live_score_Loader().execute();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// this.overridePendingTransition(R.drawable.ani_in,
		// R.drawable.ani_alpha);
	}

	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			if (ListDetail.size() == 0) {
				JSONObject obJdebug = new JSONObject();
				try {
					obJdebug.put("NotFound", "NotFound");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ListDetail.add(obJdebug);
			}
			return ListDetail.size();
		}

		public Object getItem(int position) {
			return null;// URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LinearLayout retval = new LinearLayout(mContext);
			retval.setOrientation(LinearLayout.HORIZONTAL);
			retval.setGravity(Gravity.CENTER);
			retval.setPadding(5, 0, 5, 0);
			retval.setMinimumHeight(50);

			int colors = Integer.parseInt("000000", 16) + (0xFF000000);

			if (!ListDetail.get(position).isNull("NotFound")) {
				TextView txt_T = new TextView(mContext);
				txt_T.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txt_T.setGravity(Gravity.CENTER);
				txt_T.setTextColor(colors);
				txt_T.setPadding(0, 0, 10, 0);
				txt_T.setText("ยังไม่มีข้อมูลอัพเดทในขณะนี้");
				retval.addView(txt_T);
			} else {
				try {
					JSONObject txt_Item = ListDetail.get(position);
					TextView txt_T = new TextView(mContext);
					txt_T.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					// txt.setGravity(Gravity.CENTER);
					txt_T.setTextColor(colors);
					txt_T.setPadding(0, 0, 10, 0);

					TextView txt_N = new TextView(mContext);
					txt_N.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1));
					txt_N.setTextColor(colors);

					ImageView img_E = new ImageView(mContext);
					img_E.setLayoutParams(new LayoutParams(30, 30));

					txt_T.setText(txt_Item.getString("time"));
					retval.addView(txt_T);
					if (txt_Item.getString("eventType").equals("substitution")) {
						TextView txt_Sub = new TextView(mContext);
						txt_Sub.setLayoutParams(new LinearLayout.LayoutParams(
								0, LayoutParams.WRAP_CONTENT, 1));
						txt_Sub.setTextColor(colors);
						txt_N.setLayoutParams(new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
						img_E.setImageResource(R.drawable.substitution);
						txt_N.setText(txt_Item.getString("subOut"));
						ImageView img_SubIn = new ImageView(mContext);
						img_SubIn.setLayoutParams(new LayoutParams(30, 30));
						img_SubIn.setImageResource(R.drawable.substitution_in);

						txt_Sub.setText(txt_Item.getString("subIn"));

						retval.addView(img_E);
						retval.addView(txt_N);
						retval.addView(img_SubIn);
						retval.addView(txt_Sub);
					} else {

						String Event = "";
						String msgText = txt_Item.getString("text");
						if (txt_Item.getString("eventType").contains(
								"yellow-card")) {
							img_E.setImageResource(R.drawable.yellow);
							msgText.replace("ใบเหลือง", "");
						} else if (txt_Item.getString("eventType").contains(
								"red-card")) {
							img_E.setImageResource(R.drawable.red);
							msgText.replace("ใบเแดง", "");
						} else if (txt_Item.getString("eventType").contains(
								"yellow/red-card")) {
							ImageView img_EY = new ImageView(mContext);
							img_EY.setLayoutParams(new LayoutParams(30, 30));
							img_EY.setImageResource(R.drawable.yellow);
							retval.addView(img_EY);
							img_E.setImageResource(R.drawable.red);
							msgText.replace("Yellow/Red", "");
						} else if (txt_Item.getString("eventType").contains(
								"yellow-red")) {
							ImageView img_EY = new ImageView(mContext);
							img_EY.setLayoutParams(new LayoutParams(30, 30));
							img_EY.setImageResource(R.drawable.yellow);
							retval.addView(img_EY);
							img_E.setImageResource(R.drawable.red);
							msgText.replace("Yellow/Red", "");
						} else if (txt_Item.getString("eventType").contains(
								"penalty-goal")) {
							Event = "(PG)";
							img_E.setImageResource(R.drawable.p_goal);
						} else if (txt_Item.getString("eventType").contains(
								"pen-so-goal")) {
							Event = "(PG)";
							img_E.setImageResource(R.drawable.p_goal);
							msgText.replace("Pen So Goal", "");
						} else if (txt_Item.getString("eventType").contains(
								"own-goal")) {
							Event = "(OG)";
							img_E.setImageResource(R.drawable.ow_goal);
							msgText.replace("Own Goal", "");
						} else if (txt_Item.getString("eventType").contains(
								"goal")) {
							Event = "(G)";
							img_E.setImageResource(R.drawable.goal);
							msgText.replace("ประตู", "");
						} else if (txt_Item.getString("eventType").contains(
								"assist")) {
							Event = "(A)";
							img_E.setImageResource(R.drawable.assist);
							msgText.replace("แอสซิสต์", "");
						}
						
						txt_N.setText(Event + msgText);
						retval.addView(img_E);
						retval.addView(txt_N);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (position % 2 == 0) {
				retval.setBackgroundColor(Color.GRAY);
				retval.getBackground().setAlpha(200);
			}

			return retval;

		}

	}

	class Live_score_Loader extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				loading = true;
				player_Detail.clear();
				ListDetail.clear();
				if(!score_t.equals("vs")){
					HtmlHelper_LiveScore live = new HtmlHelper_LiveScore(new URL(
							URL));
					live.getLinksByID("play-by-play");
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {

		}

		protected void onPostExecute(String file_url) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					if (player_Detail.size() <= 0) {
						player_Detail.add("NotFoundData");
					}
					if (FirstLoad) {
						LinearLayout list_layout = (LinearLayout) findViewById(R.id.list_player_Detail);
						list_layout.removeAllViews();
						list_layout.addView(lstView);
						FirstLoad = false;
					} else {
						imageAdapter.notifyDataSetChanged();
					}
					loading = false;
				}
			});
		}
	}

	public void checkRefreshDetail() {
		Runnable runnable = new Runnable() {
			public void run() {
				while (data.detailPageOpenning) {

					getValue = data.Match_list_c_JSON.get(position);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							String score_ag_t = "";
							try {
								if (link_t.equals(getValue.getString("link")
										.replace("/en/", "/th/")
										+ "/play-by-play")) {
									if ((!Time_t.equals(getValue.getString(
											"Time").substring(3)) || !score_t
											.equals(getValue.getString("score")
													.replaceAll("&nbsp;", " ")))) {
										Time_t = getValue.getString("Time")
												.substring(3);
										score_t = getValue.getString("score")
												.replaceAll("&nbsp;", " ");
										score_ag_t = getValue
												.getString("score_ag");
										Time.setText(Time_t);
										Score.setText(score_t);

										if (score_ag_t.length() >= 5) {
											txt_Aggregate.setText("AGGREGATE: "
													+ score_ag_t);
										}
									}
									if (!loading
											&& (!getValue.getString("Time")
													.substring(3)
													.contains("HT") && !getValue
													.getString("Time")
													.substring(3)
													.contains("FT"))) {
										new Live_score_Loader().execute();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					});

					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

			}
		};
		new Thread(runnable).start();
	}

	public class HtmlHelper_LiveScore {
		TagNode rootNode;

		public HtmlHelper_LiveScore(URL htmlPage) throws IOException {
			HtmlCleaner cleaner = new HtmlCleaner();
			rootNode = cleaner.clean(htmlPage);
		}

		List<TagNode> getLinksByID(String CSSIDname) throws JSONException {

			List<TagNode> linkList = new ArrayList<TagNode>();

			TagNode mainElement[] = rootNode.getElementsByName("ul", true);
			for (int i = 0; mainElement != null && i < mainElement.length; i++) {
				String AttValue = mainElement[i].getAttributeByName("class");
				if (AttValue != null && AttValue.contains("commentaries")) {
					TagNode liElement[] = mainElement[i].getElementsByName(
							"li", true);
					for (int j = 0; liElement != null && j < liElement.length; j++) {
						String eAttValue = liElement[j]
								.getAttributeByName("data-event-type");
						if (eAttValue != null) {
							if (!eAttValue.equals("action")) {
								JSONObject jObOut = new JSONObject();
								jObOut.put("eventType", eAttValue);

								TagNode divElement[] = liElement[j]
										.getElementsByName("div", true);
								for (int k = 0; divElement != null
										&& k < divElement.length; k++) {
									AttValue = divElement[k]
											.getAttributeByName("class");
									if (AttValue != null) {
										if (AttValue.equals("time")) {
											jObOut.put("time", divElement[k]
													.getText().toString()
													.replace("\n", ""));
										} else if (AttValue.equals("text")) {
											if (eAttValue
													.equals("substitution")) {
												TagNode Outtag[] = divElement[k]
														.getElementsByAttValue(
																"class",
																"sub-out",
																true, true);
												TagNode Intag[] = divElement[k]
														.getElementsByAttValue(
																"class",
																"sub-in", true,
																true);
												jObOut.put("subOut", Outtag[0]
														.getText().toString()
														.replace("\n", ""));
												jObOut.put("subIn", Intag[0]
														.getText().toString()
														.replace("\n", ""));
											} else {
												jObOut.put(
														"text",
														divElement[k]
																.getText()
																.toString()
																.replace("\n",
																		""));
											}
										}
									}
								}
								Log.d("TEST", "jObOut::" + jObOut);
								ListDetail.add(jObOut);
							}

						}
					}
				}
			}
			return linkList;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		data.detailPageOpenning = false;
		data.fragement_Section_set(1);
		overridePendingTransition(R.anim.in_trans_right_left,
				R.anim.out_trans_left_right);
		finish();
	}
}
