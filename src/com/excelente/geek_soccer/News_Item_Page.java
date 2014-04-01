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
import com.excelente.geek_soccer.adapter.NewsItemsAdapter;
import com.excelente.geek_soccer.model.CommentModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
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
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

@SuppressLint("SetJavaScriptEnabled")
public class News_Item_Page extends Activity implements View.OnClickListener, AnimationListener, NewsItemsAdapter.Callback{
	
	public static final String NEWS_POST_COMMENTS_URL = "http://183.90.171.209/gs_news_comments/post_news_comments.php";
	public static final String NEWS_READS_URL = "http://183.90.171.209/gs_news/post_news_reads.php";
	public static final String NEWS_GET_COMMENT_URL = "http://183.90.171.209/gs_news_comments/get_news_comments.php?"; 
	 
	final Activity activity = this;

	private LinearLayout upButton; 

	private FlipViewController contentFlipView;

	private RelativeLayout contentLayout;

	private ProgressBar newsWaitProcessbar;

	private Animation animFadeindown;

	private Animation animFadeinup;

	private Animation animFadeoutdown;

	private Animation animFadeoutup;

	private RelativeLayout footerLayout;

	private RelativeLayout headerLayout;

	private EditText commentNewsEdittext;

	private Button commentSendButton;
	private Animation animFadein;
	private Animation animFadeout;
	public ListView commentListview;
	private CommentAdapter commentAdapter; 
	
	boolean newsloaded; 
	boolean loaded = false;
	public TextView headeTitleTextview;
	private NewsItemsAdapter newsItemAdaptor;
	private String tag; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemeUtils.setThemeByTeamId(this, MemberSession.getMember().getTeamId());
		
