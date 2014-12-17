package com.excelente.geek_soccer;

import io.socket.SocketIO;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ControllParameter {
	private  static ControllParameter instance = null;

	Context mContext;
	public static String SERVER_URL = "www.geeksoccer.com";//183.90.171.209
	public static String CHAT_STK_URL = "http://"+ SERVER_URL + "/chat/stk/";
	public static String CHECK_CHAT_MEMBER_PERMIT_URL = "http://"+ SERVER_URL + "/gs_member_permission/check_chat_permission.php";
	public static String CHAT_MEMBER_PERMIT_URL = "http://"+ SERVER_URL + "/gs_member_permission/chat_permission.php";
	public static String GET_LIVESCORE_URL = "http://"+ SERVER_URL + "//get-livescore/ajax/goal-livescore.php";
	public static String GET_OPTA_ID_URL = "http://"+ SERVER_URL + "//get-livescore/ajax/get_opta_id.php";
	public static String GET_STK_PER_LIST = "http://"+ SERVER_URL + "/gs_stk_permission/get_stk_list.php";
	public static String SET_STK_PERMIT_ONE = "http://"+ SERVER_URL + "/gs_stk_permission/stk_permission_set_one.php";
	public static String DEL_STK_PERMIT_ONE = "http://"+ SERVER_URL + "/gs_stk_permission/stk_permission_del_one.php";

	//News Page
	public static String GET_NEWS_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_news/get_news.php";
	public static String NEWS_READS_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_news/post_news_reads.php";
	public static String NEWS_LIKES_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_news/post_news_like.php"; 
	
	//News Item Page
	public static String NEWS_POST_COMMENTS_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_news_comments/post_news_comments.php";
	public static String NEWS_GET_COMMENT_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_news_comments/get_news_comments.php?"; 
	
	//Score Board Page
	public static String TABLE_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_table/get_table.php";
	
	//Hilight Page
	public static String GET_HILIGHT_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_hilight/get_hilight.php";
	public static String HILIGHT_READS_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_hilight/post_hilight_reads.php";
	public static String HILIGHT_LIKES_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_hilight/post_hilight_like.php"; 
	
	//Hilight Item Page
	public static String HILIGHT_POST_COMMENTS_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_hilight_comments/post_hilight_comments.php";
	public static String HILIGHT_GET_COMMENT_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_hilight_comments/get_hilight_comments.php?";
	
	//Sing In Page
	public static String MEMBER_SIGN_IN_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_sign_in.php"; 
	public static String MEMBER_SIGN_UP_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_sign_up.php";
	public static String MEMBER_TOKEN_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_token.php";
	public static String MEMBER_CHECK_AND_UPDATE_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_check_and_update.php";
	
	//Select Team Page
	public static String GET_SELECT_TEAM_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_select_team/get_select_team.php";
	public static String SELECT_TEAM_UPDATE_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_update_team.php";
	
	//Profile Page
	public static String MEMBER_UPDATE_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_update.php";
	public static String MEMBER_IMAGES_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_images/";
	
	//Sing Out Page
	public static String MEMBER_SIGN_OUT_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_member/member_sign_out.php"; 
	
	//Update NEWS and Hilight
	public static String GET_NEWS_UPDATE_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_news/get_news_update.php";
	public static String GET_HILIGHT_UPDATE_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_hilight/get_hilight_update.php";
	
	//Fixtures Page
	public static String GET_FIXTURES_URL = "http://"+ SERVER_URL + "/gs_api_v2/gs_fixtures/get_fixtures_to_device.php";
	
	private  ControllParameter(Context context) {
        //randomizeServers();
    	mContext = context;
    	
    	if(SessionManager.hasMember(mContext)){
    		ID_Send = String.valueOf(SessionManager.getMember(mContext).getUid());
    		Role_ID = SessionManager.getMember(mContext).getRole();
    		ProFile_pic = SessionManager.getMember(mContext).getPhoto();
    		Name_Send = SessionManager.getMember(mContext).getNickname();
    		int teamID = SessionManager.getMember(mContext).getTeamId();
    		if(teamID > 0){
    			TeamSelect = SessionManager.getMember(mContext).getTeam().getTeamName();
    		}else{
    			TeamSelect = "OtherTeam";
    		}
    		
    		if(!SessionManager.getSetting(context, SessionManager.setting_lang).equals("null")){
    			Laugage_Select = Integer.parseInt(SessionManager.getSetting(context, SessionManager.setting_lang));
    		}else{
    			Laugage_Select = 1;
    		}
    	}
    }

    private int fragement_Section = 0;
    
    /*
    private ArrayList<String> item_Type_list = new ArrayList<String>();
    private ArrayList<String> Match_list = new ArrayList<String>();
    */
    ArrayList<String> item_Type_list_display = new ArrayList<String>();
	ArrayList<String> Match_list_Sub_display = new ArrayList<String>();
    ArrayList<String> item_Type_list = new ArrayList<String>();
    public ArrayList<JSONObject> Match_list_y_JSON = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> Match_list_c_JSON = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> Match_list_t_JSON = new ArrayList<JSONObject>();
	ArrayList<String> Match_list_Sub = new ArrayList<String>();
	HashMap<String, String> League_Map = new HashMap<String, String>();
	HashMap<String, String> League_Map_index = new HashMap<String, String>();
    HashMap<String, Bitmap> HomeMap = new HashMap<String, Bitmap>();
	HashMap<String, Bitmap> AwayMap = new HashMap<String, Bitmap>();
	public HashMap<String, Bitmap> BitMapHash = new HashMap<String, Bitmap>();
	public HashMap<String, Boolean> BitMapHashMem = new HashMap<String, Boolean>();
	HashMap<String, String> player_Detail = new HashMap<String, String>();
	public HashMap<String, JSONArray> Sticker_Set = new HashMap<String, JSONArray>();
	public HashMap<String, String> Sticker_UrlSet = new HashMap<String, String>();
	ArrayList<String> URL_list = new ArrayList<String>();
	public String PageNameSelected = "";
	String Last_League_SET="";
	String Date_Select="c";
	public String SocketSelect;
	public static String ID_Send;
	public static int Role_ID;
	public String ProFile_pic;
	String Name_Send;
	public HashMap<String, String> OldScoreH = new HashMap<String, String>();
	public HashMap<String, String> OldTimeH = new HashMap<String, String>();
	public static String TeamSelect = "";
	public static Bitmap ProFileCache = null;
	public static int Laugage_Select = 0;
	public static Boolean BanStatus = true;
	
	Boolean app_Status=true;
	public Boolean chat_on_All = false;
	public Boolean chat_on_Team = false;
	public Boolean liveScore_on = null;
	public Boolean liveScore_ChkHavePlaying = false;
	public Boolean detailPageOpenning = false;
	public int liveScore_Cur = 1;
	public int chat_Cur = 0;
	public SocketIO socket_All = null;
	public SocketIO socket_Team = null;
	public SocketIO socket_LiveScore = null;
	
	public static String UcountChatTeam = "";
	public static String UcountChatAll = "";
	public ArrayList<JSONObject> Chat_Item_list_Team = new ArrayList<JSONObject>();
	public ArrayList<JSONObject> Chat_Item_list_All = new ArrayList<JSONObject>();

	public ListView lstViewChatTeam;
	public com.excelente.geek_soccer.Chat_Team.ImageAdapter imageAdapterChatTeam;
	public com.excelente.geek_soccer.chat_page.Chat_TeamView.ImageAdapter imageAdapterChatTeamByView;
	public LinearLayout Chat_list_LayOut_Team;
	public Boolean Sticker_Layout_Stat_team = false;
	
	public ListView lstViewChatAll;
	com.excelente.geek_soccer.Chat_All.ImageAdapter imageAdapterChatAll;
	public com.excelente.geek_soccer.chat_page.Chat_AllView.ImageAdapter imageAdapterChatAllByView;
	public LinearLayout Chat_list_LayOut_All;
	public Boolean Sticker_Layout_Stat_All = false;
	public int chatDelay=0;
	
	public ListView lstViewLiveScore;
	com.excelente.geek_soccer.LiveScore_Yesterday.ImageAdapter imageAdapterLiveScoreYesterday;
	com.excelente.geek_soccer.LiveScore_Today.ImageAdapter imageAdapterLiveScoreToday;
	com.excelente.geek_soccer.LiveScore_Tomorrow.ImageAdapter imageAdapterLiveScoreTomorrow;
	public com.excelente.geek_soccer.live_score_page.LiveScore_YesterdayView.ImageAdapter imageAdapterLiveScoreYesterdayByView;
	public com.excelente.geek_soccer.live_score_page.LiveScore_TodayView.ImageAdapter imageAdapterLiveScoreTodayByView;
	public com.excelente.geek_soccer.live_score_page.LiveScore_TomorrowView.ImageAdapter imageAdapterLiveScoreTomorrowByView;
	public static JSONObject jObLiveYesterday;
	public static JSONObject jObLiveToday;
	public static JSONObject jObLiveTomorrow;
	
	public LinearLayout _Menu_Layout;
	public LinearLayout Menu_Layout;
	public View Menu_View;
	public TextView Menu_title;
	public WindowManager wm;
	public static WindowManager.LayoutParams params;
    
    public void fragement_Section_set(int section) {
        this.fragement_Section = section;
    }
    
    public int fragement_Section_get() {
        return this.fragement_Section;
    }
    public void set_HomeMap(String key, Bitmap value) {
		this.HomeMap.put(key, value);
	}
    public Bitmap get_HomeMap(String key) {
		return this.HomeMap.get(key);
	}
    
    public void set_AwayMap(String key, Bitmap value) {
		this.AwayMap.put(key, value);
	}
    public Bitmap get_AwayMap(String key) {
    	return this.AwayMap.get(key);
	}
    
    public void set_player_Detail(String key, String value) {
		this.player_Detail.put(key, value);
	}
    public String get_player_Detail(String key) {
    	return this.player_Detail.get(key);
	}
    
    public static ControllParameter getInstance(Context context) {
        if (null == instance) {
            instance = new ControllParameter(context);
        }

        return instance;
    }
}
