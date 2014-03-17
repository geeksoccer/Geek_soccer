package com.excelente.geek_soccer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HilightModel implements Serializable{

	private static final long serialVersionUID = 1L; 
	
	public static final String HILIGHT_ID = "hilight_id";
	public static final String HILIGHT_LINK = "hilight_link"; 
	public static final String HILIGHT_TYPE = "hilight_type";
	public static final String HILIGHT_TOPIC = "hilight_topic";
	public static final String HILIGHT_IMAGE = "hilight_image";
	public static final String HILIGHT_LINK_LIST = "hilight_link_list";
	public static final String HILIGHT_LIKES = "hilight_likes";
	public static final String HILIGHT_COMMENTS = "hilight_comments";
	public static final String HILIGHT_VIEWS = "hilight_views";
	public static final String HILIGHT_CREATE_TIME = "hilight_create_time";
	public static final String HILIGHT_UPDATE_TIME = "hilight_update_time";
	
	int hilightId;
	String hilightLink; 
	String hilightType;
	String hilightTopic;
	String hilightImage;
	List<HilightItemModel> hilightLinkList;
	int hilightLikes;
	int hilightComments;
	int hilightViews;
	String hilightCreateTime;
	String hilightUpdateTime;
	public int getHilightId() {
		return hilightId;
	}
	public void setHilightId(int hilightId) {
		this.hilightId = hilightId;
	}
	public String getHilightLink() {
		return hilightLink;
	}
	public void setHilightLink(String hilightLink) {
		this.hilightLink = hilightLink;
	}
	public String getHilightType() {
		return hilightType;
	}
	public void setHilightType(String hilightType) {
		this.hilightType = hilightType;
	}
	public String getHilightTopic() {
		return hilightTopic;
	}
	public void setHilightTopic(String hilightTopic) {
		this.hilightTopic = hilightTopic;
	}
	public String getHilightImage() {
		return hilightImage;
	}
	public void setHilightImage(String hilightImage) {
		this.hilightImage = hilightImage;
	}
	public List<HilightItemModel> getHilightLinkList() {
		return hilightLinkList;
	}
	public void setHilightLinkList(List<HilightItemModel> hilightLinkList) {
		this.hilightLinkList = hilightLinkList;
	}
	public int getHilightLikes() {
		return hilightLikes;
	}
	public void setHilightLikes(int hilightLikes) {
		this.hilightLikes = hilightLikes;
	}
	public int getHilightComments() {
		return hilightComments;
	}
	public void setHilightComments(int hilightComments) {
		this.hilightComments = hilightComments;
	}
	public int getHilightViews() {
		return hilightViews;
	}
	public void setHilightViews(int hilightViews) {
		this.hilightViews = hilightViews;
	}
	public String getHilightCreateTime() {
		return hilightCreateTime;
	}
	public void setHilightCreateTime(String hilightCreateTime) {
		this.hilightCreateTime = hilightCreateTime;
	}
	public String getHilightUpdateTime() {
		return hilightUpdateTime;
	}
	public void setHilightUpdateTime(String hilightUpdateTime) {
		this.hilightUpdateTime = hilightUpdateTime;
	}

	public static List<HilightModel> convertHilightStrToList(String result) { 
		//Log.e("+++++++++++++++++++", result);
		List<HilightModel> hilightList = new ArrayList<HilightModel>();
		
		try {
			
			JSONArray hilightJsonArr = new JSONArray(result); 
			for(int i=0; i<hilightJsonArr.length(); i++){
				JSONObject hilightObj = (JSONObject) hilightJsonArr.get(i);
				
				HilightModel hilight = new HilightModel();
				hilight.setHilightId(hilightObj.getInt(HILIGHT_ID));
				hilight.setHilightLink(hilightObj.getString(HILIGHT_LINK));
				hilight.setHilightType(hilightObj.getString(HILIGHT_TYPE));
				hilight.setHilightTopic(hilightObj.getString(HILIGHT_TOPIC));
				hilight.setHilightImage(hilightObj.getString(HILIGHT_IMAGE));
				hilight.setHilightLikes(hilightObj.getInt(HILIGHT_LIKES));
				hilight.setHilightComments(hilightObj.getInt(HILIGHT_COMMENTS));
				hilight.setHilightCreateTime(hilightObj.getString(HILIGHT_CREATE_TIME));
				hilight.setHilightUpdateTime(hilightObj.getString(HILIGHT_UPDATE_TIME));
				
				JSONArray hilightItemArr = hilightObj.getJSONArray(HILIGHT_LINK_LIST);
				
				List<HilightItemModel> hilightItemList = new ArrayList<HilightItemModel>();
				for(int j=0; j<hilightItemArr.length(); j++){
					JSONObject hilightItemObj = (JSONObject) hilightItemArr.get(j);
					HilightItemModel hilightItem = new HilightItemModel(hilightItemObj.getString(HilightItemModel.HILIGHT_ITEM_LINK), hilightItemObj.getString(HilightItemModel.HILIGHT_ITEM_TOPIC));
					hilightItemList.add(hilightItem);
				}
				
				hilight.setHilightLinkList(hilightItemList);
				hilightList.add(hilight); 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return hilightList;
		}
		
		return hilightList;
	}
}
