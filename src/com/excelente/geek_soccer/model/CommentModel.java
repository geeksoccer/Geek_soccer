package com.excelente.geek_soccer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String COMMENT_ID = "comment_id";
	public static final String MEMBER_UID = "member_uid"; 
	public static final String NEWS_ID = "news_id";
	public static final String HILIGHT_ID = "hilight_id";
	public static final String COMMENT_CONTENT = "comment_content";
	public static final String COMMENT_CREATE_TIME = "comment_create_time";
	public static final String COMMENT_UPDATE_TIME = "comment_update_time";
	
	public static final String MAX_COMMENT = "max_comment"; 
	
	int commentId;
	int memberUid; 
	int newsId;
	String commentContent;
	String comment_create_time;
	String comment_update_time;
	
	String memberUser;
	String memberPhoto;
	String memberNickname;
	int memberTeamId;
	
	int mexComment;
	
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public int getMemberUid() {
		return memberUid;
	}
	public void setMemberUid(int memberUid) {
		this.memberUid = memberUid;
	}
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public String getComment_create_time() {
		return comment_create_time;
	}
	public void setComment_create_time(String comment_create_time) {
		this.comment_create_time = comment_create_time;
	}
	public String getComment_update_time() {
		return comment_update_time;
	}
	public void setComment_update_time(String comment_update_time) {
		this.comment_update_time = comment_update_time;
	}
	
	public String getMemberUser() {
		return memberUser;
	}
	public void setMemberUser(String memberUser) {
		this.memberUser = memberUser;
	}
	public String getMemberPhoto() {
		return memberPhoto;
	}
	public void setMemberPhoto(String memberPhoto) {
		this.memberPhoto = memberPhoto;
	}
	public String getMemberNickname() {
		return memberNickname;
	}
	public void setMemberNickname(String memberNickname) {
		this.memberNickname = memberNickname;
	}
	public int getMemberTeamId() {
		return memberTeamId;
	}
	public void setMemberTeamId(int memberTeamId) {
		this.memberTeamId = memberTeamId;
	}
	public int getMexComment() {
		return mexComment;
	}
	public void setMaxComment(int mexComment) {
		this.mexComment = mexComment;
	}
	
	public static List<CommentModel> convertCommentStrToList(String result, String nameId) { 
		 
		List<CommentModel> commentList = new ArrayList<CommentModel>();
		
		try {
			
			JSONArray commentJsonArr = new JSONArray(result);
	
			for(int i=0; i<commentJsonArr.length(); i++){
				JSONObject commentObj = (JSONObject) commentJsonArr.get(i);
				 
				CommentModel comment = new CommentModel();
				comment.setCommentId(commentObj.getInt(CommentModel.COMMENT_ID));
				comment.setNewsId(commentObj.getInt(nameId));
				comment.setMemberUid(commentObj.getInt(CommentModel.MEMBER_UID));
				comment.setCommentContent(commentObj.getString(CommentModel.COMMENT_CONTENT));
				comment.setComment_update_time(commentObj.getString(CommentModel.COMMENT_UPDATE_TIME)); 
				
				comment.setMemberUser(commentObj.getString(MemberModel.MEMBER_USER));
				comment.setMemberPhoto(commentObj.getString(MemberModel.MEMBER_PHOTO)); 
				comment.setMemberNickname(commentObj.getString(MemberModel.MEMBER_NICKNAME)); 
				comment.setMemberTeamId(commentObj.getInt(MemberModel.MEMBER_TEAM_ID));
				
				if(!commentObj.getString(CommentModel.MAX_COMMENT).equals("null")){ 
					comment.setMaxComment(commentObj.getInt(CommentModel.MAX_COMMENT));
				}
				
				commentList.add(comment); 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return commentList;
		}
		
		return commentList;
	}
	
}
