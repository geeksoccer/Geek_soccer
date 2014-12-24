package com.excelente.geek_soccer.live_score_page.detail_view;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Live_score_detail_LineUpView {
	Activity mActivity;
	View myView;
	private ListView lstView;
	private ImageAdapter imageAdapter;
	JSONArray Team_Arr;
	JSONObject MatchData_ob;
	JSONArray Player_Arr_H;
	JSONArray Player_Arr_A;

	HashMap<String, JSONObject> Player_Map = new HashMap<String, JSONObject>();
	int MaxPlayerLength = 0;

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
			for (int i = 0; i < TeamData_Arr.length(); i++) {
				JSONObject TeamData_ob = TeamData_Arr.getJSONObject(i);
				JSONObject PlayerLineUp_ob = TeamData_ob
						.getJSONObject("PlayerLineUp");
				JSONArray MatchPlayer_Arr = PlayerLineUp_ob
						.getJSONArray("MatchPlayer");
				for (int j = 0; j < MatchPlayer_Arr.length(); j++) {
					JSONObject MatchPlayer_ob = MatchPlayer_Arr
							.getJSONObject(j);
					JSONObject attributes_ob = MatchPlayer_ob
							.getJSONObject("@attributes");
					String PlayerRef = attributes_ob.getString("PlayerRef");
					Player_Map.put(PlayerRef, MatchPlayer_ob);
				}
			}
			for (int i = 0; i < Team_Arr.length(); i++) {
				JSONObject Team_Ob = Team_Arr.getJSONObject(i);

				JSONArray Player_Arr = Team_Ob.getJSONArray("Player");
				if (i == 0) {
					Player_Arr_H = Player_Arr;
				} else {
					Player_Arr_A = Player_Arr;
				}
				if (Player_Arr.length() > MaxPlayerLength) {
					MaxPlayerLength = Player_Arr.length();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ImageAdapter extends BaseAdapter {

		public ImageAdapter(Context context) {
		}

		public int getCount() {
			return MaxPlayerLength;
		}

		public Object getItem(int position) {
			return null;// URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater factory = LayoutInflater.from(mActivity);
			View myView = factory.inflate(R.layout.lineup_cell, null);
			LinearLayout SubHeadView = (LinearLayout)myView.findViewById(R.id.SubHeadView);
			TextView H_No = (TextView) myView.findViewById(R.id.H_No);
			TextView H_Name = (TextView) myView.findViewById(R.id.H_Name);
			TextView H_Position = (TextView) myView
					.findViewById(R.id.H_Position);

			TextView A_No = (TextView) myView.findViewById(R.id.A_No);
			TextView A_Name = (TextView) myView.findViewById(R.id.A_Name);
			TextView A_Position = (TextView) myView
					.findViewById(R.id.A_Position);

			try {
				JSONObject Player_ob = Player_Arr_H.getJSONObject(position);
				JSONObject PersonName_ob = Player_ob
						.getJSONObject("PersonName");
				String PlayerName = PersonName_ob.getString("First") + " "
						+ PersonName_ob.getString("Last");
				JSONObject attributes_ob = Player_ob
						.getJSONObject("@attributes");
				String Position = attributes_ob.getString("Position");
				String uID = attributes_ob.getString("uID");
				JSONObject MatchPlayer_ob = Player_Map.get(uID);
				attributes_ob = MatchPlayer_ob.getJSONObject("@attributes");
				String ShirtNumber = attributes_ob.getString("ShirtNumber");

				H_No.setText("[" + ShirtNumber + "]");
				H_Name.setText(PlayerName);
				H_Position.setText("[" + Position + "]");
				if (Position.equals("Substitute")) {
					H_Name.setTextColor(Color.RED);
				}

				Player_ob = Player_Arr_A.getJSONObject(position);
				PersonName_ob = Player_ob.getJSONObject("PersonName");
				PlayerName = PersonName_ob.getString("First") + " "
						+ PersonName_ob.getString("Last");
				attributes_ob = Player_ob.getJSONObject("@attributes");
				Position = attributes_ob.getString("Position");
				uID = attributes_ob.getString("uID");
				MatchPlayer_ob = Player_Map.get(uID);
				attributes_ob = MatchPlayer_ob.getJSONObject("@attributes");
				ShirtNumber = attributes_ob.getString("ShirtNumber");

				A_No.setText("[" + ShirtNumber + "]");
				A_Name.setText(PlayerName);
				A_Position.setText("[" + Position + "]");
				if (Position.equals("Substitute")) {
					A_Name.setTextColor(Color.RED);
					JSONObject OldPlayer_ob = Player_Arr_H.getJSONObject(position-1);
					JSONObject Oldattributes_ob = OldPlayer_ob
							.getJSONObject("@attributes");
					String OldPosition = Oldattributes_ob.getString("Position");
					if(Position.equals(OldPosition)){
						SubHeadView.setVisibility(RelativeLayout.GONE);
					}else{
						SubHeadView.setVisibility(RelativeLayout.ABOVE);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return myView;

		}

	}
}
