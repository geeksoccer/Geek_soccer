package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.SessionManager; 
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.CommentModel;
import com.excelente.geek_soccer.utils.DateNewsUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
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
import android.widget.ProgressBar;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter{

	private Context context; 
	List<CommentModel> commentList;
	
	public CommentAdapter(Context context, List<CommentModel> commentList) {
		this.context = context;
		this.commentList = commentList;
	}
 
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		CommentModel comment = (CommentModel) getItem(pos);
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(comment.getMemberUid() == SessionManager.getMember(context).getUid()){
			convertView = mInflater.inflate(R.layout.comment_item_invert, parent, false);
		}else{
			convertView = mInflater.inflate(R.layout.comment_item, parent, false);
		}
		
		final ImageView commentImageImageview = (ImageView) convertView.findViewById(R.id.comment_image_imageview);
		final ProgressBar commentImageProgressBar = (ProgressBar) convertView.findViewById(R.id.comment_image_processbar);
		
        TextView commentContentTextview = (TextView) convertView.findViewById(R.id.comment_content_textview);
        TextView commentUpdateTimeTextview = (TextView) convertView.findViewById(R.id.comment_update_time_textview);
        TextView commentNicknameTextview = (TextView) convertView.findViewById(R.id.comment_nickname);
        commentNicknameTextview.setText(comment.getMemberNickname());
        
        if(!SessionManager.hasKey(context, comment.getMemberPhoto())){ 
	        doConfigImageLoader(100, 100);
	        ImageLoader.getInstance().displayImage(comment.getMemberPhoto(), commentImageImageview, getOptionImageLoader(comment.getMemberPhoto()), new SimpleImageLoadingListener(){
	        	
	        	public void onLoadingStarted(String imageUri, View view) {
	        		commentImageImageview.setVisibility(View.GONE);
	        		commentImageProgressBar.setVisibility(View.VISIBLE);
	        	};
	        	
	        	@Override
	        	public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
	        		super.onLoadingFailed(imageUri, view, failReason);
	        		commentImageImageview.setVisibility(View.VISIBLE);
	        		commentImageProgressBar.setVisibility(View.GONE);
	        	}
	        	
	        	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	        		commentImageImageview.setVisibility(View.VISIBLE);
	        		commentImageProgressBar.setVisibility(View.GONE);
	        		SessionManager.createNewImageSession(context, imageUri, loadedImage);
	        	};
	        });
        }else{
        	commentImageImageview.setImageBitmap(SessionManager.getImageSession(context, comment.getMemberPhoto()));
        	commentImageImageview.setVisibility(View.VISIBLE);
        	commentImageProgressBar.setVisibility(View.GONE);
        }
        
        commentContentTextview.setText(comment.getCommentContent());
        commentUpdateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(context, DateNewsUtils.convertStrDateTimeDate(comment.getComment_update_time())));
        
		return convertView;
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
		        //.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		        //.memoryCacheSize(2 * 1024 * 1024)
		        .memoryCacheSizePercentage(13) // default
		        .discCache(new UnlimitedDiscCache(cacheDir)) // default
		        //.discCacheSize(50 * 1024 * 1024)
		        //.discCacheFileCount(100)
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
	        .showImageOnLoading(R.drawable.soccer_icon) // resource or drawable
	        .showImageForEmptyUri(R.drawable.soccer_icon) // resource or drawable
	        .showImageOnFail(R.drawable.soccer_icon) // resource or drawable
	        .resetViewBeforeLoading(false)  // default
	        .delayBeforeLoading(500)
	        .cacheInMemory(false)
	        .cacheOnDisc(false)
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
		return commentList.size();
	}

	@Override
	public Object getItem(int pos) {
		return commentList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return commentList.indexOf(getItem(pos));
	}
	
	public void add(List<CommentModel> commentList) {
		for (CommentModel commentModel : commentList) {  
			this.commentList.add(0, commentModel);
		}
		
		notifyDataSetChanged();
	}
	
	public void addFooter(List<CommentModel> commentList) {
		for (CommentModel commentModel : commentList) {  
			this.commentList.add(commentModel);
		}
		
		notifyDataSetChanged();
	}
	
}
