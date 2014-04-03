package com.excelente.geek_soccer.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.excelente.geek_soccer.view.CustomWebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "SetJavaScriptEnabled", "UseSparseArrays" })
public class NewsItemsAdapter extends PagerAdapter{
	
	Context mContext;
	List<NewsModel> mNewList;
	ProgressBar newsWaitProcessbar;
	
	News_Item_Page newsItemPage;
	
	Map<Integer, NewsItemView> newsItemViews = new HashMap<Integer, NewsItemsAdapter.NewsItemView>();

	public NewsItemsAdapter(News_Item_Page context, ProgressBar newsWaitProcessbar, List<NewsModel> newsList) {
		this.newsItemPage = context;
		this.mContext = context;
		this.mNewList = newsList;
		this.newsWaitProcessbar = newsWaitProcessbar;
		newsWaitProcessbar.setVisibility(View.GONE);
	}
	
	public class NewsItemView {
		TextView newsTopicTextview; 
		TextView newsCreateTimeTextview;
		CustomWebView newsContentWebview;
		TextView newsCreditTextview;
		ImageView newsLikeImageview; 
		TextView newsLikesTextview;
		TextView newsReadsTextview;
		ImageView newsCommentImageview;
		TextView newsCommentsTextview;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		Log.e("ooooooooooooooooo", "instantiateItem " + position);
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = (View)mInflater.inflate(R.layout.news_item_item_page, null);
		
		NewsItemView newsItemView = new NewsItemView(); 
		newsItemView.newsTopicTextview = (TextView) convertView.findViewById(R.id.news_topic_textview);
		newsItemView.newsCreateTimeTextview = (TextView) convertView.findViewById(R.id.news_create_time_textview);
		newsItemView.newsContentWebview = (CustomWebView) convertView.findViewById(R.id.news_content_webview);
		newsItemView.newsCreditTextview = (TextView) convertView.findViewById(R.id.news_credit_textview);
		newsItemView.newsLikeImageview = (ImageView) convertView.findViewById(R.id.news_like); 
		newsItemView.newsLikesTextview = (TextView) convertView.findViewById(R.id.news_likes_textview);
		newsItemView.newsReadsTextview = (TextView) convertView.findViewById(R.id.news_reads_textview);
		newsItemView.newsCommentImageview = (ImageView) convertView.findViewById(R.id.news_comment_imageView);
		newsItemView.newsCommentsTextview = (TextView) convertView.findViewById(R.id.news_comments_textview);
		
        NewsModel newsModel = (NewsModel) mNewList.get(position);
        doLoadNewsToViews(position, newsModel, newsItemView);
        
        newsItemViews.put(position, newsItemView);
        
        ((ViewPager) container).addView(convertView,0);
        
        return convertView;
	}
	
	@Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
		TextView newsTopicTextview = (TextView) ((View) view).findViewById(R.id.news_topic_textview);
		TextView newsCreateTimeTextview = (TextView) ((View) view).findViewById(R.id.news_create_time_textview);
		CustomWebView newsContentWebview = (CustomWebView) ((View) view).findViewById(R.id.news_content_webview);
		TextView newsCreditTextview = (TextView) ((View) view).findViewById(R.id.news_credit_textview);
		ImageView newsLikeImageview = (ImageView) ((View) view).findViewById(R.id.news_like); 
		TextView newsLikesTextview = (TextView) ((View) view).findViewById(R.id.news_likes_textview);
		TextView newsReadsTextview = (TextView) ((View) view).findViewById(R.id.news_reads_textview);
		ImageView newsCommentImageview = (ImageView) ((View) view).findViewById(R.id.news_comment_imageView);
		TextView newsCommentsTextview = (TextView) ((View) view).findViewById(R.id.news_comments_textview);
		
		((ViewPager) collection).removeView(newsTopicTextview);
		((ViewPager) collection).removeView(newsCreateTimeTextview);
		((ViewPager) collection).removeView(newsContentWebview);
		((ViewPager) collection).removeView(newsCreditTextview);
		((ViewPager) collection).removeView(newsLikeImageview);
		((ViewPager) collection).removeView(newsLikesTextview);
		((ViewPager) collection).removeView(newsReadsTextview);
		((ViewPager) collection).removeView(newsCommentImageview);
		((ViewPager) collection).removeView(newsCommentsTextview);
        ((ViewPager) collection).removeView((View) view);
        newsItemViews.remove(position);
        Log.e("ooooooooooooooooo", "destroyItem " + position);
    }

	private void doLoadNewsToViews(final int position, final NewsModel newsModel, final NewsItemView newsItemView) { 

		newsItemView.newsTopicTextview.setText(newsModel.getNewsTopic());
		newsItemView.newsCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(mContext, DateNewsUtils.convertStrDateTimeDate(newsModel.getNewsCreateTime()))); 
		newsItemView.newsCreditTextview.setText(mContext.getString(R.string.label_credit) + " " + newsModel.getNewsCredit());

		//newsItemView.newsContentWebview.clearCache(true);
		//newsItemView.newsContentWebview.loadUrl("about:blank");

		newsItemView.newsContentWebview.getSettings().setDisplayZoomControls(false);
		newsItemView.newsContentWebview.getSettings().setJavaScriptEnabled(true);
		newsItemView.newsContentWebview.getSettings().setBuiltInZoomControls(true); 
		newsItemView.newsContentWebview.getSettings().setPluginState(PluginState.ON);
        
        //newsItemView.newsContentWebview.getSettings().setDefaultTextEncodingName("utf-8");
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
        		
        		newsWaitProcessbar.setVisibility(View.VISIBLE);
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
        		
        		timeout = false;
        		newsWaitProcessbar.setVisibility(View.GONE);
        		
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
	
	@Override
	public int getCount() {
		return mNewList.size();
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
	
	public void add(List<NewsModel> newsList) {
		if(this.mNewList.size()<=100){
		
			for (NewsModel newsModel : newsList) {
				this.mNewList.add(newsModel);
			}
			
			notifyDataSetChanged();
		}
	}
	
	
	@Override
	public Parcelable saveState() {
		return null;
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals( object );
	}

	public List<NewsModel> getmNewList() {
		return mNewList;
	}

	public void setmNewList(List<NewsModel> mNewList) {
		this.mNewList = mNewList;
	}

}