		initAnimation();
		initView(getIntent());
		//doToggleBar();
		headerLayout.setVisibility(View.GONE);
		footerLayout.setVisibility(View.GONE);
		commentListview.setVisibility(View.GONE);
		
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
	}

	private void initView(Intent intent) {
		
		setContentView(R.layout.news_item_page);
		
		//head layout
		headerLayout = (RelativeLayout) findViewById(R.id.Header_Layout);
		
		upButton = (LinearLayout) findViewById(R.id.Up_btn);
		upButton.setOnClickListener(this);
		
		headeTitleTextview = (TextView) findViewById(R.id.Head_Title);
		headeTitleTextview.setSelected(true);
		
		//content layout
		contentLayout = (RelativeLayout) findViewById(R.id.Content_Layout);
		contentLayout.setOnClickListener(this);
		
		newsWaitProcessbar = (ProgressBar) findViewById(R.id.news_wait_processbar);
		
		int position = intent.getIntExtra(News_Page.ITEM_INDEX, 0);
		tag = intent.getStringExtra(News_Page.NEWS_TAG); 
		
		contentFlipView = new FlipViewController(activity, FlipViewController.HORIZONTAL);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		//params.addRule(RelativeLayout.BELOW, R.id.news_wait_processbar);
		contentFlipView.setLayoutParams(params);
		contentFlipView.setAnimationBitmapFormat(Bitmap.Config.RGB_565);
		
		newsItemAdaptor = new NewsItemsAdapter(News_Item_Page.this, newsWaitProcessbar, News_Page.getNewsListbyTag(tag)); 
		newsItemAdaptor.setCallback(this); 
		
		contentFlipView.setAdapter(newsItemAdaptor);
		contentFlipView.setSelection(position);
		contentFlipView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		
		newsloaded = true;
		contentFlipView.setOnViewFlipListener(new ViewFlipListener() {
			int oldPosition=0;
			
			@Override
			public void onViewFlipped(View view, int position) {
				//contentFlipView.refreshPage(view);
				NewsModel news = (NewsModel) contentFlipView.getAdapter().getItem(position);
				
				if(oldPosition != position){
					
					new PostNewsReads().execute(news.getNewsId());
					news.setStatusView(1);
					news.setNewsReads(news.getNewsReads()+1);
					
					TextView viewtxt = (TextView) view.findViewById(R.id.news_reads_textview);
					viewtxt.setText(String.valueOf(news.getNewsReads()));
				}
				
				headeTitleTextview.setText(news.getNewsTopic());
				
				if(headerLayout.getVisibility() == View.VISIBLE){
					doToggleBar();
				}
				
				if(contentFlipView.getAdapter().getCount() - position == 3){
					//Toast.makeText(getApplicationContext(), "Enter", Toast.LENGTH_SHORT).show();
					if(newsloaded && contentFlipView.getAdapter().getCount() < 100 && NetworkUtils.isNetworkAvailable(getApplicationContext())){
						NewsModel newsModel = (NewsModel) contentFlipView.getAdapter().getItem(contentFlipView.getAdapter().getCount()-1);
						new LoadOldNewsTask(newsItemAdaptor, tag).execute(News_Page.getURLbyTag(newsModel.getNewsId(), tag));
						newsloaded = false;
					}
				}
				
				oldPosition = position;
				onRefesh(position);
			}
		});
		
		contentFlipView.setOnDragListener(new OnDragListener() {
			
			@Override
			public boolean onDrag(View v, DragEvent event) {
				
				return false;
			}
		});
         
		contentLayout.addView(contentFlipView);
		
		//comment listview 
		commentListview = (ListView) findViewById(R.id.news_comment_listview);
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
						new LoadCommentTask().execute(cm);
						//Toast.makeText(getActivity(), "Toast " + i++, Toast.LENGTH_SHORT).show();
					} 
					
					oldComment = cm;
					
				}
				
			}
		});
		
		//foot layout
		footerLayout = (RelativeLayout) findViewById(R.id.Footer_Layout);
		
		commentNewsEdittext = (EditText) findViewById(R.id.News_Comments);
		commentSendButton = (Button) findViewById(R.id.News_Comments_Send); 
		commentSendButton.setOnClickListener(this);
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
			if(newsItemAdaptor!=null)
				newsItemAdaptor.notifyDataSetChanged();
			
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
			
			case R.id.News_Comments_Send:{
				String content = commentNewsEdittext.getText().toString();
				if(content == null){
					break;
				} 
				
				if(content.length() > 0){
					
					NewsModel news = (NewsModel) contentFlipView.getSelectedItem();
					
					CommentModel comment = new CommentModel();
					comment.setMemberUid(MemberSession.getMember().getUid());
					comment.setNewsId(news.getNewsId());
					comment.setCommentContent(content);
					
					new PostNewsComment(news).execute(comment);
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
	
	public class PostNewsReads extends AsyncTask<Integer, Void, String>{
		
		public static final String NEWS_READS_URL = "http://183.90.171.209/gs_news/post_news_reads.php";
		
		@Override
		protected String doInBackground(Integer... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("news_id", String.valueOf(params[0])));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(MemberSession.getMember().getUid())));
			
			return HttpConnectUtils.getStrHttpPostConnect(NEWS_READS_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			//Toast.makeText(News_Item_Page.this, result, Toast.LENGTH_SHORT).show();
		}

	}
	
	public class PostNewsComment extends AsyncTask<CommentModel, Void, CommentModel>{   
		
		NewsModel news;
		
		public PostNewsComment(NewsModel news) {
			this.news = news;
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
			paramsPost.add(new BasicNameValuePair(CommentModel.NEWS_ID, String.valueOf(params[0].getNewsId())));
			paramsPost.add(new BasicNameValuePair(CommentModel.COMMENT_CONTENT, params[0].getCommentContent()));
			
			String result = HttpConnectUtils.getStrHttpPostConnect(NEWS_POST_COMMENTS_URL, paramsPost);
			if(result.trim().equals("success")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				params[0].setComment_update_time(sdf.format(new Date()));
				params[0].setMemberPhoto(MemberSession.getMember().getPhoto());
				params[0].setMemberNickname(MemberSession.getMember().getNickname());
				params[0].setMemberUser(MemberSession.getMember().getUser());
				params[0].setMemberTeamId(MemberSession.getMember().getTeamId());
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
				news.setNewsComments(news.getNewsComments()+1);
			}
			
			commentNewsEdittext.setText("");
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
			
			String result = HttpConnectUtils.getStrHttpGetConnect(NEWS_GET_COMMENT_URL + "comment_id=" + params[0].getCommentId() + "&news_id=" + params[0].getNewsId()); 
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			
			List<CommentModel> commentList = CommentModel.convertCommentStrToList(result, CommentModel.NEWS_ID);
			
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

	@Override
	public void onRefesh(final int position) {
		
		final Handler hd = new Handler();
		hd.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						contentFlipView.refreshPage(position);
					}
				});
			}
		}, 500);
	}
	
	public class LoadOldNewsTask extends AsyncTask<String, Void, List<NewsModel>>{
		
		NewsItemsAdapter newsAdapter;
		String tag;
		
		public LoadOldNewsTask(NewsItemsAdapter newsItemAdaptor, String tag) {
			this.newsAdapter = newsItemAdaptor;
			this.tag = tag;
		}
		
		@Override
		protected List<NewsModel> doInBackground(String... params) {
			
			
			String result = HttpConnectUtils.getStrHttpGetConnect(params[0]); 
			if(result.equals("") || result.equals("no news") || result.equals("no parameter")){
				return null;
			}
			//Log.e("0000000000000000000000000", result);
			List<NewsModel> newsList = NewsModel.convertNewsStrToList(result);
			
			return newsList;
		}

		@Override
		protected void onPostExecute(List<NewsModel> result) {
			super.onPostExecute(result);
			if(result!=null && !result.isEmpty()){
				newsAdapter.add(result);
				//contentFlipView.refreshAllPages();
			}
			newsloaded = true;
		}

	}
}
