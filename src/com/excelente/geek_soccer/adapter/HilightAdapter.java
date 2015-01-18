package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.utils.AnimUtil;
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
	
	Activity context;
    List<HilightModel> hilightList; 
    int count_ani=-1;
    
    boolean showHead; 
    
    HashMap<String, Bitmap> urlBitmap;
    
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FIRST = 1;
    private static final int TYPE_MAX_COUNT = TYPE_FIRST + 1;
	
	public HilightAdapter(Activity context, List<HilightModel> hilightList) {
		if(urlBitmap == null){
			urlBitmap = new HashMap<String, Bitmap>();
		}
		this.context = context;
		this.hilightList = hilightList; 
	}
	
	@Override
	public int getItemViewType(int position) {
		return position==0?TYPE_FIRST:TYPE_ITEM;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}
	
	class HilightHolder{
		TextView hilightLikesTextview;
		TextView hilightReadsTextview;
		TextView hilightCommentsTextview;
		
		ImageView hilightImageImageview;
		TextView hilightTopicTextview;
        TextView hilightTypeTextview;
        TextView hilightCreateTimeTextview;
        
        ImageView hilightLikes;
        ImageView hilightView;
        
        ImageView hilightNew;
        
        LinearLayout savemodeTextview;
	}
	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.e("POSITION+++++++++++++++++", String.valueOf(position));
		HilightHolder hilightHolder = null; 
		
		int type = getItemViewType(position);
		
		final HilightModel hilightModel = (HilightModel) getItem(position); 
        
	        if(convertView==null){
	        	doConfigImageLoader(200,200);
	    		hilightHolder = new HilightHolder(); 
	    		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		
	    		switch (type) {
		            case TYPE_ITEM:{
		            	convertView = mInflater.inflate(R.layout.hilight_page_item, parent, false);
		            	hilightHolder.hilightLikesTextview = (TextView) convertView.findViewById(R.id.hilight_likes_textview);
			    		hilightHolder.hilightReadsTextview = (TextView) convertView.findViewById(R.id.hilight_reads_textview);
			    		hilightHolder.hilightCommentsTextview = (TextView) convertView.findViewById(R.id.hilight_comments_textview);
			    		
			    		hilightHolder.hilightLikes = (ImageView) convertView.findViewById(R.id.hilight_likes);
			    		hilightHolder.hilightView = (ImageView) convertView.findViewById(R.id.hilight_view);
			    		
		                break;
		            }case TYPE_FIRST:{
		            	convertView = mInflater.inflate(R.layout.hilight_page_last, parent, false);
		                break;
		            }
	        	 }
	    		
	    		hilightHolder.hilightImageImageview = (ImageView) convertView.findViewById(R.id.hilight_image_imageview);
		        hilightHolder.hilightTopicTextview = (TextView) convertView.findViewById(R.id.hilight_topic_textview);
		        hilightHolder.hilightTypeTextview = (TextView) convertView.findViewById(R.id.hilight_type_textview);
		        hilightHolder.hilightCreateTimeTextview = (TextView) convertView.findViewById(R.id.hilight_create_time_textview);
		        hilightHolder.hilightNew = (ImageView) convertView.findViewById(R.id.hilight_new);
		        hilightHolder.savemodeTextview = (LinearLayout) convertView.findViewById(R.id.save_mode);
		        
		        convertView.setTag(hilightHolder);
	    		
	    	}else{
	    		hilightHolder = (HilightHolder) convertView.getTag();
	    	}
	        
	        doSetDataTOViews(hilightHolder, hilightModel, type);
        	
        if(count_ani<position){
        	convertView.setAnimation(AnimationUtils.loadAnimation(context, R.drawable.listview_anim));
        	count_ani=position;
        }
        
        return convertView; 
        
	} 
	
	private void doSetDataTOViews(final HilightHolder hilightHolder, final HilightModel hilightModel, int type) {
		if(type == TYPE_ITEM){
        	
	        if(hilightModel.getStatusLike()==1){
    			hilightHolder.hilightLikes.setImageResource(R.drawable.news_likes_selected);
    		}
    		
    		if(hilightModel.getStatusView()==1){
    			hilightHolder.hilightView.setImageResource(R.drawable.news_view_selected);
    		}
    		hilightHolder.hilightLikesTextview.setText(String.valueOf(hilightModel.getHilightLikes())); 
    		hilightHolder.hilightReadsTextview.setText(String.valueOf(hilightModel.getHilightViews())); 
    		hilightHolder.hilightCommentsTextview.setText(String.valueOf(hilightModel.getHilightComments()));
    		
        }
    
        final File cacheFile = ImageLoader.getInstance().getDiscCache().get(hilightModel.getHilightImage().replace(".gif", ".png"));
        if(urlBitmap.containsKey(hilightModel.getHilightImage().replace(".gif", ".png"))){
        	hilightHolder.hilightImageImageview.setImageBitmap(urlBitmap.get(hilightModel.getHilightImage().replace(".gif", ".png")));
        	hilightHolder.savemodeTextview.setVisibility(View.GONE);
        }else if(cacheFile.exists()){
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					final Bitmap bm = BitmapFactory.decodeFile(cacheFile.getPath());
					cacheMemBitMap(hilightModel.getHilightImage().replace(".gif", ".png"), bm);
					context.runOnUiThread(new Runnable() {
						
						@Override 
						public void run() {
							hilightHolder.hilightImageImageview.setImageBitmap(bm);
						}
					});
					
				}
			}).start();
        	hilightHolder.savemodeTextview.setVisibility(View.GONE); 
        }else{
        	String saveMode = SessionManager.getSetting(context, SessionManager.setting_save_mode);
        	if(saveMode == null || saveMode.equals("false") || saveMode.equals("null")){
        		doLoadImage(hilightModel, hilightHolder);
        	}else{
        		hilightHolder.savemodeTextview.setVisibility(View.VISIBLE);
        		hilightHolder.hilightImageImageview.setVisibility(View.GONE);
        	}
        
        }
        
        hilightHolder.savemodeTextview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doLoadImage(hilightModel, hilightHolder);
			}
		});
    
    	hilightHolder.hilightTopicTextview.setText(hilightModel.getHilightTopic().trim());
    	hilightHolder.hilightTypeTextview.setText(hilightModel.getHilightType().replace("&nbsp;", "").trim());
    	hilightHolder.hilightCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(context, DateNewsUtils.convertStrDateTimeDate(hilightModel.getHilightCreateTime())));
	
    	if(hilightHolder.hilightCreateTimeTextview.getText().toString().contains(context.getResources().getString(R.string.str_today_news)) && hilightModel.getStatusView()==0){
    		hilightHolder.hilightNew.setVisibility(View.VISIBLE);
		}else{ 
			hilightHolder.hilightNew.setVisibility(View.GONE);
		}
	}

	private void cacheMemBitMap(String replace, Bitmap bm) {
		if(urlBitmap.size() == 20){
			for (String key : urlBitmap.keySet()) {
				urlBitmap.remove(key);
				break;
			}
		}
		urlBitmap.put(replace, bm);
	}
	
	private void doLoadImage(final HilightModel hilightModel, final HilightHolder hilightHolder) {
		final Animation fadeIn = AnimUtil.getFadeIn();
    	fadeIn.setDuration(500);
    	
		ImageLoader.getInstance().displayImage(hilightModel.getHilightImage().replace(".gif", ".png"), hilightHolder.hilightImageImageview, getOptionImageLoader(hilightModel.getHilightImage()), new ImageLoadingListener(){
        	
        	public void onLoadingStarted(String imageUri, View view) { 
        		hilightHolder.savemodeTextview.setVisibility(View.GONE);
        		hilightHolder.hilightImageImageview.setVisibility(View.GONE);
        	};
        	
        	@Override
        	public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
        		hilightHolder.savemodeTextview.setVisibility(View.GONE);
        		hilightHolder.hilightImageImageview.setVisibility(View.VISIBLE);
        	}
        	
        	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        		hilightHolder.savemodeTextview.setVisibility(View.GONE);
        		hilightHolder.hilightImageImageview.setVisibility(View.VISIBLE);
        		hilightHolder.hilightImageImageview.startAnimation(fadeIn);
        		cacheMemBitMap(hilightModel.getHilightImage().replace(".gif", ".png"), loadedImage);
        	}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				hilightHolder.savemodeTextview.setVisibility(View.GONE);
				hilightHolder.hilightImageImageview.setVisibility(View.VISIBLE);
			};
        }); 
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
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "http://localhost");
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	        //.showImageOnLoading(R.drawable.soccer_icon) // resource or drawable
	        //.showImageForEmptyUri(R.drawable.soccer_icon) // resource or drawable
	        //.showImageOnFail(R.drawable.soccer_icon) // resource or drawable
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
		if(this.hilightList.size()<=100){
			for (HilightModel hilightModel : hilightList) {
				this.hilightList.add(hilightModel);
			}
			
			notifyDataSetChanged(); 
		}
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

