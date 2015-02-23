package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.MainActivity.OnSelectPageListener;
import com.excelente.geek_soccer.adapter.HilightPagerAdapter;
import com.excelente.geek_soccer.adapter.HilightPagerAdapter.OnLoadDataListener;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.PageView;
import com.excelente.geek_soccer.view.TabView;
import com.excelente.geek_soccer.view.TabView.OnTabChangedListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;


public class Hilight_Page extends Fragment implements OnTabChangedListener, OnClickListener{

	public static final String HILIGHT_ITEM_INDEX = "HILIGHT_ITEM_INDEX"; 
	public static final String HILIGHT_TAG = "HILIGHT_TAG"; 
	
	public static final String HILIGHT_TYPE_ALL = "All";
	public static final String HILIGHT_INDEX = "HILIGHT_INDEX";
	
	View hilightPage;
	
	public static List<String> stackTagLoading;

	private TabView tabs;

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
			((MainActivity) getActivity()).setOnSelectPageListener(new OnSelectPageListener() {
				
				@Override
				public void onSelect(int position) {
					Log.e("onSelect", "Hilight_Page onSelect: " + position);
					if(position == 4){
						initSubView();
					}
				}
			});
		}
	}

	private void initView() { 
		
		hilightPage = getView();  
		
		stackTagLoading = new ArrayList<String>();
		
		tabs = (TabView)hilightPage.findViewById(R.id.tab);
		
		String[] titleTabs = getActivity().getResources().getStringArray(R.array.titles_tabs_hilight_page);
		for (int i = 0; i < titleTabs.length; i++) {
			if(SessionManager.getMember(getActivity()).getTeamId() != 0 && i==1){
				if(SessionManager.getLang(getActivity()).equalsIgnoreCase("en")){
					tabs.addTab(SessionManager.getMember(getActivity()).getTeam().getTeamName());
				}else{
					tabs.addTab(SessionManager.getMember(getActivity()).getTeam().getTeamNameTH());
				}
			}
			
			tabs.addTab(titleTabs[i]);
		}
		
		
		tabs.setOnTabChangedListener(this);
		tabs.setCurrentTab(0);
	}
	
	private void initSubView() {
		if(viewpager!=null && viewpager.hasAdapter()){
			return;
		}
		
		viewpager = (PageView) hilightPage.findViewById(R.id.news_viewpager);
		viewpager.setVisibility(View.VISIBLE);
		
		hilightPagerAdapter = new HilightPagerAdapter(getActivity(), getTabModelList());
		hilightPagerAdapter.setOnLoadDataListener(new OnLoadDataListener() {
			
			@Override
			public void onLoaded(int position) {
				viewpager.setVisibility(View.VISIBLE);
			}
		});
		
		viewpager.setAdapter(hilightPagerAdapter);
	}
	
	private List<TabModel> getTabModelList() {
		List<String> urls = new ArrayList<String>();
		urls.add(HILIGHT_TYPE_ALL);
		if(SessionManager.getMember(getActivity()).getTeamId() != 0){
			urls.add(SessionManager.getMember(getActivity()).getTeam().getTeamNameTH());
		}
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
	public void onTabChanged(int position) {
		if(viewpager==null || !viewpager.hasAdapter()){
			return;
		}
		
		viewpager.setSelection(position);
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
				onTabChanged(tabs.getCurrentTab());
				break;
		}
	}
}
