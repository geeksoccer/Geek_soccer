package com.excelente.geek_soccer;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.utils.SecurePreferences;

import android.app.Activity;
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
	public final static String setting_ask_rateapp = "setting_countdown_rateapp";
	public final static String setting_count_use = "setting_count_use";
	public final static String setting_save_mode = "setting_save_mode";
	
	public final static String MEMBER_SHAREPREFERENCE = "MEMBER_SHAREPREFERENCE";
	public final static String FAV_TEAM_KEY = "FAV_TEAM_KEY";
	
	static MemberModel member;
	static boolean globalNews = false;
	
	public static MemberModel getMember(Context context) {
		if(context == null){
			return null;
		}
		
		MemberModel member = new MemberModel();
		SecurePreferences memberFile = new SecurePreferences(context, MEMBER_SHAREPREFERENCE, "GeekSoccer4619", true);
		
		if(memberFile.getString(MemberModel.MEMBER_UID) != null)
			member.setUid(Integer.valueOf(memberFile.getString(MemberModel.MEMBER_UID)));
		else
			member.setUid(0);
		
		if(memberFile.getString(MemberModel.MEMBER_TEAM_ID) != null)
			member.setTeamId(Integer.valueOf(memberFile.getString(MemberModel.MEMBER_TEAM_ID)));
		else
			member.setTeamId(0);
		
		if(memberFile.getString(MemberModel.MEMBER_TOKEN) != null)
			member.setToken(memberFile.getString(MemberModel.MEMBER_TOKEN));
		else
			member.setToken("");
			
		if(memberFile.getString(MemberModel.MEMBER_BIRTHDAY) != null)
			member.setBirthday(memberFile.getString(MemberModel.MEMBER_BIRTHDAY));
		else
			member.setBirthday("");
		
		if(memberFile.getString(MemberModel.MEMBER_GENDER) != null)
			member.setGender(Integer.valueOf(memberFile.getString(MemberModel.MEMBER_GENDER)));
		else
			member.setGender(0);
		
		if(memberFile.getString(MemberModel.MEMBER_NICKNAME) != null)
			member.setNickname(memberFile.getString(MemberModel.MEMBER_NICKNAME));
		else
			member.setNickname("");
		
		if(memberFile.getString(MemberModel.MEMBER_PHOTO) != null)
			member.setPhoto(memberFile.getString(MemberModel.MEMBER_PHOTO));
		else
			member.setPhoto("");
		
		if(memberFile.getString(MemberModel.MEMBER_EMAIL) != null)
			member.setEmail(memberFile.getString(MemberModel.MEMBER_EMAIL));
		else
			member.setPhoto("");
		
		if(memberFile.getString(MemberModel.MEMBER_TYPE_LOGIN) != null)
			member.setTypeLogin(memberFile.getString(MemberModel.MEMBER_TYPE_LOGIN));
		else
			member.setTypeLogin("");
		
		if(memberFile.getString(MemberModel.MEMBER_ROLE) != null)
			member.setRole(Integer.valueOf(memberFile.getString(MemberModel.MEMBER_ROLE)));
		else
			member.setRole(0);
		
		SessionManager.member = member;
		
		return member;
	}
	
	public static void setMember(Context context, MemberModel member) {
		if(member != null){
			SecurePreferences memberFile = new SecurePreferences(context, MEMBER_SHAREPREFERENCE, "GeekSoccer4619", true);
			memberFile.put(MemberModel.MEMBER_UID, String.valueOf(member.getUid()));
			memberFile.put(MemberModel.MEMBER_USER, member.getUser());
			memberFile.put(MemberModel.MEMBER_TEAM_ID, String.valueOf(member.getTeamId()));
			memberFile.put(MemberModel.MEMBER_TOKEN, member.getToken());
			memberFile.put(MemberModel.MEMBER_BIRTHDAY, member.getBirthday());
			memberFile.put(MemberModel.MEMBER_GENDER, String.valueOf(member.getGender()));
			memberFile.put(MemberModel.MEMBER_NICKNAME, member.getNickname());
			memberFile.put(MemberModel.MEMBER_PHOTO, member.getPhoto());
			memberFile.put(MemberModel.MEMBER_EMAIL, member.getEmail());
			memberFile.put(MemberModel.MEMBER_TYPE_LOGIN, member.getTypeLogin());
			memberFile.put(MemberModel.MEMBER_ROLE, String.valueOf(member.getRole()));
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
	
	public static void clearByKey(Context context, String key) {
		SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		Editor editMember = memberFile.edit();
		editMember.remove(key);
		editMember.commit();
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
    
    public static void addFavTeam(Context context, String value){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	JSONArray FavJArr;
		try {
			FavJArr = new JSONArray(memberFile.getString(FAV_TEAM_KEY, "[]"));
			FavJArr.put(value);
	    	Editor editMember = memberFile.edit();
	    	editMember.putString(FAV_TEAM_KEY, FavJArr.toString());
	    	editMember.commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void delFavTeam(Context context, String value){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	JSONArray FavJArr;
		try {
			FavJArr = new JSONArray(memberFile.getString(FAV_TEAM_KEY, "[]"));
			JSONArray outputDel = new JSONArray();
			for(int i=0; i<FavJArr.length(); i++){
				String FavSindex = FavJArr.getString(i);
				if(!value.equals(FavSindex)){
					outputDel.put(FavSindex);
				}
			}
	    	Editor editMember = memberFile.edit();
	    	editMember.putString(FAV_TEAM_KEY, outputDel.toString());
	    	editMember.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    public static Boolean chkFavContain(Context context, String value){
    	SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
    	JSONArray FavJArr;
		try {
			FavJArr = new JSONArray(memberFile.getString(FAV_TEAM_KEY, "[]"));
			return FavJArr.toString().contains(value);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
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
    
    public static void setLangApp(Context context) {
    	Locale myLocale = new Locale(getLang(context));
	    Locale.setDefault(myLocale);
	    android.content.res.Configuration config = new android.content.res.Configuration();
	    config.locale = myLocale;
	    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	}
    
    public static int getTeamColor(Context context) {
    	
    	if(hasMember(context)){
    		switch (getMember(context).getTeamId()) {
				case 1:
					return R.color.news_arsenal;
				case 2:
					return R.color.news_chelsea;
				case 3:
					return R.color.news_liverpool;
				case 4:
					return R.color.news_manu;
				default:
					return R.color.news_default;
			}
    	}
    	
    	return R.color.news_default;
	}

	private static String getLang(Context context) {
		String langInt = SessionManager.getSetting(context, SessionManager.setting_lang);
		if(langInt.equals("null")){
			return "th";
		}
		switch (Integer.valueOf(langInt)) {
			case 0:
				return "en";
			case 1:
				return "th";
		}
		return "th";
	}

	public static void clearMemberOnly(Activity context) {
		SecurePreferences memberFile = new SecurePreferences(context, MEMBER_SHAREPREFERENCE, "GeekSoccer4619", true);
		memberFile.removeValue(MemberModel.MEMBER_UID);
		memberFile.removeValue(MemberModel.MEMBER_USER);
		memberFile.removeValue(MemberModel.MEMBER_TEAM_ID);
		memberFile.removeValue(MemberModel.MEMBER_TOKEN);
		memberFile.removeValue(MemberModel.MEMBER_BIRTHDAY);
		memberFile.removeValue(MemberModel.MEMBER_GENDER);
		memberFile.removeValue(MemberModel.MEMBER_NICKNAME);
		memberFile.removeValue(MemberModel.MEMBER_PHOTO);
		memberFile.removeValue(MemberModel.MEMBER_EMAIL);
		memberFile.removeValue(MemberModel.MEMBER_TYPE_LOGIN);
		memberFile.removeValue(MemberModel.MEMBER_ROLE);
	}
}