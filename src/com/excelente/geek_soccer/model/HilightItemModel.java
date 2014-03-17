package com.excelente.geek_soccer.model;

import java.io.Serializable;

public class HilightItemModel implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final String HILIGHT_ITEM_LINK = "hilight_item_link";
	public static final String HILIGHT_ITEM_TOPIC = "hilight_item_topic";
	
	String hilightItemLink;
	String hilightItemTopic;
	
	public HilightItemModel(String hilightItemLink, String hilightItemTopic) {
		this.hilightItemLink = hilightItemLink;
		this.hilightItemTopic = hilightItemTopic;
	}
	
	public String getHilightItemLink() {
		return hilightItemLink;
	}
	public void setHilightItemLink(String hilightItemLink) {
		this.hilightItemLink = hilightItemLink;
	}
	public String getHilightItemTopic() {
		return hilightItemTopic;
	}
	public void setHilightItemTopic(String hilightItemTopic) {
		this.hilightItemTopic = hilightItemTopic;
	}

}
