package com.excelente.geek_soccer.utils.asynctask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.excelente.geek_soccer.Profile_Page;
import com.excelente.geek_soccer.SessionManager;
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

public class GetImageUriTask {
	Activity activity;
	ImageView imageView;
	String url;
	
	public GetImageUriTask(Activity activity, ImageView imageView, String url) {
		this.activity = activity;
		this.imageView = imageView;
		this.url = url;
		doConfigImageLoader(200,200);
	}
	
	public void doLoadImage(final boolean resize) {
		ImageLoader.getInstance().displayImage(url, imageView, getOptionImageLoader(url), new ImageLoadingListener() {
			 
	    	public void onLoadingStarted(String imageUri, View view) {
	    		imageView.setVisibility(View.VISIBLE);
        	};
        	 
        	@Override
        	public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
        		imageView.setVisibility(View.VISIBLE);
        	}
        	
        	public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
        		new Thread(new Runnable() {
					@Override
					public void run() {
						SessionManager.createNewImageSession(activity, url, loadedImage);
					}
				}).start();
        		
        		if(resize){
        			imageView.setImageBitmap(Profile_Page.resizeBitMap(loadedImage));
        		}
        		imageView.setVisibility(View.VISIBLE);
        	}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				imageView.setVisibility(View.VISIBLE);
			};
		});
	}



	private void doConfigImageLoader(int w, int h) {
		
		File cacheDir = StorageUtils.getCacheDirectory(activity);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
		        .memoryCacheExtraOptions(w, h) // default = device screen dimensions
		        .discCacheExtraOptions(w, h, CompressFormat.PNG, 100, null)
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
		        .imageDownloader(new BaseImageDownloader(activity))
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
}