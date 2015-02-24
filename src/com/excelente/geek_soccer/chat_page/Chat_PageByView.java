package com.excelente.geek_soccer.chat_page;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.view.TabView;
import com.excelente.geek_soccer.view.TabView.OnTabChangedListener;

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

public class Chat_PageByView extends Fragment implements OnTabChangedListener{
	Context mContext;
	View myView;
	int teamID;

	private static ControllParameter data;

	LinearLayout chatMenu;
	View chatTeamV;
	View chatAllV;
	
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
		return inflater.inflate(R.layout.chat_page_byview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		data = ControllParameter.getInstance(getActivity());
		teamID = SessionManager.getMember(getActivity()).getTeamId();

		myView = getView();
		mContext = myView.getContext();

		LinearLayout ChatContainV = (LinearLayout) myView
				.findViewById(R.id.ContainV);

		LayoutParams childParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		chatTeamV = new Chat_TeamView().getView(getActivity());
		chatAllV = new Chat_AllView().getView(getActivity());

		ChatContainV.addView(chatTeamV, childParam);
		ChatContainV.addView(chatAllV, childParam);
		
		initView();
	}
	
	private void initView() {
		tableView = getView();
		
		tabs = (TabView)tableView.findViewById(R.id.tab);
		
		if (teamID > 0) {
			String teamName = "";
			if (teamID == 1) {
				teamName = getResources().getString(R.string.chat_arsenal);
			} else if (teamID == 2) {
				teamName = getResources().getString(R.string.chat_chelsea);
			} else if (teamID == 3) {
				teamName = getResources().getString(R.string.chat_liverpool);
			} else if (teamID == 4) {
				teamName = getResources().getString(R.string.chat_manu);
			}
			tabs.addTab(teamName);
		}
		tabs.addTab(getResources().getString(R.string.chat_global));
		
		tabs.setCurrentTab(data.chat_Cur); 
		tabs.setOnTabChangedListener(this);
	}
	
	public void onTabChanged(int position) {
		if(teamID>0){
			if (position == 0) {
				chatTeamV.setVisibility(RelativeLayout.ABOVE);
				chatAllV.setVisibility(RelativeLayout.GONE);
			} else {
				chatAllV.setVisibility(RelativeLayout.ABOVE);
				chatTeamV.setVisibility(RelativeLayout.GONE);
			}
		}else{
			chatTeamV.setVisibility(RelativeLayout.GONE);
			chatAllV.setVisibility(RelativeLayout.ABOVE);
		}
	}
}
