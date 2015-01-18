package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.adapter.FixturesAdapter.ViewHoleder;
import com.excelente.geek_soccer.model.NewsModel; 
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter{
	
	Activity context;
    List<NewsModel> newsList;
    int count_ani=-1;
    
    boolean showHead; 
	
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FIRST = 1;
    private static final int TYPE_MAX_COUNT = TYPE_FIRST + 1;
    
    HashMap<String, Bitmap> urlBitmap;
    
	public NewsAdapter(Activity context, List<NewsModel> newsList) {
		if(urlBitmap == null){
			urlBitmap = new HashMap<String, Bitmap>();
		}
		this.context = context;
		this.newsList = newsList;
	}
	
	public List<NewsModel> getNewsList() {
		return newsList;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(position == 0){
			return TYPE_FIRST;
		}
		return TYPE_ITEM;
	}
	
	class ViewHoleder {
		TextView newsLikesTextview;
		TextView newsReadsTextview;
		TextView newsCommentsTextview;
		ImageView newsview;
		ImageView newslike;
		ImageView newsImageImageview;
		TextView newsTopicTextview;
        TextView newsCreateTimeTextview;
        ImageView newsNewImageview;
        LinearLayout saveModeTextview;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//Log.e("POSITION+++++++++++++++++", ""+urlBitmap.size());
		final NewsModel newsModel = (NewsModel) getItem(position);
        ViewHoleder viewHoleder = null;
		int type = getItemViewType(position);
		
        if(convertView==null){
        	 LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	 doConfigImageLoader(200,200);
        	 viewHoleder = new ViewHoleder();
        	 switch (type) {
	            case TYPE_ITEM:{
	            	convertView = mInflater.inflate(R.layout.news_page_item, parent, false);
	            	viewHoleder.newsLikesTextview = (TextView) convertView.findViewById(R.id.news_likes_textview);
	            	viewHoleder.newsReadsTextview = (TextView) convertView.findViewById(R.id.news_reads_textview);
	            	viewHoleder.newsCommentsTextview = (TextView) convertView.findViewById(R.id.news_comments_textview);
	                
	            	viewHoleder.newsview = (ImageView) convertView.findViewById(R.id.news_view);
	            	viewHoleder.newslike = (ImageView) convertView.findViewById(R.id.news_likes);
	        		
	                break;
	            }case TYPE_FIRST:{
	            	convertView = mInflater.inflate(R.layout.news_page_last, parent, false);
	                break;
	            }
        	 }
    		
        	 viewHoleder.newsImageImageview = (ImageView) convertView.findViewById(R.id.news_image_imageview);
        	 viewHoleder.newsTopicTextview = (TextView) convertView.findViewById(R.id.news_topic_textview);
        	 viewHoleder.newsCreateTimeTextview = (TextView) convertView.findViewById(R.id.news_create_time_textview);
        	 viewHoleder.newsNewImageview = (ImageView) convertView.findViewById(R.id.news_new);
        	 viewHoleder.saveModeTextview = (LinearLayout) convertView.findViewById(R.id.save_mode);
        	 
        	 convertView.setTag(viewHoleder);
    	}else{
    		viewHoleder = (ViewHoleder) convertView.getTag();
    	}
        
        doSetDataToViews(viewHoleder, newsModel, type);
		
        if(count_ani<position){
        	convertView.setAnimation(AnimationUtils.loadAnimation(context, R.drawable.listview_anim));
        	count_ani=position;
        }
        
        return convertView;
        
	}
	
	private void doSetDataToViews(final ViewHoleder viewHoleder, final NewsModel newsModel, int type) {
		if(type == TYPE_ITEM){
			if(newsModel.getStatusView()==1){
            	viewHoleder.newsview.setImageResource(R.drawable.news_view_selected);
            }
            
            if(newsModel.getStatusLike()==1){
            	viewHoleder.newslike.setImageResource(R.drawable.news_likes_selected);
            }
            
            viewHoleder.newsLikesTextview.setText(String.valueOf(newsModel.getNewsLikes())); 
            viewHoleder.newsReadsTextview.setText(String.valueOf(newsModel.getNewsReads())); 
            viewHoleder.newsCommentsTextview.setText(String.valueOf(newsModel.getNewsComments()));
		}
		
		viewHoleder.newsImageImageview.setImageBitmap(null);
    	
        final File cacheFile = ImageLoader.getInstance().getDiscCache().get(newsModel.getNewsImage().replace(".gif", ".png"));
        if(urlBitmap.containsKey(newsModel.getNewsImage().replace(".gif", ".png"))){
        	viewHoleder.newsImageImageview.setImageBitmap(urlBitmap.get(newsModel.getNewsImage().replace(".gif", ".png")));
        	viewHoleder.saveModeTextview.setVisibility(View.GONE);
        }else if(cacheFile.exists()){
        	new Thread(new Runnable() {
				
				@Override
				public void run() { 
					final Bitmap bm = BitmapFactory.decodeFile(cacheFile.getPath());
					cacheMemBitMap(newsModel.getNewsImage().replace(".gif", ".png"), bm);
					
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							viewHoleder.newsImageImageview.setImageBitmap(bm);
						}
					});
				}

			}).start();
        	viewHoleder.saveModeTextview.setVisibility(View.GONE);
        }else{ 
        	String saveMode = SessionManager.getSetting(context, SessionManager.setting_save_mode);
        	if(saveMode == null || saveMode.equals("false") || saveMode.equals("null")){
	        	doloadImage(newsModel, viewHoleder.newsImageImageview, viewHoleder.saveModeTextview);
        	}else{
        		viewHoleder.saveModeTextview.setVisibility(View.VISIBLE);
        		viewHoleder.newsImageImageview.setVisibility(View.GONE);
        	}
        }
        
        viewHoleder.saveModeTextview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doloadImage(newsModel, viewHoleder.newsImageImageview, viewHoleder.saveModeTextview);
			}
		});
        
        viewHoleder.newsTopicTextview.setText(newsModel.getNewsTopic());
        viewHoleder.newsCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(context, DateNewsUtils.convertStrDateTimeDate(newsModel.getNewsCreateTime())));
		
		if(viewHoleder.newsCreateTimeTextview.getText().toString().contains(context.getResources().getString(R.string.str_today_news)) && newsModel.getStatusView()==0){
			viewHoleder.newsNewImageview.setVisibility(View.VISIBLE);
		}else{ 
			viewHoleder.newsNewImageview.setVisibility(View.GONE);
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
	
	private void doloadImage(final NewsModel newsModel, final ImageView newsImageImageview, final LinearLayout saveModeTextview) { 
		try{ 
			final Animation fadeIn = AnimUtil.getFadeIn();
	    	fadeIn.setDuration(500);
	    	
		    ImageLoader.getInstance().displayImage(newsModel.getNewsImage().replace(".gif", ".png"), newsImageImageview, getOptionImageLoader(newsModel.getNewsImage().replace(".gif", ".png")), new ImageLoadingListener() {
				 
		    	public void onLoadingStarted(String imageUri, View view) { 
		    		saveModeTextview.setVisibility(View.GONE);
		    		newsImageImageview.setVisibility(View.GONE);
	        	};
	        	 
	        	@Override
	        	public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
	        		saveModeTextview.setVisibility(View.GONE);
	        		newsImageImageview.setVisibility(View.VISIBLE);
	        	}
	        	
	        	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	        		saveModeTextview.setVisibility(View.GONE);
	        		newsImageImageview.setVisibility(View.VISIBLE);
	        		newsImageImageview.startAnimation(fadeIn);
	        		cacheMemBitMap(newsModel.getNewsImage().replace(".gif", ".png"), loadedImage);
	        	}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					saveModeTextview.setVisibility(View.GONE);
					newsImageImageview.setVisibility(View.VISIBLE);
				};
			});
    	}catch(Exception e){
    		newsImageImageview.setVisibility(View.VISIBLE);
    		saveModeTextview.setVisibility(View.GONE);
    	}finally{
    		newsImageImageview.setVisibility(View.VISIBLE);
    		saveModeTextview.setVisibility(View.GONE);
    	}
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
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return newsList.indexOf(getItem(position));
	}
	
	public void add(List<NewsModel> newsList) {
		if(this.newsList.size()<=100){
		
			for (NewsModel newsModel : newsList) {
				this.newsList.add(newsModel);
			}
			
			notifyDataSetChanged();
		}
	}
	
	public void addHead(List<NewsModel> newsList) {
		for (int i = newsList.size()-1; i >= 0; i--) {
			NewsModel newsLast = newsList.get(i);
			NewsModel newsOld = (NewsModel)getItem(0);
			if(newsLast.getNewsId() > newsOld.getNewsId()){
				this.newsList.add(0, newsLast);
			}
			
		}
		
		notifyDataSetChanged();
	}

}
