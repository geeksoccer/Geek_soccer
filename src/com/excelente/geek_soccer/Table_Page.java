package com.excelente.geek_soccer;

import java.net.URLEncoder;
import java.util.List;

import com.excelente.geek_soccer.adapter.TableAdapter;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.view.Boast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class Table_Page extends Fragment implements OnTabChangeListener, OnItemClickListener{
	
	public static final String TABLE_URL = "http://183.90.171.209/gs_table/get_table.php";
	
	public static final String TABLE_TYPE_ALL = "All";
	
	public static final String PREMIER_LEAGUE = "Premier League";
	public static final String LALIGA = "Laliga";
	public static final String CALCAIO_SERIE_A = "Calcaio Serie A";
	public static final String LEAGUE_DE_LEAGUE1 = "League De Ligue1";
	public static final String BUNDESLIGA = "Bundesliga";
	public static final String THAI_PREMIER_LEAGUE = "Thai Premier League";
	
	View tableView;
	private ListView tablePLLayout;
	private ListView tableBLLayout;
	private ListView tableLLLayout;
	private ListView tableGLLayout;
	private ListView tableFLLayout;
	private ListView tableTPLLayout;

	private TableAdapter plAdapter;
	private TableAdapter blAdapter;
	private TableAdapter llAdapter;
	private TableAdapter glAdapter;
	private TableAdapter flAdapter;
	private TableAdapter tplAdapter;

	private boolean flagtplAdapter = true;
	private boolean flagflAdapter = true;
	private boolean flagglAdapter = true;
	private boolean flagllAdapter = true;
	private boolean flagblAdapter = true;
	private boolean flagplAdapter = true;

	private ProgressBar tableWaitProcessbar;

	private TabHost tabs;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if (container == null) {
            return null;
        }
    	
        return inflater.inflate(R.layout.table_page, container, false);
    }
   
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initSubView();
	}

	private void initView() {
		tableView = getView();
		
		tabs = (TabHost)tableView.findViewById(R.id.tabhost); 
		tabs.setup();  
		
		setupTab(R.id.table_pl, "tag1", "", R.drawable.logo_premier_league, true);
		setupTab(R.id.table_bl, "tag2", "", R.drawable.logo_bundesliga, false);
		setupTab(R.id.table_ll, "tag3", "", R.drawable.logo_laliga, false);
		setupTab(R.id.table_gl, "tag4", "", R.drawable.logo_calcio, false);
		setupTab(R.id.table_fl, "tag5", "", R.drawable.logo_ligue1, false);
		setupTab(R.id.table_tpl, "tag6", "", R.drawable.logo_tpl, false);
		
		tabs.setCurrentTab(0);
		
		tabs.setOnTabChangedListener(this);
	}
	
	private void setupTab(Integer layoutId, String name, String label, Integer iconId, boolean selected) {

	    View tab = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    if(label.equals("")){
	    	text.setVisibility(View.GONE);
	    	
	    	final float scale = getActivity().getResources().getDisplayMetrics().density;
	    	int pixels = (int) (40 * scale + 0.5f);
	    	image.getLayoutParams().width=pixels;
	    	image.getLayoutParams().height=pixels;
	    }
	    
	    View viewSelected = tab.findViewById(R.id.selected);
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
		
		tableWaitProcessbar = (ProgressBar) tableView.findViewById(R.id.table_wait_processbar);
		tableWaitProcessbar.setVisibility(View.GONE);
		
		tablePLLayout = (ListView) tableView.findViewById(R.id.table_pl);
		tableBLLayout = (ListView) tableView.findViewById(R.id.table_bl);
		tableLLLayout = (ListView) tableView.findViewById(R.id.table_ll);
		tableGLLayout = (ListView) tableView.findViewById(R.id.table_gl);
		tableFLLayout = (ListView) tableView.findViewById(R.id.table_fl);
		tableTPLLayout = (ListView) tableView.findViewById(R.id.table_tpl); 
		
		flagtplAdapter = true;
		flagflAdapter = true;
		flagglAdapter = true;
		flagllAdapter = true;
		flagblAdapter = true;
		flagplAdapter = true;
		
		if(plAdapter == null){
			try{ 
				if(NetworkUtils.isNetworkAvailable(getActivity())){
					new LoadTableTask(tablePLLayout, plAdapter, "tag1").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + URLEncoder.encode(PREMIER_LEAGUE, "utf-8") + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
				}else
					Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		
		try{
			if(tabId.equals("tag1")){
				if(flagplAdapter){ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadTableTask(tablePLLayout, plAdapter, "tag1").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + URLEncoder.encode(PREMIER_LEAGUE, "utf-8") + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}
				setSelectedTab(0);
			}else if(tabId.equals("tag2")){
				if(flagblAdapter){
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadTableTask(tableBLLayout, blAdapter, "tag2").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + BUNDESLIGA + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}
				setSelectedTab(1);
			}else if(tabId.equals("tag3")){
				if(flagllAdapter){ 
					if(NetworkUtils.isNetworkAvailable(getActivity())){
						new LoadTableTask(tableLLLayout, llAdapter, "tag3").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + LALIGA + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
					}else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}
				setSelectedTab(2);
			}else if(tabId.equals("tag4")){
				if(flagglAdapter){ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadTableTask(tableGLLayout, glAdapter, "tag4").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + URLEncoder.encode(CALCAIO_SERIE_A, "utf-8") + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}
				setSelectedTab(3);
			}else if(tabId.equals("tag5")){
				if(flagflAdapter){ 
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadTableTask(tableFLLayout, flAdapter, "tag5").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + URLEncoder.encode(LEAGUE_DE_LEAGUE1, "utf-8") + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}
				setSelectedTab(4);
			}else if(tabId.equals("tag6")){
				if(flagtplAdapter){
					if(NetworkUtils.isNetworkAvailable(getActivity()))
						new LoadTableTask(tableTPLLayout, tplAdapter, "tag6").execute(TABLE_URL + "?" + TableModel.TABLE_LEAGUE + "=" + URLEncoder.encode(THAI_PREMIER_LEAGUE, "utf-8") + "&" + TableModel.TABLE_TYPE + "=" + TABLE_TYPE_ALL);
					else
						Toast.makeText(getActivity(), NetworkUtils.getConnectivityStatusString(getActivity()), Toast.LENGTH_SHORT).show();
				}
				setSelectedTab(5);
			}
		}catch(Exception e){
			e.printStackTrace();
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
	
	public class LoadTableTask extends AsyncTask<String, Void, List<TableModel>>{
		
		ListView listview;
		TableAdapter tableAdaptor;
		String tag;
		
		public LoadTableTask(ListView listview, TableAdapter tableAdaptor, String tag) {
			this.listview = listview; 
			this.tableAdaptor = tableAdaptor;
			this.tag = tag;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(tableAdaptor==null && tableWaitProcessbar!=null)
				tableWaitProcessbar.setVisibility(View.VISIBLE);
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
				tableAdaptor = new TableAdapter(getActivity(), result);
				listview.setAdapter(tableAdaptor);
				listview.setOnItemClickListener(Table_Page.this);
				
				if(tag.equals("tag1")){
					flagplAdapter = false;
				}else if(tag.equals("tag2")){
					flagblAdapter = false;
				}else if(tag.equals("tag3")){
					flagllAdapter = false;
				}else if(tag.equals("tag4")){
					flagglAdapter = false;
				}else if(tag.equals("tag5")){
					flagflAdapter = false;
				}else if(tag.equals("tag6")){
					flagtplAdapter = false;
				}
				
				tabs.setCurrentTabByTag(tag);
			}else{
				if(getActivity()!=null){
					Toast.makeText(getActivity(), getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
				}
			} 
			
			tableWaitProcessbar.setVisibility(View.GONE);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adap, View view, int pos, long id) {
		TableModel tm = (TableModel) adap.getAdapter().getItem(pos);
		setToastSeq(tm);
	}

	private void setToastSeq(TableModel tableModel) { 
		if(tableModel.getTableStatus().trim().equals("ucl") || tableModel.getTableStatus().trim().equals("afc")){
			showToast(getResources().getString(R.string.table_ucl));
		}else if(tableModel.getTableStatus().trim().equals("ucl_pf")){
			showToast(getResources().getString(R.string.table_ucl_match));
		}else if(tableModel.getTableStatus().trim().equals("urp")){
			showToast(getResources().getString(R.string.table_upl));
		}else if(tableModel.getTableStatus().trim().equals("fail_pf")){
			showToast(getResources().getString(R.string.table_bundes_match));
		}else if(tableModel.getTableStatus().trim().equals("fail")){
			showToast(getResources().getString(R.string.table_fail));
		}else{
			showToast(getResources().getString(R.string.table_par));
		}
	}

	private void showToast(String string) {
		Boast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}
	
}
