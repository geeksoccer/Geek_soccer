package com.excelente.geek_soccer.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.MemberSession;
import com.excelente.geek_soccer.News_Item_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.CommentModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.utils.DateNewsUtils;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewsItemsAdapter extends BaseAdapter{
	
	public interface Callback {
		public void onRefesh(int position);
	}
	
	Context mContext;
	List<NewsModel> mNewList;
	ProgressBar newsWaitProcessbar;
	
	News_Item_Page newsItemPage;
	
	Callback call;

	public NewsItemsAdapter(News_Item_Page context, ProgressBar newsWaitProcessbar, List<NewsModel> newsList) {
		this.newsItemPage = context;
		this.mContext = context;
		this.mNewList = newsList;
		this.newsWaitProcessbar = newsWaitProcessbar;
	}
	
	public void setCallback(Callback call) {
		this.call = call;
	}
	
	public class NewsItemView {
		TextView newsTopicTextview; 
		TextView newsCreateTimeTextview;
		WebView newsContentWebview;
		TextView newsCreditTextview;
		ImageView newsLikeImageview;
		TextView newsLikesTextview;
		TextView newsReadsTextview;
		ImageView newsCommentImageview;
		TextView newsCommentsTextview;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        NewsItemView newsItemView;
        
        if(convertView==null){
        	newsItemView = new NewsItemView();
        	convertView = mInflater.inflate(R.layout.news_item_item_page, parent, false);
	        newsItemView.newsTopicTextview = (TextView) convertView.findViewById(R.id.news_topic_textview);
	        newsItemView.newsCreateTimeTextview = (TextView) convertView.findViewById(R.id.news_create_time_textview);
	        newsItemView.newsContentWebview = (WebView) convertView.findViewById(R.id.news_content_webview);
	        newsItemView.newsCreditTextview = (TextView) convertView.findViewById(R.id.news_credit_textview);
	        newsItemView.newsLikeImageview = (ImageView) convertView.findViewById(R.id.news_like); 
	        newsItemView.newsLikesTextview = (TextView) convertView.findViewById(R.id.news_likes_textview);
	        newsItemView.newsReadsTextview = (TextView) convertView.findViewById(R.id.news_reads_textview);
	        newsItemView.newsCommentImageview = (ImageView) convertView.findViewById(R.id.news_comment_imageView);
	        newsItemView.newsCommentsTextview = (TextView) convertView.findViewById(R.id.news_comments_textview);
	        convertView.setTag(newsItemView);
        }else{
        	newsItemView = (NewsItemView)convertView.getTag();
        }
        
        NewsModel newsModel = (NewsModel) getItem(position);
        
        setVisibleNewsContent(false, newsItemView);
        doLoadNewsToViews(position, newsModel, newsItemView);
        
		return convertView;
	}
	
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })  
	private void doLoadNewsToViews(final int position, final NewsModel newsModel, final NewsItemView newsItemView) { 
		
		newsItemView.newsTopicTextview.setText(newsModel.getNewsTopic());
		newsItemView.newsCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(mContext, DateNewsUtils.convertStrDateTimeDate(newsModel.getNewsCreateTime()))); 
		newsItemView.newsCreditTextview.setText(mContext.getString(R.string.label_credit) + " " + newsModel.getNewsCredit());
        
		newsItemView.newsContentWebview.getSettings().setDisplayZoomControls(false);
		newsItemView.newsContentWebview.getSettings().setJavaScriptEnabled(true);
		newsItemView.newsContentWebview.getSettings().setBuiltInZoomControls(true); 
		newsItemView.newsContentWebview.getSettings().setPluginState(PluginState.ON);
        
		SwipeDetector swipeDetector = new SwipeDetector(); 
		newsItemView.newsContentWebview.setOnTouchListener(swipeDetector);
        //newsContentWebview.getSettings().setDefaultTextEncodingName("utf-8");
		newsItemView.newsContentWebview.setWebChromeClient(new WebChromeClient(){

			public void onProgressChanged(WebView view, int progress){
        		newsWaitProcessbar.setProgress(progress);
            }
			
			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
				Log.e("onShowCustomView", "OK");
			}
			
			@Override
			public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
				super.onReceivedTouchIconUrl(view, url, precomposed);
				Log.e("onReceivedTouchIconUrl", "OK");
			}
			
			@Override
			public void onHideCustomView() {
				super.onHideCustomView();
				Log.e("onHideCustomView", "OK");
			}
        });
		newsItemView.newsContentWebview.setWebViewClient(new WebViewClient(){
        	boolean timeout;
        	
        	@Override
        	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        		
        		String upic_me = "http://upic.me/";
        		String image_ohozaa_com = "http://image.ohozaa.com/";
        		
        		if(url.substring(0, upic_me.length()).equals(upic_me)){
        			HttpGet getRequest = new HttpGet(url);
        			
	        		getRequest.addHeader("Accept", "image/webp,*/*;q=0.8");
	        		getRequest.addHeader("Accept-Encoding", "gzip,deflate,sdch");
	        		getRequest.addHeader("Accept-Language", "en-US,en;q=0.8");
	        		getRequest.addHeader("Connection", "keep-alive");
	        		getRequest.addHeader("Cookie", "PHPSESSID=cgmh87ivlp1pstuf56c4b65qr1; iz_uid=450499963b4f3f32857f0ed793dd2175; testcookie=enabled; __utma=113133888.1190374733.1390030425.1390030425.1390030425.1; __utmc=113133888; __utmz=113133888.1390030425.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
	        		getRequest.addHeader("Host", "upic.me");
	        		getRequest.addHeader("Referer", "http://localhost");
	        		getRequest.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36");
	        		
	        		DefaultHttpClient client = new DefaultHttpClient();
	        		try {
						HttpResponse httpReponse = client.execute(getRequest);
						InputStream reponseInputStream = httpReponse.getEntity().getContent();
						return new WebResourceResponse(httpReponse.getEntity().getContentType().getValue(), "utf-8", reponseInputStream);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        		
        		}else if(url.substring(0, image_ohozaa_com.length()).equals(image_ohozaa_com)){ 
        			HttpGet getRequest = new HttpGet(url);
        			
        			getRequest.addHeader("Accept", "image/webp,*/*;q=0.8");
	        		getRequest.addHeader("Accept-Encoding", "gzip,deflate,sdch");
	        		getRequest.addHeader("Accept-Language", "en-US,en;q=0.8");
	        		getRequest.addHeader("Connection", "keep-alive");
	        		getRequest.addHeader("Cookie", "ozuid=848712369; _cbclose=1; _cbclose19784=1; _uid19784=D96E3980.2; visit_time=3");
	        		getRequest.addHeader("Host", "image.ohozaa.com");
	        		getRequest.addHeader("Referer", "http://localhost");
	        		getRequest.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36");
	        		
	        		DefaultHttpClient client = new DefaultHttpClient();
	        		try {
						HttpResponse httpReponse = client.execute(getRequest);
						InputStream reponseInputStream = httpReponse.getEntity().getContent();
						return new WebResourceResponse(httpReponse.getEntity().getContentType().getValue(), "utf-8", reponseInputStream);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        		
        		}
        		
        		
        		return super.shouldInterceptRequest(view, url);
        	}
        	
        	@Override
        	public void onPageStarted(final WebView view, String url, Bitmap favicon) {
        		timeout = true;
        		super.onPageStarted(view, url, favicon);
        		
        		Runnable timeoutRun = new Runnable() {
                    @Override
                    public void run() {
                        if(timeout) {
                        	view.stopLoading();
                    		timeout = false;
                        }
                    }
                };
        		
        		new Handler().postDelayed(timeoutRun, 20000);
        	}
        	
        	
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		super.onPageFinished(view, url);
        		setVisibleNewsContent(true, newsItemView); 
        		timeout = false;
        		
        		call.onRefesh(position);
        	}
        });
        
		String htmlData = "";
		if(MemberSession.getMember().getTeamId() == 2)
			htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ newsModel.getNewsContent() +"</body><script> function myFunction(){document.body.style.fontSize ='12px';}</script></html>";
		else
			htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ newsModel.getNewsContent() +"</body><script> function myFunction(){document.body.style.fontSize ='16px';}</script></html>";

		newsItemView.newsContentWebview.loadData( htmlData, "text/html; charset=UTF-8", null);
        
        newsItemView.newsLikeImageview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (NetworkUtils.isNetworkAvailable(mContext)){
					if(newsModel.getStatusLike()==0){
						newsItemView.newsLikeImageview.setImageResource(R.drawable.news_likes_selected);
						newsModel.setNewsLikes(newsModel.getNewsLikes() + 1);
						newsModel.setStatusLike(1);
					}else{
						newsItemView.newsLikeImageview.setImageResource(R.drawable.news_likes);
						newsModel.setNewsLikes(newsModel.getNewsLikes() - 1);
						newsModel.setStatusLike(0);
					}
					
					new PostNewsLikes().execute(newsModel);
				}else
					Toast.makeText(mContext, NetworkUtils.getConnectivityStatusString(mContext), Toast.LENGTH_SHORT).show();
				
				newsItemView.newsLikesTextview.setText(String.valueOf(newsModel.getNewsLikes()));
			}
		});
        
        newsItemView.newsCommentImageview.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommentModel comment = new CommentModel();
				comment.setNewsId(newsModel.getNewsId());
				comment.setMemberUid(MemberSession.getMember().getUid());
				
				newsItemPage.doClickNewsComment(comment, newsModel);
			}
		});
        
        newsItemView.newsLikesTextview.setText(String.valueOf(newsModel.getNewsLikes())); 
        newsItemView.newsReadsTextview.setText(String.valueOf(newsModel.getNewsReads()));
        newsItemView.newsCommentsTextview.setText(String.valueOf(newsModel.getNewsComments()));
        
        if(newsModel.getStatusLike()==0){
			newsItemView.newsLikeImageview.setImageResource(R.drawable.news_likes);
		}else{
			newsItemView.newsLikeImageview.setImageResource(R.drawable.news_likes_selected);
		}
	}

	private void setVisibleNewsContent(boolean visible, NewsItemView newsItemView) {
		if(!visible){
			newsItemView.newsContentWebview.setVisibility(View.GONE);
			newsItemView.newsCreditTextview.setVisibility(View.GONE);
			newsWaitProcessbar.setVisibility(View.VISIBLE);
		}else{
			newsItemView.newsContentWebview.setVisibility(View.VISIBLE);
			newsItemView.newsCreditTextview.setVisibility(View.VISIBLE);
			newsWaitProcessbar.setVisibility(View.GONE);
		}
	}
	
	@Override
	public int getCount() {
		return mNewList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mNewList.get(pos);
	}

	@Override
	public long getItemId(int position) {
		return mNewList.indexOf(mNewList.get(position)); 
	}
	
	public class PostNewsLikes extends AsyncTask<NewsModel, Void, String>{
		
		private static final String NEWS_LIKES_URL = "http://183.90.171.209/gs_news/post_news_like.php"; 

		@Override
		protected String doInBackground(NewsModel... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("news_id", String.valueOf(params[0].getNewsId())));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(MemberSession.getMember().getUid())));
			paramsPost.add(new BasicNameValuePair("status_like", String.valueOf(params[0].getStatusLike())));
			
			return HttpConnectUtils.getStrHttpPostConnect(NEWS_LIKES_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			//Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
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

}
