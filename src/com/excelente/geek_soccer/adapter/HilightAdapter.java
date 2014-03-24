package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.utils.DateNewsUtils;
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

public class HilightAdapter extends BaseAdapter{
	
	Context context;
    List<HilightModel> hilightList; 
    int count_ani=-1;
    
    boolean showHead; 
    
    HashMap<String, Bitmap> urlBitmap = new HashMap<String, Bitmap>();
	
	public HilightAdapter(Context context, List<HilightModel> hilightList) {
		this.context = context;
		this.hilightList = hilightList; 
	}
	
	class HilightHolder{
		TextView hilightLikesTextview;
		TextView hilightReadsTextview;
		TextView hilightCommentsTextview;
		
		ImageView hilightImageImageview;
		TextView hilightTopicTextview;
        TextView hilightTypeTextview;
        TextView hilightCreateTimeTextview;
        ProgressBar hilightImageProgressBar; 
        
        ImageView hilightLikes;
        ImageView hilightView;
        
        ImageView hilightNew;
	}
	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.e("POSITION+++++++++++++++++", String.valueOf(position));
		final HilightHolder hilightHolder; 
		
		HilightModel hilightModel = (HilightModel) getItem(position); 
		 
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        hilightHolder = new HilightHolder();
        
	        if(position==0){
	        	doConfigImageLoader(200,200);
	    		convertView = mInflater.inflate(R.layout.hilight_page_last, parent, false);
	    	}else{
	    		doConfigImageLoader(200,200); 
	    		convertView = mInflater.inflate(R.layout.hilight_page_item, parent, false);
	    		
	    		hilightHolder.hilightLikesTextview = (TextView) convertView.findViewById(R.id.hilight_likes_textview);
	    		hilightHolder.hilightReadsTextview = (TextView) convertView.findViewById(R.id.hilight_reads_textview);
	    		hilightHolder.hilightCommentsTextview = (TextView) convertView.findViewById(R.id.hilight_comments_textview);
	    		
	    		hilightHolder.hilightLikes = (ImageView) convertView.findViewById(R.id.hilight_likes);
	    		hilightHolder.hilightView = (ImageView) convertView.findViewById(R.id.hilight_view);
	    		
	    		hilightHolder.hilightLikesTextview.setText(String.valueOf(hilightModel.getHilightLikes())); 
	    		hilightHolder.hilightReadsTextview.setText(String.valueOf(hilightModel.getHilightViews())); 
	    		hilightHolder.hilightCommentsTextview.setText(String.valueOf(hilightModel.getHilightComments()));
	    		
	    		if(hilightModel.getStatusLike()==1){
	    			hilightHolder.hilightLikes.setImageResource(R.drawable.news_likes_selected);
	    		}
	    		
	    		if(hilightModel.getStatusView()==1){
	    			hilightHolder.hilightView.setImageResource(R.drawable.news_view_selected);
	    		}
	    	}
	        
	        hilightHolder.hilightImageImageview = (ImageView) convertView.findViewById(R.id.hilight_image_imageview);
	        hilightHolder.hilightTopicTextview = (TextView) convertView.findViewById(R.id.hilight_topic_textview);
	        hilightHolder.hilightTypeTextview = (TextView) convertView.findViewById(R.id.hilight_type_textview);
	        hilightHolder.hilightCreateTimeTextview = (TextView) convertView.findViewById(R.id.hilight_create_time_textview);
	        hilightHolder.hilightImageProgressBar = (ProgressBar) convertView.findViewById(R.id.hilight_image_processbar);
	        
	        hilightHolder.hilightNew = (ImageView) convertView.findViewById(R.id.hilight_new);
        
	        if(urlBitmap.containsKey(hilightModel.getHilightImage().replace(".gif", ".png"))){
	        	hilightHolder.hilightImageImageview.setImageBitmap(urlBitmap.get(hilightModel.getHilightImage().replace(".gif", ".png"))); 
	        }else{
	        	ImageLoader.getInstance().displayImage(hilightModel.getHilightImage().replace(".gif", ".png"), hilightHolder.hilightImageImageview, getOptionImageLoader(hilightModel.getHilightImage()), new ImageLoadingListener(){
	            	
	            	public void onLoadingStarted(String imageUri, View view) { 
	            		hilightHolder.hilightImageImageview.setVisibility(View.GONE);
	            		hilightHolder.hilightImageProgressBar.setVisibility(View.VISIBLE);
	            	};
	            	
	            	@Override
	            	public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
	            		hilightHolder.hilightImageImageview.setVisibility(View.VISIBLE);
	            		hilightHolder.hilightImageProgressBar.setVisibility(View.GONE);
	            	}
	            	
	            	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	            		hilightHolder.hilightImageImageview.setVisibility(View.VISIBLE);
	            		hilightHolder.hilightImageProgressBar.setVisibility(View.GONE);
	            	}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						hilightHolder.hilightImageImageview.setVisibility(View.VISIBLE);
	            		hilightHolder.hilightImageProgressBar.setVisibility(View.GONE);
					};
	            }); 
	        }
        
        	hilightHolder.hilightTopicTextview.setText(hilightModel.getHilightTopic().trim());
        	hilightHolder.hilightTypeTextview.setText(hilightModel.getHilightType().replace("&nbsp;", "").trim());
        	hilightHolder.hilightCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(context, DateNewsUtils.convertStrDateTimeDate(hilightModel.getHilightCreateTime())));
		
        	if(hilightHolder.hilightCreateTimeTextview.getText().toString().contains(context.getResources().getString(R.string.str_today_news)) && hilightModel.getStatusView()==0){
        		hilightHolder.hilightNew.setVisibility(View.VISIBLE);
    		}else{ 
    			hilightHolder.hilightNew.setVisibility(View.GONE);
    		}
        	
        if(count_ani<position){
        	convertView.setAnimation(AnimationUtils.loadAnimation(context, R.drawable.listview_anim));
        	count_ani=position;
        }
        
        return convertView;
        
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
				//Log.e("URL___________________OK", url);
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
	        .resetViewBeforeLoading(false)  // default
	        //.delayBeforeLoading(500)
	        .cacheInMemory(false)
	        .cacheOnDisc(true)
	        .considerExifParams(false) // default
	        .imageScaleType(ImageScaleType.NONE) // default
	        .bitmapConfig(Bitmap.Config.RGB_565) // default
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

	@Override
	public int getCount() {
		return hilightList.size();
	}

	@Override
	public Object getItem(int position) {
		return hilightList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return hilightList.indexOf(getItem(position));
	}
	
	public void add(List<HilightModel> hilightList) {
		for (HilightModel hilightModel : hilightList) {
			this.hilightList.add(hilightModel);
		}
		
		notifyDataSetChanged(); 
	}
	
	public void addHead(List<HilightModel> hilightList) {
		for (int i = hilightList.size()-1; i >= 0; i--) { 
			HilightModel hilightLast = hilightList.get(i);
			HilightModel hilightOld = (HilightModel)getItem(0);
			if(hilightLast.getHilightId() > hilightOld.getHilightId()){
				this.hilightList.add(0, hilightLast);
			}
			
		}
		
		notifyDataSetChanged();
	}

}

