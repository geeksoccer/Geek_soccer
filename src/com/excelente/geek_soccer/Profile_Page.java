package com.excelente.geek_soccer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.excelente.geek_soccer.utils.ThemeUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Profile_Page extends Activity implements OnClickListener{
	
	private LinearLayout upBtn;
	private ImageView memberPhoto;
	private EditText memberName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeUtils.setThemeByTeamId(this, MemberSession.getMember().getTeamId());
		setContentView(R.layout.profile_page);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		initView();
	}
	
	private void initView() {
		upBtn = (LinearLayout) findViewById(R.id.Up_btn);
		upBtn.setOnClickListener(this);
		
		memberPhoto = (ImageView) findViewById(R.id.member_photo);
		doConfigImageLoader(200, 200); 
		ImageLoader.getInstance().displayImage(MemberSession.getMember().getPhoto(), memberPhoto, getOptionImageLoader(MemberSession.getMember().getPhoto()));
		memberPhoto.setOnClickListener(this);
		
		memberName = (EditText) findViewById(R.id.member_name);
		memberName.setText(MemberSession.getMember().getNickname());
		memberPhoto.setOnClickListener(this);
	}
	
	private void doConfigImageLoader(int w, int h) {
		
		File cacheDir = StorageUtils.getCacheDirectory(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
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
		        .imageDownloader(new CustomImageDownaloder(this))
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.Up_btn:{
				onBackPressed();
				break;
			}
		}
	}

}
