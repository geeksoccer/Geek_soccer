package com.excelente.geek_soccer.live_score_page.detail_view;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.excelente.geek_soccer.GetdipSize;
import com.excelente.geek_soccer.R;

public class Live_score_detail_statistic {
	Activity mActivity;
	View myView;
	JSONArray Team_Arr;
	JSONObject MatchData_ob;

	HashMap<String, String> Static_Map = new HashMap<String, String>();

	TextView total_scoring_att0, attempts_conceded_ibox0, won_corners0,
			fk_foul_lost0, possession_percentage0;
	TextView total_scoring_att1, attempts_conceded_ibox1, won_corners1,
			fk_foul_lost1, possession_percentage1;

	public View getView(Activity activity, JSONArray Team_Arr,
			JSONObject MatchData_ob) {
		this.Team_Arr = Team_Arr;
		this.MatchData_ob = MatchData_ob;
		mActivity = activity;

		LayoutInflater factory = LayoutInflater.from(mActivity);
		myView = factory.inflate(R.layout.detail_statistic_view, null);

		prepareData();
		DetailViewSetup();

		return myView;
	}

	public void prepareData() {
		try {
			JSONArray TeamData_Arr = MatchData_ob.getJSONArray("TeamData");
			for (int i = 0; i < TeamData_Arr.length(); i++) {
				JSONObject TeamData_ob = TeamData_Arr.getJSONObject(i);
				JSONArray Stat_Arr = TeamData_ob.getJSONArray("Stat");

				for (int j = 0; j < Stat_Arr.length(); j++) {
					JSONObject Stat_ob = Stat_Arr.getJSONObject(j);
					JSONObject attributes_ob = Stat_ob
							.getJSONObject("@attributes");
					String Type = attributes_ob.getString("Type")+i;

					String value = Stat_ob.getString("@value");

					Static_Map.put(Type, value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void DetailViewSetup() {
		total_scoring_att0 = (TextView) myView
				.findViewById(R.id.total_scoring_att0);
		attempts_conceded_ibox0 = (TextView) myView
				.findViewById(R.id.attempts_conceded_ibox0);
		won_corners0 = (TextView) myView.findViewById(R.id.won_corners0);
		fk_foul_lost0 = (TextView) myView.findViewById(R.id.fk_foul_lost0);
		possession_percentage0 = (TextView) myView
				.findViewById(R.id.possession_percentage0);

		total_scoring_att1 = (TextView) myView
				.findViewById(R.id.total_scoring_att1);
		attempts_conceded_ibox1 = (TextView) myView
				.findViewById(R.id.attempts_conceded_ibox1);
		won_corners1 = (TextView) myView.findViewById(R.id.won_corners1);
		fk_foul_lost1 = (TextView) myView.findViewById(R.id.fk_foul_lost1);
		possession_percentage1 = (TextView) myView
				.findViewById(R.id.possession_percentage1);
		
		String total_scoring_att0_S = Static_Map.get("total_scoring_att0");
		String attempts_conceded_ibox0_S = Static_Map.get("attempts_conceded_ibox0");
		String won_corners0_S = Static_Map.get("won_corners0");
		String fk_foul_lost0_S = Static_Map.get("fk_foul_lost0");
		String possession_percentage0_S = Static_Map.get("possession_percentage0");
		
		String total_scoring_att1_S = Static_Map.get("total_scoring_att1");
		String attempts_conceded_ibox1_S = Static_Map.get("attempts_conceded_ibox1");
		String won_corners1_S = Static_Map.get("won_corners1");
		String fk_foul_lost1_S = Static_Map.get("fk_foul_lost1");
		String possession_percentage1_S = Static_Map.get("possession_percentage1");
		
		total_scoring_att0.setText(total_scoring_att0_S);
		attempts_conceded_ibox0.setText(attempts_conceded_ibox0_S);
		won_corners0.setText(won_corners0_S);
		fk_foul_lost0.setText(fk_foul_lost0_S);
		possession_percentage0.setText(possession_percentage0_S);
		
		total_scoring_att1.setText(total_scoring_att1_S);
		attempts_conceded_ibox1.setText(attempts_conceded_ibox1_S);
		won_corners1.setText(won_corners1_S);
		fk_foul_lost1.setText(fk_foul_lost1_S);
		possession_percentage1.setText(possession_percentage1_S);

		int TextH = GetdipSize.dip(mActivity, 30);
		
		float total_scoring_att0_f = Float.parseFloat(total_scoring_att0_S);
		float attempts_conceded_ibox0_f = Float.parseFloat(attempts_conceded_ibox0_S);
		float won_corners0_f = Float.parseFloat(won_corners0_S);
		float fk_foul_lost0_f = Float.parseFloat(fk_foul_lost0_S);
		float possession_percentage0_f =Float.parseFloat(possession_percentage0_S);
		
		float total_scoring_att1_f = Float.parseFloat(total_scoring_att1_S);
		float attempts_conceded_ibox1_f = Float.parseFloat(attempts_conceded_ibox1_S);
		float won_corners1_f = Float.parseFloat(won_corners1_S);
		float fk_foul_lost1_f = Float.parseFloat(fk_foul_lost1_S);
		float possession_percentage1_f = Float.parseFloat(possession_percentage1_S);
		
		total_scoring_att0_f = total_scoring_att0_f/(total_scoring_att0_f+total_scoring_att1_f);
		attempts_conceded_ibox0_f = attempts_conceded_ibox0_f/(attempts_conceded_ibox0_f+attempts_conceded_ibox1_f);
		won_corners0_f = won_corners0_f/(won_corners0_f+won_corners1_f);
		fk_foul_lost0_f = fk_foul_lost0_f/(fk_foul_lost0_f+fk_foul_lost1_f);
		possession_percentage0_f = possession_percentage0_f/(possession_percentage0_f+possession_percentage1_f);
		
		total_scoring_att1_f = total_scoring_att1_f/(total_scoring_att0_f+total_scoring_att1_f);
		attempts_conceded_ibox1_f = attempts_conceded_ibox1_f/(attempts_conceded_ibox0_f+attempts_conceded_ibox1_f);
		won_corners1_f = won_corners1_f/(won_corners0_f+won_corners1_f);
		fk_foul_lost1_f = fk_foul_lost1_f/(fk_foul_lost0_f+fk_foul_lost1_f);
		possession_percentage1_f = possession_percentage1_f/(possession_percentage0_f+possession_percentage1_f);
		
		total_scoring_att0_f = (total_scoring_att0_f>0.1) ? total_scoring_att0_f : 0.1f;
		attempts_conceded_ibox0_f = (attempts_conceded_ibox0_f>0.1) ? attempts_conceded_ibox0_f : 0.1f;
		won_corners0_f = (won_corners0_f>0.1) ? won_corners0_f : 0.1f;
		fk_foul_lost0_f = (fk_foul_lost0_f>0.1) ? fk_foul_lost0_f : 0.1f;
		possession_percentage0_f = (possession_percentage0_f>0.1) ? possession_percentage0_f : 0.1f;
		
		total_scoring_att1_f = (total_scoring_att1_f>0.1) ? total_scoring_att1_f : 0.1f;
		attempts_conceded_ibox1_f = (attempts_conceded_ibox1_f>0.1) ? attempts_conceded_ibox1_f : 0.1f;
		won_corners1_f = (won_corners1_f>0.1) ? won_corners1_f : 0.1f;
		fk_foul_lost1_f = (fk_foul_lost1_f>0.1) ? fk_foul_lost1_f : 0.1f;
		possession_percentage1_f = (possession_percentage1_f>0.1) ? possession_percentage1_f : 0.1f;
		
		total_scoring_att0.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, total_scoring_att0_f));
		attempts_conceded_ibox0.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, attempts_conceded_ibox0_f));
		won_corners0.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, won_corners0_f));
		fk_foul_lost0.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, fk_foul_lost0_f));
		possession_percentage0.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, possession_percentage0_f));
		
		total_scoring_att1.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, total_scoring_att1_f));
		attempts_conceded_ibox1.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, attempts_conceded_ibox1_f));
		won_corners1.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, won_corners1_f));
		fk_foul_lost1.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, fk_foul_lost1_f));
		possession_percentage1.setLayoutParams(new LinearLayout.LayoutParams(0, TextH, possession_percentage1_f));
	}
}
