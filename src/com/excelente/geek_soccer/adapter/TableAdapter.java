package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.Table_Page;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.model.TableModel;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TableAdapter extends BaseAdapter{
	
	Context context;
	List<TableModel> tableList;
	private int count_ani = -1;
	
	HashMap<String, Bitmap> urlBitmap = new HashMap<String, Bitmap>();
	
	public TableAdapter(Context context, List<TableModel> tableList) {
		this.context = context;
		this.tableList = tableList;
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
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TableHolder tableHolder; 
		TableModel tableModel = (TableModel) getItem(position); 
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
       
        if(convertView == null){
    		
    		convertView = mInflater.inflate(R.layout.table_item_page, parent, false);
    		
    		tableHolder = new TableHolder();
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
     
        if(urlBitmap.containsKey(tableModel.getTableTeamImage().replace(".gif", ".png"))){
        	tableHolder.tableTeamImage.setImageBitmap(urlBitmap.get(tableModel.getTableTeamImage().replace(".gif", ".png"))); 
        }else{
		    doConfigImageLoader(10, 10);
		    ImageLoader.getInstance().displayImage(tableModel.getTableTeamImage().replace(".gif", ".png"), tableHolder.tableTeamImage, getOptionImageLoader(tableModel.getTableTeamImage().replace(".gif", ".png")), new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					
				}
				
				@Override
				public void onLoadingComplete(String url, View arg1, Bitmap bitmap) {
					//Log.e("IMAGELOADER", url);
					urlBitmap.put(url, bitmap);
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
    		
    		setSeqColor(convertView, tableModel);
    		
    		if(count_ani < position){
            	//convertView.setAnimation(AnimationUtils.loadAnimation(context, R.drawable.listview_anim));
            }
    		count_ani = position;
		
		return convertView;
	}

	private void setSeqColor(View convertView, TableModel tableModel) { 
		if(tableModel.getTableSeq()%2 == 0){ 
			convertView.setBackgroundResource(R.drawable.bg_press_table_gray); 
		}else{
			convertView.setBackgroundResource(R.drawable.bg_press_table_white); 
		}
		
		if(tableModel.getTableLeague().equals(Table_Page.PREMIER_LEAGUE)){
			if(tableModel.getTableSeq() < 4){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green); 
			}else if(tableModel.getTableSeq() < 5){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green_light); 
			}else if(tableModel.getTableSeq() < 6){
				convertView.setBackgroundResource(R.drawable.bg_press_table_blue_light); 
			}else if(tableModel.getTableSeq() > 17){
				convertView.setBackgroundResource(R.drawable.bg_press_table_red); 
			}
		}else if(tableModel.getTableLeague().equals(Table_Page.BUNDESLIGA)){
			if(tableModel.getTableSeq() < 4){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green); 
			}else if(tableModel.getTableSeq() < 5){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green_light); 
			}else if(tableModel.getTableSeq() < 7){
				convertView.setBackgroundResource(R.drawable.bg_press_table_blue_light); 
			}else if(tableModel.getTableSeq() > 16){
				convertView.setBackgroundResource(R.drawable.bg_press_table_red); 
			}else if(tableModel.getTableSeq() > 15){
				convertView.setBackgroundResource(R.drawable.bg_press_table_yellow); 
			}
		}else if(tableModel.getTableLeague().equals(Table_Page.LALIGA)){
			if(tableModel.getTableSeq() < 4){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green); 
			}else if(tableModel.getTableSeq() < 5){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green_light); 
			}else if(tableModel.getTableSeq() < 7){
				convertView.setBackgroundResource(R.drawable.bg_press_table_blue_light); 
			}else if(tableModel.getTableSeq() > 17){
				convertView.setBackgroundResource(R.drawable.bg_press_table_red); 
			}
		}else if(tableModel.getTableLeague().equals(Table_Page.CALCAIO_SERIE_A)){
			if(tableModel.getTableSeq() < 3){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green); 
			}else if(tableModel.getTableSeq() < 4){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green_light); 
			}else if(tableModel.getTableSeq() < 6){
				convertView.setBackgroundResource(R.drawable.bg_press_table_blue_light); 
			}else if(tableModel.getTableSeq() > 17){
				convertView.setBackgroundResource(R.drawable.bg_press_table_red); 
			}
		}else if(tableModel.getTableLeague().equals(Table_Page.LEAGUE_DE_LEAGUE1)){
			if(tableModel.getTableSeq() < 3){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green); 
			}else if(tableModel.getTableSeq() < 4){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green_light); 
			}else if(tableModel.getTableSeq() < 6){
				convertView.setBackgroundResource(R.drawable.bg_press_table_blue_light); 
			}else if(tableModel.getTableSeq() > 17){
				convertView.setBackgroundResource(R.drawable.bg_press_table_red); 
			}
		}else if(tableModel.getTableLeague().equals(Table_Page.THAI_PREMIER_LEAGUE)){
			if(tableModel.getTableSeq() < 2){
				convertView.setBackgroundResource(R.drawable.bg_press_table_green); 
			}else if(tableModel.getTableSeq() > 15){
				convertView.setBackgroundResource(R.drawable.bg_press_table_red); 
			}
		}
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
		        .discCacheExtraOptions(w, h, CompressFormat.JPEG, 75, null)
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
		String upic_me = "http://upic.me/";
		String image_ohozaa_com = "http://image.ohozaa.com/";
		//String cdn_images_express_co_uk = "http://cdn.images.express.co.uk/";
		Map<String, String> headers = new HashMap<String, String>();
		if(url.length() > upic_me.length()){
			if(url.substring(0, upic_me.length()).equals(upic_me)){
				headers.put("Accept", "image/webp,*/*;q=0.8");
				headers.put("Accept-Encoding", "gzip,deflate,sdch");
				headers.put("Accept-Language", "en-US,en;q=0.8");
				headers.put("Connection", "keep-alive");
				headers.put("Cookie", "PHPSESSID=cgmh87ivlp1pstuf56c4b65qr1; iz_uid=450499963b4f3f32857f0ed793dd2175; testcookie=enabled; __utma=113133888.1190374733.1390030425.1390030425.1390030425.1; __utmc=113133888; __utmz=113133888.1390030425.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
				headers.put("Host", "upic.me");
				headers.put("Referer", "http://localhost");
				headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36");
			}else if(url.substring(0, image_ohozaa_com.length()).equals(image_ohozaa_com)){ 
				Log.e("URL___________________OK", url);
				headers.put("Accept", "image/webp,*/*;q=0.8");
				headers.put("Accept-Encoding", "gzip,deflate,sdch");
				headers.put("Accept-Language", "th-TH,th;q=0.8,en;q=0.6");
				headers.put("Connection", "keep-alive");
				headers.put("Cookie", "ozuid=146653405; _cbclose19784=1; _uid19784=D50B2ECF.1; _cbclose=1; _ctout19784=1");
				headers.put("Host", "image.ohozaa.com");
				headers.put("Referer", "http://localhost");
				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36");
			}
			
		}
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	        //.showImageOnLoading(R.drawable.soccer_icon) // resource or drawable
	        .showImageForEmptyUri(R.drawable.soccer_icon) // resource or drawable
	        .showImageOnFail(R.drawable.soccer_icon) // resource or drawable
	        .resetViewBeforeLoading(true)  // default
	        //.delayBeforeLoading(500)
	        .cacheInMemory(false)
	        .cacheOnDisc(true)
	        .considerExifParams(false) // default
	        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
	        .bitmapConfig(Bitmap.Config.RGB_565) // default
	        //.decodingOptions()
	        .displayer(new RoundedBitmapDisplayer(10)) // default
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
