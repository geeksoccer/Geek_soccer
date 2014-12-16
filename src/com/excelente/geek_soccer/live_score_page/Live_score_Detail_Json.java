package com.excelente.geek_soccer.live_score_page;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.JSONParser;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.live_score_page.detail_view.Live_score_Detail_LiveView;
import com.excelente.geek_soccer.live_score_page.detail_view.Live_score_detail_LineUpView;
import com.excelente.geek_soccer.livescore_noty.LiveScoreReload;
import com.excelente.geek_soccer.livescore_noty.LiveScoreReload.LiveScoreCallbackClass;
import com.excelente.geek_soccer.pic_download.DownLiveScorePic;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Live_score_Detail_Json extends Activity {

	JSONParser jParser = new JSONParser();
	JSONObject products = null;
	
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

	LinearLayout list_layout;
	LiveScoreReload LiveScoreReloadCallBack;
	
	JSONObject MatchData_ob;
	JSONArray Team_Arr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setUpHeaderView();

		new Live_score_Loader().execute();
	}
	
	public void setUpHeaderView(){
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
		list_layout = (LinearLayout) myView.findViewById(R.id.list_player_Detail);
		//data.detailPageOpenning = true;

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
		String saveModeGet = SessionManager.getSetting(this,
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

		if (score_ag_t.length() >= 5 && !detail_t.equals("Aggregate " + score_ag_t)) {
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
			//checkRefreshDetail();
		}

		if (SessionManager.chkFavContain(this, id_t)
				|| Away_name_t.contains(ControllParameter.TeamSelect)
				|| Home_name_t.contains(ControllParameter.TeamSelect)) {
			Fav_btn.setImageResource(R.drawable.favorite_icon_full);
		}

		LiveScoreReloadCallBack = new LiveScoreReload();
		LiveScoreReloadCallBack.registerCallback(new LiveScoreCallbackClass() {
			@Override
			public void LiveScorecallbackReturn() {
				Fav_btn.setEnabled(true);
			}
		});
		Fav_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Fav_btn.setEnabled(false);
				if (SessionManager.chkFavContain(Live_score_Detail_Json.this, id_t)) {
					SessionManager.delFavTeam(Live_score_Detail_Json.this, id_t);
					Fav_btn.setImageResource(R.drawable.favorite_icon_hole);
				} else {
					SessionManager.addFavTeam(Live_score_Detail_Json.this, id_t);
					Fav_btn.setImageResource(R.drawable.favorite_icon_full);
				}
				LiveScoreReloadCallBack.SelectReload(type, Live_score_Detail_Json.this);
			}
		});
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
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				products = jParser
						.makeHttpRequest(
								"http://www.goal.com/feed/matches/statistics?optaMatchId=755457",
								"POST", params);
				
				if (products!=null) {
					
					JSONObject SoccerFeed_ob = products.getJSONObject("SoccerFeed");
					JSONObject SoccerDocument_ob = SoccerFeed_ob.getJSONObject("SoccerDocument");
					MatchData_ob = SoccerDocument_ob.getJSONObject("MatchData");
					Team_Arr = SoccerDocument_ob.getJSONArray("Team");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {

		}

		protected void onPostExecute(String file_url) {
			Live_score_Detail_Json.this.runOnUiThread(new Runnable() {
				public void run() {
					list_layout.removeAllViews();
					//list_layout.addView(new Live_score_detail_LineUpView().getView(Live_score_Detail_Json.this, Team_Arr, MatchData_ob));
					list_layout.addView(new Live_score_Detail_LiveView().getView(Live_score_Detail_Json.this, Team_Arr, MatchData_ob));
				}
			});
		}
	}
}
