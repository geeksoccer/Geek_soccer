package com.excelente.geek_soccer.adapter;

import java.io.ByteArrayOutputStream;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.excelente.geek_soccer.News_Item_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.CommentModel;
import com.excelente.geek_soccer.model.NewsModel;
import com.excelente.geek_soccer.utils.DateNewsUtils;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.IntentVideoViewUtils;
import com.excelente.geek_soccer.view.CustomWebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private AlphaAnimation alpha;

	public NewsItemsAdapter(News_Item_Page context, ProgressBar newsWaitProcessbar, List<NewsModel> newsList) {
		this.newsItemPage = context;
		this.mContext = context;
		this.mNewList = newsList;
		this.newsWaitProcessbar = newsWaitProcessbar;
		newsWaitProcessbar.setVisibility(View.GONE);
		
		alpha = new AlphaAnimation(0.8f, 0.8f);
		alpha.setDuration(0); 
		alpha.setFillAfter(true);
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
		LinearLayout newsHead;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		//Log.e("ooooooooooooooooo", "instantiateItem " + position);
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = (View)mInflater.inflate(R.layout.news_item_item_page, null);
		
		NewsItemView newsItemView = new NewsItemView(); 
		newsItemView.newsTopicTextview = (TextView) convertView.findViewById(R.id.news_topic_textview);
	    newsItemView.newsTopicTextview.startAnimation(alpha);
		
		newsItemView.newsCreateTimeTextview = (TextView) convertView.findViewById(R.id.news_create_time_textview);
		newsItemView.newsContentWebview = (CustomWebView) convertView.findViewById(R.id.news_content_webview);
		newsItemView.newsContentWebview.setTag("lin");
		newsItemView.newsCreditTextview = (TextView) convertView.findViewById(R.id.news_credit_textview);
		newsItemView.newsLikeImageview = (ImageView) convertView.findViewById(R.id.news_like); 
		newsItemView.newsLikesTextview = (TextView) convertView.findViewById(R.id.news_likes_textview);
		newsItemView.newsReadsTextview = (TextView) convertView.findViewById(R.id.news_reads_textview);
		newsItemView.newsCommentImageview = (ImageView) convertView.findViewById(R.id.news_comment_imageView);
		newsItemView.newsCommentsTextview = (TextView) convertView.findViewById(R.id.news_comments_textview);
		newsItemView.newsHead = (LinearLayout) convertView.findViewById(R.id.news_header);
		
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
    }

	private void doLoadNewsToViews(final int position, final NewsModel newsModel, final NewsItemView newsItemView) { 

		newsItemView.newsTopicTextview.setText(newsModel.getNewsTopic());
		newsItemView.newsTopicTextview.setSelected(true);
		newsItemView.newsCreateTimeTextview.setText(DateNewsUtils.convertDateToUpdateNewsStr(mContext, DateNewsUtils.convertStrDateTimeDate(newsModel.getNewsCreateTime()))); 
		newsItemView.newsCreditTextview.setText(mContext.getString(R.string.label_credit) + " " + newsModel.getNewsCredit());

		//newsItemView.newsContentWebview.clearCache(true);
		newsItemView.newsContentWebview.loadUrl("about:blank");
		//newsItemView.newsContentWebview.getSettings().setDisplayZoomControls(false);
		newsItemView.newsContentWebview.getSettings().setJavaScriptEnabled(true);
		newsItemView.newsContentWebview.getSettings().setBuiltInZoomControls(true); 
		newsItemView.newsContentWebview.getSettings().setPluginState(PluginState.ON);
		//newsItemView.newsContentWebview.getSettings().setUserAgent(0);
        //newsItemView.newsContentWebview.getSettings().setDefaultTextEncodingName("utf-8");
		newsItemView.newsContentWebview.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress){
	    		newsWaitProcessbar.setProgress(progress);
	        }
		});
		
		newsItemView.newsContentWebview.setWebViewClient(new WebViewClient(){
        	boolean timeout;
        	List<String> urls;
        	
        	@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		view.stopLoading();
        		Log.e("LOG", url);
                Uri uri = Uri.parse(url);
                if (uri.getHost().contains("youtube.com")) {
                    IntentVideoViewUtils.playYoutubeVideo(newsItemPage, url);
                    return true;
                }else if (uri.getHost().contains("facebook.com")){
                	IntentVideoViewUtils.playFacebookVideo(newsItemPage, url);
                	return true;
                }else if (isImageType(url)){
                	/*String upic_me = "http://upic.me/";
            		String image_ohozaa_com = "http://image.ohozaa.com/";
            		if((url.length() > upic_me.length() && url.substring(0, upic_me.length()).equals(upic_me)) || (url.length() > image_ohozaa_com.length() && url.substring(0, image_ohozaa_com.length()).equals(image_ohozaa_com))){
                		doPushImage(view, url);
                		new PushImageTask(view, html).execute(url);
                	}else{
                		new PushImageTask(view, html).execute(url);
                	}*/
            		
            		doPushImage(view, url);
                	return false;
                }else{
                	return false;
                }
            }

			private boolean isImageType(String url) {
				
				String[] imgType = new String[]{".jpg", ".png", ".gif", ".jpeg"};
				for (String type : imgType) {
					if(url.contains(type)){
						return true;
					}
				}
				return false;
				
			}

			@Override
        	public void onPageStarted(final WebView view, String url, Bitmap favicon) {
				
        		urls = new ArrayList<String>();
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
        	
        	@SuppressLint("DefaultLocale") 
        	@Override
        	public void onLoadResource(WebView view, String url) {
        		super.onLoadResource(view, url);
        		String upic_me = "http://upic.me/";
        		String image_ohozaa_com = "http://image.ohozaa.com/";
        		if((url.length() > upic_me.length() && url.substring(0, upic_me.length()).equals(upic_me)) || (url.length() > image_ohozaa_com.length() && url.substring(0, image_ohozaa_com.length()).equals(image_ohozaa_com))){
            		urls.add(url);
            	}
        	}
        	
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		super.onPageFinished(view, url);
        		timeout = false;
        		newsWaitProcessbar.setVisibility(View.GONE);
        		
        		if(urls!=null && !urls.isEmpty()){
        			new PushImagesTask(view, urls, newsModel.getNewsContent()).execute();
        		}
        		
        		String javascript = "javascript:" +
        	            "var iframes = document.getElementsByTagName('iframe');" +
        	            "for (var i = 0, l = iframes.length; i < l; i++) {" +
        	            "   var iframe = iframes[i]," +
        	            "   a = document.createElement('a');" +
        	            "   a.setAttribute('href', iframe.src);" +
        	            "   d = document.createElement('div');" +
        	            "   d.style.width = iframe.offsetWidth + 'px';" +
        	            "   d.style.height = iframe.offsetHeight + 'px';" +
        	            "   d.style.top = iframe.offsetTop + 'px';" +
        	            "   d.style.left = iframe.offsetLeft + 'px';" +
        	            "   d.style.position = 'absolute';" +
        	            "   d.style.opacity = '0';" +
        	            "   d.style.filter = 'alpha(opacity=0)';" +
        	            "   d.style.background = 'black';" +
        	            "   a.appendChild(d);" +
        	            "   iframe.offsetParent.appendChild(a);" +
        	            "}"; 
        	    view.loadUrl(javascript);
        	    
        	    String javascripts = "javascript:" +
        	            "var iframes = document.getElementsByTagName('embed');" +
        	            "for (var i = 0, l = iframes.length; i < l; i++) {" +
        	            "   var iframe = iframes[i]," +
        	            "   a = document.createElement('a');" +
        	            "   a.setAttribute('href', iframe.src);" +
        	            "   d = document.createElement('div');" +
        	            "   d.style.width = iframe.offsetWidth + 'px';" +
        	            "   d.style.height = iframe.offsetHeight + 'px';" +
        	            "   d.style.top = iframe.offsetTop + 'px';" +
        	            "   d.style.left = iframe.offsetLeft + 'px';" +
        	            "   d.style.position = 'absolute';" +
        	            "   d.style.opacity = '0';" +
        	            "   d.style.filter = 'alpha(opacity=0)';" +
        	            "   d.style.background = 'black';" +
        	            "   a.appendChild(d);" +
        	            "   iframe.offsetParent.appendChild(a);" +
        	            "}"; 
        	    view.loadUrl(javascripts);
        	}
        });
		
		String htmlData = getHtml(newsModel);

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
				comment.setMemberUid(SessionManager.getMember(mContext).getUid());

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
	
	private void doPushImage(WebView view, String url) {
		String javascripts = "javascript:" +
	            "var as = document.getElementsByTagName('a');" +
	            "for (var i = 0; i < as.length; i++) {" +
	            "   var a = as[i];" +
	            "   if(a.getAttribute('href') == '"+url+"'){" +
	            "   	img = document.createElement('img');" +
	            "   	img.setAttribute('src', '"+url+"');" +
	            "   	a.parentNode.replaceChild(img, a);" +
	            "   	break;" +
	            "   }" +
	            "}"; 
		
		
		view.loadUrl(javascripts);
	}
	
	private String getHtml(NewsModel newsModel) { 
		String htmlData = "";
		String saveMode = SessionManager.getSetting(mContext, SessionManager.setting_save_mode);
		if(saveMode != null && saveMode.equals("true")){
			Document doc = Jsoup.parse(newsModel.getNewsContent());
			for (Element image : doc.select("img")) {
				Attributes attrs = new Attributes();
				attrs.put("href", image.attr("src"));
				attrs.put("style", "max-width: 100%;width:300px;margin:10px;padding:5px;border:1px solid #BBB;height: 150px;text-align:center;background-color:#EEE;line-height: 150px;vertical-align:middle;color:#666;display: block;");
				Element a = new Element(Tag.valueOf("a"), "", attrs);
				a.appendText(mContext.getString(R.string.show_photo));
				image.replaceWith(a); 
			}
			
			htmlData = doc.toString();
			if(SessionManager.getMember(mContext).getTeamId() == 2)
				htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;} embed{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ htmlData +"<br><br><br><br></body><script> function myFunction(){document.body.style.fontSize ='12px';}</script></html>";
			else
				htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;} embed{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ htmlData +"<br><br><br><br></body><script> function myFunction(){document.body.style.fontSize ='16px';}</script></html>";
		}else{
			if(SessionManager.getMember(mContext).getTeamId() == 2)
				htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;} embed{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ newsModel.getNewsContent() +"<br><br><br><br></body><script> function myFunction(){document.body.style.fontSize ='12px';}</script></html>";
			else
				htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;} embed{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ newsModel.getNewsContent() +"<br><br><br><br></body><script> function myFunction(){document.body.style.fontSize ='16px';}</script></html>";
		}
		return htmlData;
	}
	
	@Override
	public int getCount() {
		return mNewList.size();
	}
	
	public class PushImageTask extends AsyncTask<String, Void, String>{
		
		WebView webview;
		String html;
		String url;
		
		public PushImageTask(WebView wv, String html) {
			this.webview = wv;
			this.html = html;
		}

		@Override
		protected String doInBackground(String... params) {
				url = params[0];
				String encodedString = "";
				
				HttpGet getRequest = new HttpGet(url); 
				getRequest.addHeader("Referer", "http://localhost");
	    		DefaultHttpClient client = new DefaultHttpClient();
	    		try {
					HttpResponse httpReponse = client.execute(getRequest);
					InputStream reponseInputStream = httpReponse.getEntity().getContent();
					
					Bitmap bm = BitmapFactory.decodeStream(reponseInputStream);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					bm.compress(Bitmap.CompressFormat.PNG, 75, baos); //bm is the bitmap object   
					byte[] b = baos.toByteArray();
		    		encodedString = "data:image/png;base64," + new String(Base64.encode(b, Base64.NO_WRAP));
		    		
		    		reponseInputStream.close();
				} catch (ClientProtocolException e){
					return "";
				} catch (IOException e) {
					return "";
				}
			
			return encodedString;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			doPushImage(webview, result);
		} 
		
	}
	
	public class PushImagesTask extends AsyncTask<String, Void, Void>{
		
		WebView webview;
		List<String> urls;
		List<String> base64s;
		String html;
		
		public PushImagesTask(WebView wv, List<String> urls, String html) {
			this.webview = wv;
			this.urls = urls;
			this.html = html;
			this.base64s = new ArrayList<String>();
		}

		@Override
		protected Void doInBackground(String... params) {
			
			for (String url : urls) {
				String encodedString = "";
				
				HttpGet getRequest = new HttpGet(url); 
				getRequest.addHeader("Referer", "http://localhost");
	    		DefaultHttpClient client = new DefaultHttpClient();
	    		try {
					HttpResponse httpReponse = client.execute(getRequest);
					InputStream reponseInputStream = httpReponse.getEntity().getContent();
					
					Bitmap bm = BitmapFactory.decodeStream(reponseInputStream);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					bm.compress(Bitmap.CompressFormat.PNG, 75, baos); //bm is the bitmap object   
					byte[] b = baos.toByteArray();
		    		encodedString = "data:image/png;base64," + Base64.encodeToString(b, Base64.DEFAULT);
		    		
		    		reponseInputStream.close();
				} catch (ClientProtocolException e) {
					base64s.add(encodedString);
					continue;
				} catch (IOException e) {
					base64s.add(encodedString);
					continue;
				}
	    		
	    		base64s.add(encodedString);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			String htmlData = "";
			if(SessionManager.getMember(mContext).getTeamId() == 2)
				htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;} embed{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ html +"<br><br><br><br></body><script> function myFunction(){document.body.style.fontSize ='12px';}</script></html>";
			else
				htmlData = "<html><head><style>img{max-width: 100%; width:auto; height: auto;} iframe{max-width: 100%; width:auto; height: auto;} embed{max-width: 100%; width:auto; height: auto;}</style></head><body onload='myFunction()' >"+ html +"<br><br><br><br></body><script> function myFunction(){document.body.style.fontSize ='16px';}</script></html>";
	
			for (int i=0; i < urls.size(); i++) {
				htmlData = htmlData.replace(urls.get(i), base64s.get(i));  
			}
			
			webview.loadData(htmlData, "text/html; charset=UTF-8", null);
			
		} 
		
	}
	
	public class PostNewsLikes extends AsyncTask<NewsModel, Void, String>{
		
		private static final String NEWS_LIKES_URL = "http://183.90.171.209/gs_news/post_news_like.php"; 

		@Override
		protected String doInBackground(NewsModel... params) {
			
			List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();
			paramsPost.add(new BasicNameValuePair("news_id", String.valueOf(params[0].getNewsId())));
			paramsPost.add(new BasicNameValuePair("member_id", String.valueOf(SessionManager.getMember(mContext).getUid())));
			paramsPost.add(new BasicNameValuePair("status_like", String.valueOf(params[0].getStatusLike())));
			
			return HttpConnectUtils.getStrHttpPostConnect(NEWS_LIKES_URL, paramsPost);
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
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
