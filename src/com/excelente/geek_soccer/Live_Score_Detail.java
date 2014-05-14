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
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.Boast;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Live_Score_Detail extends Activity{
	
	Context mContext;
	String URL = "http://www.goal.com";
	int Detail_positon;
	TextView Time;
	TextView Score;
	TextView Home_name;
	TextView Away_name;
	ImageView Home_Pic;
	ImageView Away_Pic;
	LinearLayout Up_btn;
	JSONObject getValue;
	String get_Time = "";
	String get_Score = "";
	String get_Home_name = "";
	String get_Away_name = "";
	private static ControllParameter data;
	//String player_Detail[];
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
	String Home_img_t = "";
	String Away_img_t = "";
	String Home_name_t = "";
	String Away_name_t = "";
	String score_ag_t = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = ControllParameter.getInstance(this);
		
		data.fragement_Section_set(0);
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this).getTeamId());
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(R.layout.live_score_detail, null);
		setContentView(myView);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		Up_btn = (LinearLayout)myView.findViewById(R.id.Up_btn);
		Home_Pic = (ImageView)myView.findViewById(R.id.Home_Pic);
		Away_Pic = (ImageView)myView.findViewById(R.id.Away_Pic);
		Score = (TextView)myView.findViewById(R.id.Score);
		Home_name = (TextView)myView.findViewById(R.id.Home_name);
		Away_name = (TextView)myView.findViewById(R.id.Away_name);
		Time = (TextView)myView.findViewById(R.id.Time);
		txt_Aggregate = (TextView)myView.findViewById(R.id.Score_Aggregate);
		data.detailPageOpenning = true;
		mContext = this;
		position = getIntent().getExtras().getInt("URL");
		type = getIntent().getExtras().getString("TYPE");
		if(type.equals("y")){
			getValue = data.Match_list_y_JSON.get(position);
		}else if(type.equals("c")){
			getValue = data.Match_list_c_JSON.get(position);
		}else if(type.equals("t")){
			getValue = data.Match_list_t_JSON.get(position);
		}
		
		Up_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		try {
			Time_t = getValue.getString("Time").substring(3);
			link_t = getValue.getString("link").replace("/en/", "/th/")+"/play-by-play";
			Home_img_t = getValue.getString("Home_img");
			Away_img_t = getValue.getString("Away_img");
			Home_name_t = getValue.getString("Home");
			score_t = getValue.getString("score").replaceAll("&nbsp;", " ");
			data.OldScore_Detail = score_t;
			data.OldTime_Detail = Time_t;
			Away_name_t = getValue.getString("Away");
			score_ag_t = getValue.getString("score_ag");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Time.setText(Time_t);
		URL+=link_t;
		if (data.get_HomeMap(Home_img_t) != null) {
			Home_Pic.setImageBitmap(data.get_HomeMap(Home_img_t));
		}
		if (data.get_AwayMap(Away_img_t) != null) {
			Away_Pic.setImageBitmap(data.get_AwayMap(Away_img_t));
		}
		
		Score.setText(score_t);
		Home_name.setText(Home_name_t);
		Away_name.setText(Away_name_t);
		
		if(score_ag_t.length()>=5){
			txt_Aggregate.setText("AGGREGATE: "+score_ag_t);
		}
		if(type.equals("c")){
			checkRefreshDetail();
		}
		
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
				String txt_Item = player_Detail.get(position);
				if(!txt_Item.equals("NotFoundData")){
					String Split_item[] = txt_Item.replaceAll("&quot;", "\"").split(":");
					String eventStr = "";
					if(Split_item[1].contains("ใบเหลือง")){
						eventStr = "ได้รับใบเหลือง";
					}else if(Split_item[1].contains("Yellow Card")){
						eventStr = "Yellow Card";
					}else if(Split_item[1].contains("ใบแดง") ){
						eventStr = "ได้รับใบแดง";
					}else if(Split_item[1].contains("Red Card")){
						eventStr = "Red Card";
					}else if(Split_item[1].contains("Yellow/Red")){
						if(link_t.contains("/en/")){
							eventStr = "Yellow/Red";
						}else{
							eventStr = "ได้รับใบเหลืองใบที่ 2 / ได้รับใบแดง";
						}
					}else if(Split_item[1].contains("ยิงจุดโทษได้")
							|| Split_item[1].contains("Pen SO Goal")
							|| Split_item[1].contains("Pen SO Miss")){
						if(link_t.contains("/en/")){
							eventStr = "Pen Goal";
						}else{
							eventStr = "ทำประตูได้จากจุดโทษ";
						}	
					}else if(Split_item[1].contains("ทำเข้าประตูตัวเอง")){
						eventStr = "ทำเข้าประตูตัวเอง";
					}else if(Split_item[1].contains("Own Goal")){
						eventStr = "Own Goal";
					}else if(Split_item[1].contains("ประตู")){
						eventStr = "ทำประตูได้";
					}else if(Split_item[1].contains("Goal")){
						eventStr = "Goal";
					}else if(Split_item[1].contains("แอสซิสต์")){
						eventStr = "จ่ายให้เพื่อนทำประตูได้";
					}else if(Split_item[1].contains("Assist")){
						eventStr = "Assist";
					}else if(Split_item[1].equals("เปลี่ยนตัว")){
						eventStr = "เปลี่ยนตัว";
					}else if(Split_item[1].equals("Substitution")){
						eventStr = "Substitution";
					}
					
					Boast.makeText(mContext, eventStr, Toast.LENGTH_LONG).show();
				}
				
			}
		});
		new Live_score_Loader().execute();
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//this.overridePendingTransition(R.drawable.ani_in, R.drawable.ani_alpha);
	}

	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return player_Detail.size();//+League_list.size();
		}

		public Object getItem(int position) {
			return null;//URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			LinearLayout retval = new LinearLayout(mContext);
			retval.setOrientation(LinearLayout.HORIZONTAL);
			retval.setGravity(Gravity.CENTER);
			retval.setPadding(5, 0, 5, 0);
			retval.setMinimumHeight(50);
			String txt_Item = player_Detail.get(position);
			int colors = Integer.parseInt("000000", 16) + (0xFF000000);
			if(txt_Item.equals("NotFoundData")){
				TextView txt_T = new TextView(mContext);
				txt_T.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txt_T.setGravity(Gravity.CENTER);
				txt_T.setTextColor(colors);
				txt_T.setPadding(0, 0, 10, 0);
				txt_T.setText("ยังไม่มีข้อมูลอัพเดทในขณะนี้");
				retval.addView(txt_T);
			}else{
				
				TextView txt_T = new TextView(mContext);
				txt_T.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				//txt.setGravity(Gravity.CENTER);
				txt_T.setTextColor(colors);
				txt_T.setPadding(0, 0, 10, 0);
				
				TextView txt_N = new TextView(mContext);
				txt_N.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
				txt_N.setTextColor(colors);
				
				ImageView img_E = new ImageView(mContext);
				img_E.setLayoutParams(new LayoutParams(30, 30));

				String Split_item[] = txt_Item.replaceAll("&quot;", "\"").split(":");
				String NameSub[] = Split_item[2].split("//");
				
				txt_T.setText(Split_item[0]+"'");
				
				retval.addView(txt_T);
				String Event = "";
				if(Split_item[1].contains("ใบเหลือง")
						|| Split_item[1].contains("Yellow Card")){
					img_E.setImageResource(R.drawable.yellow);
				}else if(Split_item[1].contains("ใบแดง")
						|| Split_item[1].contains("Red Card")){
					img_E.setImageResource(R.drawable.red);
				}else if(Split_item[1].contains("Yellow/Red")){
					ImageView img_EY = new ImageView(mContext);
					img_EY.setLayoutParams(new LayoutParams(30, 30));
					img_EY.setImageResource(R.drawable.yellow);
					retval.addView(img_EY);
					img_E.setImageResource(R.drawable.red);
				}else if(Split_item[1].contains("ยิงจุดโทษได้")
						|| Split_item[1].contains("Pen SO Goal")
						|| Split_item[1].contains("Pen SO Miss")){
					Event = "(PG)";
					img_E.setImageResource(R.drawable.goal);
				}else if(Split_item[1].contains("ทำเข้าประตูตัวเอง")
						|| Split_item[1].contains("Own Goal")){
					Event = "(OG)";
					img_E.setImageResource(R.drawable.goal);
				}else if(Split_item[1].contains("ประตู")
						|| Split_item[1].contains("Goal")){
					Event = "(G)";
					img_E.setImageResource(R.drawable.goal);
				}else if(Split_item[1].contains("แอสซิสต์")
						|| Split_item[1].contains("Assist")){
					Event = "(A)";
					img_E.setImageResource(R.drawable.assist);
				}
				
				TextView txt_Sub = new TextView(mContext);
				txt_Sub.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
				txt_Sub.setTextColor(colors);
				
				retval.addView(img_E);
				retval.addView(txt_N);
				if(Split_item[1].equals("เปลี่ยนตัว")
						|| Split_item[1].equals("Substitution")){
					txt_N.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					img_E.setImageResource(R.drawable.substitution);
					txt_N.setText(NameSub[1]);
					ImageView img_SubIn = new ImageView(mContext);
					img_SubIn.setLayoutParams(new LayoutParams(30, 30));
					img_SubIn.setImageResource(R.drawable.substitution_in);
					retval.addView(img_SubIn);
					txt_Sub.setText(NameSub[2]);
					retval.addView(txt_Sub);
				}else{
					txt_N.setText(Event+Split_item[2]);
				}
			}
			
			if(position%2==0){
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
				HtmlHelper_LiveScore live = new HtmlHelper_LiveScore(new URL(
						URL));
				live.getLinksByID("play-by-play");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(String file_url) {
			//pDialog.dismiss();
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					if(player_Detail.size()<=0){
						player_Detail.add("NotFoundData");
					}
					if(FirstLoad){
						LinearLayout list_layout = (LinearLayout)findViewById(R.id.list_player_Detail);					
						list_layout.removeAllViews();
						list_layout.addView(lstView);
						FirstLoad = false;
					}else{
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
								if (link_t.equals(getValue.getString("link").replace("/en/", "/th/") + "/play-by-play") ) {
									if ((!Time_t.equals(getValue.getString("Time").substring(3))
											|| !score_t.equals(getValue.getString("score").replaceAll("&nbsp;", " "))) ) {
										Time_t = getValue.getString("Time")
												.substring(3);
										score_t = getValue.getString("score")
												.replaceAll("&nbsp;", " ");
										score_ag_t = getValue
												.getString("score_ag");
										if (!Away_name_t.contains(ControllParameter.TeamSelect)
												&& !Home_name_t.contains(ControllParameter.TeamSelect)) {
											NotifyLiveScore(Home_name_t, score_t, Away_name_t, Time_t);
											data.OldScore_Detail = score_t;
											data.OldTime_Detail = Time_t;
										}
										
										
										Time.setText(Time_t);
										Score.setText(score_t);

										if (score_ag_t.length() >= 5) {
											txt_Aggregate.setText("AGGREGATE: "
													+ score_ag_t);
										}
									}
									if(!loading
											&&(!getValue.getString("Time").substring(3).contains("HT")
													&&!getValue.getString("Time").substring(3).contains("FT"))){
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
	
	public void NotifyLiveScore(final String Home, final String newScore, final String Away, final String Time) {
		if(SessionManager.getSetting( mContext, SessionManager.setting_notify_livescore)==null){
			SessionManager.setSetting(mContext, SessionManager.setting_notify_livescore, "true");
		}
		if(SessionManager.getSetting( mContext, SessionManager.setting_notify_livescore).equals("true")){
			if(!data.OldTime_Detail.equals("FT")){
				if(!Time.equals("FT")
						|| !data.OldTime_Detail.equals("")){
					if((!newScore.equals(data.OldScore_Detail) && !data.OldScore_Detail.equals(""))
							|| (!Time.equals(data.OldTime_Detail) && ((Time.equals("HT")) || Time.equals("FT")))
							|| data.OldTime_Detail.equals("")
							|| (!Time.equals(data.OldTime_Detail) && ((data.OldTime_Detail.equals("HT")))) ){
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
	
	public class HtmlHelper_LiveScore {
		TagNode rootNode;

		public HtmlHelper_LiveScore(URL htmlPage) throws IOException {
			HtmlCleaner cleaner = new HtmlCleaner();

			rootNode = cleaner.clean(htmlPage);
		}

		List<TagNode> getLinksByID(String CSSIDname) {
			List<TagNode> linkList = new ArrayList<TagNode>();
			TagNode linkElements[] = rootNode.getElementsByName("div", true);
			for (int i = 0; linkElements != null && i < linkElements.length; i++) {
				String idName = linkElements[i].getAttributeByName("id");
				if (idName != null && idName.contains(CSSIDname)) {
					Log.d("TEST", "_idName::"+idName);
					TagNode Elements_live_comments_item[] = linkElements[i].getElementsByName("div", true);
					String outPut = "";
					for (int j = 0; Elements_live_comments_item != null && j < Elements_live_comments_item.length; j++) {
						String className = Elements_live_comments_item[j].getAttributeByName("class");
						
						if(className!=null){
							if(className.contains("live_comments_minute")){
								Log.d("TEST", "Value_Time::"
								+Elements_live_comments_item[j].getElementsByName("strong", true)[0].getText().toString());
								outPut = Elements_live_comments_item[j].getElementsByName("strong", true)[0]
										.getText().toString().replace("&prime;", "")+":";
							}else if(className.contains("live_comments_text")){
								if(Elements_live_comments_item[j].getElementsByName("span", true).length>0){
									String Value_Span = Elements_live_comments_item[j].getElementsByName("span", true)[0].getText().toString();
									if(!Value_Span.contains("พลาดจุดโทษ")&&!Value_Span.contains("เซฟจุดโทษได้")){
										outPut+=Value_Span+":";
										outPut+=Elements_live_comments_item[j].getText().toString().replace(Value_Span, "").substring(1).replaceAll("  ", "//");
										Log.d("TEST", "Value_text_Span::" + outPut);
										player_Detail.add(outPut);
									}
									
								}
								
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
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		finish();
	}
	/*
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			data.fragement_Section_set(1);
			overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	*/
}
