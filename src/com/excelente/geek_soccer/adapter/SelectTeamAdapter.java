package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.LeagueModel;
import com.excelente.geek_soccer.model.TeamModel;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectTeamAdapter extends BaseExpandableListAdapter{
	
	HashMap<String, Bitmap> urlBitmap;
	Activity act; 
	List<LeagueModel> leagueList;
	
	public SelectTeamAdapter(Activity act, List<LeagueModel> leagueList) {
		this.urlBitmap = new HashMap<String, Bitmap>();
		this.act = act;
		this.leagueList = leagueList;
		doConfigImageLoader(200,200);
	}

	@Override
	public int getGroupCount() {
		return leagueList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return leagueList.get(groupPosition).getTeams().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return leagueList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return leagueList.get(groupPosition).getTeams().get(childPosition);
	} 

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
	
	class ViewLeagueHoleder{
		TextView leageueName;
		ImageView leagueImg;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewLeagueHoleder viewLeagueHoleder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.league_listrow, parent, false);
			 
			viewLeagueHoleder = new ViewLeagueHoleder();
			viewLeagueHoleder.leageueName = (TextView) convertView.findViewById(R.id.league_title);
			viewLeagueHoleder.leagueImg = (ImageView) convertView.findViewById(R.id.league_img);
			
			convertView.setTag(viewLeagueHoleder);
		}else{
			viewLeagueHoleder = (ViewLeagueHoleder) convertView.getTag();
		}
		
		LeagueModel league = (LeagueModel) getGroup(groupPosition);
		String lang = SessionManager.getLang(act);
		if(lang.equals("en")){
			viewLeagueHoleder.leageueName.setText(league.getName());
		}else{
			viewLeagueHoleder.leageueName.setText(league.getNameTH());
		}
		viewLeagueHoleder.leageueName.setText(league.getNameTH());
		loadImageViewUrl("", viewLeagueHoleder.leagueImg);
		
		return convertView;
	}
	
	class ViewTeamHoleder {
		TextView teamName;
		ImageView teamImg;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ViewTeamHoleder viewTeamHoleder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.team_listrow, parent, false);
			  
			viewTeamHoleder = new ViewTeamHoleder();
			viewTeamHoleder.teamName = (TextView) convertView.findViewById(R.id.team_name);
			viewTeamHoleder.teamImg = (ImageView) convertView.findViewById(R.id.team_img);
			
			convertView.setTag(viewTeamHoleder);
		}else{
			viewTeamHoleder = (ViewTeamHoleder) convertView.getTag();
		}
		
		TeamModel team = (TeamModel) getChild(groupPosition, childPosition);
		String lang = SessionManager.getLang(act);
		if(lang.equals("en")){
			viewTeamHoleder.teamName.setText(team.getTeamName());
		}else{
			viewTeamHoleder.teamName.setText(team.getTeamNameTH());
		}
		loadImageViewUrl(team.getTeamImage(), viewTeamHoleder.teamImg);
		 
		return convertView;
	}
	
	private void loadImageViewUrl(final String url, final ImageView imgView) {
		if(url==null || url.trim().equals("")){
			imgView.setVisibility(View.GONE);
			return;
		}
		//final File cacheFile = ImageLoader.getInstance().getDiscCache().get(url);
		if(urlBitmap.containsKey(url)){
			imgView.setImageBitmap(urlBitmap.get(url));
        }/*else if(cacheFile.exists()){
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
        }*/else{ 
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
							SessionManager.createNewImageSession(act, url, loadedImage);
							cacheMemBitMap(url, SessionManager.getImageSession(act, url));
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
		
		File cacheDir = StorageUtils.getCacheDirectory(act);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(act)
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
		        .imageDownloader(new BaseImageDownloader(act))
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

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
