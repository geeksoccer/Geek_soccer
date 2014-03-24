package com.excelente.geek_soccer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.adapter.HilightAdapter;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.PullToRefreshListView;
import com.excelente.geek_soccer.view.PullToRefreshListView.OnRefreshListener;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public class Hilight_Page extends Fragment implements OnItemClickListener, OnTabChangeListener{
	
	public static final String GET_HILIGHT_URL = "http://183.90.171.209/gs_hilight/get_hilight.php";

	public static final String HILIGHT_ITEM_INDEX = "HILIGHT_ITEM_INDEX"; 
	public static final String HILIGHT_TAG = "HILIGHT_TAG"; 
	
	public static final String HILIGHT_TYPE_ALL = "All";
	
	public static final String HILIGHT_TYPE_PREMIER_LEAGUE = "&nbsp;æ√’‡¡’¬√Ï≈’°";
	public static final String HILIGHT_TYPE_LALIGA = "&nbsp;≈“≈’°“";
	public static final String HILIGHT_TYPE_CALCAIO_SERIE_A = "&nbsp;°—≈‚™Ë ‡´‡√’¬ Õ“";
	public static final String HILIGHT_TYPE_LEAGUE_DE_LEAGUE1 = "&nbsp;≈’°‡Õ‘ß";
	public static final String HILIGHT_TYPE_BUNDESLIGA = "&nbsp;∫ÿπ‡¥ ≈’°“";
	public static final String HILIGHT_TYPE_UCL = "&nbsp;¬ŸøË“ ·™¡ªÏ‡ª’È¬π Ï ≈’°";
	public static final String HILIGHT_TYPE_UPL = "&nbsp;¬ŸøË“ ¬Ÿ‚√ªÈ“ ≈’°";
	public static final String HILIGHT_TYPE_CHAMPIAN_CHIP = "&nbsp;·™¡‡ª’È¬π™‘æ Õ—ß°ƒ…";
	public static final String HILIGHT_TYPE_CAPITAL_ONE_CUP = "&nbsp;·§ª‘µÕ≈ «—π §—æ";
	
	public static List<HilightModel> hilightListAll;
	public static List<HilightModel> hilightListPl; 
	public static List<HilightModel> hilightListBl;
	public static List<HilightModel> hilightListLl;
	public static List<HilightModel> hilightListGl;
	public static List<HilightModel> hilightListFl;
	private static List<HilightModel> hilightListUcl;
	private static List<HilightModel> hilightListUpl;
	private static List<HilightModel> hilightListChamp;
	private static List<HilightModel> hilightListCapital;
	
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

	private TabHost tabs;

	private HorizontalScrollView scrollTab;
	
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
		initView();
		initSubView();
	}

	private void initView() { 
		
		hilightPage = getView();  
		
		tabs = (TabHost)hilightPage.findViewById(R.id.tabhost); 
		tabs.setup(); 
        
		TabHost.TabSpec spec = tabs.newTabSpec("tag0");  
		spec.setContent(R.id.hilight_listview_all);
		spec.setIndicator("", getResources().getDrawable(R.drawable.world));  
		tabs.addTab(spec); 
		
		spec = tabs.newTabSpec("tag1"); 
		spec.setContent(R.id.hilight_listview_pl); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_premier_league));  
		tabs.addTab(spec); 
		
		spec = tabs.newTabSpec("tag2"); 
		spec.setContent(R.id.hilight_listview_bl); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_bundesliga)); 
		tabs.addTab(spec); 
		
		spec = tabs.newTabSpec("tag3"); 
		spec.setContent(R.id.hilight_listview_ll); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_laliga)); 
		tabs.addTab(spec); 
		
		spec = tabs.newTabSpec("tag4"); 
		spec.setContent(R.id.hilight_listview_gl); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_calcio)); 
		tabs.addTab(spec); 
		
		spec = tabs.newTabSpec("tag5"); 
		spec.setContent(R.id.hilight_listview_fl); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_ligue1)); 
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("tag6"); 
		spec.setContent(R.id.hilight_listview_ucl); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_ucl)); 
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("tag7"); 
		spec.setContent(R.id.hilight_listview_upl); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_europa_league)); 
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("tag8"); 
		spec.setContent(R.id.hilight_listview_champ); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_championschip)); 
		tabs.addTab(spec);
		
		spec = tabs.newTabSpec("tag9"); 
		spec.setContent(R.id.hilight_listview_capital); 
		spec.setIndicator("", getResources().getDrawable(R.drawable.logo_capital_one_cup)); 
		tabs.addTab(spec);
		
		tabs.setCurrentTab(0);
		tabs.setOnTabChangedListener(this);
		
		scrollTab = (HorizontalScrollView) hilightPage.findViewById(R.id.scroll_tab);
		scrollTab.setSmoothScrollingEnabled(true);
	}
	
	private void initSubView() {
		
		hilightWaitProcessbar = (ProgressBar) hilightPage.findViewById(R.id.hilight_wait_processbar);
		hilightWaitProcessbar.setVisibility(View.GONE);
		
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
		
		if(hilightAdapterAll == null){
			try{ 
				if(NetworkUtils.isNetworkAvailable(getActivity()))
					new LoadOldHilightTask(hilightListviewAll, hilightAdapterAll, "tag0").execute(getURLbyTag(0, "tag0"));
				else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private String getURLbyTag(int id, String tag) {
		String url = ""; 
		
		try{
			if(tag.equals("tag0")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + HILIGHT_TYPE_ALL + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag1")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_PREMIER_LEAGUE, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag2")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_BUNDESLIGA, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag3")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_LALIGA, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag4")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_CALCAIO_SERIE_A, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag5")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_LEAGUE_DE_LEAGUE1, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag6")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_UCL, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag7")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id+ "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_UPL, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag8")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id+ "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_CHAMPIAN_CHIP, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
			}else if(tag.equals("tag9")){
				url = GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id+ "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(HILIGHT_TYPE_CAPITAL_ONE_CUP, "utf-8") + "&member_id=" + MemberSession.getMember().getUid();
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
			}
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
			if(hilightAdapter!=null && hilightAdapter.getCount() > 0){
				doLoadOldHilightToListView(result, tag);  
				hilightLoadingFooterProcessbar.setVisibility(View.GONE);
			}else{
				doLoadHilightToListView(result, tag);
				hilightWaitProcessbar.setVisibility(View.GONE);
			}
			
			loaded = true;
		}

	}
	
	private void doLoadHilightToListView(List<HilightModel> hilightList, String tag) { 
		
		if(hilightList == null || hilightList.isEmpty()){ 
			Toast.makeText(getActivity(), "No Hilight", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(tag.equals("tag0")){
			Hilight_Page.hilightListAll = hilightList;
			
			hilightAdapterAll = new HilightAdapter(getActivity(), hilightList);
			hilightListviewAll.setAdapter(hilightAdapterAll);
			hilightListviewAll.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewAll, hilightAdapterAll, tag);
		}else if(tag.equals("tag1")){
			Hilight_Page.hilightListPl = hilightList;
			
			hilightAdapterPl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewPl.setAdapter(hilightAdapterPl);
			hilightListviewPl.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewPl, hilightAdapterPl, tag);
		}else if(tag.equals("tag2")){
			Hilight_Page.hilightListBl = hilightList;
			
			hilightAdapterBl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewBl.setAdapter(hilightAdapterBl);
			hilightListviewBl.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewBl, hilightAdapterBl, tag);
		}else if(tag.equals("tag3")){
			Hilight_Page.hilightListLl = hilightList;
			
			hilightAdapterLl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewLl.setAdapter(hilightAdapterLl);
			hilightListviewLl.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewLl, hilightAdapterLl, tag);
		}else if(tag.equals("tag4")){
			Hilight_Page.hilightListGl = hilightList;
			
			hilightAdapterGl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewGl.setAdapter(hilightAdapterGl);
			hilightListviewGl.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewGl, hilightAdapterGl, tag);
		}else if(tag.equals("tag5")){
			Hilight_Page.hilightListFl = hilightList;
			
			hilightAdapterFl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewFl.setAdapter(hilightAdapterFl);
			hilightListviewFl.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewFl, hilightAdapterFl, tag);
		}else if(tag.equals("tag6")){
			Hilight_Page.hilightListUcl = hilightList;
			 
			hilightAdapterUcl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewUcl.setAdapter(hilightAdapterUcl);
			hilightListviewUcl.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewUcl, hilightAdapterUcl, tag);
		}else if(tag.equals("tag7")){
			Hilight_Page.hilightListUpl = hilightList;
			 
			hilightAdapterUpl = new HilightAdapter(getActivity(), hilightList);
			hilightListviewUpl.setAdapter(hilightAdapterUpl);
			hilightListviewUpl .setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewUpl , hilightAdapterUpl, tag);
		}else if(tag.equals("tag8")){
			Hilight_Page.hilightListChamp = hilightList; 
			
			hilightAdapterChamp = new HilightAdapter(getActivity(), hilightList);
			hilightListviewChamp.setAdapter(hilightAdapterChamp);
			hilightListviewChamp.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewChamp, hilightAdapterChamp, tag);
		}else if(tag.equals("tag9")){
			Hilight_Page.hilightListCapital = hilightList;
			 
			hilightAdapterCapital = new HilightAdapter(getActivity(), hilightList);
			hilightListviewCapital.setAdapter(hilightAdapterCapital);
			hilightListviewCapital.setVisibility(View.VISIBLE);
			
			setListViewEvents(hilightListviewCapital, hilightAdapterCapital, tag);
		}
		
	}
	
	private void doLoadOldHilightToListView(List<HilightModel> result, String tag) { 
		if(result == null || result.isEmpty()){
			Toast.makeText(getActivity(), "No Old Hilight", Toast.LENGTH_SHORT).show();
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
			if(!result.isEmpty()){
				doLoadRefeshToListView(result, tag);
			}else{
				Toast.makeText(getActivity(), "No Hilight Update", Toast.LENGTH_SHORT).show();
			}
			
			hilightListview.onRefreshComplete();
		}

	}
	
	private void doLoadRefeshToListView(List<HilightModel> result, String tag) {
		
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
		
	}
	
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
					if(!hm.equals(oldHilight)){
						
						if(NetworkUtils.isNetworkAvailable(getActivity())){
							hilightLoadingFooterProcessbar.setVisibility(View.VISIBLE);
							new LoadOldHilightTask(hilightListview, hilightAdapter, tag).execute(getURLbyTag(hm.getHilightId(), tag));  
						}else
							Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
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
				}else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	@Override
	public void onItemClick(AdapterView<?> adap, View v, int pos, long id) {
		HilightModel hilight = (HilightModel) adap.getAdapter().getItem(pos); 
		
		if(NetworkUtils.isNetworkAvailable(getActivity())){
			hilight.setHilightViews(hilight.getHilightViews()+1);
			hilight.setStatusView(1);
			
			new PostHilightReads().execute(hilight.getHilightId()); 
		}else
			Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
		
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
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(MemberSession.getMember().getUid())));
			
			return HttpConnectUtils.getStrHttpPostConnect(Hilight_Item_Page.HILIGHT_READS_URL, paramsPost);
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
		if(tag.equals("tag0")){
			if(hilightAdapterAll == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewAll, hilightAdapterAll, "tag0").execute(getURLbyTag(0, "tag0"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else if(tag.equals("tag1")){
			if(hilightAdapterPl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewPl, hilightAdapterPl, "tag1").execute(getURLbyTag(0, "tag1"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else if(tag.equals("tag2")){
			if(hilightAdapterBl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewBl, hilightAdapterBl, "tag2").execute(getURLbyTag(0, "tag2"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}else if(tag.equals("tag3")){
			if(hilightAdapterLl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewLl, hilightAdapterLl, "tag3").execute(getURLbyTag(0, "tag3"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}else if(tag.equals("tag4")){
			if(hilightAdapterGl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewGl, hilightAdapterGl, "tag4").execute(getURLbyTag(0, "tag4"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			int scrollX = (scrollTab.getLeft() - (screenWidth / 2)) + (scrollTab.getWidth() / 2);
			scrollTab.smoothScrollTo(scrollX, 0);
			
		}else if(tag.equals("tag5")){
			if(hilightAdapterFl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewFl, hilightAdapterFl, "tag5").execute(getURLbyTag(0, "tag5"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			int scrollX = (scrollTab.getRight() - (screenWidth / 2)) + (scrollTab.getWidth() / 2);
			scrollTab.smoothScrollTo(scrollX, 0);
			
		}else if(tag.equals("tag6")){
			if(hilightAdapterUcl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewUcl, hilightAdapterUcl, "tag6").execute(getURLbyTag(0, "tag6"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else if(tag.equals("tag7")){
			if(hilightAdapterUpl == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewUpl, hilightAdapterUpl, "tag7").execute(getURLbyTag(0, "tag7"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else if(tag.equals("tag8")){
			if(hilightAdapterChamp == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewChamp, hilightAdapterChamp, "tag8").execute(getURLbyTag(0, "tag8"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else if(tag.equals("tag9")){
			if(hilightAdapterCapital == null){
				try{ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadOldHilightTask(hilightListviewCapital, hilightAdapterCapital, "tag9").execute(getURLbyTag(0, "tag9"));
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
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
}
