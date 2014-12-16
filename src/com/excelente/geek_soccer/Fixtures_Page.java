package com.excelente.geek_soccer;

import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;

import com.excelente.geek_soccer.adapter.FixturesAdapter;
import com.excelente.geek_soccer.model.FixturesGroupLists;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.Boast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Fixtures_Page extends Activity implements OnClickListener{
	
	private LinearLayout upBtn;
	private ExpandableListView groupListview;
	private TextView fixturesSeason; 

	private ImageView refeshBtn;
	private ProgressBar fixturesProgressbar;
	private TextView fixturesEmpty;
	private RelativeLayout Header_Layout;
	private TextView Title_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
	}

	private void initView() {
		setContentView(R.layout.fixtures_page);
		
		Header_Layout = (RelativeLayout) findViewById(R.id.Header_Layout);
		Title_bar = (TextView) findViewById(R.id.Title_bar);
		
		upBtn = (LinearLayout) findViewById(R.id.Up_btn);
		fixturesSeason = (TextView) findViewById(R.id.fixtures_season);
		refeshBtn = (ImageView) findViewById(R.id.refesh_fixtures);
		groupListview = (ExpandableListView) findViewById(R.id.group_listView);
		fixturesProgressbar = (ProgressBar) findViewById(R.id.fixtures_progressbar);
		fixturesEmpty = (TextView) findViewById(R.id.fixtures_empty); 
		
		upBtn.setOnClickListener(this);
		refeshBtn.setOnClickListener(this);
		fixturesEmpty.setOnClickListener(this);
		
		groupListview.setSmoothScrollbarEnabled(true);
		
		createData();
		
		setThemeToview();
	}
	
	private void setThemeToview() {
		ThemeUtils.setThemeToView(getApplicationContext(), ThemeUtils.TYPE_BACKGROUND_COLOR, Header_Layout);
		ThemeUtils.setThemeToView(getApplicationContext(), ThemeUtils.TYPE_TEXT_COLOR, Title_bar);
	}

	public void createData() { 
		
		refeshBtn.setVisibility(View.GONE);
		fixturesEmpty.setVisibility(View.GONE);
		
		if (!NetworkUtils.isNetworkAvailable(this)){
			Boast.makeText(this, NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
			refeshBtn.setVisibility(View.VISIBLE);
			return;
		}
		
		String teamName = SessionManager.getMember(getApplicationContext()).getTeam().getTeamName();
		if(teamName == null || teamName.equals("")){
			refeshBtn.setVisibility(View.VISIBLE);
			return;
		}
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setMaxRetriesAndTimeout(2, 10000);
		
		RequestParams params = new RequestParams("_t", teamName);
		params.put("time", new Date().getTime());
		params.put("m_uid", SessionManager.getMember(getApplicationContext()).getUid());
		params.put("m_token", SessionManager.getMember(getApplicationContext()).getToken());
	
		
		client.get(ControllParameter.GET_FIXTURES_URL, params, new JsonHttpResponseHandler() {
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				fixturesProgressbar.setVisibility(View.VISIBLE);
				groupListview.setVisibility(View.INVISIBLE);
				fixturesEmpty.setVisibility(View.GONE);
				refeshBtn.setVisibility(View.GONE);
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				if(statusCode == 200){
					fixturesProgressbar.setVisibility(View.GONE);
					groupListview.setVisibility(View.VISIBLE);
					fixturesEmpty.setVisibility(View.GONE);
					
					FixturesGroupLists groups = new FixturesGroupLists(Fixtures_Page.this, response);
					FixturesAdapter fixturesAdapter = new FixturesAdapter(Fixtures_Page.this, groups.build().getFixturesGroupLists(), groups);
					//Parcelable state = groupListview.onSaveInstanceState();
					groupListview.setAdapter(fixturesAdapter);
					expandAll(groupListview, groups.getFixturesGroupLists().size());
					//groupListview.onRestoreInstanceState(state);
					
					if(groups.getIndexNextMatch() > -1){
						groupListview.expandGroup(groups.getIndexNextMatchGroup());
						groupListview.setSelectedGroup(groups.getIndexNextMatchGroup());
						groupListview.setSelectedChild(groups.getIndexNextMatchGroup(), groups.getIndexNextMatch(), true);
					}
					
					fixturesSeason.setText(groups.getFixturesSeason());
					
				}
				
				refeshBtn.setVisibility(View.VISIBLE);
			}

			private void expandAll(ExpandableListView groupListview, int lenght) {
				for (int i = 0; i < lenght; i++) {
					groupListview.expandGroup(i);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				fixturesProgressbar.setVisibility(View.GONE);
				groupListview.setVisibility(View.INVISIBLE);
				fixturesEmpty.setVisibility(View.VISIBLE);
				
				refeshBtn.setVisibility(View.VISIBLE);
				
				Boast.makeText(Fixtures_Page.this, NetworkUtils.getConnectivityStatusString(Fixtures_Page.this), Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.Up_btn:{
				onBackPressed();
				break;
			}
			
			case R.id.refesh_fixtures:{
				createData();
				break;
			}
			
			case R.id.fixtures_empty:{
				createData();
				break;
			}

		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		finish();
	}
}
