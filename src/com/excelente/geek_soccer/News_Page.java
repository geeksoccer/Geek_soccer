package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.adapter.NewsPagerAdapter;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.PageView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class News_Page extends Fragment implements OnTabChangeListener, OnClickListener{
	
	interface OnNewsLoadedListener{
		public void onNewsLoaded();
	}
	
	public static final String ITEM_INDEX = "NEWS_MODEL";
	public static final String NEWS_LIST_MODEL = "NEWS_LIST_MODEL";
	public static final String NEWS_POSITION = "NEWS_POSITION";
	
	View newsPage;
	
	private TabHost tabs;
	private TabWidget tabWidget;
	private PageView viewpager;
	private NewsPagerAdapter newsPagerAdapter;
	//private HorizontalScrollView scrollTab;
	
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
			tabWidget.setVisibility(View.GONE);
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
		
		tabs = (TabHost)newsPage.findViewById(R.id.tabhost); 
		tabs.setup();
		
		setupTab(R.id.content,  "0", getResources().getString(R.string.team_news), R.drawable.news_likes_selected, true);
		setupTab(R.id.content,  "1", getResources().getString(R.string.global_news), R.drawable.world, false);
		
		tabs.setCurrentTab(0); 
		tabs.setOnTabChangedListener(this);
		
		tabWidget = (TabWidget) newsPage.findViewById(android.R.id.tabs); 
		
		//scrollTab = (HorizontalScrollView) newsPage.findViewById(R.id.scroll_tab);
		//scrollTab.setSmoothScrollingEnabled(true);
	}
	
	private void setupTab(Integer layoutId, String name, String label, Integer iconId, boolean selected) {

	    View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    View viewSelected = tab.findViewById(R.id.selected);
	    View viewLine = tab.findViewById(R.id.view_line);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewSelected);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewLine);
	    
	    if(selected)
	    	viewSelected.setVisibility(View.VISIBLE);
	    
	    if(iconId != null){
	        image.setImageResource(iconId);
	    }
	    text.setText(label);

	    TabSpec spec = tabs.newTabSpec(name).setIndicator(tab).setContent(layoutId);
	    tabs.addTab(spec);

	}

	@Override
	public void onTabChanged(String tag) {
		int position = Integer.valueOf(tag);
		setSelectedTab(position);
		viewpager.setSelection(position);
	}
	
	public void setSelectedTab(int index){
		for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
			View v = tabs.getTabWidget().getChildAt(i).findViewById(R.id.selected);
			if(i == index){
				v.setVisibility(View.VISIBLE);
	            //focusAndScrollView(v, index);
			}else{
				v.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/*private void focusAndScrollView(View v, int index) {
		int width = v.getWidth();
		int scollX = 0;
		if(index > 2){
        	scollX = (width/2) * (index+1);
		}
        scrollTab.smoothScrollTo(scollX, 0);
	}*/
	
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
				onTabChanged(tabs.getCurrentTabTag());
				break;
		}
	}
	
}
