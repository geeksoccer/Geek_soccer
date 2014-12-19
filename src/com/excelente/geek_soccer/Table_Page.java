package com.excelente.geek_soccer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.adapter.TablePagerAdapter;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.model.TablePagerModel;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class Table_Page extends Fragment implements OnTabChangeListener, OnPageChangeListener, OnTouchListener{
	
	
	public static final String TABLE_TYPE_ALL = "All";
	
	public static final String PREMIER_LEAGUE = "Premier League";
	public static final String LALIGA = "Laliga";
	public static final String CALCAIO_SERIE_A = "Calcaio Serie A";
	public static final String LEAGUE_DE_LEAGUE1 = "League De Ligue1";
	public static final String BUNDESLIGA = "Bundesliga";
	public static final String THAI_PREMIER_LEAGUE = "Thai Premier League";
	
	View tableView;

	private TabHost tabs;

	private CustomViewPager viewpager;
	
	public static List<String> stackTagLoading;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if (container == null) {
            return null;
        }
    	
        return inflater.inflate(R.layout.table_page, container, false);
    }
   
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null){
        	initView();
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getView() != null){
			initSubView();
		}
	}

	private void initView() {
		tableView = getView();
		
		stackTagLoading = new ArrayList<String>();
		
		tabs = (TabHost)tableView.findViewById(R.id.tabhost); 
		tabs.setup();  
		
		setupTab(R.id.content, "0", "", R.drawable.logo_premier_league, true);
		setupTab(R.id.content, "1", "", R.drawable.logo_bundesliga, false);
		setupTab(R.id.content, "2", "", R.drawable.logo_laliga, false);
		setupTab(R.id.content, "3", "", R.drawable.logo_calcio, false);
		setupTab(R.id.content, "4", "", R.drawable.logo_ligue1, false);
		setupTab(R.id.content, "5", "", R.drawable.logo_tpl, false);
		
		tabs.setCurrentTab(0);
		
		tabs.setOnTabChangedListener(this);
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
		viewpager = (CustomViewPager)tableView.findViewById(R.id.viewpager);
		viewpager.setPagingEnabled(false);
		viewpager.setOnTouchListener(this);
		try {
			TablePagerAdapter tablePagerAdapter = new TablePagerAdapter(getActivity(), getTablePagerModelList());
			viewpager.setAdapter(tablePagerAdapter);
			viewpager.setOnPageChangeListener(this);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	} 
	
	private List<TablePagerModel> getTablePagerModelList() throws UnsupportedEncodingException {
		String[] league = {PREMIER_LEAGUE, LALIGA, BUNDESLIGA, CALCAIO_SERIE_A, LEAGUE_DE_LEAGUE1, THAI_PREMIER_LEAGUE};
		List<TablePagerModel> tablePagerModelList = new ArrayList<TablePagerModel>();
		for (String l : league) {
			TablePagerModel tablePagerModel = new TablePagerModel();
			tablePagerModel.setUrl(getTableUrl(l, TABLE_TYPE_ALL));
			tablePagerModelList.add(tablePagerModel);
		}
		return tablePagerModelList;
	}

	private String getTableUrl(String league, String type) throws UnsupportedEncodingException {
		MemberModel member = SessionManager.getMember(getActivity());
		String url = ControllParameter.TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + URLEncoder.encode(league, "utf-8") + "&" + TableModel.TABLE_TYPE + "=" + type + "&m_uid=" + member.getUid() + "&m_token=" + member.getToken();
		return url;
	}

	@Override
	public void onTabChanged(String tabId) {
		setSelectedTab(Integer.valueOf(tabId));
		viewpager.setCurrentItem(Integer.valueOf(tabId), false);
	}
	 
	public void setSelectedTab(int index){
		for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
			View v = tabs.getTabWidget().getChildAt(i).findViewById(R.id.selected);
			if(i == index){
				v.setVisibility(View.VISIBLE);
			}else{
				v.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		setSelectedTab(Integer.valueOf(position));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.viewpager:{
				if(event.getAction() == MotionEvent.ACTION_MOVE && v instanceof ViewGroup) {
					((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
				}
				break;
			}
		}
		return false;
	}
	
}
