package com.excelente.geek_soccer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.adapter.HilightAdapter;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.Boast;
import com.excelente.geek_soccer.view.PullToRefreshListView;
import com.excelente.geek_soccer.view.PullToRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public class Hilight_Page extends Fragment implements OnItemClickListener, OnTabChangeListener, OnClickListener{

	public static final String HILIGHT_ITEM_INDEX = "HILIGHT_ITEM_INDEX"; 
	public static final String HILIGHT_TAG = "HILIGHT_TAG"; 
	
	public static final String HILIGHT_TYPE_ALL = "All";
	
	public static List<HilightModel> hilightListAll;
	public static List<HilightModel> hilightListPl; 
	public static List<HilightModel> hilightListBl;
	public static List<HilightModel> hilightListLl;
	public static List<HilightModel> hilightListGl;
	public static List<HilightModel> hilightListFl;
	public static List<HilightModel> hilightListUcl;
	public static List<HilightModel> hilightListUpl;
	public static List<HilightModel> hilightListChamp;
	public static List<HilightModel> hilightListCapital;
	
	View hilightPage;
	private ProgressBar hilightWaitProcessbar;
	private ProgressBar hilightLoadingFooterProcessbar;

	HilightModel oldHilight; 
	private boolean loaded;

	private PullToRefreshListView hilightListviewAll;
	private PullToRefreshListView hilightListviewPl;
	private PullToRefreshListView hilightListviewBl;
	private PullToRefreshListView hilightListviewLl;
	private PullToRefreshListView hilightListviewGl;
	private PullToRefreshListView hilightListviewFl;
	private PullToRefreshListView hilightListviewUcl;
	private PullToRefreshListView hilightListviewUpl;
	private PullToRefreshListView hilightListviewChamp;
	private PullToRefreshListView hilightListviewCapital;

	private HilightAdapter hilightAdapterAll; 
	private HilightAdapter hilightAdapterPl;
	private HilightAdapter hilightAdapterBl;
	private HilightAdapter hilightAdapterLl;
	private HilightAdapter hilightAdapterGl;
	private HilightAdapter hilightAdapterFl;
	private HilightAdapter hilightAdapterUcl;
	private HilightAdapter hilightAdapterUpl;
	private HilightAdapter hilightAdapterChamp;
	private HilightAdapter hilightAdapterCapital;
	
	public static List<String> stackTagLoading;

	private TabHost tabs;

	private HorizontalScrollView scrollTab;
	private TextView textEmpty;
	
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
        
		setupTab(R.id.hilight_listview_all, "tag0", "", R.drawable.news_new, true);
		setupTab(R.id.hilight_listview_pl, "tag1", "", R.drawable.logo_premier_league, false);
		setupTab(R.id.hilight_listview_bl, "tag2", "", R.drawable.logo_bundesliga, false);
		setupTab(R.id.hilight_listview_ll, "tag3", "", R.drawable.logo_laliga, false);
		setupTab(R.id.hilight_listview_gl, "tag4", "", R.drawable.logo_calcio, false);
		setupTab(R.id.hilight_listview_fl, "tag5", "", R.drawable.logo_ligue1, false);
		setupTab(R.id.hilight_listview_ucl, "tag6", "", R.drawable.logo_ucl, false);
		setupTab(R.id.hilight_listview_upl, "tag7", "", R.drawable.logo_europa_league, false);
		setupTab(R.id.hilight_listview_champ, "tag8", "", R.drawable.logo_championschip, false);
		setupTab(R.id.hilight_listview_capital, "tag9", "", R.drawable.logo_capital_one_cup, false);
		
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
		
		hilightWaitProcessbar = (ProgressBar) hilightPage.findViewById(R.id.hilight_wait_processbar);
		hilightWaitProcessbar.setVisibility(View.VISIBLE);
		
		hilightListviewAll = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_all);
		hilightListviewAll.setVisibility(View.VISIBLE);
		
		hilightListviewPl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_pl);
		hilightListviewPl.setVisibility(View.GONE);
		
		hilightListviewBl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_bl);
		hilightListviewBl.setVisibility(View.GONE);
		
		hilightListviewLl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_ll);
		hilightListviewLl.setVisibility(View.GONE);
		
		hilightListviewGl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_gl);
		hilightListviewGl.setVisibility(View.GONE);
		
		hilightListviewFl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_fl);
		hilightListviewFl.setVisibility(View.GONE);
		
		hilightListviewUcl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_ucl);
		hilightListviewUcl.setVisibility(View.GONE);
		 
		hilightListviewUpl = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_upl);
		hilightListviewUpl.setVisibility(View.GONE);
		
		hilightListviewChamp = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_champ);
		hilightListviewChamp.setVisibility(View.GONE);
		 
		hilightListviewCapital = (PullToRefreshListView) hilightPage.findViewById(R.id.hilight_listview_capital);
		hilightListviewCapital.setVisibility(View.GONE);
		
		hilightLoadingFooterProcessbar = (ProgressBar) hilightPage.findViewById(R.id.hilight_loading_footer_processbar);
		hilightLoadingFooterProcessbar.setVisibility(View.GONE);
		
		textEmpty = (TextView) hilightPage.findViewById(R.id.empty);
		textEmpty.setOnClickListener(this); 
		
		hilightAdapterAll = null;
		hilightAdapterPl = null;
		hilightAdapterBl = null;
		hilightAdapterLl = null;
		hilightAdapterGl = null;
		hilightAdapterFl = null;
		hilightAdapterUcl = null;
		hilightAdapterUpl = null;
		hilightAdapterChamp = null;
		hilightAdapterCapital = null;
		
		hilightListAll = null;
		hilightListPl = null;
		hilightListBl = null;
		hilightListLl = null;
		hilightListGl = null;
		hilightListFl = null;
		hilightListUcl = null;
		hilightListUpl = null;
		hilightListChamp = null;
		hilightListCapital = null;
		
		if(hilightAdapterAll == null){
			try{ 
				if(NetworkUtils.isNetworkAvailable(getActivity())){
					new LoadOldHilightTask(hilightListviewAll, hilightAdapterAll, "tag0").execute(getURLbyTag(0, "tag0"));
				}else{
					Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
					setMessageEmptyListView(hilightListAll, hilightAdapterAll, hilightListviewAll, "tag0");
				}
			}catch(Exception e){
				e.printStackTrace();
				setMessageEmptyListView(hilightListAll, hilightAdapterAll, hilightListviewAll, "tag0");
			}
		}
	}

	private void setMessageEmptyListView(List<HilightModel> hilightList, HilightAdapter hilightAdapter, PullToRefreshListView hilightListView, String tag) {
		hilightWaitProcessbar.setVisibility(View.GONE);
		hilightListView.setVisibility(View.GONE);
		
		if(hilightAdapter == null || hilightAdapter.isEmpty())
			textEmpty.setVisibility(View.VISIBLE);
		else
			textEmpty.setVisibility(View.GONE);
	} 

	public String getURLbyTag(int id, String tag) {
		String url = ""; 
		
		try{
			if(tag.equals("tag0")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + HILIGHT_TYPE_ALL + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag1")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_PREMIER_LEAGUE), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag2")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_BUNDESLIGA), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag3")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_LALIGA), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag4")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_CALCAIO_SERIE_A), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag5")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_LEAGUE_DE_LEAGUE1), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag6")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_UCL), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag7")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id+ "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_UPL), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag8")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id+ "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_CHAMPIAN_CHIP), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}else if(tag.equals("tag9")){
				url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id+ "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode("&nbsp;" + getActivity().getResources().getString(R.string.HILIGHT_TYPE_CAPITAL_ONE_CUP), "utf-8") + "&member_id=" + SessionManager.getMember(getActivity()).getUid() + "&m_token=" + SessionManager.getMember(getActivity()).getToken();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return url;
	}

	public class LoadOldHilightTask extends AsyncTask<String, Void, List<HilightModel>>{ 
		
		PullToRefreshListView hilightListview;
		HilightAdapter hilightAdapter;
		String tag; 
		
		public LoadOldHilightTask(PullToRefreshListView hilightListview, HilightAdapter hilightAdapter, String tag) {
			this.hilightListview = hilightListview;
			this.hilightAdapter = hilightAdapter; 
			this.tag = tag;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(hilightAdapter==null && hilightWaitProcessbar!=null){
				hilightWaitProcessbar.setVisibility(View.VISIBLE);
				stackTagLoading.add(tag);
				textEmpty.setVisibility(View.GONE);
			}
		}
		
		@Override
		protected List<HilightModel> doInBackground(String... params) {
			
			if(hilightAdapter!=null && hilightAdapter.getCount()>100)
				return null;
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]);  
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			
			List<HilightModel> hilightList = HilightModel.convertHilightStrToList(result);
			
			return hilightList;
		}

		@Override
		protected void onPostExecute(List<HilightModel> result) {
			super.onPostExecute(result);
			if(hilightAdapter!=null && hilightAdapter.getCount() > 0){
				doLoadOldHilightToListView(result, tag);  
				hilightLoadingFooterProcessbar.setVisibility(View.GONE);
			}else{
				doLoadHilightToListView(result, tag);
				stackTagLoading.remove(tag);
				if(stackTagLoading != null && stackTagLoading.size() > 0 && !tag.equals(tabs.getCurrentTabTag())){ 
					hilightWaitProcessbar.setVisibility(View.VISIBLE);
				}else{
					hilightWaitProcessbar.setVisibility(View.GONE);
				}
			}
			
			if(hilightAdapter==null || hilightAdapter.getCount() < 100){
				loaded = true;
			}else{
				loaded = false;
			}
		}

	}
	
	private void doLoadHilightToListView(List<HilightModel> hilightList, String tag) {
		
		if(tag.equals("tag0")){
			Hilight_Page.hilightListAll = getListView(hilightList, tag, Hilight_Page.hilightListAll);
			
			if(Hilight_Page.hilightListAll==null)
				return;
			
			hilightAdapterAll = new HilightAdapter(getActivity(), Hilight_Page.hilightListAll);
			hilightListviewAll.setAdapter(hilightAdapterAll);
			
			if(!hilightAdapterAll.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewAll.setVisibility(View.VISIBLE);
			
			if(hilightAdapterAll.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewAll, hilightAdapterAll, tag);
		}else if(tag.equals("tag1")){
			Hilight_Page.hilightListPl = getListView(hilightList, tag, Hilight_Page.hilightListPl);
			
			if(Hilight_Page.hilightListPl==null)
				return;
			
			hilightAdapterPl = new HilightAdapter(getActivity(), Hilight_Page.hilightListPl);
			hilightListviewPl.setAdapter(hilightAdapterPl);
			
			if(!hilightAdapterPl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewPl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterPl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewPl, hilightAdapterPl, tag);
		}else if(tag.equals("tag2")){
			Hilight_Page.hilightListBl = getListView(hilightList, tag, Hilight_Page.hilightListBl);
			
			if(Hilight_Page.hilightListBl==null)
				return;
			
			hilightAdapterBl = new HilightAdapter(getActivity(), Hilight_Page.hilightListBl);
			hilightListviewBl.setAdapter(hilightAdapterBl);
			
			if(!hilightAdapterBl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewBl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterBl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewBl, hilightAdapterBl, tag);
		}else if(tag.equals("tag3")){
			Hilight_Page.hilightListLl = getListView(hilightList, tag, Hilight_Page.hilightListLl);
			
			if(Hilight_Page.hilightListLl==null)
				return;
			
			hilightAdapterLl = new HilightAdapter(getActivity(), Hilight_Page.hilightListLl);
			hilightListviewLl.setAdapter(hilightAdapterLl);
			
			if(!hilightAdapterLl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewLl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterLl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewLl, hilightAdapterLl, tag);
		}else if(tag.equals("tag4")){
			Hilight_Page.hilightListGl = getListView(hilightList, tag, Hilight_Page.hilightListGl);
			
			if(Hilight_Page.hilightListGl==null)
				return;
			
			hilightAdapterGl = new HilightAdapter(getActivity(), Hilight_Page.hilightListGl);
			hilightListviewGl.setAdapter(hilightAdapterGl);
			
			if(!hilightAdapterGl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewGl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterGl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewGl, hilightAdapterGl, tag);
		}else if(tag.equals("tag5")){
			Hilight_Page.hilightListFl = getListView(hilightList, tag, Hilight_Page.hilightListFl);
			
			if(Hilight_Page.hilightListFl==null)
				return;
			
			hilightAdapterFl = new HilightAdapter(getActivity(), Hilight_Page.hilightListFl);
			hilightListviewFl.setAdapter(hilightAdapterFl);
			
			if(!hilightAdapterFl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewFl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterFl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewFl, hilightAdapterFl, tag);
		}else if(tag.equals("tag6")){
			Hilight_Page.hilightListUcl = getListView(hilightList, tag, Hilight_Page.hilightListUcl);
			
			if(Hilight_Page.hilightListUcl==null)
				return;
			
			hilightAdapterUcl = new HilightAdapter(getActivity(), Hilight_Page.hilightListUcl);
			hilightListviewUcl.setAdapter(hilightAdapterUcl);
			
			if(!hilightAdapterUcl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewUcl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterUcl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
		
			setListViewEvents(hilightListviewUcl, hilightAdapterUcl, tag);
		}else if(tag.equals("tag7")){
			Hilight_Page.hilightListUpl = getListView(hilightList, tag, Hilight_Page.hilightListUpl);
			
			if(Hilight_Page.hilightListUpl==null)
				return;
			
			hilightAdapterUpl = new HilightAdapter(getActivity(), Hilight_Page.hilightListUpl);
			hilightListviewUpl.setAdapter(hilightAdapterUpl);
			
			if(!hilightAdapterUpl.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewUpl.setVisibility(View.VISIBLE);
			
			if(hilightAdapterUpl.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewUpl, hilightAdapterUpl, tag);
		}else if(tag.equals("tag8")){
			Hilight_Page.hilightListChamp = getListView(hilightList, tag, Hilight_Page.hilightListChamp);
			
			if(Hilight_Page.hilightListChamp==null)
				return;
			
			hilightAdapterChamp = new HilightAdapter(getActivity(), Hilight_Page.hilightListChamp);
			hilightListviewChamp.setAdapter(hilightAdapterChamp);
			
			if(!hilightAdapterChamp.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewChamp.setVisibility(View.VISIBLE);
			
			if(hilightAdapterChamp.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewChamp, hilightAdapterChamp, tag);
		}else if(tag.equals("tag9")){
			Hilight_Page.hilightListCapital = getListView(hilightList, tag, Hilight_Page.hilightListCapital);
			
			if(Hilight_Page.hilightListCapital==null)
				return;
			
			hilightAdapterCapital = new HilightAdapter(getActivity(), Hilight_Page.hilightListCapital);
			hilightListviewCapital.setAdapter(hilightAdapterCapital);
			
			if(!hilightAdapterCapital.isEmpty() && tag.equals(tabs.getCurrentTabTag()))
				hilightListviewCapital.setVisibility(View.VISIBLE);
			
			if(hilightAdapterCapital.isEmpty()){
				textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(hilightListviewCapital, hilightAdapterCapital, tag);
		}
		
	}
	
	private List<HilightModel> getListView(List<HilightModel> hilightlist, String tag, List<HilightModel> hilightModelList) {
		//tabs.setCurrentTabByTag(tag);
		
		if(hilightlist == null || hilightlist.isEmpty()){ 
			//Toast.makeText(getActivity(), "No News", Toast.LENGTH_SHORT).show(); 
			if(hilightModelList == null && getActivity()!=null){  
				Boast.makeText(getActivity(), getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				return new ArrayList<HilightModel>(); 
			}
		}else{
			if(hilightModelList == null || hilightModelList.isEmpty()){
				loaded = true;
				
				if(tag.equals("tag0"))
					storeMaxIdToPerference(hilightlist.get(0), HilightModel.HILIGHT_ID);
				//intentNewsUpdate(newsList.get(0));
				return hilightlist;
			}else if(hilightModelList.get(0).getHilightId() < hilightlist.get(0).getHilightId()){
				loaded = true;
				
				if(tag.equals("tag0"))
					storeMaxIdToPerference(hilightlist.get(0), HilightModel.HILIGHT_ID);
				//intentNewsUpdate(newsList.get(0));
				return hilightlist;
			}
		}
		
		return hilightModelList;
	}
	
	@SuppressLint("CommitPrefEdits")
	private void storeMaxIdToPerference(HilightModel hilightModel, String tag) {
		if(getActivity() != null){
			SharedPreferences sharePre = getActivity().getSharedPreferences(UpdateService.SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			editSharePre.putInt(tag, hilightModel.getHilightId());
			editSharePre.commit();
		}
	}

	private void doLoadOldHilightToListView(List<HilightModel> result, String tag) { 
		if(result == null || result.isEmpty()){
			//oldHilight=null;
			return;
		}
		
		if(tag.equals("tag0")){
			hilightAdapterAll.add(result);
		}else if(tag.equals("tag1")){
			hilightAdapterPl.add(result);
		}else if(tag.equals("tag2")){
			hilightAdapterBl.add(result);
		}else if(tag.equals("tag3")){
			hilightAdapterLl.add(result);
		}else if(tag.equals("tag4")){
			hilightAdapterGl.add(result);
		}else if(tag.equals("tag5")){
			hilightAdapterFl.add(result);
		}else if(tag.equals("tag6")){
			hilightAdapterUcl.add(result);
		}else if(tag.equals("tag7")){
			hilightAdapterUpl.add(result);
		}else if(tag.equals("tag8")){
			hilightAdapterChamp.add(result);
		}else if(tag.equals("tag9")){
			hilightAdapterCapital.add(result);
		}
		
	}
	
	public class LoadLastHilightTask extends AsyncTask<String, Void, List<HilightModel>>{ 
		
		PullToRefreshListView hilightListview;
		String tag;
		
		public LoadLastHilightTask(PullToRefreshListView hilightListview, String tag) { 
			this.hilightListview = hilightListview;
			this.tag = tag;
		}
		
		@Override
		protected List<HilightModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
		
			List<HilightModel> hilightList = HilightModel.convertHilightStrToList(result);
			 
			return hilightList;
		}

		@Override
		protected void onPostExecute(List<HilightModel> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				doLoadHilightToListView(result, tag); 
			}
			
			hilightListview.onRefreshComplete();
		}

	}
	
	/*private void doLoadRefeshToListView(List<HilightModel> result, String tag) {
		
		if(tag.equals("tag0")){
			hilightAdapterAll.addHead(result);
		}else if(tag.equals("tag1")){
			hilightAdapterPl.addHead(result);
		}else if(tag.equals("tag2")){
			hilightAdapterBl.addHead(result);
		}else if(tag.equals("tag3")){
			hilightAdapterLl.addHead(result);
		}else if(tag.equals("tag4")){
			hilightAdapterGl.addHead(result);
		}else if(tag.equals("tag5")){
			hilightAdapterFl.addHead(result);
		}else if(tag.equals("tag6")){
			hilightAdapterUcl.addHead(result);
		}else if(tag.equals("tag7")){
			hilightAdapterUpl.addHead(result);
		}else if(tag.equals("tag8")){
			hilightAdapterChamp.addHead(result);
		}else if(tag.equals("tag9")){
			hilightAdapterCapital.addHead(result);
		}
		
	}*/
	
	private void setListViewEvents(final PullToRefreshListView hilightListview, final HilightAdapter hilightAdapter, final String tag) {
		
		hilightListview.setOnItemClickListener(this); 
		
		hilightListview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastVisibleItem = firstVisibleItem + visibleItemCount;
				if((lastVisibleItem == totalItemCount) && loaded && totalItemCount>0){
					HilightModel hm = (HilightModel)view.getAdapter().getItem(totalItemCount-1);  
					if(hm!=null && !hm.equals(oldHilight)){
						
						if(NetworkUtils.isNetworkAvailable(getActivity())){
							hilightLoadingFooterProcessbar.setVisibility(View.VISIBLE);
							new LoadOldHilightTask(hilightListview, hilightAdapter, tag).execute(getURLbyTag(hm.getHilightId(), tag));  
						}else
							Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						//Toast.makeText(getActivity(), "Toast " + i++, Toast.LENGTH_SHORT).show();
					}
					
					oldHilight = hm;
				}
				
			}
		});
		
		hilightListview.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if(NetworkUtils.isNetworkAvailable(getActivity())){
					new LoadLastHilightTask(hilightListview, tag).execute(getURLbyTag(0, tabs.getCurrentTabTag())); 
				}else{
					Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
					hilightListview.onRefreshComplete();
				}
			}
		}); 
	}

	@Override
	public void onItemClick(AdapterView<?> adap, View v, int pos, long id) {
		pos++;
		HilightModel hilight = (HilightModel) adap.getAdapter().getItem(pos); 
		
		if(NetworkUtils.isNetworkAvailable(getActivity())){
			hilight.setHilightViews(hilight.getHilightViews()+1);
			hilight.setStatusView(1);
			
			new PostHilightReads().execute(hilight.getHilightId()); 
		}else
			Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
		
		Intent hilightItemPage = new Intent(getActivity(), Hilight_Item_Page.class);
		hilightItemPage.putExtra(HILIGHT_ITEM_INDEX, pos-1);
		hilightItemPage.putExtra(HILIGHT_TAG, tabs.getCurrentTabTag());
		startActivity(hilightItemPage);
	}
	
	public class PostHilightReads extends AsyncTask<Integer, Void, String>{
		
		@Override
		protected String doInBackground(Integer... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("hilight_id", String.valueOf(params[0])));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(SessionManager.getMember(getActivity()).getUid())));
			paramsPost.add(new BasicNameValuePair("m_token", String.valueOf(SessionManager.getMember(getActivity()).getToken())));
			
			return HttpConnectUtils.getStrHttpPostConnect(ControllParameter.HILIGHT_READS_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
		}

	} 

	@SuppressWarnings("deprecation")
	@Override
	public void onTabChanged(String tag) {
		hilightWaitProcessbar.setVisibility(View.GONE);
		textEmpty.setVisibility(View.GONE);
		
		if(tag.equals("tag0")){
			if(hilightAdapterAll == null || hilightAdapterAll.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewAll, hilightAdapterAll, "tag0").execute(getURLbyTag(0, "tag0"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListAll, hilightAdapterAll, hilightListviewAll, "tag0");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListAll, hilightAdapterAll, hilightListviewAll, "tag0");
				}
			}
			setSelectedTab(0);
		}else if(tag.equals("tag1")){
			if(hilightAdapterPl == null || hilightAdapterPl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewPl, hilightAdapterPl, "tag1").execute(getURLbyTag(0, "tag1"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListPl, hilightAdapterPl, hilightListviewPl, "tag1");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListPl, hilightAdapterPl, hilightListviewPl, "tag1");
				}
			}
			setSelectedTab(1);
		}else if(tag.equals("tag2")){
			if(hilightAdapterBl == null || hilightAdapterBl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewBl, hilightAdapterBl, "tag2").execute(getURLbyTag(0, "tag2"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListBl, hilightAdapterBl, hilightListviewBl, "tag2");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListBl, hilightAdapterBl, hilightListviewBl, "tag2");
				}
			}
			setSelectedTab(2);
		}else if(tag.equals("tag3")){
			if(hilightAdapterLl == null || hilightAdapterLl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewLl, hilightAdapterLl, "tag3").execute(getURLbyTag(0, "tag3"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListLl, hilightAdapterLl, hilightListviewLl, "tag3");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListLl, hilightAdapterLl, hilightListviewLl, "tag3");
				}
			}
			setSelectedTab(3);
		}else if(tag.equals("tag4")){
			if(hilightAdapterGl == null || hilightAdapterGl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewGl, hilightAdapterGl, "tag4").execute(getURLbyTag(0, "tag4"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListGl, hilightAdapterGl, hilightListviewGl, "tag4");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListGl, hilightAdapterGl, hilightListviewGl, "tag4");
				}
			}
			
			int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			int scrollX = (scrollTab.getLeft() - (screenWidth / 2)) + (scrollTab.getWidth() / 2);
			scrollTab.smoothScrollTo(scrollX, 0);
			
			setSelectedTab(4);
		}else if(tag.equals("tag5")){
			if(hilightAdapterFl == null || hilightAdapterFl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewFl, hilightAdapterFl, "tag5").execute(getURLbyTag(0, "tag5"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListFl, hilightAdapterFl, hilightListviewFl, "tag5");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListFl, hilightAdapterFl, hilightListviewFl, "tag5");
				}
			}
			
			int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth(); 
			int scrollX = (scrollTab.getRight() - (screenWidth / 2)) + (scrollTab.getWidth() / 2);
			scrollTab.smoothScrollTo(scrollX, 0);
			
			setSelectedTab(5);
		}else if(tag.equals("tag6")){
			if(hilightAdapterUcl == null || hilightAdapterUcl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewUcl, hilightAdapterUcl, "tag6").execute(getURLbyTag(0, "tag6"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListUcl, hilightAdapterUcl, hilightListviewUcl, "tag6");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListUcl, hilightAdapterUcl, hilightListviewUcl, "tag6");
				}
			}
			setSelectedTab(6);
		}else if(tag.equals("tag7")){
			if(hilightAdapterUpl == null || hilightAdapterUpl.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewUpl, hilightAdapterUpl, "tag7").execute(getURLbyTag(0, "tag7"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListUpl, hilightAdapterUpl, hilightListviewUpl, "tag7");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListUpl, hilightAdapterUpl, hilightListviewUpl, "tag7");
				}
			}
			setSelectedTab(7);
		}else if(tag.equals("tag8")){
			if(hilightAdapterChamp == null || hilightAdapterChamp.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewChamp, hilightAdapterChamp, "tag8").execute(getURLbyTag(0, "tag8"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListChamp, hilightAdapterChamp, hilightListviewChamp, "tag8");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListChamp, hilightAdapterChamp, hilightListviewChamp, "tag8");
				}
			}
			setSelectedTab(8);
		}else if(tag.equals("tag9")){
			if(hilightAdapterCapital == null || hilightAdapterCapital.isEmpty()){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						hilightWaitProcessbar.setVisibility(View.VISIBLE);
						new LoadOldHilightTask(hilightListviewCapital, hilightAdapterCapital, "tag9").execute(getURLbyTag(0, "tag9"));
					}else{
						Boast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(hilightListCapital, hilightAdapterCapital, hilightListviewCapital, "tag9");
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(hilightListCapital, hilightAdapterCapital, hilightListviewCapital, "tag9");
				}
			}
			setSelectedTab(9);
		}
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
	
	public static List<HilightModel> getHilightListbyTag(String tag) {
		List<HilightModel> hilightModelList = new ArrayList<HilightModel>();
			if(tag.equals("tag0")){
				hilightModelList = getHilightListAll();
			}else if(tag.equals("tag1")){
				hilightModelList = getHilightListPl();
			}else if(tag.equals("tag2")){
				hilightModelList = getHilightListBl();
			}else if(tag.equals("tag3")){
				hilightModelList = getHilightListLl();			
			}else if(tag.equals("tag4")){
				hilightModelList = getHilightListGl();			
			}else if(tag.equals("tag5")){
				hilightModelList = getHilightListFl();			
			}else if(tag.equals("tag6")){
				hilightModelList = getHilightListUcl();		
			}else if(tag.equals("tag7")){
				hilightModelList = getHilightListUpl();			
			}else if(tag.equals("tag8")){
				hilightModelList = getHilightListChamp();			
			}else if(tag.equals("tag9")){
				hilightModelList = getHilightListCapital();			
			}
		
		return hilightModelList;
	}

	public static List<HilightModel> getHilightListAll() {
		return hilightListAll;
	}

	public static void setHilightListAll(List<HilightModel> hilightListAll) {
		Hilight_Page.hilightListAll = hilightListAll;
	}

	public static List<HilightModel> getHilightListPl() {
		return hilightListPl;
	}

	public static void setHilightListPl(List<HilightModel> hilightListPl) {
		Hilight_Page.hilightListPl = hilightListPl;
	}

	public static List<HilightModel> getHilightListBl() {
		return hilightListBl;
	}

	public static void setHilightListBl(List<HilightModel> hilightListBl) {
		Hilight_Page.hilightListBl = hilightListBl;
	}

	public static List<HilightModel> getHilightListLl() {
		return hilightListLl;
	}

	public static void setHilightListLl(List<HilightModel> hilightListLl) {
		Hilight_Page.hilightListLl = hilightListLl;
	}

	public static List<HilightModel> getHilightListGl() {
		return hilightListGl;
	}

	public static void setHilightListGl(List<HilightModel> hilightListGl) {
		Hilight_Page.hilightListGl = hilightListGl;
	}

	public static List<HilightModel> getHilightListFl() {
		return hilightListFl;
	}

	public static void setHilightListFl(List<HilightModel> hilightListFl) {
		Hilight_Page.hilightListFl = hilightListFl;
	}

	public PullToRefreshListView getHilightListviewCapital() {
		return hilightListviewCapital;
	}

	public void setHilightListviewCapital(PullToRefreshListView hilightListviewCapital) {
		this.hilightListviewCapital = hilightListviewCapital;
	}

	public static List<HilightModel> getHilightListUcl() {
		return hilightListUcl;
	}

	public static void setHilightListUcl(List<HilightModel> hilightListUcl) {
		Hilight_Page.hilightListUcl = hilightListUcl;
	}

	public static List<HilightModel> getHilightListUpl() {
		return hilightListUpl;
	}

	public static void setHilightListUpl(List<HilightModel> hilightListUpl) {
		Hilight_Page.hilightListUpl = hilightListUpl;
	}

	public static List<HilightModel> getHilightListChamp() {
		return hilightListChamp;
	}

	public static void setHilightListChamp(List<HilightModel> hilightListChamp) {
		Hilight_Page.hilightListChamp = hilightListChamp;
	}

	public static List<HilightModel> getHilightListCapital() {
		return hilightListCapital;
	}

	public static void setHilightListCapital(List<HilightModel> hilightListCapital) {
		Hilight_Page.hilightListCapital = hilightListCapital;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(tabs.getCurrentTabTag().equals("tag0")){ 
			if(hilightAdapterAll != null){
				hilightAdapterAll.notifyDataSetChanged();
			}
		}else if(tabs.getCurrentTabTag().equals("tag1")){
			if(hilightAdapterPl != null){
				hilightAdapterPl.notifyDataSetChanged();
			}
		}else if(tabs.getCurrentTabTag().equals("tag2")){
			if(hilightAdapterBl != null){
				hilightAdapterBl.notifyDataSetChanged();
			}
			
		}else if(tabs.getCurrentTabTag().equals("tag3")){
			if(hilightAdapterLl != null){
				hilightAdapterLl.notifyDataSetChanged();
			}
			
		}else if(tabs.getCurrentTabTag().equals("tag4")){
			if(hilightAdapterGl != null){
				hilightAdapterGl.notifyDataSetChanged();
			}
			
		}else if(tabs.getCurrentTabTag().equals("tag5")){
			if(hilightAdapterFl != null){
				hilightAdapterFl.notifyDataSetChanged();
			}
			
		}else if(tabs.getCurrentTabTag().equals("tag6")){
			if(hilightAdapterUcl != null){
				hilightAdapterUcl.notifyDataSetChanged();
			}
		}else if(tabs.getCurrentTabTag().equals("tag7")){
			if(hilightAdapterUpl != null){
				hilightAdapterUpl.notifyDataSetChanged();
			}
		}else if(tabs.getCurrentTabTag().equals("tag8")){
			if(hilightAdapterChamp != null){
				hilightAdapterChamp.notifyDataSetChanged();
			}
		}else if(tabs.getCurrentTabTag().equals("tag9")){
			if(hilightAdapterCapital != null){
				hilightAdapterCapital.notifyDataSetChanged();
			}
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
