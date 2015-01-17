package com.excelente.geek_soccer.adapter;

import java.util.List;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.model.TabModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.Boast;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TablePagerAdapter extends BaseAdapter implements OnItemClickListener{
	
	Activity mContext;
	List<TabModel> tablePagerModelList;
	
	public TablePagerAdapter(Activity mContext, List<TabModel> tablePagerModelList) {
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
	public Object getItem(int position) {
		return tablePagerModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tablePagerModelList.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TableItemView tableItemView = null;
		
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) mInflater.inflate(R.layout.table_pager_item, parent, false);
			
			tableItemView = new TableItemView();
			tableItemView.tableListView = (ListView) convertView.findViewById(R.id.table_listview);
			tableItemView.progressbar = (ProgressBar) convertView.findViewById(R.id.table_wait_processbar);
			tableItemView.emptyText = (TextView) convertView.findViewById(R.id.empty);
			
			convertView.setTag(tableItemView);
		}else{
			tableItemView = (TableItemView) convertView.getTag();
		}
		
		TabModel tablePagerModel = (TabModel) getItem(position);
		doInitViews(tableItemView, tablePagerModel);
		
		return convertView;
	}
	
	private void doInitViews(final TableItemView tableItemView, final TabModel tablePagerModel) {
		final TableAdapter tableAdapter = null;
		
		tableItemView.emptyText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
		});
		
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
		
		//setMessageEmptyListView(tableAdapter, tableItemView);
	}
	
	private void setMessageEmptyListView(TableAdapter tableAdapter, TableItemView tableItemView) {
		tableItemView.progressbar.setVisibility(View.GONE);
		tableItemView.tableListView.setVisibility(View.GONE); 
		
		if(tableAdapter==null || tableAdapter.isEmpty())
			tableItemView.emptyText.setVisibility(View.VISIBLE);
		else
			tableItemView.emptyText.setVisibility(View.GONE);
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
			
			if((tableAdaptor==null || tableAdaptor.isEmpty()) && tableItemView.progressbar!=null){
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
				tableItemView.tableListView.setVisibility(View.VISIBLE);
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
