package com.excelente.geek_soccer;

import io.socket.SocketIO;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ControllParameter {
	private static ControllParameter instance = null;


    private ControllParameter() {
        //randomizeServers();
    }

    private int fragement_Section = 0;
    String SERVER_URL = "183.90.171.209";
    /*
    private ArrayList<String> item_Type_list = new ArrayList<String>();
    private ArrayList<String> Match_list = new ArrayList<String>();
    */
    ArrayList<String> item_Type_list_display = new ArrayList<String>();
	ArrayList<String> Match_list_Sub_display = new ArrayList<String>();
    ArrayList<String> item_Type_list = new ArrayList<String>();
    ArrayList<JSONObject> Match_list_y_JSON = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Match_list_c_JSON = new ArrayList<JSONObject>();
    ArrayList<JSONObject> Match_list_t_JSON = new ArrayList<JSONObject>();
	ArrayList<String> Match_list_Sub = new ArrayList<String>();
	HashMap<String, String> League_Map = new HashMap<String, String>();
	HashMap<String, String> League_Map_index = new HashMap<String, String>();
    HashMap<String, Bitmap> HomeMap = new HashMap<String, Bitmap>();
	HashMap<String, Bitmap> AwayMap = new HashMap<String, Bitmap>();
	HashMap<String, Bitmap> BitMapHash = new HashMap<String, Bitmap>();
	HashMap<String, String> player_Detail = new HashMap<String, String>();
	HashMap<String, JSONArray> Sticker_Set = new HashMap<String, JSONArray>();
	HashMap<String, String> Sticker_UrlSet = new HashMap<String, String>();
	ArrayList<String> URL_list = new ArrayList<String>();
	String Last_League_SET="";
	String Date_Select="c";
	String SocketSelect;
	String ID_Send = String.valueOf(MemberSession.getMember().getUid());
	String ProFile_pic = MemberSession.getMember().getPhoto();
	String Name_Send = MemberSession.getMember().getNickname();
	
	Boolean app_Status=true;
	Boolean chat_on_All = false;
	Boolean chat_on_Team = false;
	Boolean liveScore_on = null;
	int liveScore_Cur = 1;
	int chat_Cur = 0;
	SocketIO socket_All = null;
	SocketIO socket_Team = null;
	SocketIO socket_LiveScore = null;
	
	
	ArrayList<JSONObject> Chat_Item_list_Team = new ArrayList<JSONObject>();
	ArrayList<JSONObject> Chat_Item_list_All = new ArrayList<JSONObject>();

	ListView lstViewChatTeam;
	com.excelente.geek_soccer.Chat_Team.ImageAdapter imageAdapterChatTeam;
	LinearLayout Chat_list_LayOut_Team;
	ListView lstViewChatAll;
	com.excelente.geek_soccer.Chat_All.ImageAdapter imageAdapterChatAll;
	LinearLayout Chat_list_LayOut_All;
	ListView lstViewLiveScore;
	com.excelente.geek_soccer.LiveScore_Today.ImageAdapter imageAdapterLiveScore;
    
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
    
    public static ControllParameter getInstance() {
        if (null == instance) {
            instance = new ControllParameter();
        }

        return instance;
    }
}
