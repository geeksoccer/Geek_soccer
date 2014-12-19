package com.excelente.geek_soccer.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.model.TablePagerModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.Boast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class TablePagerAdapter extends PagerAdapter implements OnItemClickListener{
	
	Activity mContext;
	Map<Integer, TableItemView> tableItemViews = new HashMap<Integer, TableItemView>();
	List<TablePagerModel> tablePagerModelList;
	
	public TablePagerAdapter(Activity mContext, List<TablePagerModel> tablePagerModelList) {
		this.mContext = mContext;
		this.tablePagerModelList = tablePagerModelList;
	}

	@Override
	public int getCount() {
		return tablePagerModelList.size();
	}
	
	public class TableItemView {
		ListView tableListView;
		ProgressBar progressbar;
		TextView emptyText;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = (View) mInflater.inflate(R.layout.table_page_item, null);
		
		TableItemView tableItemView = new TableItemView();
		tableItemView.tableListView = (ListView) convertView.findViewById(R.id.table_listview);
		tableItemView.progressbar = (ProgressBar) convertView.findViewById(R.id.table_wait_processbar);
		tableItemView.emptyText = (TextView) convertView.findViewById(R.id.empty);
		
		TablePagerModel tablePagerModel = tablePagerModelList.get(position);
		doInitViews(tableItemView, tablePagerModel);
		
		tableItemViews.put(position, tableItemView);
		
		((ViewPager) container).addView(convertView,0);
		
		return convertView;
	}
	
	@Override
	public void destroyItem(ViewGroup collection, int position, Object view) {
		 
		ListView tableListView = (ListView) ((View) view).findViewById(R.id.table_listview);
		ProgressBar progressbar = (ProgressBar) ((View) view).findViewById(R.id.progressBar);
		TextView emptyText = (TextView) ((View) view).findViewById(R.id.empty);
		
		((ViewPager) collection).removeView(tableListView);
		((ViewPager) collection).removeView(progressbar);
		((ViewPager) collection).removeView(emptyText); 
        ((ViewPager) collection).removeView((View) view);
        
        tableItemViews.remove(position);
	}
	
	private void doInitViews(TableItemView tableItemView, TablePagerModel tablePagerModel) {
		TableAdapter tableAdapter = null;
		
		try{ 
			if(NetworkUtils.isNetworkAvailable(mContext)){
				new LoadTableTask(tableItemView, tableAdapter).execute(tablePagerModel.getUrl());
			}else{
				Boast.makeText(mContext, NetworkUtils.getConnectivityStatusString(mContext), Toast.LENGTH_SHORT).show();
				setMessageEmptyListView(tableAdapter, tableItemView);
			}
		}catch(Exception e){
			e.printStackTrace();
			setMessageEmptyListView(tableAdapter, tableItemView);
		}
	}
	
	private void setMessageEmptyListView(TableAdapter tableAdapter, TableItemView tableItemView) {
		tableItemView.progressbar.setVisibility(View.GONE);
		tableItemView.tableListView.setVisibility(View.GONE); 
		
		if(tableAdapter==null || tableAdapter.isEmpty())
			tableItemView.emptyText.setVisibility(View.VISIBLE);
		else
			tableItemView.emptyText.setVisibility(View.GONE);
	} 

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals( object );
	}
	
	public class LoadTableTask extends AsyncTask<String, Void, List<TableModel>>{
		
		TableItemView tableItemView;
		TableAdapter tableAdaptor;
		
		public LoadTableTask(TableItemView tableItemView, TableAdapter tableAdaptor) {
			this.tableItemView = tableItemView; 
			this.tableAdaptor = tableAdaptor;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(tableAdaptor==null && tableItemView.progressbar!=null){
				tableItemView.progressbar.setVisibility(View.VISIBLE);
				tableItemView.emptyText.setVisibility(View.GONE);
			}
		}
		
		@Override
		protected List<TableModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			//Log.e("000000000000000000", result);
			List<TableModel> tableList = TableModel.convertTableStrToList(result); 
			
			return tableList;
		}

		@Override
		protected void onPostExecute(List<TableModel> result) {
			super.onPostExecute(result);
			if(result != null){
				tableAdaptor = new TableAdapter(mContext, result);
				tableItemView.tableListView.setAdapter(tableAdaptor);
				tableItemView.tableListView.setOnItemClickListener(TablePagerAdapter.this);
				tableItemView.emptyText.setVisibility(View.GONE);
				tableItemView.progressbar.setVisibility(View.GONE);
			}else{
				if(mContext!=null){
					Boast.makeText(mContext, mContext.getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				}
				
				if(tableAdaptor == null || tableAdaptor.isEmpty()){
					tableItemView.emptyText.setVisibility(View.VISIBLE);
				}
				
				tableItemView.progressbar.setVisibility(View.GONE);
			} 
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TableModel tm = (TableModel) parent.getAdapter().getItem(position);
		setToastSeq(tm);
	}

	private void setToastSeq(TableModel tableModel) { 
		if(tableModel.getTableStatus().trim().equals("ucl") || tableModel.getTableStatus().trim().equals("afc")){
			showToast(mContext.getResources().getString(R.string.table_ucl));
		}else if(tableModel.getTableStatus().trim().equals("ucl_pf")){
			showToast(mContext.getResources().getString(R.string.table_ucl_match));
		}else if(tableModel.getTableStatus().trim().equals("urp")){
			showToast(mContext.getResources().getString(R.string.table_upl));
		}else if(tableModel.getTableStatus().trim().equals("fail_pf")){
			showToast(mContext.getResources().getString(R.string.table_bundes_match));
		}else if(tableModel.getTableStatus().trim().equals("fail")){
			showToast(mContext.getResources().getString(R.string.table_fail));
		}else{
			showToast(mContext.getResources().getString(R.string.table_par));
		}
	}

	private void showToast(String string) {
		Boast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
	}

}
