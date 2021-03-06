package com.excelente.geek_soccer;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.utils.ThemeUtils;

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
	private static ControllParameter data;
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
		
		data = ControllParameter.getInstance(getActivity());
		
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
		setupTab(YesterdayIntent, "y", getResources().getString(R.string.str_yesterday_livescore), 0, false);

		Intent TodayIntent = new Intent().setClass(getActivity(), LiveScore_Today.class);
		setupTab(TodayIntent, "c", getResources().getString(R.string.str_today_livescore), 0, true);

		Intent TomorrowIntent = new Intent().setClass(getActivity(), LiveScore_Tomorrow.class);
		setupTab(TomorrowIntent, "t", getResources().getString(R.string.str_tomorrow_livescore), 0, false);

		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).findViewById(R.id.selected).setVisibility(View.INVISIBLE);
		}
		
		if(data.Match_list_t_JSON.size()==0){
			tabHost.setCurrentTab(2);
		}
		tabHost.setCurrentTab(data.liveScore_Cur);
		
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewById(R.id.selected).setVisibility(View.VISIBLE);
		tabHost.setOnTabChangedListener(this);
	}
	 
	private void setupTab(Intent intent, String name, String label, Integer iconId, boolean selected) {

	    View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    View viewSelected = tab.findViewById(R.id.selected);
	    View viewLine = tab.findViewById(R.id.view_line);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewSelected);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewLine);
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
		Log.d("TEST", "data.liveScore_Cur::"+data.liveScore_Cur);
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			if (i == pos) {
				tabHost.getTabWidget().getChildAt(i).findViewById(R.id.selected).setVisibility(View.VISIBLE);
			} else {
				tabHost.getTabWidget().getChildAt(i).findViewById(R.id.selected).setVisibility(View.INVISIBLE);
			}

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(data.imageAdapterLiveScoreYesterday!=null){
			data.imageAdapterLiveScoreYesterday.notifyDataSetChanged();
		}
		if(data.imageAdapterLiveScoreToday!=null){
			data.imageAdapterLiveScoreToday.notifyDataSetChanged();
		}
		if(data.imageAdapterLiveScoreTomorrow!=null){
			data.imageAdapterLiveScoreTomorrow.notifyDataSetChanged();
		}
	}
}
