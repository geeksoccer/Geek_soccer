package com.excelente.geek_soccer;

import com.excelente.geek_soccer.adapter.FixturesAdapter;
import com.excelente.geek_soccer.model.FixturesGroupList;
import com.excelente.geek_soccer.model.FixturesModel;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Fixtures_Page extends Activity implements OnClickListener{

	SparseArray<FixturesGroupList> groups = new SparseArray<FixturesGroupList>();
	
	private LinearLayout upBtn;
	private ExpandableListView groupListview;
	private TextView titleBar;

	private ImageView refeshBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this).getTeamId());
		
		initView();
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
	}

	private void initView() {
		setContentView(R.layout.fixtures_page);
		
		upBtn = (LinearLayout) findViewById(R.id.Up_btn);
		titleBar = (TextView) findViewById(R.id.Title_bar);
		refeshBtn = (ImageView) findViewById(R.id.refesh_fixtures);
		groupListview = (ExpandableListView) findViewById(R.id.group_listView);
		
		upBtn.setOnClickListener(this);
		refeshBtn.setOnClickListener(this);
		 
		createData();
		FixturesAdapter fixturesAdapter = new FixturesAdapter(this, groups);
		groupListview.setAdapter(fixturesAdapter);
	}
	
	public void createData() {
	    for (int j = 0; j < 5; j++) {
	      FixturesGroupList group = new FixturesGroupList("Test " + j);
	      for (int i = 0; i < 5; i++) {
	    	  FixturesModel fixtures = new FixturesModel();
	    	  fixtures.setAwayImg("http://secure.cache.images.core.optasports.com/soccer/teams/150x150/702.png");
	    	  fixtures.setAwayName("Queens Park Rangers");
	    	  fixtures.setCredit("http://www.goal.com");
	    	  fixtures.setHomeImg("http://secure.cache.images.core.optasports.com/soccer/teams/150x150/661.png");
	    	  fixtures.setHomeName("Chelsea");
	    	  fixtures.setId("1703679"); 
	    	  fixtures.setLink("/en/match/chelsea-vs-queens-park-rangers/1703679");
	    	  fixtures.setMatchDate("2014-11-01");
	    	  fixtures.setMatchTime("22:00:00");
	    	  fixtures.setMatchType("Premier League");
	    	  fixtures.setScore(" 2 - 1 ");
	    	  group.children.add(fixtures);
	      }
	      groups.append(j, group);
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.Up_btn:{
				onBackPressed();
				break;
			}
			
			case R.id.refesh_fixtures:{
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
