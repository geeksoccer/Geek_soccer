package com.excelente.geek_soccer.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.excelente.geek_soccer.Hilight_Item_Page;
import com.excelente.geek_soccer.MemberSession;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.CommentModel;
import com.excelente.geek_soccer.model.HilightItemModel;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.player.VideoPlayer;
import com.excelente.geek_soccer.utils.DateNewsUtils;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;

public class HilightItemsAdapter extends BaseAdapter{
	
	Context mContext;
	List<HilightModel> mHilightList;  
	ProgressBar hilightWaitProcessbar;
	
	Hilight_Item_Page hilightItemPage;  
  
	public HilightItemsAdapter(Hilight_Item_Page context, ProgressBar hilightWaitProcessbar, List<HilightModel> hilightList) {
		this.hilightItemPage = context;
		this.mContext = context;
		this.mHilightList = hilightList;     
		this.hilightWaitProcessbar = hilightWaitProcessbar;  
	}
	
	public class HilightItemView{ 
		TextView hilightTopicTextview;  
		TextView hilightCreateTimeTextview;
		ListView hilightContentListview;
		ImageView hilightLikeImageview;
		TextView hilightLikesTextview;
		TextView hilightReadsTextview;
		ImageView hilightCommentImageview; 
		TextView hilightCommentsTextview; 
		TextView hilightTypeTextview; 
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        HilightItemView hilightItemView; 
        
        if(convertView==null){
        	convertView = mInflater.inflate(R.layout.hilight_item_item_page, parent, false);
        	
        	hilightItemView = new HilightItemView(); 
	        hilightItemView.hilightTopicTextview = (TextView) convertView.findViewById(R.id.hilight_topic_textview);
	        hilightItemView.hilightCreateTimeTextview = (TextView) convertView.findViewById(R.id.hilight_create_time_textview);
	        hilightItemView.hilightContentListview = (ListView) convertView.findViewById(R.id.hilight_content_listview);
	        hilightItemView.hilightLikeImageview = (ImageView) convertView.findViewById(R.id.hilight_like); 
	        hilightItemView.hilightLikesTextview = (TextView) convertView.findViewById(R.id.hilight_likes_textview);
	        hilightItemView.hilightReadsTextview = (TextView) convertView.findViewById(R.id.hilight_reads_textview);
	        hilightItemView.hilightCommentImageview = (ImageView) convertView.findViewById(R.id.hilight_comment_imageView);
	        hilightItemView.hilightCommentsTextview = (TextView) convertView.findViewById(R.id.hilight_comments_textview);
	        hilightItemView.hilightTypeTextview = (TextView) convertView.findViewById(R.id.hilight_type_textview);
	        
	        convertView.setTag(hilightItemView);
        }else{
        	hilightItemView = (HilightItemView)convertView.getTag();
        }
        
        HilightModel hilightModel = (HilightModel) getItem(position); 
        
        setVisibleHilightContent(true, hilightItemView); 
        doLoadHilightToViews(hilightModel, hilightItemView); 
        
		return convertView;
	}
	
	private void doLoadHilightToViews(final HilightModel hilightModel, final HilightItemView hilightItemView) {  
		
		hilightItemView.hilightTopicTextview.setText(hilightModel.getHilightTopic().trim());
		hilightItemView.hilightCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(mContext, DateNewsUtils.convertStrDateTimeDate(hilightModel.getHilightCreateTime()))); 
        
        hilightItemView.hilightLikeImageview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(NetworkUtils.isNetworkAvailable(mContext)){
					
					if(hilightModel.getStatusLike()==0){
						hilightItemView.hilightLikeImageview.setImageResource(R.drawable.news_likes_selected);
						hilightModel.setHilightLikes(hilightModel.getHilightLikes() + 1);
						hilightModel.setStatusLike(1);
					}else{
						hilightItemView.hilightLikeImageview.setImageResource(R.drawable.news_likes);
						hilightModel.setHilightLikes(hilightModel.getHilightLikes() - 1);
						hilightModel.setStatusLike(0);
					}
					
					new PostHilightLikes().execute(hilightModel);
				}else{
					Toast.makeText(mContext, NetworkUtils.getConnectivityStatusString(mContext), Toast.LENGTH_SHORT).show();
				}
				 
