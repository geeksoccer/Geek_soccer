package com.excelente.geek_soccer;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.TeamModel;
import com.excelente.geek_soccer.model.ThemeModel;
import com.excelente.geek_soccer.pic_download.DownChatPic;
import com.excelente.geek_soccer.pic_download.DownLiveScorePic;
import com.excelente.geek_soccer.utils.SecurePreferences;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory; 
import android.os.AsyncTask;
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
	public final static String setting_save_mode_ask = "setting_save_mode_ask";
	
	public final static int TOTAL_TEAM = 4;
	
	public final static String MEMBER_SHAREPREFERENCE = "MEMBER_SHAREPREFERENCE";
	public final static String FAV_TEAM_KEY = "FAV_TEAM_KEY";
	
	static MemberModel member;
	static boolean globalNews = false;
	private static String[] teamName = {"Arsenal", "Chelsea", "Liverpool", "Manchester United"};
	
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
		
		if(memberFile.getString(MemberModel.MEMBER_TEAM_ID) != null){
			member.setTeamId(Integer.valueOf(memberFile.getString(MemberModel.MEMBER_TEAM_ID)));
			member.setTeam(getTeamMember(memberFile));
		}else
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
		
		if(memberFile.getString(MemberModel.MEMBER_THEME_ID) != null){
			member.setThemeId(Integer.valueOf(memberFile.getString(MemberModel.MEMBER_THEME_ID)));
			if(member.getThemeId()>-1){
				member.setTheme(getThemeMember(memberFile));
			}
		}else
			member.setThemeId(-1);
		
		SessionManager.member = member;
		
		return member;
	}
	
	private static TeamModel getTeamMember(SecurePreferences memberFile) {
		TeamModel team = new TeamModel();
		
		if(memberFile.getString(TeamModel.TEAM_ID) != null)
			team.setTeamId(Integer.valueOf(memberFile.getString(TeamModel.TEAM_ID)));
		else
			team.setTeamId(0);
		
		if(memberFile.getString(TeamModel.TEAM_NAME) != null)
			team.setTeamName(memberFile.getString(TeamModel.TEAM_NAME));
		else
			team.setTeamName("");
		
		if(memberFile.getString(TeamModel.TEAM_NAME_TH) != null)
			team.setTeamNameTH(memberFile.getString(TeamModel.TEAM_NAME_TH));
		else
			team.setTeamNameTH("");
		
		if(memberFile.getString(TeamModel.TEAM_LEAGUE) != null)
			team.setTeamLeague(memberFile.getString(TeamModel.TEAM_LEAGUE));
		else
			team.setTeamLeague("");
		
		if(memberFile.getString(TeamModel.TEAM_NAME_FIND) != null)
			team.setTeamNameFind(memberFile.getString(TeamModel.TEAM_NAME_FIND));
		else
			team.setTeamNameFind("");
		
		if(memberFile.getString(TeamModel.TEAM_SHORT_NAME) != null)
			team.setTeamShortName(memberFile.getString(TeamModel.TEAM_SHORT_NAME));
		else
			team.setTeamShortName("");
		
		return team;
	}

	private static ThemeModel getThemeMember(SecurePreferences memberFile) {
		ThemeModel theme = new ThemeModel();
		
		if(memberFile.getString(ThemeModel.THEME_ID) != null)
			theme.setThemeId(Integer.valueOf(memberFile.getString(ThemeModel.THEME_ID)));
		else
			theme.setThemeId(-1);
		
		if(memberFile.getString(ThemeModel.THEME_NAME) != null)
			theme.setThemeName(memberFile.getString(ThemeModel.THEME_NAME));
		else
			theme.setThemeName("");
		
		if(memberFile.getString(ThemeModel.THEME_NAME_TH) != null)
			theme.setThemeNameTH(memberFile.getString(ThemeModel.THEME_NAME_TH));
		else
			theme.setThemeNameTH("");
		
		if(memberFile.getString(ThemeModel.THEME_COLOR) != null)
			theme.setThemeColor(memberFile.getString(ThemeModel.THEME_COLOR));
		else
			theme.setThemeColor("");
		
		if(memberFile.getString(ThemeModel.THEME_LOGO) != null)
			theme.setThemeLogo(memberFile.getString(ThemeModel.THEME_LOGO));
		else
			theme.setThemeLogo("");
		
		if(memberFile.getString(ThemeModel.THEME_TEXT_COLOR) != null)
			theme.setThemeTextColor(memberFile.getString(ThemeModel.THEME_TEXT_COLOR));
		else
			theme.setThemeTextColor("");
		
		if(memberFile.getString(ThemeModel.THEME_CREATE) != null)
			theme.setThemeCreate(memberFile.getString(ThemeModel.THEME_CREATE));
		else
			theme.setThemeCreate("");
		
		return theme;
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
			memberFile.put(MemberModel.MEMBER_THEME_ID, String.valueOf(member.getThemeId()));
			if(member.getThemeId()>-1){
				setThemeMember(context, member.getTheme(), memberFile);
			}
			setTeamMember(member.getTeam(), memberFile);
		}
		
		SessionManager.member = member;
	}
	
	private static void setTeamMember(TeamModel team, SecurePreferences memberFile) {
		if(team != null){
			memberFile.put(TeamModel.TEAM_ID, String.valueOf(team.getTeamId()));
			memberFile.put(TeamModel.TEAM_NAME, team.getTeamName());
			memberFile.put(TeamModel.TEAM_NAME_TH, team.getTeamNameTH());
			memberFile.put(TeamModel.TEAM_NAME_FIND, team.getTeamNameFind());
			memberFile.put(TeamModel.TEAM_LEAGUE, team.getTeamLeague());
			memberFile.put(TeamModel.TEAM_SHORT_NAME, team.getTeamShortName());
		}
	} 

	private static void setThemeMember(final Context context, final ThemeModel theme, SecurePreferences memberFile) {
		if(theme != null){
			memberFile.put(ThemeModel.THEME_ID, String.valueOf(theme.getThemeId()));
			memberFile.put(ThemeModel.THEME_NAME, theme.getThemeName());
			memberFile.put(ThemeModel.THEME_NAME_TH, theme.getThemeNameTH());
			memberFile.put(ThemeModel.THEME_COLOR, theme.getThemeColor());
			memberFile.put(ThemeModel.THEME_LOGO, theme.getThemeLogo());
			memberFile.put(ThemeModel.THEME_TEXT_COLOR, theme.getThemeTextColor());
			memberFile.put(ThemeModel.THEME_CREATE, theme.getThemeCreate());
			
			if(!hasKey(context, theme.getThemeLogo())){
				AsyncHttpClient client = new AsyncHttpClient();
				client.get(theme.getThemeLogo(), new AsyncHttpResponseHandler() {
					
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if(arg2!=null){
							Bitmap bitmap = BitmapFactory.decodeByteArray(arg2 , 0, arg2.length);
							SessionManager.createNewImageSession(context, theme.getThemeLogo(), bitmap);
						}
					}
					
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						
					}
				});
			}
			
		}
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
	
	public static boolean hasThemeMember(Activity context) {
		if(SessionManager.getMember(context).getThemeId()>-1){
			return true;
		}
		return false;
	}
	
	public static boolean hasTeamMember(Activity context) {
		MemberModel member = SessionManager.getMember(context);
		if(member.getTeamId()>-1 && member.getTeamId() == member.getTeam().getTeamId()){
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
    
    public static String getTeamName(Context context) {
    	
    	if(hasMember(context)){
			return teamName[getMember(context).getTeamId() - 1];
    	}
    	
    	return "";
	}

	public static String getLang(Context context) {
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