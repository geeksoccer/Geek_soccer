package com.excelente.geek_soccer.adapter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.Hilight_Item_Page;
import com.excelente.geek_soccer.Hilight_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.Boast;
import com.excelente.geek_soccer.view.PullToRefreshListView;
import com.excelente.geek_soccer.view.PullToRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class HilightPagerAdapter extends BaseAdapter{

	Activity activity; 
	public static List<TabModel> tabModelList;
	HilightModel oldHilight; 
	private boolean loaded;
	
	public HilightPagerAdapter(Activity activity, List<TabModel> tabModelList) {
		this.activity = activity;
		HilightPagerAdapter.tabModelList = tabModelList;
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
			if(HilightPagerAdapter.tabModelList.get(i).adapter!=null){
				HilightPagerAdapter.tabModelList.get(i).adapter.notifyDataSetChanged();
			}
		}
	}
	
	class ViewItem{
		PullToRefreshListView hilightListView;
		ProgressBar hilightWaitProgressBar;
		ProgressBar hilightLoadingFooterProcessbar;
		TextView textEmpty;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItem viewItem = null;
		
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) mInflater.inflate(R.layout.news_pager_item, parent, false);
			
			viewItem = new ViewItem();
			viewItem.hilightWaitProgressBar = (ProgressBar) convertView.findViewById(R.id.news_wait_processbar);
			viewItem.hilightLoadingFooterProcessbar = (ProgressBar) convertView.findViewById(R.id.news_loading_footer_processbar);
			viewItem.hilightListView = (PullToRefreshListView) convertView.findViewById(R.id.news_listview);
			viewItem.textEmpty = (TextView) convertView.findViewById(R.id.empty);
			
			convertView.setTag(viewItem);
		}else{
			viewItem = (ViewItem) convertView.getTag();
		}
		
		TabModel tabModel = (TabModel) getItem(position);
		doInitView(position, viewItem ,tabModel);
		
		return convertView;
	}

	private void doInitView(final int position, final ViewItem viewItem, final TabModel tabModel) {
		viewItem.hilightLoadingFooterProcessbar.setVisibility(View.GONE);
		viewItem.textEmpty.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View v) {
				try{ 
					if(NetworkUtils.isNetworkAvailable(activity)){
						new LoadOldHilightTask(viewItem, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, tabModel).execute(getURLbyTag(activity, 0, tabModel.getUrl()));
					}else{
						Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
						setMessageEmptyListView(HilightPagerAdapter.tabModelList.get(position).hilightList, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
					}
				}catch(Exception e){
					e.printStackTrace();
					setMessageEmptyListView(HilightPagerAdapter.tabModelList.get(position).hilightList, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
				}
			}
		});
		
		if(HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter == null){
			try{ 
				if(NetworkUtils.isNetworkAvailable(activity)){
					new LoadOldHilightTask(viewItem, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, tabModel).execute(getURLbyTag(activity, 0, tabModel.getUrl()));
				}else{
					Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
					setMessageEmptyListView(HilightPagerAdapter.tabModelList.get(position).hilightList, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
				}
			}catch(Exception e){
				e.printStackTrace();
				setMessageEmptyListView(HilightPagerAdapter.tabModelList.get(position).hilightList, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
			}
		}
		//setMessageEmptyListView(HilightPagerAdapter.tabModelList.get(position).hilightList, (HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, viewItem);
	}
	
	public static String getURLbyTag(Context context, int id, String tag) {
		String url = ""; 
		 
		try{
			url = ControllParameter.GET_HILIGHT_URL + "?" + HilightModel.HILIGHT_ID + "=" + id + "&" + HilightModel.HILIGHT_TYPE + "=" + URLEncoder.encode(tag, "utf-8") + "&member_id=" + SessionManager.getMember(context).getUid() + "&m_token=" + SessionManager.getMember(context).getToken();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return url;
	}
	
	private void setMessageEmptyListView(List<HilightModel> hilightList, HilightAdapter hilightAdapter, ViewItem viewItem) {
		viewItem.hilightWaitProgressBar.setVisibility(View.GONE);
		viewItem.hilightListView.setVisibility(View.GONE);
		
		if(hilightAdapter == null || hilightAdapter.isEmpty())
			viewItem.textEmpty.setVisibility(View.VISIBLE);
		else
			viewItem.textEmpty.setVisibility(View.GONE);
	} 
	
	public class LoadOldHilightTask extends AsyncTask<String, Void, List<HilightModel>>{ 
		
		ViewItem viewItem;
		HilightAdapter hilightAdapter;
		TabModel tabModel; 
		  
		public LoadOldHilightTask(ViewItem viewItem, BaseAdapter adapter, TabModel tabModel) {
			this.viewItem = viewItem;
			this.hilightAdapter = (HilightAdapter) adapter; 
			this.tabModel = tabModel;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(hilightAdapter==null && viewItem.hilightWaitProgressBar!=null){
				viewItem.hilightLoadingFooterProcessbar.setVisibility(View.GONE);
				viewItem.hilightWaitProgressBar.setVisibility(View.VISIBLE);
				viewItem.textEmpty.setVisibility(View.GONE);
			}else{
				viewItem.hilightLoadingFooterProcessbar.setVisibility(View.VISIBLE);
				viewItem.hilightWaitProgressBar.setVisibility(View.GONE);
				viewItem.textEmpty.setVisibility(View.GONE);
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
			
			viewItem.hilightLoadingFooterProcessbar.setVisibility(View.GONE);
			viewItem.hilightWaitProgressBar.setVisibility(View.GONE);
			
			if(hilightAdapter!=null && hilightAdapter.getCount() > 0){
				doLoadOldHilightToListView(result, tabModel);  
			}else{
				doLoadHilightToListView(result, viewItem, tabModel);
			}
			
			if(hilightAdapter==null || hilightAdapter.getCount() < 100){
				loaded = true;
			}else{
				loaded = false;
			}
		}

	}
	
	private void doLoadHilightToListView(List<HilightModel> hilightList, ViewItem viewItem, TabModel tabModel) {
		
			HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).hilightList = getListView(hilightList, tabModel, HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).hilightList);
			
			if(HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).hilightList==null)
				return;
			
			HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter = new HilightAdapter(activity, HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).hilightList);
			viewItem.hilightListView.setAdapter(HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter);
			
			if(!HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter.isEmpty())
				viewItem.hilightListView.setVisibility(View.VISIBLE);
			
			if(HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter.isEmpty()){
				viewItem.textEmpty.setVisibility(View.VISIBLE);
			}
			
			setListViewEvents(viewItem, HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter, tabModel);
		
	}
	
	private List<HilightModel> getListView(List<HilightModel> hilightlist, TabModel tabModel, List<HilightModel> hilightModelList) {
		//tabs.setCurrentTabByTag(tag);
		
		if(hilightlist == null || hilightlist.isEmpty()){ 
			//Toast.makeText(getActivity(), "No News", Toast.LENGTH_SHORT).show(); 
			if(hilightModelList == null && activity!=null){  
				Boast.makeText(activity, activity.getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				return new ArrayList<HilightModel>(); 
			}
		}else{
			if(hilightModelList == null || hilightModelList.isEmpty()){
				loaded = true;
				
				if(tabModel.getUrl().equals(Hilight_Page.HILIGHT_TYPE_ALL))
					storeMaxIdToPerference(hilightlist.get(0), HilightModel.HILIGHT_ID);
				return hilightlist;
			}else if(hilightModelList.get(0).getHilightId() < hilightlist.get(0).getHilightId()){
				loaded = true;
				
				if(tabModel.getUrl().equals(Hilight_Page.HILIGHT_TYPE_ALL))
					storeMaxIdToPerference(hilightlist.get(0), HilightModel.HILIGHT_ID);
				return hilightlist;
			}
		}
		
		return hilightModelList;
	}
	
	@SuppressLint("CommitPrefEdits")
	private void storeMaxIdToPerference(HilightModel hilightModel, String tag) {
		if(activity != null){
			SharedPreferences sharePre = activity.getSharedPreferences(UpdateService.SHARE_PERFERENCE, Context.MODE_PRIVATE);
			Editor editSharePre = sharePre.edit();
			editSharePre.putInt(tag, hilightModel.getHilightId());
			editSharePre.commit();
		}
	}

	private void doLoadOldHilightToListView(List<HilightModel> result, TabModel tabModel) { 
		if(result == null || result.isEmpty()){
			return;
		}
		
		((HilightAdapter) HilightPagerAdapter.tabModelList.get(Integer.valueOf(tabModel.getIndex())).adapter).add(result);
		
	}
	
	public class LoadLastHilightTask extends AsyncTask<String, Void, List<HilightModel>>{ 
		
		ViewItem viewItem;
		TabModel tabModel;
		
		public LoadLastHilightTask(ViewItem viewItem, TabModel tabModel) { 
			this.viewItem = viewItem;
			this.tabModel = tabModel;
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
				doLoadHilightToListView(result, viewItem, tabModel); 
			}
			
			viewItem.hilightListView.onRefreshComplete();
		}

	}
	
	private void setListViewEvents(final ViewItem viewItem, final BaseAdapter adapter, final TabModel tabModel) {
		
		viewItem.hilightListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adap, View v, int pos, long id) {
				pos++;
				HilightModel hilight = (HilightModel) adap.getAdapter().getItem(pos); 
				
				if(NetworkUtils.isNetworkAvailable(activity)){
					hilight.setHilightViews(hilight.getHilightViews()+1);
					hilight.setStatusView(1);
					
					new PostHilightReads().execute(hilight.getHilightId()); 
				}else
					Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
				
				Intent hilightItemPage = new Intent(activity, Hilight_Item_Page.class);
				hilightItemPage.putExtra(Hilight_Page.HILIGHT_ITEM_INDEX, pos-1);
				hilightItemPage.putExtra(Hilight_Page.HILIGHT_TAG, tabModel.getUrl());
				hilightItemPage.putExtra(Hilight_Page.HILIGHT_INDEX, tabModel.getIndex());
				activity.startActivity(hilightItemPage);
			}
		}); 
		
		viewItem.hilightListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastVisibleItem = firstVisibleItem + visibleItemCount;
				if((lastVisibleItem == totalItemCount) && loaded && totalItemCount>0){
					HilightModel hm = (HilightModel)view.getAdapter().getItem(totalItemCount-1);  
					if(hm!=null && !hm.equals(oldHilight)){
						
						if(NetworkUtils.isNetworkAvailable(activity)){
							viewItem.hilightLoadingFooterProcessbar.setVisibility(View.VISIBLE);
							new LoadOldHilightTask(viewItem, adapter, tabModel).execute(getURLbyTag(activity, hm.getHilightId(), tabModel.getUrl()));  
						}else
							Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
						//Toast.makeText(getActivity(), "Toast " + i++, Toast.LENGTH_SHORT).show();
					}
					
					oldHilight = hm;
				}
				
			}
		});
		
		viewItem.hilightListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if(NetworkUtils.isNetworkAvailable(activity)){
					new LoadLastHilightTask(viewItem, tabModel).execute(getURLbyTag(activity, 0, tabModel.getUrl())); 
				}else{
					Boast.makeText(activity, NetworkUtils.getConnectivityStatusString(activity), Toast.LENGTH_SHORT).show();
					viewItem.hilightListView.onRefreshComplete();
				}
			}
		}); 
	}
	
	public class PostHilightReads extends AsyncTask<Integer, Void, String>{
		
		@Override
		protected String doInBackground(Integer... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("hilight_id", String.valueOf(params[0])));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(SessionManager.getMember(activity).getUid())));
			paramsPost.add(new BasicNameValuePair("m_token", String.valueOf(SessionManager.getMember(activity).getToken())));
			
			return HttpConnectUtils.getStrHttpPostConnect(ControllParameter.HILIGHT_READS_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

	} 

}
