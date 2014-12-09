package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.FixturesGroupList;
import com.excelente.geek_soccer.model.FixturesGroupLists;
import com.excelente.geek_soccer.model.FixturesModel;
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
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class FixturesAdapter extends BaseExpandableListAdapter {

	private final List<FixturesGroupList> groups;
	public Activity activity;
	HashMap<String, Bitmap> urlBitmap;
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_NEXTMATCH = 1;
    private static final int TYPE_MAX_COUNT = TYPE_NEXTMATCH + 1;
    FixturesGroupLists groupsList;

	public FixturesAdapter(Activity act, List<FixturesGroupList> groups, FixturesGroupLists groupsList) {
		this.activity = act;
		this.groups = groups;
		this.urlBitmap = new HashMap<String, Bitmap>();
		this.groupsList = groupsList;
	}
	
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		if(groupsList.getIndexNextMatch() == childPosition && groupsList.getIndexNextMatchGroup() == groupPosition){
			return TYPE_NEXTMATCH;
		}
		return TYPE_ITEM;
	}
	
	@Override
	public int getChildTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	
	class ViewHoleder {
		TextView matchType;
		TextView matchDate;
		TextView homeName;
		TextView awayName;
		TextView score;
		ImageView homeImg;
		ImageView awayImg;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		FixturesModel fixtures = (FixturesModel) getChild(groupPosition, childPosition);
		ViewHoleder fixturesView = null;
		int type = getChildType(groupPosition, childPosition);
		if (convertView == null) {
			doConfigImageLoader(200,200);
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (type) {
	            case TYPE_ITEM:
	            	convertView = inflater.inflate(R.layout.fixtures_listrow_details, parent, false);
	                break;
	            case TYPE_NEXTMATCH:
	            	convertView = inflater.inflate(R.layout.fixtures_listrow_nextmatch, parent, false);
	                break;
	        }
			
			fixturesView = new ViewHoleder();
			fixturesView.awayImg = (ImageView) convertView.findViewById(R.id.away_img);
			fixturesView.awayName = (TextView) convertView.findViewById(R.id.away_name);
			fixturesView.homeImg = (ImageView) convertView.findViewById(R.id.home_img);
			fixturesView.homeName = (TextView) convertView.findViewById(R.id.home_name);
			fixturesView.matchDate = (TextView) convertView.findViewById(R.id.match_date);
			fixturesView.matchType = (TextView) convertView.findViewById(R.id.match_type);
			fixturesView.score = (TextView) convertView.findViewById(R.id.score);
			
			convertView.setTag(fixturesView);
			
		}else{
			fixturesView = (ViewHoleder) convertView.getTag();
		}
		
		//Log.e("getChildView", "groupPosition: " + groupPosition + " childPosition: " + childPosition + " isNextMatch: " + fixtures.isNextMatch());
	
		setFixturesView(fixturesView, fixtures);
		
		return convertView;
	}

	private void setFixturesView(ViewHoleder fixturesView, final FixturesModel fixtures) {
		fixturesView.awayName.setText(fixtures.getAwayName());
		fixturesView.homeName.setText(fixtures.getHomeName());
		fixturesView.matchDate.setText(fixtures.getMatchDateDisplay());
		fixturesView.matchType.setText(fixtures.getMatchType());
		fixturesView.score.setText(fixtures.getScore());
		
		loadImageViewUrl(fixtures.getAwayImg(), fixturesView.awayImg);
		loadImageViewUrl(fixtures.getHomeImg(), fixturesView.homeImg);
	}
	
	private void loadImageViewUrl(final String url, final ImageView imgView) {
		final File cacheFile = ImageLoader.getInstance().getDiscCache().get(url);
		if(urlBitmap.containsKey(url)){
			imgView.setImageBitmap(urlBitmap.get(url));
        }else if(cacheFile.exists()){
        	new Thread(new Runnable() {
				
				@Override
				public void run() { 
					final Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
					cacheMemBitMap(url, bm);
					
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							imgView.setImageBitmap(bm);  
						}
					});
				}

			}).start();
        }else{ 
        	ImageLoader.getInstance().displayImage(url, imgView, getOptionImageLoader(url), new ImageLoadingListener() {
				 
		    	public void onLoadingStarted(String imageUri, View view) {
	        	};
	        	 
	        	@Override
	        	public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
	        	}
	        	
	        	public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
	        		new Thread(new Runnable() {
						@Override
						public void run() {
							SessionManager.createNewImageSession(activity, url, loadedImage);
							cacheMemBitMap(url, SessionManager.getImageSession(activity, url));
						}
					}).start();
	        	}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
				};
			});
        }
	}

	private void cacheMemBitMap(String replace, Bitmap bm) {
		urlBitmap.put(replace, bm);
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
	        .showImageOnLoading(R.drawable.soccer_icon) // resource or drawable
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
	
	class ViewGroupHoleder{
		TextView headerTitle;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		//ExpandableListView mExpandableListView = (ExpandableListView) parent;
	    //mExpandableListView.expandGroup(groupPosition);
	    ViewGroupHoleder viewGroupHoleder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.fixtures_listrow_group, parent, false);
			
			viewGroupHoleder = new ViewGroupHoleder();
			viewGroupHoleder.headerTitle = (TextView) convertView.findViewById(R.id.header_title);
			
			convertView.setTag(viewGroupHoleder);
		}else{
			viewGroupHoleder = (ViewGroupHoleder) convertView.getTag();
		}
		
		FixturesGroupList group = (FixturesGroupList) getGroup(groupPosition);
		viewGroupHoleder.headerTitle.setText(group.headerTitle);
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
