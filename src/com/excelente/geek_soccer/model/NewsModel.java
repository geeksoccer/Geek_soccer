package com.excelente.geek_soccer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class NewsModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String NEWS_ID = "news_id";
	public static final String NEWS_LINK = "news_link"; 
	public static final String NEWS_TEAM_ID = "news_team_id";
	public static final String NEWS_TOPIC = "news_topic";
	public static final String NEWS_IMAGE = "news_image";
	public static final String NEWS_CONTENT = "news_content";
	public static final String NEWS_LIKES = "news_likes";
	public static final String NEWS_COMMENTS = "news_comments"; 
	public static final String NEWS_CREDIT = "news_credit";
	public static final String NEWS_LANGUAGE = "news_language";
	public static final String NEWS_READS = "news_reads";
	public static final String NEWS_CREATE_TIME = "news_create_time";
	public static final String NEWS_UPDATE_TIME = "news_update_time";
	public static final String NEWS_STATUS_VIEW = "status_view";
	public static final String NEWS_STATUS_LIKE = "status_like";
	
	int newsId;
	String newsLink;
	int newsTeamId;
	String newsTopic;
	String newsImage;
	String newsContent;
	int newsLikes;
	int newsComments; 
	String newsCredit;
	String newsLanguage;
	int newsReads;
	String newsCreateTime;
	String newsUpdateTime;
	
	int statusView;
	int statusLike;
	
	Bitmap newsBitmapImage;
	
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public String getNewsLink() {
		return newsLink;
	}
	public void setNewsLink(String newsLink) {
		this.newsLink = newsLink;
	}
	public int getNewsTeamId() {
		return newsTeamId;
	}
	public void setNewsTeamId(int newsTeamId) { 
		this.newsTeamId = newsTeamId;
	}
	public String getNewsTopic() {
		return newsTopic;
	}
	public void setNewsTopic(String newsTopic) {
		this.newsTopic = newsTopic.replace("\n", "").replace("\r", "").trim();
	}
	public String getNewsImage() {
		return newsImage;
	}
	public void setNewsImage(String newsImage) {
		this.newsImage = newsImage;
	}
	public String getNewsContent() {
		return newsContent;
	}
	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}
	public int getNewsLikes() {
		return newsLikes;
	}
	public void setNewsLikes(int newsLikes) {
		this.newsLikes = newsLikes;
	}
	public int getNewsComments() { 
		return newsComments;
	}
	public void setNewsComments(int newsComments) {
		this.newsComments = newsComments;
	}
	public String getNewsCredit() {
		return newsCredit;
	}
	public void setNewsCredit(String newsCredit) {
		this.newsCredit = newsCredit;
	}
	public String getNewsCreateTime() {
		return newsCreateTime;
	}
	public void setNewsCreateTime(String newsCreateTime) {
		this.newsCreateTime = newsCreateTime;
	}
	public String getNewsLanguage() {
		return newsLanguage;
	}
	public void setNewsLanguage(String newsLanguage) {
		this.newsLanguage = newsLanguage;
	}
	public String getNewsUpdateTime() {
		return newsUpdateTime;
	}
	public void setNewsUpdateTime(String newsUpdateTime) {
		this.newsUpdateTime = newsUpdateTime;
	}
	public Bitmap getNewsBitmapImage() {
		return newsBitmapImage;
	}
	public void setNewsBitmapImage(Bitmap newsBitmapImage) {
		this.newsBitmapImage = newsBitmapImage;
	}
	public int getNewsReads() {
		return newsReads;
	}
	public void setNewsReads(int newsReads) { 
		this.newsReads = newsReads;
	}
	
	public int getStatusView() {
		return statusView;
	}
	public void setStatusView(int statusView) {
		this.statusView = statusView;
	}
	
	public int getStatusLike() {
		return statusLike;
	}
	public void setStatusLike(int statusLike) {
		this.statusLike = statusLike;
	}
	public static List<NewsModel> convertNewsStrToList(String result) { 
		
		List<NewsModel> newsList = new ArrayList<NewsModel>();
		
		try {
			
			JSONArray newsJsonArr = new JSONArray(result);
			for(int i=0; i<newsJsonArr.length(); i++){
				JSONObject newsObj = (JSONObject) newsJsonArr.get(i);
				
				NewsModel news = new NewsModel();
				news.setNewsId(newsObj.getInt(NewsModel.NEWS_ID));
				news.setNewsLink(newsObj.getString(NewsModel.NEWS_LINK)); 
				news.setNewsTeamId(newsObj.getInt(NewsModel.NEWS_TEAM_ID));
				news.setNewsTopic(StringEscapeUtils.unescapeHtml4(newsObj.getString(NewsModel.NEWS_TOPIC)));
				news.setNewsImage(newsObj.getString(NewsModel.NEWS_IMAGE)); 
				news.setNewsContent(newsObj.getString(NewsModel.NEWS_CONTENT));
				news.setNewsCredit(newsObj.getString(NewsModel.NEWS_CREDIT));
				news.setNewsLikes(newsObj.getInt(NewsModel.NEWS_LIKES));
				news.setNewsComments(newsObj.getInt(NewsModel.NEWS_COMMENTS));
				news.setNewsLanguage(newsObj.getString(NewsModel.NEWS_LANGUAGE));
				news.setNewsReads(newsObj.getInt(NewsModel.NEWS_READS));
				news.setNewsCreateTime(newsObj.getString(NewsModel.NEWS_CREATE_TIME));
				news.setNewsUpdateTime(newsObj.getString(NewsModel.NEWS_UPDATE_TIME));
				news.setStatusView(newsObj.getInt(NewsModel.NEWS_STATUS_VIEW));
				news.setStatusLike(newsObj.getInt(NewsModel.NEWS_STATUS_LIKE));
				
				newsList.add(news);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return newsList;
		}
		
		return newsList;
	}
	
}
