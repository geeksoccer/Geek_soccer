package com.excelente.geek_soccer.model;

import java.util.List;

import com.excelente.geek_soccer.adapter.NewsAdapter;

public class TabModel{
	
	private String url;
	public List<NewsModel> newsList;
	public NewsAdapter adapter;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
