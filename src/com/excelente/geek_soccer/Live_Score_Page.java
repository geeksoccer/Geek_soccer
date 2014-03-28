package com.excelente.geek_soccer;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
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

		Intent YesterdayIntent = new Intent().setClass(getActivity(), LiveScore_Yesterday.class);
		setupTab(YesterdayIntent, "y", "YESTERDAY", 0, false);

		Intent TodayIntent = new Intent().setClass(getActivity(), LiveScore_Today.class);
		setupTab(TodayIntent, "c", "TODAY", 0, true);

		Intent TomorrowIntent = new Intent().setClass(getActivity(), LiveScore_Tomorrow.class);
		setupTab(TomorrowIntent, "t", "TOMORROW", 0, false);

		if(data.Match_list_t_JSON.size()==0){
			tabHost.setCurrentTab(2);
		}
		
		tabHost.setCurrentTab(data.liveScore_Cur);

		tabHost.setOnTabChangedListener(this);
	}
	 
	private void setupTab(Intent intent, String name, String label, Integer iconId, boolean selected) {

	    View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    text.setTypeface(null,Typeface.BOLD);
	    if(label.equals("")){
	    	text.setVisibility(View.GONE);
	    	
	    	final float scale = getActivity().getResources().getDisplayMetrics().density;
	    	int pixels = (int) (40 * scale + 0.5f);
	    	image.getLayoutParams().width=pixels;
	    	image.getLayoutParams().height=pixels;
	    }
	    
	    if(iconId ==0){
	    	image.setVisibility(View.GONE);
	    	
	    	final float scale = getActivity().getResources().getDisplayMetrics().density;
	    	int pixels = (int) (40 * scale + 0.5f);
	    	text.getLayoutParams().height=pixels;
	    }
	    
	    View viewSelected = tab.findViewById(R.id.selected);
	    if(selected)
	    	viewSelected.setVisibility(View.VISIBLE);
	    
	    if(iconId != null){
	        image.setImageResource(iconId);
	    }
	    text.setText(label); 

	    TabSpec spec = tabHost.newTabSpec(name).setIndicator(tab).setContent(intent);
	    tabHost.addTab(spec);

	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		int pos = this.tabHost.getCurrentTab();
		data.liveScore_Cur = pos;
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			if (i == pos) {
				tabHost.getTabWidget().getChildAt(i).findViewById(R.id.selected).setVisibility(View.VISIBLE);
			} else {
				tabHost.getTabWidget().getChildAt(i).findViewById(R.id.selected).setVisibility(View.INVISIBLE);
			}

		}
	}

}
