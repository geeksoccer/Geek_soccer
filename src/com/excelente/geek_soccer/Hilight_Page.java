package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.adapter.HilightPagerAdapter;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.PageView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;


public class Hilight_Page extends Fragment implements OnTabChangeListener, OnClickListener{

	public static final String HILIGHT_ITEM_INDEX = "HILIGHT_ITEM_INDEX"; 
	public static final String HILIGHT_TAG = "HILIGHT_TAG"; 
	
	public static final String HILIGHT_TYPE_ALL = "All";
	public static final String HILIGHT_INDEX = "HILIGHT_INDEX";
	
	View hilightPage;
	
	public static List<String> stackTagLoading;

	private TabHost tabs;

	private HorizontalScrollView scrollTab;
	private HilightPagerAdapter hilightPagerAdapter;
	private PageView viewpager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
            return null;
        }
        
        return inflater.inflate(R.layout.hilight_page, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getView() != null){
			initView();
			initSubView();
		}
	}

	private void initView() { 
		
		hilightPage = getView();  
		
		stackTagLoading = new ArrayList<String>();
		
		tabs = (TabHost)hilightPage.findViewById(R.id.tabhost); 
		tabs.setup(); 
        
		setupTab(R.id.content, "0", "", R.drawable.news_new, true);
		setupTab(R.id.content, "1", "", R.drawable.logo_premier_league, false);
		setupTab(R.id.content, "2", "", R.drawable.logo_bundesliga, false);
		setupTab(R.id.content, "3", "", R.drawable.logo_laliga, false);
		setupTab(R.id.content, "4", "", R.drawable.logo_calcio, false);
		setupTab(R.id.content, "5", "", R.drawable.logo_ligue1, false);
		setupTab(R.id.content, "6", "", R.drawable.logo_ucl, false);
		setupTab(R.id.content, "7", "", R.drawable.logo_europa_league, false);
		setupTab(R.id.content, "8", "", R.drawable.logo_championschip, false);
		setupTab(R.id.content, "9", "", R.drawable.logo_capital_one_cup, false);
		
		tabs.setCurrentTab(0);
		tabs.setOnTabChangedListener(this);
		
		scrollTab = (HorizontalScrollView) hilightPage.findViewById(R.id.scroll_tab);
		scrollTab.setSmoothScrollingEnabled(true);
	}
	
	private void setupTab(Integer layoutId, String name, String label, Integer iconId, boolean selected) {

	    View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    View viewSelected = tab.findViewById(R.id.selected);
	    View viewLine = tab.findViewById(R.id.view_line);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewSelected);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewLine);
	    
	    if(label.equals("")){
	    	text.setVisibility(View.GONE);
	    	
	    	final float scale = getActivity().getResources().getDisplayMetrics().density;
	    	int pixels = (int) (40 * scale + 0.5f);
	    	image.getLayoutParams().width=pixels;
	    	image.getLayoutParams().height=pixels;
	    }
	    
	    if(selected)
	    	viewSelected.setVisibility(View.VISIBLE);
	    
	    if(iconId != null){
	        image.setImageResource(iconId);
	    }
	    text.setText(label);

	    TabSpec spec = tabs.newTabSpec(name).setIndicator(tab).setContent(layoutId);
	    tabs.addTab(spec);

	}
	
	private void initSubView() { 
		hilightPagerAdapter = new HilightPagerAdapter(getActivity(), getTabModelList());
		viewpager = (PageView) hilightPage.findViewById(R.id.news_viewpager);
		viewpager.setAdapter(hilightPagerAdapter);
	}
	
	private List<TabModel> getTabModelList() {
		List<String> urls = new ArrayList<String>();
		urls.add(HILIGHT_TYPE_ALL);
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_PREMIER_LEAGUE));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_BUNDESLIGA));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_LALIGA));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_CALCAIO_SERIE_A));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_LEAGUE_DE_LEAGUE1));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_UCL));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_UPL));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_CHAMPIAN_CHIP));
		urls.add("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_CAPITAL_ONE_CUP));
		
		List<TabModel> tabList = new ArrayList<TabModel>();
		
		for (int i = 0; i < urls.size(); i++) {
			TabModel tabModel = new TabModel();
			tabModel.setIndex(i);
			tabModel.setUrl(urls.get(i));
			tabList.add(tabModel);
		}
		
		return tabList;
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
	            focusAndScrollView(v, index);
			}else{
				v.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void focusAndScrollView(View v, int index) {
		int width = v.getWidth();
		int scollX = 0;
		if(index > 2){
        	scollX = (width/2) * (index+1);
		}
        scrollTab.smoothScrollTo(scollX, 0);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(hilightPagerAdapter!=null){
			hilightPagerAdapter.notifyDataSetChanged();
		}
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
