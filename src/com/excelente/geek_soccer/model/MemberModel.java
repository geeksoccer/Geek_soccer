package com.excelente.geek_soccer.model;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemberModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String MEMBER_KEY = "MEMBER_KEY";
	
	public static final String MEMBER_UID = "m_uid";
	public static final String MEMBER_USER = "m_user";
	public static final String MEMBER_PASS = "m_pass";
	public static final String MEMBER_BIRTHDAY = "m_birthday";
	public static final String MEMBER_GENDER = "m_gender";
	public static final String MEMBER_NICKNAME = "m_nickname";
	public static final String MEMBER_PHOTO = "m_photo";
	public static final String MEMBER_EMAIL = "m_email";
	public static final String MEMBER_TEAM_ID = "m_team_id";
	public static final String MEMBER_TYPE_LOGIN = "m_type_login";
	public static final String MEMBER_ROLE = "m_role";
	public static final String MEMBER_TOKEN = "m_token";
	public static final String MEMBER_DEVID = "m_devid";
	
	public static final String MEMBER_GLOBAL_NEWS = "m_global_news";
	
	int uid;
	String user;
	String pass;
	String birthday;
	int gender;
    String nickname;
    String photo;
    String email;
    int teamId;
    String typeLogin;
    int role;
    String token;
    
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public String getTypeLogin() {
		return typeLogin;
	}
	public void setTypeLogin(String typeLogin) {
		this.typeLogin = typeLogin;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public static MemberModel convertMemberJSONToList(String result) { 
		MemberModel member = new MemberModel();
		
		try {
			
			JSONArray memberJsonArr = new JSONArray(result);
			JSONObject memberObj = (JSONObject) memberJsonArr.get(0); 
				
			member.setUid(memberObj.getInt(MemberModel.MEMBER_UID));
			member.setUser(memberObj.getString(MemberModel.MEMBER_USER)); 
			member.setBirthday(memberObj.getString(MemberModel.MEMBER_BIRTHDAY));
			member.setGender(memberObj.getInt(MemberModel.MEMBER_GENDER)); 
			member.setNickname(memberObj.getString(MemberModel.MEMBER_NICKNAME));
			member.setPhoto(memberObj.getString(MemberModel.MEMBER_PHOTO));
			member.setEmail(memberObj.getString(MemberModel.MEMBER_EMAIL)); 
			member.setTeamId(memberObj.getInt(MemberModel.MEMBER_TEAM_ID)); 
			member.setTypeLogin(memberObj.getString(MemberModel.MEMBER_TYPE_LOGIN));
			member.setRole(memberObj.getInt(MemberModel.MEMBER_ROLE));
			member.setToken(memberObj.getString(MemberModel.MEMBER_TOKEN));
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return member;
	}
}
