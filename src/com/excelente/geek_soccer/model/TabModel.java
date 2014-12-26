package com.excelente.geek_soccer.model;

import java.util.List;

import android.widget.BaseAdapter;

public class TabModel{
	
	private int index;
	private String url;
	public List<NewsModel> newsList;
	public BaseAdapter adapter;
	public List<HilightModel> hilightList;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
