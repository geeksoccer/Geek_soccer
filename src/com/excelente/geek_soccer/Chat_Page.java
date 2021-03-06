package com.excelente.geek_soccer;

import com.excelente.geek_soccer.utils.ThemeUtils;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class Chat_Page extends Fragment implements TabHost.OnTabChangeListener {
	
	Context mContext;
	View myView;
	int teamID;
	TabHost tabHost;
	private static ControllParameter data;
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
        return (TabHost)inflater.inflate(R.layout.chat_page, container, false);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		data = ControllParameter.getInstance(getActivity());
		teamID = SessionManager.getMember(getActivity()).getTeamId();
		
		myView = getView();
		mContext = myView.getContext();
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(getActivity(), false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost = (TabHost)myView.findViewById(android.R.id.tabhost);
        tabHost.setup(mLocalActivityManager);

        if(teamID!=5){
        	Intent FeaturedIntent = new Intent().setClass(myView.getContext(), Chat_Team.class);
            String teamName="";
            if(teamID == 1){
            	teamName = getResources().getString(R.string.chat_arsenal);
    		}else if(teamID == 2){
    			teamName = getResources().getString(R.string.chat_chelsea);
    		}else if(teamID == 3){
    			teamName = getResources().getString(R.string.chat_liverpool);
    		}else if(teamID == 4){
    			teamName = getResources().getString(R.string.chat_manu);
    		}        
            setupTab(FeaturedIntent, "Team", teamName, 0, true);
        }

        Intent browseIntent = new Intent().setClass(myView.getContext(), Chat_All.class);
        setupTab(browseIntent, "All", getResources().getString(R.string.chat_global), 0, false);
        
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).findViewById(R.id.selected).setVisibility(View.INVISIBLE);
		}
		tabHost.setCurrentTab(data.chat_Cur);
		
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewById(R.id.selected).setVisibility(View.VISIBLE);
        
    	tabHost.setOnTabChangedListener(this);
	}
	
	private void setupTab(Intent intent, String name, String label, Integer iconId, boolean selected) {

	    View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    View viewLine = tab.findViewById(R.id.view_line);
	    View viewSelected = tab.findViewById(R.id.selected);
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
		data.chat_Cur = pos;
		 for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++){
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
		ControllParameter.ProFileCache = SessionManager.getImageSession(mContext, SessionManager.getMember(mContext).getPhoto());
		if(data.imageAdapterChatTeam!=null){
			data.imageAdapterChatTeam.notifyDataSetChanged();
		}
		
		if(data.imageAdapterChatAll!=null){
			data.imageAdapterChatAll.notifyDataSetChanged();
		}
	}
}
