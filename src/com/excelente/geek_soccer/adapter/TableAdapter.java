package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.TableModel;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TableAdapter extends BaseAdapter{
	
	Activity activity;
	Context context;
	List<TableModel> tableList;
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_ITEM_TEAM = 1;
    private static final int TYPE_MAX_COUNT = TYPE_ITEM_TEAM + 1;
	private int count_ani = -1;
	private String teamNameTH;
	
	HashMap<String, Bitmap> urlBitmap;
	
	public TableAdapter(Activity context, List<TableModel> tableList) {
		this.activity = context;
		this.context = context;
		this.tableList = tableList;
		this.teamNameTH = SessionManager.getMember(context).getTeam().getTeamNameTH();
		this.urlBitmap = new HashMap<String, Bitmap>();
	}
	
	@Override
	public int getItemViewType(int position) {
		TableModel tableModel = (TableModel) getItem(position);
		if(tableModel!=null && !tableModel.getTableTeam().trim().equals("") && tableModel.getTableTeam().trim().equals(teamNameTH)){
			return TYPE_ITEM_TEAM;
		}
		return TYPE_ITEM;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}
	
	public class TableHolder{
		TextView tableSeq;
		ImageView tableTeamImage;
		TextView tableTeam;
		TextView tableMatch;
		TextView tableWin;
		TextView tableDraw;
		TextView tableLose;
		TextView tableGetGoal;
		TextView tableLoseGoal;
		TextView tableResultGoal;
		TextView tableMark;
		LinearLayout table_layout;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TableHolder tableHolder; 
		final TableModel tableModel = (TableModel) getItem(position); 
        
        int type = getItemViewType(position);
        if(convertView == null){
        	LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	switch (type) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.table_item_page, parent, false);
					break;
				case TYPE_ITEM_TEAM:
					convertView = mInflater.inflate(R.layout.table_item_team_page, parent, false);
					break;
			}
    		
    		tableHolder = new TableHolder();
    		tableHolder.table_layout = (LinearLayout) convertView.findViewById(R.id.table_layout);
    		tableHolder.tableSeq = (TextView) convertView.findViewById(R.id.table_seq);
    		tableHolder.tableTeamImage = (ImageView) convertView.findViewById(R.id.table_team_image);
    		tableHolder.tableTeam = (TextView) convertView.findViewById(R.id.table_team);
    		tableHolder.tableMatch = (TextView) convertView.findViewById(R.id.table_match);
    		tableHolder.tableWin = (TextView) convertView.findViewById(R.id.table_win);
    		tableHolder.tableDraw = (TextView) convertView.findViewById(R.id.table_draw);
    		tableHolder.tableLose = (TextView) convertView.findViewById(R.id.table_lose);
    		tableHolder.tableGetGoal = (TextView) convertView.findViewById(R.id.table_get_goal);
    		tableHolder.tableLoseGoal = (TextView) convertView.findViewById(R.id.table_lose_goal);
    		tableHolder.tableResultGoal = (TextView) convertView.findViewById(R.id.table_result_goal);
    		tableHolder.tableMark = (TextView) convertView.findViewById(R.id.table_mark);
    		
    		convertView.setTag(tableHolder);
    		
        }else{
        	tableHolder = (TableHolder) convertView.getTag();
        }
      
        setThemeToView(tableHolder, type);
        //final File cacheFile = ImageLoader.getInstance().getDiscCache().get(tableModel.getTableTeamImage().replace(".gif", ".png"));
        if(urlBitmap.containsKey(tableModel.getTableTeamImage().replace(".gif", ".png"))){
        	tableHolder.tableTeamImage.setImageBitmap(urlBitmap.get(tableModel.getTableTeamImage().replace(".gif", ".png"))); 
        }/*else if(cacheFile.exists()){
        	new Thread(new Runnable() {
				
				@Override
				public void run() { 
					final Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
					urlBitmap.put(tableModel.getTableTeamImage().replace(".gif", ".png"), bm);
					
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tableHolder.tableTeamImage.setImageBitmap(bm);  
						}
					});
				}

			}).start();
        }*/else{
		    doConfigImageLoader(10, 10);
		    ImageLoader.getInstance().displayImage(tableModel.getTableTeamImage().replace(".gif", ".png"), tableHolder.tableTeamImage, getOptionImageLoader(tableModel.getTableTeamImage().replace(".gif", ".png")), new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					
				}
				
				@Override
				public void onLoadingComplete(final String url, View arg1, final Bitmap bitmap) {
					//Log.e("IMAGELOADER", url);
					urlBitmap.put(url, bitmap);
					new Thread(new Runnable() {
						@Override
						public void run() {
							SessionManager.createNewImageSession(context, url, bitmap);
						}
					}).start();
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					
				}
			});
		}
		    
    		tableHolder.tableSeq.setText(String.valueOf(tableModel.getTableSeq())); 
    		tableHolder.tableTeam.setText(tableModel.getTableTeam());
    		tableHolder.tableMatch.setText(String.valueOf(tableModel.getTableMatch()));
    		tableHolder.tableWin.setText(String.valueOf(tableModel.getTableWin()));
    		tableHolder.tableDraw.setText(String.valueOf(tableModel.getTableDraw()));
    		tableHolder.tableLose.setText(String.valueOf(tableModel.getTableLose()));
    		tableHolder.tableGetGoal.setText(String.valueOf(tableModel.getTableGetGoal()));
    		tableHolder.tableLoseGoal.setText(String.valueOf(tableModel.getTableLoseGoal()));
    		tableHolder.tableResultGoal.setText(String.valueOf(tableModel.getTableResultGoal()));
    		tableHolder.tableMark.setText(String.valueOf(tableModel.getTableMark()));
    		
    		if(TYPE_ITEM == type)
    			setSeqColor(convertView, tableModel);
    		
    		if(count_ani < position){
            	//convertView.setAnimation(AnimationUtils.loadAnimation(context, R.drawable.listview_anim));
            }
    		count_ani = position;
		
		return convertView;
	}

	private void setThemeToView(TableHolder tableHolder, int type) {
		switch (type) { 
			case TYPE_ITEM_TEAM:
				ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, tableHolder.table_layout);
				break;
		}
	}

	private int getColorSeq(String status, int seq){
		if(status.trim().equals("ucl") || status.trim().equals("afc")){
			return R.drawable.bg_press_table_green;
		}else if(status.trim().equals("ucl_pf")){
			return R.drawable.bg_press_table_green_light;
		}else if(status.trim().equals("urp")){
			return R.drawable.bg_press_table_blue_light;
		}else if(status.trim().equals("fail_pf")){
			return R.drawable.bg_press_table_yellow;
		}else if(status.trim().equals("fail")){
			return R.drawable.bg_press_table_red;
		}else{
			if(seq%2 == 0){ 
				return R.drawable.bg_press_table_gray; 
			}else{
				return R.drawable.bg_press_table_white; 
			}
		}
	}

	private void setSeqColor(View convertView, TableModel tableModel) {
		convertView.setBackgroundResource(getColorSeq(tableModel.getTableStatus(), tableModel.getTableSeq())); 
	}

	@Override
	public int getCount() {
		return tableList.size();
	}

	@Override
	public Object getItem(int position) {
		return tableList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tableList.indexOf(getItem(position));  
	}
	
	private void doConfigImageLoader(int w, int h) {
		
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		        .memoryCacheExtraOptions(w, h) // default = device screen dimensions
		        .discCacheExtraOptions(w, h, CompressFormat.PNG, 75, null)
		        .threadPoolSize(3) // default
		        .threadPriority(Thread.NORM_PRIORITY - 1) // default
		        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		        .denyCacheImageMultipleSizesInMemory()
		        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		        .memoryCacheSize(2 * 1024 * 1024)
		        .memoryCacheSizePercentage(13) // default
		        .discCache(new UnlimitedDiscCache(cacheDir)) // default
		        .discCacheSize(20 * 1024 * 1024)
		        .discCacheFileCount(100)
		        .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
		        .imageDownloader(new CustomImageDownaloder(context))
		        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		        .writeDebugLogs()
		        .build();
		ImageLoader.getInstance().init(config);
	}
	
	private DisplayImageOptions getOptionImageLoader(String url) {
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "http://localhost");
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	        //.showImageOnLoading(R.drawable.soccer_icon) // resource or drawable
	        .resetViewBeforeLoading(true)  // default
	        //.delayBeforeLoading(500)
	        .cacheInMemory(false)
	        .cacheOnDisc(false)
	        .considerExifParams(false) // default
	        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
	        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
	        //.decodingOptions()
	        .displayer(new SimpleBitmapDisplayer()) // default
	        .handler(new Handler()) // default
	        .extraForDownloader(headers)
	        .build();
		
		return options;
	}
	
	public class CustomImageDownaloder extends BaseImageDownloader {

	    public CustomImageDownaloder(Context context) {
	        super(context);
	    }

	    public CustomImageDownaloder(Context context, int connectTimeout, int readTimeout) {
	        super(context, connectTimeout, readTimeout);
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
	        HttpURLConnection conn = super.createConnection(url, extra);
	        Map<String, String> headers = (Map<String, String>) extra;
	        if (headers != null) {
	            for (Map.Entry<String, String> header : headers.entrySet()) {
	                conn.setRequestProperty(header.getKey(), header.getValue());
	            }
	        }
	        return conn;
	    }
	}


	public void add(List<TableModel> tableList) { 
		for (TableModel tableModel : tableList) { 
			this.tableList.add(tableModel);
		}
		
		notifyDataSetChanged();
	}
	
}
