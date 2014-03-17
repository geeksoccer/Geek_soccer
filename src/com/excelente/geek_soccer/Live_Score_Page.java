package com.excelente.geek_soccer;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Live_Score_Page extends Fragment implements
		TabHost.OnTabChangeListener {

	Context mContext;
	View myView;
	private static ControllParameter data = ControllParameter.getInstance();
	TabHost tabHost;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * if (container == null) { return null; } return (LinearLayout)
		 * inflater.inflate(R.layout.live_score_page, container, false);
		 */
		if (container == null) {
			return null;
		}
		return (TabHost) inflater.inflate(R.layout.live_score_page, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myView = getView();
		mContext = myView.getContext();
		data.fragement_Section_set(1);
		Log.d("TEST", "fragement_Section_set::"+data.fragement_Section_get());
		/*
		 * Button Next_Date = (Button) myView.findViewById(R.id.Next); Button
		 * Previous_Date = (Button) myView.findViewById(R.id.Previous);
		 * Next_Date.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { } });
		 * Previous_Date.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { } });
		 */
		
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(
				getActivity(), false);
		mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost = (TabHost) myView.findViewById(android.R.id.tabhost);
		tabHost.setup(mLocalActivityManager);

		Intent YesterdayIntent = new Intent().setClass(getActivity(),
				LiveScore_Yesterday.class);
		TabSpec Featured = tabHost
				.newTabSpec("y")
				.setIndicator("YESTERDAY")
				.setContent(YesterdayIntent);

		Intent TodayIntent = new Intent().setClass(getActivity(),
				LiveScore_Today.class);
		TabSpec browse = tabHost
				.newTabSpec("c")
				.setIndicator("TODAY")
				.setContent(TodayIntent);

		Intent TomorrowIntent = new Intent().setClass(getActivity(),
				LiveScore_Tomorrow.class);
		TabSpec chart = tabHost
				.newTabSpec("t")
				.setIndicator("TOMORROW")
				.setContent(TomorrowIntent);

		tabHost.addTab(Featured);
		tabHost.addTab(browse);
		tabHost.addTab(chart);

		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.color.tran);
		}
		if(data.Match_list_t.size()==0){
			tabHost.setCurrentTab(2);
		}
		
		tabHost.setCurrentTab(data.liveScore_Cur);
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
				.setBackgroundResource(R.color.silver);

		tabHost.setOnTabChangedListener(this);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		int pos = this.tabHost.getCurrentTab();
		data.liveScore_Cur = pos;
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			if (i == pos) {
				tabHost.getTabWidget().getChildAt(i)
						.setBackgroundResource(R.color.silver);
			} else {
				tabHost.getTabWidget().getChildAt(i)
						.setBackgroundResource(R.color.tran);
			}

		}
	}

}
