package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.adapter.NewsPagerAdapter;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.PageView;
import com.excelente.geek_soccer.view.TabView;
import com.excelente.geek_soccer.view.TabView.OnTabChangedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class News_Page extends Fragment implements OnTabChangedListener, OnClickListener{
	
	interface OnNewsLoadedListener{
		public void onNewsLoaded();
	}
	
	public static final String ITEM_INDEX = "NEWS_MODEL";
	public static final String NEWS_LIST_MODEL = "NEWS_LIST_MODEL";
	public static final String NEWS_POSITION = "NEWS_POSITION";
	
	View newsPage;
	
	private TabView tabs;
	private PageView viewpager;
	private NewsPagerAdapter newsPagerAdapter;
	
	public static String NEWS_TAG = "NEWS_TAG";
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        return inflater.inflate(R.layout.news_page, container, false);
    }
   
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null){
        	initView();
        	initSubview();
        }
	}

	private void initSubview() {
		
		if(SessionManager.getMember(getActivity()).getTeamId() == 0){
			tabs.setVisibility(View.GONE);
		}
		
		newsPagerAdapter = new NewsPagerAdapter(getActivity(), getTabModelList());
		viewpager = (PageView) newsPage.findViewById(R.id.news_viewpager);
		viewpager.setAdapter(newsPagerAdapter);
		
		if(getActivity().getIntent().getIntExtra(NewsModel.NEWS_ID+"tag", 0)==1 && SessionManager.getMember(getActivity()).getTeamId() != 0){
			tabs.setCurrentTab(1);
		}
	
	}

	private List<TabModel> getTabModelList() {
		List<String> urls = new ArrayList<String>();
		if(SessionManager.getMember(getActivity()).getTeamId() != 0){
			urls.add(String.valueOf(SessionManager.getMember(getActivity()).getTeamId()));
		}
		urls.add("0");
		
		List<TabModel> tabList = new ArrayList<TabModel>();
		
		for (int i = 0; i < urls.size(); i++) {
			TabModel tabModel = new TabModel();
			tabModel.setUrl(urls.get(i));
			tabModel.setIndex(i);
			tabList.add(tabModel);
		}
		
		return tabList;
	}

	private void initView() {
		newsPage = getView(); 
		
		tabs = (TabView)newsPage.findViewById(R.id.tab);
		
		//setupTab(R.id.content,  "0", getResources().getString(R.string.team_news), R.drawable.news_likes_selected, true);
		//setupTab(R.id.content,  "1", getResources().getString(R.string.global_news), R.drawable.world, false);
		
		tabs.addTab(R.id.content,  0, getResources().getString(R.string.team_news), true);
		tabs.addTab(R.id.content,  1, getResources().getString(R.string.global_news), false);
		
		tabs.setCurrentTab(0); 
		tabs.setOnTabChangedListener(this);
	}

	@Override
	public void onTabChanged(int position) {
		viewpager.setSelection(position);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(newsPagerAdapter!=null)
			newsPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.empty:
				v.setVisibility(View.GONE);
				onTabChanged(tabs.getCurrentTab());
				break;
		}
	}
	
}
