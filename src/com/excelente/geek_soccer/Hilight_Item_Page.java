package com.excelente.geek_soccer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.FlipViewController.ViewFlipListener;
import com.excelente.geek_soccer.adapter.CommentAdapter;
import com.excelente.geek_soccer.adapter.HilightItemsAdapter;
import com.excelente.geek_soccer.model.CommentModel;
import com.excelente.geek_soccer.model.HilightModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class Hilight_Item_Page extends Activity implements View.OnClickListener, AnimationListener{
	
	public static final String HILIGHT_POST_COMMENTS_URL = "http://183.90.171.209/gs_hilight_comments/post_hilight_comments.php";
	public static final String HILIGHT_READS_URL = "http://183.90.171.209/gs_hilight/post_hilight_reads.php";
	public static final String HILIGHT_GET_COMMENT_URL = "http://183.90.171.209/gs_hilight_comments/get_hilight_comments.php?";
	
	final Activity activity = this;
	
	private RelativeLayout headerLayout;
	private LinearLayout upButton;
	private TextView headeTitleTextview;
	private RelativeLayout contentLayout;
	private FlipViewController contentFlipView;
	private ListView commentListview;
	private RelativeLayout footerLayout;
	private Button commentSendButton;
	
	boolean hilightloaded; 
	boolean loaded = false;
	private ProgressBar hilightWaitProcessbar;
	private Animation animFadeindown;
	private Animation animFadeinup;
	private Animation animFadeoutdown;
	private Animation animFadeoutup;
	private Animation animFadein;
	private Animation animFadeout;

	private CommentAdapter commentAdapter;

	private EditText commentHilightEdittext;
	private HilightItemsAdapter hilightItemAdaptor;
	private String tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this).getTeamId());
		
		initAnimation();
		initView(getIntent());
		//doToggleBar();
		headerLayout.setVisibility(View.GONE);
		footerLayout.setVisibility(View.GONE);
		commentListview.setVisibility(View.GONE);
		
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
	}
	
	private void initView(Intent intent) {
		
		setContentView(R.layout.hilight_item_page);
		
		//head layout
		headerLayout = (RelativeLayout) findViewById(R.id.Header_Layout);
		
		upButton = (LinearLayout) findViewById(R.id.Up_btn);
		upButton.setOnClickListener(this);
		
		headeTitleTextview = (TextView) findViewById(R.id.Head_Title);
		headeTitleTextview.setSelected(true);
		
		//content layout
		contentLayout = (RelativeLayout) findViewById(R.id.Content_Layout);
		contentLayout.setOnClickListener(this);
		
		hilightWaitProcessbar = (ProgressBar) findViewById(R.id.hilight_wait_processbar);
		
		int position = intent.getIntExtra(Hilight_Page.HILIGHT_ITEM_INDEX, 0);
		tag = intent.getStringExtra(Hilight_Page.HILIGHT_TAG); 
		
		contentFlipView = new FlipViewController(activity, FlipViewController.HORIZONTAL);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		//params.addRule(RelativeLayout.BELOW, R.id.news_wait_processbar);
		contentFlipView.setLayoutParams(params);
		contentFlipView.setAnimationBitmapFormat(Bitmap.Config.RGB_565);
		
		hilightItemAdaptor = new HilightItemsAdapter(Hilight_Item_Page.this, hilightWaitProcessbar, Hilight_Page.getHilightListbyTag(tag)); 
		  
		contentFlipView.setAdapter(hilightItemAdaptor);
		contentFlipView.setSelection(position);
		contentFlipView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		
		hilightloaded = true;
		contentFlipView.setOnViewFlipListener(new ViewFlipListener() {
			int oldPosition=-1;
			
			@Override
			public void onViewFlipped(View view, int position) {
				HilightModel hilight = (HilightModel) contentFlipView.getAdapter().getItem(position);
				
				if(NetworkUtils.isNetworkAvailable(getApplicationContext())){ 
					new PostHilightReads().execute(hilight.getHilightId());
					hilight.setHilightViews(hilight.getHilightViews()+1);
					hilight.setStatusView(1);
					
					if(oldPosition != position){
						TextView viewtxt = (TextView) view.findViewById(R.id.hilight_reads_textview);
						viewtxt.setText(String.valueOf(hilight.getHilightViews()));
					}
				}else{
					Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(getApplicationContext()), Toast.LENGTH_SHORT).show();
				}
				
				
				headeTitleTextview.setText(hilight.getHilightTopic());
				
				
				if(headerLayout.getVisibility() == View.VISIBLE){
					doToggleBar();
				}
				
				if(contentFlipView.getAdapter().getCount() - position == 3){
					//Toast.makeText(getApplicationContext(), "Enter", Toast.LENGTH_SHORT).show();
					if(hilightloaded && contentFlipView.getAdapter().getCount() < 100 && NetworkUtils.isNetworkAvailable(getApplicationContext())){
						HilightModel hilights = (HilightModel) contentFlipView.getAdapter().getItem(contentFlipView.getAdapter().getCount()-1);
						new LoadOldHilightTask(hilightItemAdaptor, tag).execute(new Hilight_Page().getURLbyTag(hilights.getHilightId(), tag));
						hilightloaded = false;
					}
				}
				
				oldPosition = position;
				onRefesh(position);
			}
		});
         
		contentLayout.addView(contentFlipView);
		
		//comment listview 
		commentListview = (ListView) findViewById(R.id.hilight_comment_listview);
		commentListview.setOnScrollListener(new OnScrollListener() {
			
			private CommentModel oldComment;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//int lastVisibleItem = firstVisibleItem + visibleItemCount;
				if((firstVisibleItem == 0) && loaded && totalItemCount>0){
					CommentModel cm = (CommentModel)view.getAdapter().getItem(0);
					if(!cm.equals(oldComment)){
						//newsLoadingFooterProcessbar.setVisibility(View.VISIBLE);
						if(NetworkUtils.isNetworkAvailable(getApplicationContext())){ 
							new LoadCommentTask().execute(cm);
						}else{
							Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(getApplicationContext()), Toast.LENGTH_SHORT).show();
						}
						//Toast.makeText(getActivity(), "Toast " + i++, Toast.LENGTH_SHORT).show();
					} 
					
					oldComment = cm;
					
				}
				
			}
		});
		
		//foot layout
		footerLayout = (RelativeLayout) findViewById(R.id.Footer_Layout);
		
		commentHilightEdittext = (EditText) findViewById(R.id.Hilight_Comments);
		commentSendButton = (Button) findViewById(R.id.Hilight_Comments_Send); 
		commentSendButton.setOnClickListener(this);
	}
	
	public void onRefesh(final int position) {
		
		final Handler hd = new Handler();
		hd.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						contentFlipView.refreshPage(position); 
						contentFlipView.refreshPage(position+1);
					}
				});
			}
		}, 200);
	}
	
	public void doToggleBar() {
		if(headerLayout.getVisibility() == View.GONE){
			headerLayout.setVisibility(View.VISIBLE);
			footerLayout.setVisibility(View.VISIBLE);
			commentListview.setVisibility(View.VISIBLE);
			
			headerLayout.startAnimation(animFadeindown);
			footerLayout.startAnimation(animFadeinup);
			commentListview.startAnimation(animFadein);
		}else{
			if(hilightItemAdaptor!=null)
				hilightItemAdaptor.notifyDataSetChanged();
			
			headerLayout.setVisibility(View.GONE);
			footerLayout.setVisibility(View.GONE);
			commentListview.setVisibility(View.GONE);
			
			headerLayout.startAnimation(animFadeoutup);
			footerLayout.startAnimation(animFadeoutdown);
			commentListview.startAnimation(animFadeout);
		}
	}

	private void initAnimation() {
		animFadeindown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_and_slide_down);
		animFadeindown.setAnimationListener(this);
		
		animFadeinup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_and_slide_up);
		animFadeinup.setAnimationListener(this);
		
		animFadeoutdown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_and_slide_down);
		animFadeoutdown.setAnimationListener(this);
		
		animFadeoutup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_and_slide_up); 
		animFadeoutup.setAnimationListener(this);
		
		animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
		animFadein.setAnimationListener(this);
		
		animFadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out); 
		animFadeout.setAnimationListener(this);
	}
	
	public void doClickNewsComment(CommentModel commentModel, NewsModel news) {
		
		headeTitleTextview.setText(news.getNewsTopic());
		
		doToggleBar();
		
		if(NetworkUtils.isNetworkAvailable(getApplicationContext())){ 
			commentAdapter = new CommentAdapter(this, new ArrayList<CommentModel>()); 
			commentListview.setAdapter(commentAdapter);
			new LoadCommentTask().execute(commentModel);
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(getApplicationContext()), Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.Up_btn:{
				if(headerLayout.getVisibility() == View.VISIBLE){
					doToggleBar();
				}
				break;
			}
			
			case R.id.Hilight_Comments_Send:{ 
				String content = commentHilightEdittext.getText().toString();
				if(content == null){
					break;
				} 
				
				if(content.length() > 0){
					 
					HilightModel hilight = (HilightModel) contentFlipView.getSelectedItem();
					CommentModel comment = new CommentModel();
					comment.setMemberUid(SessionManager.getMember(this).getUid());
					comment.setNewsId(hilight.getHilightId());
					comment.setCommentContent(content);
					
					if(NetworkUtils.isNetworkAvailable(getApplicationContext())){ 
						new PostHilightComment(hilight).execute(comment);
					}else{
						Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(getApplicationContext()), Toast.LENGTH_SHORT).show();
					}
				}
				
				break;
			}
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    contentFlipView.onResume();
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    contentFlipView.onPause();
	}
	
	@Override
	public void onBackPressed() {
		
		if(headerLayout.getVisibility() == View.VISIBLE){
			doToggleBar();
		}else{
			super.onBackPressed();
			
			overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		}
		
	}
	
	public class PostHilightReads extends AsyncTask<Integer, Void, Void>{
		
		@Override
		protected Void doInBackground(Integer... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("hilight_id", String.valueOf(params[0])));
			
			HttpConnectUtils.getStrHttpPostConnect(HILIGHT_READS_URL, paramsPost);
			
			return null;
		}

	} 
	
	public class PostHilightComment extends AsyncTask<CommentModel, Void, CommentModel>{   
		
		HilightModel hilight;
		
		public PostHilightComment(HilightModel hilight) {
			this.hilight = hilight;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			commentSendButton.setClickable(false);
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected CommentModel doInBackground(CommentModel... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair(CommentModel.MEMBER_UID, String.valueOf(params[0].getMemberUid())));
			paramsPost.add(new BasicNameValuePair(CommentModel.HILIGHT_ID, String.valueOf(params[0].getNewsId())));
			paramsPost.add(new BasicNameValuePair(CommentModel.COMMENT_CONTENT, params[0].getCommentContent()));
			
			String result = HttpConnectUtils.getStrHttpPostConnect(HILIGHT_POST_COMMENTS_URL, paramsPost);
			if(result.trim().equals("success")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				params[0].setComment_update_time(sdf.format(new Date()));
				params[0].setMemberPhoto(SessionManager.getMember(Hilight_Item_Page.this).getPhoto());
				params[0].setMemberNickname(SessionManager.getMember(Hilight_Item_Page.this).getNickname());
				params[0].setMemberUser(SessionManager.getMember(Hilight_Item_Page.this).getUser());
				params[0].setMemberTeamId(SessionManager.getMember(Hilight_Item_Page.this).getTeamId());
			}
			return params[0];
		}
		
		@Override
		protected void onPostExecute(CommentModel result) {
			super.onPostExecute(result);
			
			if(result.getMemberNickname() == null){
				Toast.makeText(getApplicationContext(), "Missed", Toast.LENGTH_SHORT).show();
			}else{
				List<CommentModel> commentList = new ArrayList<CommentModel>();
				commentList.add(result);
				commentAdapter.addFooter(commentList);
				commentListview.setSelection(commentAdapter.getCount()-1);
				hilight.setHilightComments(hilight.getHilightComments()+1);
			}
			
			commentHilightEdittext.setText("");
			commentSendButton.setClickable(true);
		}

	}
	
	public class LoadCommentTask extends AsyncTask<CommentModel, Void, List<CommentModel>>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			loaded = false;
		}
		
		@Override
		protected List<CommentModel> doInBackground(CommentModel... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(HILIGHT_GET_COMMENT_URL + "comment_id=" + params[0].getCommentId() + "&hilight_id=" + params[0].getNewsId()); 
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			
			List<CommentModel> commentList = CommentModel.convertCommentStrToList(result, CommentModel.HILIGHT_ID);
			
			return commentList;
		}

		@Override
		protected void onPostExecute(List<CommentModel> result) {
			super.onPostExecute(result);
			doLoadCommentToListView(result);
		}

	}
	
	public void doLoadCommentToListView(List<CommentModel> commentList) { 
		
		if(commentList == null || commentList.isEmpty()){
			Toast.makeText(this, "No Comments", Toast.LENGTH_SHORT).show();
			return;
		}
		
		commentAdapter.add(commentList);
		if(commentList.get(commentList.size()-1).getMexComment() > 0 ){
			commentListview.setSelection(commentAdapter.getCount()-1);
		}else{
			commentListview.setSelection(commentList.size()-1);
		}
		loaded = true;
		//commentListview.setVisibility(View.VISIBLE);
		//setListViewEvents();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	public void doClickHilightComment(CommentModel commentModel, HilightModel hilight) {
		
		headeTitleTextview.setText(hilight.getHilightTopic());
		
		doToggleBar();
		
		if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
			commentAdapter = new CommentAdapter(this, new ArrayList<CommentModel>()); 
			commentListview.setAdapter(commentAdapter);
			new LoadCommentTask().execute(commentModel);
		}else{
			Toast.makeText(getApplicationContext(), NetworkUtils.getConnectivityStatusString(getApplicationContext()), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public class LoadOldHilightTask extends AsyncTask<String, Void, List<HilightModel>>{ 
		
		HilightItemsAdapter hilightAdapter;
		String tag;
		
		public LoadOldHilightTask(HilightItemsAdapter hilightAdapter, String tag) {
			this.hilightAdapter = hilightAdapter; 
			this.tag = tag;
		}
		
		@Override
		protected List<HilightModel> doInBackground(String... params) {
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]);  
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			
			List<HilightModel> hilightList = HilightModel.convertHilightStrToList(result);
			
			return hilightList;
		}

		@Override
		protected void onPostExecute(List<HilightModel> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				hilightAdapter.add(result);
				//contentFlipView.refreshAllPages();
				
			}
			
			hilightloaded = true; 
		}

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("oooooooooooooooo", "test");
		contentFlipView.refreshAllPages();
	}

}
