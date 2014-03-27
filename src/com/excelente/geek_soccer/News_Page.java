package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.adapter.NewsAdapter;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.PullToRefreshListView;
import com.excelente.geek_soccer.view.PullToRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class News_Page extends Fragment implements OnItemClickListener, OnTabChangeListener{
	
	public static final String ITEM_INDEX = "NEWS_MODEL";
	public static final String NEWS_LIST_MODEL = "NEWS_LIST_MODEL"; 
	public static final String GET_NEWS_URL = "http://183.90.171.209/gs_news/get_news.php";
	
	View newsPage;
	
	private PullToRefreshListView newsListViewTeam;
	private PullToRefreshListView newsListViewGlobal;
	
	private ProgressBar newsWaitProgressBar;
	private NewsAdapter newsAdapterTeam;
	private NewsAdapter newsAdapterGlobal;
	
	NewsModel oldNews;
	
	private boolean loaded;
	private ProgressBar newsLoadingFooterProcessbar;
	
	private TabHost tabs;
	private TabWidget tabWidget;
	
	public static List<NewsModel> newsModelTeamList;
	public static List<NewsModel> newsModelGlobalList;
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
        initView();
        Timer timeStart = new Timer();
        timeStart.schedule(new TimerTask() {
			
			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
				        initSubview();
					}
				});
			}
		}, 0);
	}

	private void initSubview() {
		
		newsWaitProgressBar = (ProgressBar) newsPage.findViewById(R.id.news_wait_processbar);
		newsWaitProgressBar.setVisibility(View.VISIBLE);
		
		newsListViewTeam = (PullToRefreshListView) newsPage.findViewById(R.id.news_listview_team);
		newsListViewTeam.setVisibility(View.GONE); 
		
		newsListViewGlobal = (PullToRefreshListView) newsPage.findViewById(R.id.news_listview_global);
		newsListViewGlobal.setVisibility(View.GONE);
		
		newsLoadingFooterProcessbar = (ProgressBar) newsPage.findViewById(R.id.news_loading_footer_processbar);
		newsLoadingFooterProcessbar.setVisibility(View.GONE);
		
		newsAdapterTeam = null;
		newsAdapterGlobal = null;
		
		loaded = false;
		
		oldNews = null;
		
		newsModelTeamList = null;
		newsAdapterGlobal = null;
		
		if(getActivity().getIntent().getIntExtra(NewsModel.NEWS_ID+"tag", 0)==1){
			tabs.setCurrentTab(1);
		}else{
			if (newsAdapterTeam == null) {
				if (NetworkUtils.isNetworkAvailable(getActivity())){
					new LoadOldNewsTask(newsListViewTeam, newsAdapterTeam, "tag0").execute(getURLbyTag(0, "tag0"));
				}else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}
		}
		
	} 

	private void initView() {
		newsPage = getView();  
		
		tabs = (TabHost)newsPage.findViewById(R.id.tabhost); 
		tabs.setup();
        
		TabHost.TabSpec spec = tabs.newTabSpec("tag0");  
		spec.setContent(R.id.news_listview_team);
		spec.setIndicator("team news");   
		tabs.addTab(spec);
		
		
		spec = tabs.newTabSpec("tag1"); 
		spec.setContent(R.id.news_listview_global); 
		spec.setIndicator("global news");
		tabs.addTab(spec); 
		
		tabs.setCurrentTab(0); 
		tabs.setOnTabChangedListener(this);
		
		TextView tvTeam = (TextView) tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
		Drawable imgTeam = getResources().getDrawable(R.drawable.news_likes_selected); 
		imgTeam.setBounds(0, 0, 40, 40);
		tvTeam.setCompoundDrawables(null, imgTeam, null, null); 
		
		TextView tvGlobal = (TextView) tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
		Drawable imgGlobal = getResources().getDrawable(R.drawable.world);
		imgGlobal.setBounds(0, 0, 40, 40);
		tvGlobal.setCompoundDrawables(null, imgGlobal, null, null);
		
		tabWidget = (TabWidget) newsPage.findViewById(android.R.id.tabs); 
		
		if(MemberSession.getMember().getTeamId()>4){
			tabWidget.setVisibility(View.GONE);
			tabs.setCurrentTab(1);
		}
	}
	
	private String getURLbyTag(int id, String tag) {
		String url = ""; 
		
		if(tag.equals("tag0")){
			url = GET_NEWS_URL + "?" + NewsModel.NEWS_TEAM_ID + "=" + MemberSession.getMember().getTeamId() + "&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ MemberSession.getMember().getUid();
		}else if(tag.equals("tag1")){
			url = GET_NEWS_URL + "?" + NewsModel.NEWS_TEAM_ID + "=0&" + NewsModel.NEWS_ID + "=" + id + "&" + NewsModel.NEWS_LANGUAGE + "=TH&member_id="+ MemberSession.getMember().getUid();
		}
		
		return url;
	}

	public class LoadOldNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		PullToRefreshListView newsListview;
		NewsAdapter newsAdapter;
		String tag;
		
		public LoadOldNewsTask(PullToRefreshListView newsListview, NewsAdapter newsAdapter, String tag) {
			this.newsListview = newsListview;
			this.newsAdapter = newsAdapter;
			this.tag = tag;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(newsAdapter==null && newsWaitProgressBar!=null){
				newsWaitProgressBar.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		protected List<NewsModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			//Log.e("0000000000000000000000000", result);
			List<NewsModel> newsList = NewsModel.convertNewsStrToList(result);
			
			return newsList;
		}

		@Override
		protected void onPostExecute(List<NewsModel> result) {
			super.onPostExecute(result);
			if(newsAdapter!=null && newsAdapter.getCount() > 0){
				doLoadOldNewsToListView(result, tag);
				newsLoadingFooterProcessbar.setVisibility(View.GONE);
			}else{
				doLoadNewsToListView(result, tag);
				newsWaitProgressBar.setVisibility(View.GONE);
			}
			
			loaded = true;
		}

	}
	
	public class LoadLastNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		PullToRefreshListView newsListView;
		String tag;
		 
		public LoadLastNewsTask(PullToRefreshListView newsListView, String tag) {
			this.newsListView = newsListView;
			this.tag = tag;
		}
		
		@Override
		protected List<NewsModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			//Log.e("000000000000", result);
			List<NewsModel> newsList = NewsModel.convertNewsStrToList(result);
			
			return newsList;
		}

		@Override
		protected void onPostExecute(List<NewsModel> result) {
			super.onPostExecute(result);
			if(!result.isEmpty()){
				doLoadNewsToListView(result, tag);
			}else{
				Toast.makeText(getActivity(), "No News Update", Toast.LENGTH_SHORT).show();
			}
			
			newsListView.onRefreshComplete();
		}

	}
	
	private void doLoadNewsToListView(List<NewsModel> newsList , String tag) {
		
		if(tag.equals("tag0")){
			if(newsList == null || newsList.isEmpty()){
				//Toast.makeText(getActivity(), "No News", Toast.LENGTH_SHORT).show();
				if(newsModelTeamList == null){  
					newsModelTeamList = new ArrayList<NewsModel>(); 
					Toast.makeText(getActivity(), getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				}
			}else{
				if(newsModelTeamList == null || newsModelTeamList.isEmpty()){
					newsModelTeamList = newsList;
					loaded = true;
					storeMaxIdToPerference(newsList.get(0), NewsModel.NEWS_ID+tag);
					//intentNewsUpdate(newsList.get(0));
				}else if(newsModelTeamList.get(0).getNewsId() < newsList.get(0).getNewsId()){
					newsModelTeamList = newsList; 
					loaded = true;
					storeMaxIdToPerference(newsList.get(0), NewsModel.NEWS_ID+tag);
					//intentNewsUpdate(newsList.get(0));
				}else{
					return;
				}
			}
			
			newsAdapterTeam = new NewsAdapter(getActivity(), newsModelTeamList);
			newsListViewTeam.setAdapter(newsAdapterTeam);
			newsListViewTeam.setVisibility(View.VISIBLE);
			setListViewEvents(newsListViewTeam, newsAdapterTeam, tag);
		}else if(tag.equals("tag1")){
			if(newsList == null || newsList.isEmpty()){
				//Toast.makeText(getActivity(), "No News", Toast.LENGTH_SHORT).show();
				if(newsModelGlobalList == null){  
					newsModelGlobalList = new ArrayList<NewsModel>(); 
					Toast.makeText(getActivity(), getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				}
			}else{
				if(newsModelGlobalList == null || newsModelGlobalList.isEmpty()){
					newsModelGlobalList = newsList;
					loaded = true;
					storeMaxIdToPerference(newsList.get(0), NewsModel.NEWS_ID+tag);
					//intentNewsUpdate(newsList.get(0));
				}else if(newsList.get(0).getNewsId() < newsList.get(0).getNewsId()){
					newsModelGlobalList = newsList; 
					loaded = true;
					storeMaxIdToPerference(newsList.get(0), NewsModel.NEWS_ID+tag);
					//intentNewsUpdate(newsList.get(0));
				}else{
					return;
				}
			}
			
			newsAdapterGlobal = new NewsAdapter(getActivity(), newsModelGlobalList);
			newsListViewGlobal.setAdapter(newsAdapterGlobal);
			newsListViewGlobal.setVisibility(View.VISIBLE);
			setListViewEvents(newsListViewGlobal, newsAdapterGlobal, tag);
		}
		
	}
	
	@SuppressLint("CommitPrefEdits")
	private void storeMaxIdToPerference(NewsModel newsModel, String tag) {
		SharedPreferences sharePre = getActivity().getSharedPreferences(UpdateService.SHARE_PERFERENCE, Context.MODE_PRIVATE);
		Editor editSharePre = sharePre.edit();
		editSharePre.putInt(tag, newsModel.getNewsId());
		editSharePre.commit();
	}
	
	/*private void doLoadRefeshToListView(List<NewsModel> result, String tag) {
		if(tag.equals("tag0")){
			newsAdapterTeam.addHead(result);
		}else if(tag.equals("tag1")){
			newsAdapterGlobal.addHead(result);
		} 
	}*/
	
	private void doLoadOldNewsToListView(List<NewsModel> result, String tag) { 
		if(result == null || result.isEmpty()){
			//Toast.makeText(getActivity(), "No Old News", Toast.LENGTH_SHORT).show();
			oldNews=null;
			return;
		}
		
		if(tag.equals("tag0")){
			newsAdapterTeam.add(result);
		}else if(tag.equals("tag1")){
			newsAdapterGlobal.add(result);
		} 
	}
	 
	private void setListViewEvents(final PullToRefreshListView newsListView, final NewsAdapter newsAdapter, final String tag) {
		newsListView.setOnItemClickListener(this);
		
		newsListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastVisibleItem = firstVisibleItem + visibleItemCount;
				if((lastVisibleItem == totalItemCount) && loaded && totalItemCount>0){
					NewsModel nm = (NewsModel)view.getAdapter().getItem(totalItemCount-1);
					if(nm!=null && !nm.equals(oldNews)){
						
						if (NetworkUtils.isNetworkAvailable(getActivity())){
							newsLoadingFooterProcessbar.setVisibility(View.VISIBLE);
							new LoadOldNewsTask(newsListView, newsAdapter, tag).execute(getURLbyTag(nm.getNewsId(), tag));
						}else
							Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
						//Toast.makeText(getActivity(), "Toast " + i++, Toast.LENGTH_SHORT).show();
					}
					
					oldNews = nm;
				}
				
			}
		});
		
		newsListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if (NetworkUtils.isNetworkAvailable(getActivity())){
					new LoadLastNewsTask(newsListView, tag).execute(getURLbyTag(0, tag));
				}else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	@Override
	public void onItemClick(AdapterView<?> adap, View view, int pos, long id) {
		
		NewsModel news = (NewsModel) adap.getAdapter().getItem(pos);
		new PostNewsReads().execute(news.getNewsId());
		
		news.setNewsReads(news.getNewsReads()+1);
		news.setStatusView(1);
		
		Intent newsItemPage = new Intent(getActivity(), News_Item_Page.class);
		newsItemPage.putExtra(ITEM_INDEX, pos-1); 
		newsItemPage.putExtra(NEWS_TAG, tabs.getCurrentTabTag()); 
		startActivity(newsItemPage);
	}
	
	public class PostNewsReads extends AsyncTask<Integer, Void, String>{
		
		public static final String NEWS_READS_URL = "http://183.90.171.209/gs_news/post_news_reads.php";
		
		@Override
		protected String doInBackground(Integer... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("news_id", String.valueOf(params[0])));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(MemberSession.getMember().getUid())));
			
			return HttpConnectUtils.getStrHttpPostConnect(NEWS_READS_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			//Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onTabChanged(String tag) {
		if(tag.equals("tag0")){
			if(newsAdapterTeam == null) {
				if (NetworkUtils.isNetworkAvailable(getActivity()))
					new LoadOldNewsTask(newsListViewTeam, newsAdapterTeam, "tag0").execute(getURLbyTag(0, "tag0"));
				else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}
		}else if(tag.equals("tag1")){
			if(newsAdapterGlobal == null) {
				if (NetworkUtils.isNetworkAvailable(getActivity()))
					new LoadOldNewsTask(newsListViewGlobal, newsAdapterGlobal, "tag1").execute(getURLbyTag(0, "tag1"));
				else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}
		} 
	}
	
	public static List<NewsModel> getNewsListbyTag(String tag) {
		List<NewsModel> newsModelList = new ArrayList<NewsModel>(); 
			if(tag.equals("tag0")){
				newsModelList = getNewsModelTeamList();
			}else if(tag.equals("tag1")){
				newsModelList = getNewsModelGlobalList();
			}
		return newsModelList;
	}

	public static List<NewsModel> getNewsModelTeamList() {
		return newsModelTeamList;
	}

	public static void setNewsModelTeamList(List<NewsModel> newsModelTeamList) {
		News_Page.newsModelTeamList = newsModelTeamList;
	}

	public static List<NewsModel> getNewsModelGlobalList() {
		return newsModelGlobalList;
	}

	public static void setNewsModelGlobalList(List<NewsModel> newsModelGlobalList) {
		News_Page.newsModelGlobalList = newsModelGlobalList;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(tabs.getCurrentTabTag().equals("tag0") && newsAdapterTeam!=null){
			newsAdapterTeam.notifyDataSetChanged();
		}if(tabs.getCurrentTabTag().equals("tag1") && newsAdapterGlobal!=null){
			newsAdapterGlobal.notifyDataSetChanged();
		}
	}
	
}