				hilightItemView.hilightLikesTextview.setText(String.valueOf(hilightModel.getHilightLikes()));
			}
		});
        
        hilightItemView.hilightCommentImageview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommentModel comment = new CommentModel();
				comment.setNewsId(hilightModel.getHilightId());
				comment.setMemberUid(MemberSession.getMember().getUid());
				
				hilightItemPage.doClickHilightComment(comment, hilightModel);
			}
		});
        
        hilightItemView.hilightLikesTextview.setText(String.valueOf(hilightModel.getHilightLikes())); 
        hilightItemView.hilightReadsTextview.setText(String.valueOf(hilightModel.getHilightViews()));
        hilightItemView.hilightCommentsTextview.setText(String.valueOf(hilightModel.getHilightComments()));
        
        HilightVdoAdapter hvAdapter = new HilightVdoAdapter(mContext, hilightModel.getHilightLinkList(), hilightModel);
        hilightItemView.hilightContentListview.setAdapter(hvAdapter);
        hilightItemView.hilightContentListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adap, View view, int pos, long id) {
				
				if(NetworkUtils.isNetworkAvailable(mContext)){
					HilightItemModel hilightItem = (HilightItemModel)adap.getAdapter().getItem(pos);
					
					Intent intent = new Intent(mContext, VideoPlayer.class);
					intent.putExtra(VideoPlayer.VDO_URL, hilightItem.getHilightItemLink().trim());
					mContext.startActivity(intent); 
				}else{
					Toast.makeText(mContext, NetworkUtils.getConnectivityStatusString(mContext), Toast.LENGTH_SHORT).show();
				}
			}
		});
        hilightItemView.hilightContentListview.setOnTouchListener(new SwipeDetector());
        //setVisibleHilightContent(true, hilightItemView);
        hilightItemView.hilightTypeTextview.setText(hilightModel.getHilightType().replace("&nbsp;", "").trim());
        
        if(hilightModel.getStatusLike()==0){
        	hilightItemView.hilightLikeImageview.setImageResource(R.drawable.news_likes);
		}else{
			hilightItemView.hilightLikeImageview.setImageResource(R.drawable.news_likes_selected);
		}
	}

	private void setVisibleHilightContent(boolean visible, HilightItemView hilightItemView) {
		if(!visible){ 
			hilightItemView.hilightContentListview.setVisibility(View.GONE);
			hilightWaitProcessbar.setVisibility(View.VISIBLE);
		}else{
			hilightItemView.hilightContentListview.setVisibility(View.VISIBLE);
			hilightWaitProcessbar.setVisibility(View.GONE);
		}
	}
	
	@Override
	public int getCount() {
		return mHilightList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mHilightList.get(pos);
	}

	@Override
	public long getItemId(int position) {
		return mHilightList.indexOf(mHilightList.get(position)); 
	}
	 
	public class PostHilightLikes extends AsyncTask<HilightModel, Void, Void>{
		
		private static final String HILIGHT_LIKES_URL = "http://183.90.171.209/gs_hilight/post_hilight_like.php"; 

		@Override 
		protected Void doInBackground(HilightModel... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("hilight_id", String.valueOf(params[0].getHilightId())));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(MemberSession.getMember().getUid())));
			paramsPost.add(new BasicNameValuePair("status_like", String.valueOf(params[0].getStatusLike())));
				
			HttpConnectUtils.getStrHttpPostConnect(HILIGHT_LIKES_URL, paramsPost);
			
			return null;
		}

	}
	
	public class SwipeDetector implements View.OnTouchListener {

		static final String logTag = "SwipeDetector";
		static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;

		public void onRightToLeftSwipe(View v){
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}

		public void onLeftToRightSwipe(View v){
			v.getParent().requestDisallowInterceptTouchEvent(false);
		}

		public void onTopToBottomSwipe(View v){
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}

		public void onBottomToTopSwipe(View v){
			v.getParent().requestDisallowInterceptTouchEvent(true);
		}

		public boolean onTouch(View v, MotionEvent event) {
		    switch(event.getAction()){
		        case MotionEvent.ACTION_DOWN: {
		            downX = event.getX();
		            downY = event.getY();
		            v.getParent().requestDisallowInterceptTouchEvent(true);
		            break;
		        }
		        case MotionEvent.ACTION_MOVE: {
		            upX = event.getX();
		            upY = event.getY();

		            float deltaX = downX - upX;
		            float deltaY = downY - upY;

		            if(Math.abs(deltaY) > MIN_DISTANCE){
		                // top or down
		                if(deltaY < 0) { this.onTopToBottomSwipe(v);}
		                if(deltaY > 0) { this.onBottomToTopSwipe(v);}
		            } else if(Math.abs(deltaX) > MIN_DISTANCE){
		                // left or right
		                if(deltaX < 0) { this.onLeftToRightSwipe(v);}
		                if(deltaX > 0) { this.onRightToLeftSwipe(v);}
		            }
		            break;
		        }
		        case MotionEvent.ACTION_UP: {
		        	v.getParent().requestDisallowInterceptTouchEvent(true);
		        	break;
		        }
		    }
		    return false;
		}

	}
	
	public void add(List<HilightModel> hilightList) {
		if(this.mHilightList.size()<=100){
		
			for (HilightModel hilightModel : hilightList) {
				this.mHilightList.add(hilightModel);
			}
			
			notifyDataSetChanged();
		}
	}

}

