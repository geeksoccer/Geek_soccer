package com.excelente.geek_soccer.live_score_page;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.view.TabView;
import com.excelente.geek_soccer.view.TabView.OnTabChangedListener;

public class Live_Score_PageByView extends Fragment implements OnTabChangedListener{
	Context mContext;
	View myView;
	LinearLayout ChatContainV;
	LayoutParams childParam;
	View LiveYesterdayView;
	View LiveTodayView;
	View LiveTomorrowView;
	
	View tableView;

	private TabView tabs;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		return inflater.inflate(R.layout.livescore_page_byview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		myView = getView();
		mContext = myView.getContext();
		
		ChatContainV = (LinearLayout) myView
				.findViewById(R.id.ContainV);

		childParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		LiveTodayView = new LiveScore_TodayView().getView(getActivity());
		ChatContainV.addView(LiveTodayView, childParam);
		initView();
	}
	
	private void initView() {
		tableView = getView();
		
		tabs = (TabView)tableView.findViewById(R.id.tab);
		
		tabs.addTab(getResources().getString(R.string.str_yesterday_livescore));
		tabs.addTab(getResources().getString(R.string.str_today_livescore));
		tabs.addTab(getResources().getString(R.string.str_tomorrow_livescore));
		
		tabs.setCurrentTab(1); 
		tabs.setOnTabChangedListener(this);
	}
	
	public void onTabChanged(int position) {
		if (position == 0) {
			if(LiveYesterdayView!=null){
				LiveYesterdayView.setVisibility(RelativeLayout.ABOVE);
			}else{
				LiveYesterdayView = new LiveScore_YesterdayView().getView(getActivity());
				ChatContainV.addView(LiveYesterdayView, childParam);
			}
			LiveTodayView.setVisibility(RelativeLayout.GONE);
			if(LiveTomorrowView!=null){
				LiveTomorrowView.setVisibility(RelativeLayout.GONE);
			}
		} else if (position == 1) {
			if(LiveYesterdayView!=null){
				LiveYesterdayView.setVisibility(RelativeLayout.GONE);
			}
			LiveTodayView.setVisibility(RelativeLayout.ABOVE);
			if(LiveTomorrowView!=null){
				LiveTomorrowView.setVisibility(RelativeLayout.GONE);
			}
		}else {
			if(LiveYesterdayView!=null){
				LiveYesterdayView.setVisibility(RelativeLayout.GONE);
			}
			LiveTodayView.setVisibility(RelativeLayout.GONE);
			if(LiveTomorrowView!=null){
				LiveTomorrowView.setVisibility(RelativeLayout.ABOVE);
			}else{
				LiveTomorrowView = new LiveScore_TomorrowView().getView(getActivity());
				ChatContainV.addView(LiveTomorrowView, childParam);
			}
		}
	}
}
