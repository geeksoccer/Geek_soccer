package com.excelente.geek_soccer.live_score_page.detail_view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.live_score_page.Live_score_Detail_Json;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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

public class Live_score_Detail_LiveView {
	Activity mActivity;
	View myView;
	private ListView lstView;
	private ImageAdapter imageAdapter;
	JSONArray Team_Arr;
	JSONObject MatchData_ob;

	ArrayList<JSONObject> LiveDetail_List = new ArrayList<JSONObject>();
	HashMap<String, String> Player_Map = new HashMap<String, String>();

	public View getView(Activity activity, JSONArray Team_Arr,
			JSONObject MatchData_ob) {
		this.Team_Arr = Team_Arr;
		this.MatchData_ob = MatchData_ob;
		mActivity = activity;

		prepareData();

		lstView = new ListView(mActivity);
		lstView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		lstView.setClipToPadding(false);
		imageAdapter = new ImageAdapter(mActivity.getApplicationContext());
		lstView.setAdapter(imageAdapter);
		lstView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});

		return lstView;
	}

	public void prepareData() {
		try {
			JSONArray TeamData_Arr = MatchData_ob.getJSONArray("TeamData");
			String TeamID = "H";
			for (int i = 0; i < TeamData_Arr.length(); i++) {
				if (i == 0) {
					TeamID = "H";
				} else {
					TeamID = "A";
				}
				JSONObject TeamData_ob = TeamData_Arr.getJSONObject(i);

				JSONArray Booking_Arr = TeamData_ob.optJSONArray("Booking");
				JSONArray Goal_Arr = TeamData_ob.optJSONArray("Goal");
				JSONArray Substitution_Arr = TeamData_ob
						.optJSONArray("Substitution");

				if (Booking_Arr == null) {
					JSONObject Booking_ob = TeamData_ob
							.optJSONObject("Booking");
					Booking_Arr = new JSONArray();
					if (Booking_ob != null) {
						Booking_Arr.put(Booking_ob);
					}
				}
				if (Goal_Arr == null) {
					JSONObject Goal_ob = TeamData_ob
							.optJSONObject("Goal");
					Goal_Arr = new JSONArray();
					if (Goal_ob != null) {
						Goal_Arr.put(TeamData_ob.optJSONObject("Goal"));
					}
				}
				if (Substitution_Arr == null) {
					JSONObject Substitution_ob = TeamData_ob
							.optJSONObject("Substitution");
					Substitution_Arr = new JSONArray();
					if (Substitution_ob != null) {
						Substitution_Arr.put(TeamData_ob
								.optJSONObject("Substitution"));
					}
				}

				for (int j = 0; j < Booking_Arr.length(); j++) {
					JSONObject Booking_ob = Booking_Arr.getJSONObject(j);
					JSONObject attributes = Booking_ob
							.getJSONObject("@attributes");
					attributes.put("eventType",
							attributes.getString("CardType"));
					attributes.put("TeamID", TeamID);
					LiveDetail_List.add(attributes);
				}
				for (int j = 0; j < Goal_Arr.length(); j++) {
					JSONObject Goal_ob = Goal_Arr.getJSONObject(j);
					JSONObject attributes = Goal_ob
							.getJSONObject("@attributes");
					JSONObject Assist_ob = Goal_ob.optJSONObject("Assist");
					if (Assist_ob != null) {
						JSONObject Assist_ob_attributes = Assist_ob
								.getJSONObject("@attributes");
						Assist_ob.put("Time", attributes.getString("Time"));
						Assist_ob.put("PlayerRef",
								Assist_ob_attributes.getString("PlayerRef"));
						Assist_ob.put("eventType", "Assist");
						Assist_ob.put("TeamID", TeamID);
						LiveDetail_List.add(Assist_ob);
					}
					attributes.put("TeamID", TeamID);
					attributes.put("eventType", attributes.getString("Type"));
					LiveDetail_List.add(attributes);
				}
				for (int j = 0; j < Substitution_Arr.length(); j++) {
					JSONObject Substitution_ob = Substitution_Arr
							.getJSONObject(j);
					JSONObject attributes = Substitution_ob
							.getJSONObject("@attributes");
					attributes.put("eventType", "substitution");
					attributes.put("TeamID", TeamID);
					LiveDetail_List.add(attributes);
				}
			}

			Collections.sort(LiveDetail_List, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject s1, JSONObject s2) {
					try {
						return s2.getString("Time").compareToIgnoreCase(
								s1.getString("Time"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return 0;
				}
			});

			for (int i = 0; i < Team_Arr.length(); i++) {
				JSONObject Team_Ob = Team_Arr.getJSONObject(i);

				JSONArray Player_Arr = Team_Ob.getJSONArray("Player");
				for (int j = 0; j < Player_Arr.length(); j++) {
					JSONObject Player_ob = Player_Arr.getJSONObject(j);
					JSONObject PersonName_ob = Player_ob
							.getJSONObject("PersonName");
					String PlayerName = PersonName_ob.getString("First") + " "
							+ PersonName_ob.getString("Last");
					JSONObject attributes_ob = Player_ob
							.getJSONObject("@attributes");
					String uID = attributes_ob.getString("uID");

					Player_Map.put(uID, PlayerName);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {

			if (LiveDetail_List.size() == 0) {
				JSONObject obJdebug = new JSONObject();
				try {
					obJdebug.put("NotFound", "NotFound");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				LiveDetail_List.add(obJdebug);
			}

			return LiveDetail_List.size();
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

			if (!LiveDetail_List.get(position).isNull("NotFound")) {
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
					JSONObject txt_Item = LiveDetail_List.get(position);

					ImageView img_Team = new ImageView(mContext);
					img_Team.setLayoutParams(new LayoutParams(30, 30));
					String TeamID = txt_Item.getString("TeamID");
					if (TeamID.equals("H")) {
						if (Live_score_Detail_Json.data
								.get_HomeMap(Live_score_Detail_Json.Home_img_t) != null) {
							img_Team.setImageBitmap(Live_score_Detail_Json.data
									.get_HomeMap(Live_score_Detail_Json.Home_img_t));
						}
					} else if (TeamID.equals("A")) {
						if (Live_score_Detail_Json.data
								.get_AwayMap(Live_score_Detail_Json.Away_img_t) != null) {
							img_Team.setImageBitmap(Live_score_Detail_Json.data
									.get_AwayMap(Live_score_Detail_Json.Away_img_t));
						}
					}

					TextView txt_T = new TextView(mContext);
					txt_T.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					// txt.setGravity(Gravity.CENTER);
					txt_T.setTextColor(colors);
					txt_T.setPadding(10, 0, 10, 0);

					TextView txt_N = new TextView(mContext);
					txt_N.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1));
					txt_N.setTextColor(colors);

					ImageView img_E = new ImageView(mContext);
					img_E.setLayoutParams(new LayoutParams(30, 30));

					String Time = txt_Item.getString("Time");

					txt_T.setText(Time + "'");

					retval.addView(img_Team);
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

						String subOut = txt_Item.getString("SubOff");
						subOut = Player_Map.get(subOut);

						txt_N.setText(subOut);
						ImageView img_SubIn = new ImageView(mContext);
						img_SubIn.setLayoutParams(new LayoutParams(30, 30));
						img_SubIn.setImageResource(R.drawable.substitution_in);

						String subIn = txt_Item.getString("SubOn");
						subIn = Player_Map.get(subIn);
						txt_Sub.setText(subIn);

						retval.addView(img_E);
						retval.addView(txt_N);
						retval.addView(img_SubIn);
						retval.addView(txt_Sub);
					} else {
						String Event = "";
						String msgText = txt_Item.getString("PlayerRef");
						msgText = Player_Map.get(msgText);
						if (txt_Item.getString("eventType").contains("Yellow")) {
							img_E.setImageResource(R.drawable.yellow);
						} else if (txt_Item.getString("eventType").contains(
								"Red")) {
							img_E.setImageResource(R.drawable.red);
						} else if (txt_Item.getString("eventType").contains(
								"SeccondYellow")) {
							ImageView img_EY = new ImageView(mContext);
							img_EY.setLayoutParams(new LayoutParams(30, 30));
							img_EY.setImageResource(R.drawable.yellow);
							retval.addView(img_EY);
							img_E.setImageResource(R.drawable.red);
						} else if (txt_Item.getString("eventType").contains(
								"yellow-red")) {
							ImageView img_EY = new ImageView(mContext);
							img_EY.setLayoutParams(new LayoutParams(30, 30));
							img_EY.setImageResource(R.drawable.yellow);
							retval.addView(img_EY);
							img_E.setImageResource(R.drawable.red);
						} else if (txt_Item.getString("eventType").contains(
								"Penalty")) {
							Event = "(PG)";
							img_E.setImageResource(R.drawable.p_goal);
						} else if (txt_Item.getString("eventType").contains(
								"pen-so-goal")) {
							Event = "(PG)";
							img_E.setImageResource(R.drawable.p_goal);
						} else if (txt_Item.getString("eventType").contains(
								"Own")) {
							Event = "(OG)";
							img_E.setImageResource(R.drawable.ow_goal);
						} else if (txt_Item.getString("eventType").contains(
								"Goal")) {
							Event = "(G)";
							img_E.setImageResource(R.drawable.goal);
						} else if (txt_Item.getString("eventType").contains(
								"Assist")) {
							Event = "(A)";
							img_E.setImageResource(R.drawable.assist);
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
}
