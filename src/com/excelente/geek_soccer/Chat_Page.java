package com.excelente.geek_soccer;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Chat_Page extends Fragment implements TabHost.OnTabChangeListener {
	
	Context mContext;
	View myView;
	int teamID = MemberSession.getMember().getTeamId();
	TabHost tabHost;
	private static ControllParameter data = ControllParameter.getInstance();
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		if (container == null) {
			return null;
		}
		return (LinearLayout) inflater.inflate(R.layout.live_score_page,
				container, false);
				*/
		if (container == null) {
            return null;
        }
        return (TabHost)inflater.inflate(R.layout.live_score_page, container, false);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myView = getView();
		mContext = myView.getContext();
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(getActivity(), false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost = (TabHost)myView.findViewById(android.R.id.tabhost);
        tabHost.setup(mLocalActivityManager);
        
        Intent FeaturedIntent = new Intent().setClass(myView.getContext(), Chat_Team.class);
        
        TabSpec Featured = tabHost
		  .newTabSpec("Team")
		  .setContent(FeaturedIntent);
        if(teamID == 1){
        	Featured.setIndicator("Arsenal Room");
		}else if(teamID == 2){
			Featured.setIndicator("Chelsea Room");
		}else if(teamID == 3){
			Featured.setIndicator("liverpool Room");
		}else if(teamID == 4){
			Featured.setIndicator("Manchester United Room");
		}        
        
        
        Intent browseIntent = new Intent().setClass(myView.getContext(), Chat_All.class);
        TabSpec browse = tabHost
      		  .newTabSpec("All")
      		  .setIndicator("Global Room")
      		  .setContent(browseIntent);
        
        tabHost.addTab(Featured);
        tabHost.addTab(browse);
        
        for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++){
        	tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.tran);
        }
        
        tabHost.setCurrentTab(data.chat_Cur);
    	tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.color.silver);
    	
    	
    	tabHost.setOnTabChangedListener(this);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		int pos = this.tabHost.getCurrentTab();
		data.chat_Cur = pos;
		 for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++){
	        	if(i==pos){
	        		tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.silver);
	        	}else{
	        		tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.tran);
	        	}
	        	
	        }
	}
	
}
