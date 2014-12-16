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
import com.excelente.geek_soccer.live_score_page.detail_view.Live_score_detail_statistic;
import com.excelente.geek_soccer.livescore_noty.LiveScoreReload;
import com.excelente.geek_soccer.livescore_noty.LiveScoreReload.LiveScoreCallbackClass;
import com.excelente.geek_soccer.pic_download.DownLiveScorePic;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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
	public static ControllParameter data;
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
	String id_t = "", Home_name_t = "", Away_name_t = "", score_ag_t = "", detail_t = "";
	public static String Home_img_t = "", Away_img_t = "";
	
	LinearLayout MenuLayout;
	LinearLayout list_layout;
	LiveScoreReload LiveScoreReloadCallBack;
	
	JSONObject MatchData_ob;
	JSONArray Team_Arr;
	
	View StatisticDetailView;
	View LineUpView;
	View LiveDetailView;
	android.widget.LinearLayout.LayoutParams childParam;
	String optaID="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setUpHeaderView();

		if(!score_t.equals("vs")){
			new OptaID_Loader().execute();
		}else{
			list_layout.removeAllViews();
			TextView txt_T = new TextView(this);
			txt_T.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			txt_T.setGravity(Gravity.CENTER);
			txt_T.setTextColor(Color.BLACK);
			txt_T.setPadding(0, 0, 10, 0);
			txt_T.setText("ยังไม่มีข้อมูลอัพเดทในขณะนี้");
			list_layout.addView(txt_T);
		}
	}
	
	public void setUpHeaderView(){
		data = ControllParameter.getInstance(this);

		data.fragement_Section_set(0);
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this)
				.getTeamId());
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(R.layout.live_score_detail_json, null);
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
		
		MenuLayout = (LinearLayout) myView.findViewById(R.id.MenuLayout);
		list_layout = (LinearLayout) myView.findViewById(R.id.list_player_Detail);
		data.detailPageOpenning = true;
		
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
	
	private void setupTab(final String name, String label, Integer iconId,
			boolean selected) {

		View tab = LayoutInflater.from(this).inflate(
				R.layout.custom_tab, null);
		ImageView image = (ImageView) tab.findViewById(R.id.icon);
		TextView text = (TextView) tab.findViewById(R.id.text);
		text.setTypeface(null, Typeface.BOLD);
		if (label.equals("")) {
			text.setVisibility(View.GONE);

			final float scale = this.getResources()
					.getDisplayMetrics().density;
			int pixels = (int) (40 * scale + 0.5f);
			image.getLayoutParams().width = pixels;
			image.getLayoutParams().height = pixels;
		}

		if (iconId == 0) {
			image.setVisibility(View.GONE);

			final float scale = this.getResources()
					.getDisplayMetrics().density;
			int pixels = (int) (40 * scale + 0.5f);
			text.getLayoutParams().height = pixels;
		}

		View viewSelected = tab.findViewById(R.id.selected);
		if (selected)
			viewSelected.setVisibility(View.VISIBLE);

		if (iconId != null) {
			image.setImageResource(iconId);
		}
		text.setText(label);

		LayoutParams childParam = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.MATCH_PARENT, 1);

		tab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name.equals("s")) {
					setCurrentTab(0);
				} else if (name.equals("d")) {
					setCurrentTab(1);
				} else if (name.equals("l")) {
					setCurrentTab(2);
				}
			}
		});

		MenuLayout.addView(tab, childParam);
	}

	public void setCurrentTab(int index) {
		for (int i = 0; i < MenuLayout.getChildCount(); i++) {
			if (i == index) {
				MenuLayout.getChildAt(i).findViewById(R.id.selected)
				.setVisibility(View.VISIBLE);
			} else {
				MenuLayout.getChildAt(i).findViewById(R.id.selected)
				.setVisibility(View.INVISIBLE);
			}
		}
		if (index == 0) {
			if(StatisticDetailView!=null){
				StatisticDetailView.setVisibility(RelativeLayout.ABOVE);
			}
			if(LiveDetailView!=null){
				LiveDetailView.setVisibility(RelativeLayout.GONE);
			}
			if(LineUpView!=null){
				LineUpView.setVisibility(RelativeLayout.GONE);
			}
		} else if (index == 1) {
			if(StatisticDetailView!=null){
				StatisticDetailView.setVisibility(RelativeLayout.GONE);
			}
			if(LiveDetailView!=null){
				LiveDetailView.setVisibility(RelativeLayout.ABOVE);
			}
			if(LineUpView!=null){
				LineUpView.setVisibility(RelativeLayout.GONE);
			}
		} else if (index == 2) {
			if(StatisticDetailView!=null){
				StatisticDetailView.setVisibility(RelativeLayout.GONE);
			}
			if(LiveDetailView!=null){
				LiveDetailView.setVisibility(RelativeLayout.GONE);
			}
			if(LineUpView!=null){
				LineUpView.setVisibility(RelativeLayout.ABOVE);
			}
		}
		
	}
	
	class OptaID_Loader extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				products = jParser
						.makeHttpRequest(
								ControllParameter.GET_OPTA_ID_URL+"?matchID="+id_t,
								"POST", params);
				
				if (products!=null) {
					optaID = products.optString("optaMatchId");
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
					new Live_score_Loader().execute();
				}
			});
		}
	}

	class Live_score_Loader extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				products = jParser
						.makeHttpRequest(
								"http://www.goal.com/feed/matches/statistics?optaMatchId="+optaID,
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
					StatisticDetailView = new Live_score_detail_statistic().getView(Live_score_Detail_Json.this, Team_Arr, MatchData_ob);
					LineUpView = new Live_score_detail_LineUpView().getView(Live_score_Detail_Json.this, Team_Arr, MatchData_ob);
					LiveDetailView = new Live_score_Detail_LiveView().getView(Live_score_Detail_Json.this, Team_Arr, MatchData_ob);
					
					setupTab("s", "Statistics", 0, false);
					setupTab("d", "Events", 0, false);
					setupTab("l", "Lineups", 0, false);
					
					childParam = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					
					list_layout.addView(StatisticDetailView, childParam);
					list_layout.addView(LiveDetailView, childParam);
					list_layout.addView(LineUpView, childParam);
					LiveDetailView.setVisibility(RelativeLayout.GONE);
					LineUpView.setVisibility(RelativeLayout.GONE);
					setCurrentTab(0);
				}
			});
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
