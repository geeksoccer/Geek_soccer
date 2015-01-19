package com.excelente.geek_soccer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.MainActivity.OnSelectPageListener;
import com.excelente.geek_soccer.adapter.TablePagerAdapter;
import com.excelente.geek_soccer.adapter.TablePagerAdapter.OnLoadDataListener;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.PageView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class Table_Page extends Fragment implements OnTabChangeListener{
	
	public static final String TABLE_TYPE_ALL = "All";
	
	public static final String PREMIER_LEAGUE = "Premier League";
	public static final String LALIGA = "Laliga";
	public static final String CALCAIO_SERIE_A = "Calcaio Serie A";
	public static final String LEAGUE_DE_LEAGUE1 = "League De Ligue1";
	public static final String BUNDESLIGA = "Bundesliga";
	public static final String THAI_PREMIER_LEAGUE = "Thai Premier League";
	
	View tableView;

	private TabHost tabs;

	private PageView viewpager;

	private HorizontalScrollView scrollTab;

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
        	((MainActivity) getActivity()).setOnSelectPageListener(new OnSelectPageListener() {
    			
    			@Override
    			public void onSelect(int position) {
    				Log.e("onSelect", "Table_page onSelect: " + position);
    				if(position == 3){
    					initSubView();
    				}
    			}
    		});
        }
	}

	private void initView() {
		tableView = getView();
		
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
		
		scrollTab = (HorizontalScrollView) tableView.findViewById(R.id.horizoltal_scroll_tab);
		scrollTab.setSmoothScrollingEnabled(true);
		
		View viewLine = tableView.findViewById(R.id.line);
		ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewLine);
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
	
	protected void initSubView() {
		if(viewpager!=null && viewpager.hasAdapter()){
			return;
		}
		
		viewpager = (PageView) tableView.findViewById(R.id.table_viewpager);
		viewpager.setVisibility(View.VISIBLE);
		
		try {
			TablePagerAdapter tablePagerAdapter = new TablePagerAdapter(getActivity(), getTablePagerModelList());
			viewpager.setAdapter(tablePagerAdapter);
			tablePagerAdapter.setOnLoadDataListener(new TablePagerAdapter.OnLoadDataListener() {
				
				@Override
				public void onLoaded(int position) {
					viewpager.setVisibility(View.VISIBLE);
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	private List<TabModel> getTablePagerModelList() throws UnsupportedEncodingException {
		String[] league = {PREMIER_LEAGUE, BUNDESLIGA, LALIGA, CALCAIO_SERIE_A, LEAGUE_DE_LEAGUE1, THAI_PREMIER_LEAGUE};
		List<TabModel> tablePagerModelList = new ArrayList<TabModel>();
		for (String l : league) {
			TabModel tablePagerModel = new TabModel();
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
		int position = Integer.valueOf(tabId);
		viewpager.setSelection(position);
		setSelectedTab(position);
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
	
}
