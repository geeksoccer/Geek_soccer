package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import com.excelente.geek_soccer.adapter.SelectTeamAdapter;
import com.excelente.geek_soccer.model.LeagueModel;
import com.excelente.geek_soccer.model.TeamModel;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.Boast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class SelectTeamPage extends Activity implements OnChildClickListener, OnClickListener{

	private ExpandableListView expansListView;
	private TextView noTeam;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
	}

	private void initView() {
		setContentView(R.layout.select_team_page);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		noTeam = (TextView) findViewById(R.id.None_Select);
		noTeam.setOnClickListener(this);
		
		expansListView = (ExpandableListView) findViewById(R.id.expandableListView);
		
		if (NetworkUtils.isNetworkAvailable(this)){
			loadData();
		}else{
			Boast.makeText(this, NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
		}
		
	}

	private void loadData() {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams param = new RequestParams();
		param.put("time", new Date().getTime());
		client.get(ControllParameter.GET_SELECT_TEAM_URL, param,new JsonHttpResponseHandler(){
			@Override 
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				progressBar.setVisibility(View.GONE);
				updateExpandListView(response.toString());
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Boast.makeText(SelectTeamPage.this, NetworkUtils.getConnectivityStatusString(SelectTeamPage.this), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void updateExpandListView(String result) {
	
		try {
			JSONArray response = new JSONArray(result);
			List<LeagueModel> leagueList = new ArrayList<LeagueModel>();
			for (int i = 0; i < response.length(); i++) {
		
				LeagueModel league = new LeagueModel();
				league.setId(response.getJSONObject(i).getString("league_id"));
				league.setName(response.getJSONObject(i).getString("league_name"));
				league.setNameTH(response.getJSONObject(i).getString("league_name_th"));
				league.setImage(response.getJSONObject(i).getString("league_image"));
				league.setTeams(new ArrayList<TeamModel>());
				
				JSONArray teamList = response.getJSONObject(i).getJSONArray("team_list");
				if(teamList != null && teamList.length()>0){
					List<TeamModel> teams = new ArrayList<TeamModel>();
					
					for (int j = 0; j < teamList.length(); j++) {
						TeamModel team = new TeamModel();
						team.setTeamId(teamList.getJSONObject(j).getInt("team_id"));
						team.setTeamName(teamList.getJSONObject(j).getString("team_name"));
						team.setTeamNameTH(teamList.getJSONObject(j).getString("team_name_th"));
						team.setTeamShortName(teamList.getJSONObject(j).getString("team_short_name"));
						team.setTeamColor(teamList.getJSONObject(j).getString("team_color"));
						team.setTeamImage(teamList.getJSONObject(j).getString("team_image"));
						team.setTeamTextColor(teamList.getJSONObject(j).getString("team_text_color"));
						team.setTeamNameFind(teamList.getJSONObject(j).getString("team_name_find"));
						team.setTeamLeague(teamList.getJSONObject(j).getString("team_league"));
						team.setTeamPort(teamList.getJSONObject(j).getString("team_port"));
						teams.add(team);
					}
					
					league.setTeams(teams);
				}
				leagueList.add(league);
			}
			
			SelectTeamAdapter sta = new SelectTeamAdapter(SelectTeamPage.this, leagueList);
			expansListView.setAdapter(sta);
			expansListView.setOnChildClickListener(this);
			expandAll(expansListView, leagueList.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void expandAll(ExpandableListView expansListView, int size) {
		for (int i = 0; i < size; i++) {
			expansListView.expandGroup(i);
		}
	}

	@Override
	public void onBackPressed() {
		doNoneSelectTeam();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		TeamModel team = (TeamModel) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
		showConfirmDialog(team);
		return true;
	}
	
	protected void showConfirmDialog(final TeamModel team) {
		
		final Dialog confirmDialog = new Dialog(this);  
		
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null);
		RelativeLayout main_action_bar = (RelativeLayout) view.findViewById(R.id.main_action_bar);
		main_action_bar.setBackgroundColor(Color.parseColor(team.getTeamColor()));
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		title.setTextColor(Color.parseColor(team.getTeamTextColor()));
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		btComfirm.setBackgroundColor(Color.parseColor(team.getTeamColor()));
		TextView button_confirm_ok = (TextView) view.findViewById(R.id.button_confirm_ok);
		button_confirm_ok.setTextColor(Color.parseColor(team.getTeamTextColor()));
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
	
		String lang = SessionManager.getLang(getApplicationContext());
		if(lang.equals("en")){
			title.setText(team.getTeamName());
		}else{
			title.setText(team.getTeamNameTH());
		}
		
		Drawable img;
		if(!SessionManager.hasKey(this, team.getTeamImage())){
			img = this.getResources().getDrawable(R.drawable.ic_action_accept);
		}else{
			Bitmap bm = SessionManager.getImageSession(this, team.getTeamImage());
			img = new BitmapDrawable(getResources(), bm);
		}
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		if(team.getTeamId() == 0){
			question.setText(getResources().getString(R.string.question_select_team_other));
		}else{
			question.setText(getResources().getString(R.string.question_select_team));
		}
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doConfirmSelectTeam(team);
				confirmDialog.dismiss();
			}

		});
		
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}
	
	private void doConfirmSelectTeam(TeamModel team) {
		Intent intent=new Intent();  
        intent.putExtra("SELECT_TEAM", team); 
        setResult(Sign_In_Page.REQUEST_CODE_SELECT_TEAM, intent); 
        overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
        finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.None_Select:
				doNoneSelectTeam();
				break;
		}
	}

	private void doNoneSelectTeam() {
		TeamModel team = new TeamModel();
		team.setTeamId(0);
		team.setTeamNameTH(getResources().getString(R.string.no_favorite_team));
		team.setTeamTextColor(getResources().getString(R.color.white));
		team.setTeamImage("");
		team.setTeamColor(getResources().getString(R.color.news_default));
		showConfirmDialog(team);
	}
	
}
