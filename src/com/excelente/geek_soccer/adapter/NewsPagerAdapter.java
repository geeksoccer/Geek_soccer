package com.excelente.geek_soccer.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.News_Item_Page;
import com.excelente.geek_soccer.News_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.Boast;
import com.excelente.geek_soccer.view.PullToRefreshListView;
import com.excelente.geek_soccer.view.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class NewsPagerAdapter extends BaseAdapter{
	
	NewsModel oldNews;
	Boolean loaded;
	Activity activity;
	public static List<TabModel> tabModelList;
	
	public NewsPagerAdapter(Activity activity, List<TabModel> tabModelList) {
		this.activity = activity;
		NewsPagerAdapter.tabModelList = tabModelList;
		this.loaded = false;
	} 

	@Override
	public int getCount() {
		return tabModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return tabModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tabModelList.indexOf(getItem(position));
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		for (int i = 0; i < getCount(); i++) {
			if(NewsPagerAdapter.tabModelList.get(i).adapter!=null){
				NewsPagerAdapter.tabModelList.get(i).adapter.notifyDataSetChanged();
			}
		}
	}

	class ViewItem{
		PullToRefreshListView newsListView;
		ProgressBar newsWaitProgressBar;
		ProgressBar newsLoadingFooterProcessbar;
		TextView textEmpty;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItem viewItem = null;
		
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) mInflater.inflate(R.layout.news_pager_item, parent, false);
			
			viewItem = new ViewItem();
			viewItem.newsWaitProgressBar = (ProgressBar) convertView.findViewById(R.id.news_wait_processbar);
			viewItem.newsLoadingFooterProcessbar = (ProgressBar) convertView.findViewById(R.id.news_loading_footer_processbar);
			viewItem.newsListView = (PullToRefreshListView) convertView.findViewById(R.id.news_listview);
			viewItem.textEmpty = (TextView) convertView.findViewById(R.id.empty);
			
			convertView.setTag(viewItem);
		}else{
			viewItem = (ViewItem) convertView.getTag();
		}
		
		TabModel tabModel = (TabModel) getItem(position);
		doInitView(viewItem ,tabModel);
		
		return convertView;
	}

	private void doInitView(final ViewItem viewItem, final TabModel tabModel) {
		viewItem.newsLoadingFooterProcessbar.setVisibility(View.GONE);
		viewItem.textEmpty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (NetworkUtils.isNetworkAvailable(activity)) {
					new LoadOldNewsTask(viewItem, (NewsAdapter) NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, tabModel).execute(getURLbyTag(activity, 0, tabModel.getUrl()));
				} else {
					Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
					setMessageEmptyListView((NewsAdapter) NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
				}
			}
		});
		
		if (NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter == null) {
			if (NetworkUtils.isNetworkAvailable(activity)) {
				new LoadOldNewsTask(viewItem, (NewsAdapter) NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, tabModel).execute(getURLbyTag(activity, 0, tabModel.getUrl()));
			} else {
				Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
				setMessageEmptyListView((NewsAdapter) NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
			}
		}
	}
	
	private void setMessageEmptyListView(NewsAdapter newsAdapter, ViewItem viewItem) {
		viewItem.newsWaitProgressBar.setVisibility(View.GONE);
		viewItem.newsListView.setVisibility(View.GONE);
		
		if(newsAdapter==null || newsAdapter.isEmpty())
			viewItem.textEmpty.setVisibility(View.VISIBLE);
		else
			viewItem.textEmpty.setVisibility(View.GONE);
	}
	
	public static String getURLbyTag(Context context, int id, String tag) {
		String url = ControllParameter.GET_NEWS_URL + "?" + NewsModel.NEWS_TEAM_ID + "=" + tag + "&" + NewsModel.NEWS_ID + "=" + id + "&member_id="+ SessionManager.getMember(context).getUid() + "&m_token="+SessionManager.getMember(context).getToken();
		return url;
	}
	
	public class LoadOldNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		ViewItem viewItem;
		NewsAdapter newsAdapter;
		TabModel tabModel;
		
		public LoadOldNewsTask(ViewItem viewItem, NewsAdapter newsAdapter, TabModel tabModel) {
			this.viewItem = viewItem;
			this.newsAdapter = newsAdapter;
			this.tabModel = tabModel;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(newsAdapter==null && viewItem.newsWaitProgressBar!=null){
				viewItem.newsWaitProgressBar.setVisibility(View.VISIBLE);
				viewItem.textEmpty.setVisibility(View.GONE);
			}
		}
		
		@Override
		protected List<NewsModel> doInBackground(String... params) {
			
			if(newsAdapter!=null && newsAdapter.getCount()>100)
				return null;
			
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
				doLoadOldNewsToListView(result, tabModel);
				viewItem.newsLoadingFooterProcessbar.setVisibility(View.GONE);
			}else{
				doLoadNewsToListView(result, viewItem, tabModel);
				viewItem.newsWaitProgressBar.setVisibility(View.GONE);
			}
			
			if(newsAdapter==null || newsAdapter.getCount() < 100){
				loaded = true;
			}else{
				loaded = false;
			}
		}

	}
	
	public class LoadLastNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		ViewItem viewItem;
		NewsAdapter newsAdapter;
		TabModel tabModel;
		 
		public LoadLastNewsTask(ViewItem viewItem, NewsAdapter newsAdapter, TabModel tabModel) {
			this.viewItem = viewItem;
			this.newsAdapter = newsAdapter;
			this.tabModel = tabModel;
		}
		
		@Override
		protected List<NewsModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			
			List<NewsModel> newsList = NewsModel.convertNewsStrToList(result);
			
			return newsList;
		}

		@Override
		protected void onPostExecute(List<NewsModel> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				doLoadNewsToListView(result, viewItem, tabModel);
			}
			
			viewItem.newsListView.onRefreshComplete();
		}

	}
	
	private void doLoadNewsToListView(List<NewsModel> newsList, ViewItem viewItem, TabModel tabModel) {
		if(activity == null){
			return;
		}
		
		if(newsList == null || newsList.isEmpty()){
			//Toast.makeText(getActivity(), "No News", Toast.LENGTH_SHORT).show();
			if(NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList == null && activity!=null){  
				NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList = new ArrayList<NewsModel>(); 
				Boast.makeText(activity, activity.getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
			}
		}else{
			if(NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList == null || NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList.isEmpty()){
				NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList = newsList;
				loaded = true;
				storeMaxIdToPerference(newsList.get(0), NewsModel.NEWS_ID+"tag"+tabModel.getIndex());
				//intentNewsUpdate(newsList.get(0));
			}else if(NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList.get(0).getNewsId() < newsList.get(0).getNewsId()){
				NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList = newsList; 
				loaded = true;
				storeMaxIdToPerference(newsList.get(0), NewsModel.NEWS_ID+"tag"+tabModel.getIndex());
				//intentNewsUpdate(newsList.get(0));
			}else{
				return;
			}
		}
			
			NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter = new NewsAdapter(activity, NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).newsList);
			viewItem.newsListView.setAdapter(NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter);
			if(!NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter.isEmpty()){
				viewItem.newsListView.setVisibility(View.VISIBLE);
			}
			
			if(NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter.isEmpty()){
				viewItem.textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents((NewsAdapter) NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem, tabModel);
		
	}
	
	private void storeMaxIdToPerference(NewsModel newsModel, String tag) {
		if(activity != null){
			SharedPreferences sharePre = activity.getSharedPreferences(UpdateService.SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			editSharePre.putInt(tag, newsModel.getNewsId());
			editSharePre.commit(); 
		}
	}
	
	private void doLoadOldNewsToListView(List<NewsModel> result, TabModel tabModel) { 
		if(result == null || result.isEmpty()){
			oldNews=null; 
			return;
		}
		 
		((NewsAdapter) NewsPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter).add(result);
	}
	 
	private void setListViewEvents(final NewsAdapter newsAdapter, final ViewItem viewItem, final TabModel tabModel) {
		viewItem.newsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adap, View view, int pos, long id) {
				pos++;
				NewsModel news = (NewsModel) adap.getAdapter().getItem(pos);
				new PostNewsReads().execute(news.getNewsId());
				
				news.setNewsReads(news.getNewsReads()+1);
				news.setStatusView(1);
				
				Intent newsItemPage = new Intent(activity, News_Item_Page.class);
				newsItemPage.putExtra(News_Page.ITEM_INDEX, pos-1); 
				newsItemPage.putExtra(News_Page.NEWS_TAG, tabModel.getUrl());
				newsItemPage.putExtra(News_Page.NEWS_POSITION, tabModel.getIndex());
				activity.startActivity(newsItemPage);
			}
		});
		
		viewItem.newsListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastVisibleItem = firstVisibleItem + visibleItemCount;
				if((lastVisibleItem == totalItemCount) && loaded && totalItemCount>0){
					NewsModel nm = (NewsModel)view.getAdapter().getItem(totalItemCount-1);
					if(nm!=null && !nm.equals(oldNews)){
						
						if (NetworkUtils.isNetworkAvailable(activity)){
							viewItem.newsLoadingFooterProcessbar.setVisibility(View.VISIBLE);
							new LoadOldNewsTask(viewItem, newsAdapter, tabModel).execute(getURLbyTag(activity, nm.getNewsId(), tabModel.getUrl()));
						}else{
							Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
						}//Toast.makeText(getActivity(), "Toast " + i++, Toast.LENGTH_SHORT).show();
					}
					
					oldNews = nm;
				}
				
			}
		});
		
		viewItem.newsListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if (NetworkUtils.isNetworkAvailable(activity)){
					new LoadLastNewsTask(viewItem, newsAdapter, tabModel).execute(getURLbyTag(activity, 0, tabModel.getUrl()));
				}else{
					viewItem.newsListView.onRefreshComplete();
					Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}
	
	public class PostNewsReads extends AsyncTask<Integer, Void, String>{
		
		
		@Override
		protected String doInBackground(Integer... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("news_id", String.valueOf(params[0])));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(SessionManager.getMember(activity).getUid())));
			paramsPost.add(new BasicNameValuePair("m_token", String.valueOf(SessionManager.getMember(activity).getToken())));
			
			return HttpConnectUtils.getStrHttpPostConnect(ControllParameter.NEWS_READS_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

	}

}
