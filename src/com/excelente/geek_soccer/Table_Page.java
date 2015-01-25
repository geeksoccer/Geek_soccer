package com.excelente.geek_soccer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.MainActivity.OnSelectPageListener;
import com.excelente.geek_soccer.adapter.TablePagerAdapter;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.PageView;
import com.excelente.geek_soccer.view.TabView;
import com.excelente.geek_soccer.view.TabView.OnTabChangedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Table_Page extends Fragment implements OnTabChangedListener{
	
	public static final String TABLE_TYPE_ALL = "All";
	
	public static final String PREMIER_LEAGUE = "Premier League";
	public static final String LALIGA = "Laliga";
	public static final String CALCAIO_SERIE_A = "Calcaio Serie A";
	public static final String LEAGUE_DE_LEAGUE1 = "League De Ligue1";
	public static final String BUNDESLIGA = "Bundesliga";
	public static final String THAI_PREMIER_LEAGUE = "Thai Premier League";
	
	View tableView;

	private TabView tabs;

	private PageView viewpager;

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
		
		tabs = (TabView)tableView.findViewById(R.id.tab);
		
		/*setupTab(R.id.content, "0", "พรีเมียร์ลีก", R.drawable.logo_premier_league, true);
		setupTab(R.id.content, "1", "บุนเดสลิก้า", R.drawable.logo_bundesliga, false);
		setupTab(R.id.content, "2", "ลาลีก้า", R.drawable.logo_laliga, false);
		setupTab(R.id.content, "3", "กัลโช่", R.drawable.logo_calcio, false);
		setupTab(R.id.content, "4", "ลีกเดอ", R.drawable.logo_ligue1, false);
		setupTab(R.id.content, "5", "ไทยแลนด์ พรีเมียร์ลีก", R.drawable.logo_tpl, false);*/
		
		tabs.addTab(R.id.content, 0, "พรีเมียร์ลีก", true);
		tabs.addTab(R.id.content, 1, "บุนเดสลิก้า", false);
		tabs.addTab(R.id.content, 2, "ลาลีก้า", false);
		tabs.addTab(R.id.content, 3, "กัลโช่ซีรีเอ", false);
		tabs.addTab(R.id.content, 4, "ลีกเดอ", false);
		tabs.addTab(R.id.content, 5, "ไทยพรีเมียร์ลีก", false);
		
		tabs.setCurrentTab(0); 
		tabs.setOnTabChangedListener(this);
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
	public void onTabChanged(int position) {
		viewpager.setSelection(position);
	}
	
}
