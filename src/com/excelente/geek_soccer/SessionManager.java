package com.excelente.geek_soccer;

import java.io.ByteArrayOutputStream;

import com.excelente.geek_soccer.model.MemberModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory; 
import android.util.Base64;
import android.util.Log;
 
public class SessionManager {
    
	public final static String setting_lang = "setting_lang";
	public final static String setting_notify_team_news = "setting_notify_team_news";
	public final static String setting_notify_global_news = "setting_notify_global_news";
	public final static String setting_notify_hilight = "setting_notify_hilight";
	public final static String setting_notify_livescore = "setting_notify_livescore";
	
	public final static String MEMBER_SHAREPREFERENCE = "MEMBER_SHAREPREFERENCE";
	
	static MemberModel member;
	static boolean globalNews = false;
	
	public static MemberModel getMember(Context context) {
		MemberModel member = new MemberModel();
		SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		member.setUid(memberFile.getInt(MemberModel.MEMBER_UID, 0));
		member.setTeamId(memberFile.getInt(MemberModel.MEMBER_TEAM_ID, 0));
		member.setToken(memberFile.getString(MemberModel.MEMBER_TOKEN, ""));
		member.setBirthday(memberFile.getString(MemberModel.MEMBER_BIRTHDAY, ""));
		member.setGender(memberFile.getInt(MemberModel.MEMBER_GENDER, 0));
		member.setNickname(memberFile.getString(MemberModel.MEMBER_NICKNAME, ""));
		member.setPhoto(memberFile.getString(MemberModel.MEMBER_PHOTO, ""));
		member.setEmail(memberFile.getString(MemberModel.MEMBER_EMAIL, ""));
		member.setTypeLogin(memberFile.getString(MemberModel.MEMBER_TYPE_LOGIN, ""));
		member.setRole(memberFile.getInt(MemberModel.MEMBER_ROLE, 0));
		SessionManager.member = member;
		return member;
	}
	
	public static void setMember(Context context, MemberModel member) {
		if(member != null){
			SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		
			Editor editMember = memberFile.edit();
			editMember.putInt(MemberModel.MEMBER_UID, member.getUid());
			editMember.putString(MemberModel.MEMBER_USER, member.getUser());
			editMember.putInt(MemberModel.MEMBER_TEAM_ID, member.getTeamId());
			editMember.putString(MemberModel.MEMBER_TOKEN, member.getToken());
			editMember.putString(MemberModel.MEMBER_BIRTHDAY, member.getBirthday());
			editMember.putInt(MemberModel.MEMBER_GENDER, member.getGender());
			editMember.putString(MemberModel.MEMBER_NICKNAME, member.getNickname());
			editMember.putString(MemberModel.MEMBER_PHOTO, member.getPhoto());
			editMember.putString(MemberModel.MEMBER_EMAIL, member.getEmail());
			editMember.putString(MemberModel.MEMBER_TYPE_LOGIN, member.getTypeLogin());
			editMember.putInt(MemberModel.MEMBER_ROLE, member.getRole());
			editMember.commit();
		}
		
		SessionManager.member = member;
	}
	
	public static void clearMember(Context context) {
		SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		
		Editor editMember = memberFile.edit();
		editMember.clear();
		editMember.commit();
		
		SessionManager.member = null;
	}
	
	public static boolean hasMember(Context context) {
		if(SessionManager.getMember(context) != null && SessionManager.getMember(context).getUid()>0){
			return true;
		}
		return false;
	}
    
    public static void createNewImageSession(Context context, String key, Bitmap value){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	Editor editMember = memberFile.edit();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			value.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

			editMember.putString(key, encodedImage);
			editMember.commit();
		} catch (OutOfMemoryError e) {
			Log.e("err", "Out of memory error :(");
		}
    }
    
    public static Bitmap getImageSession(Context context, String key){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		try {
			if (memberFile.getString(key, null) != null) {
				byte[] imageAsBytes = Base64.decode(memberFile.getString(key, null)
						.getBytes(), Context.MODE_PRIVATE);
				return BitmapFactory.decodeByteArray(imageAsBytes, 0,
						imageAsBytes.length);// pref.getString(key, null);
			} else {
				return null;
			}
		} catch (OutOfMemoryError e) {
			Log.e("err", "Out of memory error :(");
			return null;
		}
    }
    
    public static void createNewJsonSession(Context context, String key, String value){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	Editor editMember = memberFile.edit();
    	editMember.putString(key, value);
    	editMember.commit();
    } 
    
    public static String getJsonSession(Context context, String key){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	return memberFile.getString(key, null);
    }
    
    public static boolean hasKey(Context context, String key){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	return memberFile.contains(key);
    }
    
    public static void setSetting(Context context, String key, String val) {
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		Editor editMember = memberFile.edit();
		editMember.putString(key, val);
		editMember.commit();
	}
    
    public static String getSetting(Context context, String key) {
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	return memberFile.getString(key, "null");
	}
}