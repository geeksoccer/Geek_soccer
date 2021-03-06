package com.excelente.geek_soccer.chat_page;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.GetdipSize;
import com.excelente.geek_soccer.JSONParser;
import com.excelente.geek_soccer.MainActivity;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.STKShop_Page;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.chat_menu.Chat_Menu_LongClick;
import com.excelente.geek_soccer.date_convert.Date_Covert;
import com.excelente.geek_soccer.pic_download.DownChatPic;
import com.excelente.geek_soccer.user_rule.User_Rule;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Chat_TeamView{
	
	Activity mActivity;
	
	int width;
	int height;
	private static ControllParameter data;
	JSONParser jParser = new JSONParser();
	private Handler handler = new Handler(Looper.getMainLooper());
	String Msg_Send = "";
	long time = System.currentTimeMillis() / 1000L;

	String TimeStamp_Send = "0";
	String old_timeStamp = "0";

	EditText Chat_input;
	Button send_Btn;
	Button sendSticker_Btn;

	WindowManager wm;

	LinearLayout input_layout;
	TextView UserCountTXT;
	public static View StikerV;

	ImageView allRoom, TeamRoom;

	String Stick_Set = "1";
	LinearLayout StickerSelectorLayout;
	ImageView Stick_1, Stick_2, Stick_3, Stick_4, Stick_5, Stick_6, Stick_7, Stick_8, Stick_9, Stick_10, Stick_11, Stick_12;
	static HashMap<String, ImageView> Sticker_ImgVSet = new HashMap<String, ImageView>();
	static HashMap<String, ImageView> Sticker_ButVSet = new HashMap<String, ImageView>();
	static HashMap<String, LinearLayout> Sticker_ButLayoutVSet = new HashMap<String, LinearLayout>();
	String root = Environment.getExternalStorageDirectory().toString();
	ProgressBar progressBar;
	String saveModeGet;
	
	public View getView(Activity activity){
		mActivity = activity;
		data = ControllParameter.getInstance(activity);
		LayoutInflater factory = LayoutInflater.from(activity);
		final View myView = factory.inflate(R.layout.chat_layout, null);

		ControllParameter.ProFileCache = SessionManager.getImageSession(mActivity, SessionManager.getMember(mActivity).getPhoto());
		saveModeGet = SessionManager.getSetting(mActivity,
				SessionManager.setting_save_mode);
		
		UserCountTXT = (TextView)myView.findViewById(R.id.ShowUserCount);
		if(ControllParameter.Role_ID==1){
			UserCountTXT.setText(mActivity.getResources().getString(R.string.user_count)+ ": " +ControllParameter.UcountChatTeam);
		}else{
			UserCountTXT.setVisibility(RelativeLayout.GONE);
		}
		
		data.lstViewChatTeam = new ListView(mActivity);
		data.lstViewChatTeam.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		data.lstViewChatTeam.setClipToPadding(false);
		data.imageAdapterChatTeamByView = new ImageAdapter(mActivity);
		data.lstViewChatTeam.setAdapter(data.imageAdapterChatTeamByView);
		data.lstViewChatTeam.setDividerHeight(0);
		data.Chat_list_LayOut_Team = (LinearLayout) myView.findViewById(R.id.Chat_list_Layout);
		(data.Chat_list_LayOut_Team).addView(data.lstViewChatTeam);

		data.lstViewChatTeam
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int position, long arg3) {
						String m_photo;
						try {
							m_photo = data.Chat_Item_list_Team.get(position).getString("m_photo");
							new Chat_Menu_LongClick().ChatMenu(mActivity, data.Chat_Item_list_Team
									.get(position), m_photo, saveModeGet, data);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return false;
					}
				});
		data.SocketSelect = SessionManager.getMember(mActivity).getTeam().getTeamPort();
		
		if (data.Chat_Item_list_Team.size() > 0) {
			if (data.Chat_list_LayOut_Team.getChildCount() > 1) {
				data.Chat_list_LayOut_Team.removeViewAt(0);
			}
			data.imageAdapterChatTeamByView.notifyDataSetChanged();
			data.lstViewChatTeam.setSelection(data.imageAdapterChatTeamByView
					.getCount());
		}
		
		if (data.socket_Team == null) {
			RefreshView(mActivity.getResources().getString(R.string.pull_to_refresh_tap_join_label));//Chat_Loader();//
		}else if (!data.socket_Team.isConnected()) {
			Chat_Loader();
		}
		new check_Permit().execute();

		Chat_input = (EditText) myView.findViewById(R.id.Chat_input);
		send_Btn = (Button) myView.findViewById(R.id.send_btn);

		Chat_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (data.Sticker_Layout_Stat_team) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_team = false;
				}
			}
		});

		Chat_input.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (data.Sticker_Layout_Stat_team) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_team = false;
				}
			}
		});

		Chat_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (Chat_input.getLineCount() > 1) {
					Chat_input.setLayoutParams(new LinearLayout.LayoutParams(0, (GetdipSize.dip(mActivity, 40) * 3) / 2, 1));
				} else {
					Chat_input.setLayoutParams(new LinearLayout.LayoutParams(0, GetdipSize.dip(mActivity, 40), 1));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		send_Btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Msg_Send = Chat_input.getText().toString();
				Chat_input.setText("");
				chat_Sender();
			}
		});
		input_layout = (LinearLayout) myView.findViewById(R.id.input_Layout);
		sendSticker_Btn = (Button) myView.findViewById(R.id.sendSticker_btn);
		Create_Stick_view();

		sendSticker_Btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (data.Sticker_Layout_Stat_team) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_team = false;
				} else {
					StikerV.setVisibility(RelativeLayout.ABOVE);
					data.Sticker_Layout_Stat_team = true;
					StickViewClear();
					StickerPrepare();
					StickViewCall(Stick_Set);
					StickerSelectorLayout.removeAllViews();
					LayoutParams paramsLayoutBtn = new LinearLayout.LayoutParams(GetdipSize.dip(mActivity, 40), GetdipSize.dip(mActivity, 40));
					LayoutParams paramsBtn = new LinearLayout.LayoutParams(GetdipSize.dip(mActivity, 35), GetdipSize.dip(mActivity, 35));
					LayoutParams paramsLine = new LinearLayout.LayoutParams(GetdipSize.dip(mActivity, 1), LayoutParams.MATCH_PARENT);
					((MarginLayoutParams) paramsLayoutBtn).setMargins(5, 0, 5, 0);
					View line = new View(mActivity);
					line.setBackgroundColor(Color.GRAY);
					line.getBackground().setAlpha(100);
					line.setLayoutParams(paramsLine);
					StickerSelectorLayout.addView(line);
					for (int i = 0; i < data.Sticker_Set.size(); i++) {
						final ImageView StickSet_1 = new ImageView(mActivity);
						StickSet_1.setLayoutParams(paramsBtn);
						final int StickPosition = i + 1;
						StickBTNPrepare(String
								.valueOf(StickPosition), StickSet_1);
						
						StickSet_1
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View arg0) {
										StickViewClear();
										Stick_Set = String
												.valueOf(StickPosition);
										arg0.setEnabled(false);
										for (int j = 0; j < data.Sticker_Set
												.size(); j++) {
											if (!String.valueOf(j + 1).equals(
													Stick_Set)) {
												Sticker_ButLayoutVSet.get(
														String.valueOf(j))
														.setBackgroundColor(color.transparent);
												Sticker_ButVSet.get(
														String.valueOf(j))
														.setEnabled(true);
											}else{
												Sticker_ButLayoutVSet.get(
														String.valueOf(j)).setBackgroundColor(Color.parseColor(SessionManager.getMember(mActivity).getTheme().getThemeColor()));
											}
										}
										
										StickViewCall(Stick_Set);
									}

								});
						Sticker_ButVSet.put(String.valueOf(i), StickSet_1);
						
						LinearLayout StickButLayout = new LinearLayout(mActivity);
						StickButLayout.setLayoutParams(paramsLayoutBtn);
						StickButLayout.setGravity(Gravity.CENTER);
						StickButLayout.addView(StickSet_1);
						Sticker_ButLayoutVSet.put(String.valueOf(i), StickButLayout);
						if (String.valueOf(StickPosition).equals(Stick_Set)) {
							StickSet_1.setEnabled(false);
							StickButLayout.setBackgroundColor(Color.parseColor(SessionManager.getMember(mActivity).getTheme().getThemeColor()));
						} else {
							StickSet_1.setEnabled(true);
							StickButLayout.setBackgroundColor(color.transparent);
						}
						StickerSelectorLayout.addView(StickButLayout);
						line = new View(mActivity);
						line.setBackgroundColor(Color.GRAY);
						line.getBackground().setAlpha(100);
						line.setLayoutParams(paramsLine);
						StickerSelectorLayout.addView(line);
					}
				}
			}
		});

		data.lstViewChatTeam.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (data.Sticker_Layout_Stat_team) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_team = false;
				}
			}
		});
		
		Chat_input.setEnabled(ControllParameter.BanStatus);
		if(!ControllParameter.BanStatus){
			Chat_input.setHint(R.string.chat_ban);
			send_Btn.setBackgroundResource(R.drawable.question_btn);
		}
		
		return myView;
	}

	public void StickViewClear() {
		for (final String key : Sticker_ImgVSet.keySet()) {
			Sticker_ImgVSet.get(key).setImageResource(R.drawable.livescore_h);
		}
	}

	public void StickerPrepare() {
		String StickJset = SessionManager.getJsonSession(mActivity,
				"StickerSet");
		if (StickJset != null) {
			JSONObject json_ob;
			try {
				json_ob = new JSONObject(StickJset);
				data.Sticker_Set.clear();
				data.Sticker_UrlSet.clear();
				for (Iterator<?> league_Item_key = json_ob.keys(); league_Item_key
						.hasNext();) {
					String key_Item = (String) league_Item_key.next();
					JSONArray json_arr = json_ob.getJSONArray(key_Item);
					data.Sticker_Set.put(key_Item, json_arr);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void StickBTNPrepare(final String position, ImageView StkBtn){
		try {
			ArrayList<String> STK_exist_list = new ArrayList<String>();
			for (String key : data.Sticker_Set.keySet()) {
				STK_exist_list.add(key);
			}
			
			JSONArray j_arr = null;
			String S_postion = "";
			if(STK_exist_list.size()>0){
				if(STK_exist_list.size()>(Integer.parseInt(position)-1)){
					S_postion = STK_exist_list.get(Integer.parseInt(position)-1);
					j_arr = data.Sticker_Set.get(S_postion);
				}else{
					S_postion = STK_exist_list.get(0);
					j_arr = data.Sticker_Set.get(S_postion);
				}
			}
			
			if (j_arr != null) {
				data.Sticker_UrlSet.clear();
				for (int i = 0; i < j_arr.length(); i++) {
					JSONObject json_Value = j_arr.getJSONObject(i);
					data.Sticker_UrlSet.put(
							S_postion + "_" + json_Value.getString("sk_id"),
							json_Value.getString("sk_img"));

				}
			} else {
				String StickJset = SessionManager.getJsonSession(
						mActivity, "StickerSet");
				if (StickJset != null) {
					JSONObject json_ob = new JSONObject(StickJset);
					data.Sticker_Set.clear();
					data.Sticker_UrlSet.clear();
					for (Iterator<?> league_Item_key = json_ob.keys(); league_Item_key
							.hasNext();) {
						String key_Item = (String) league_Item_key.next();
						JSONArray json_arr = json_ob.getJSONArray(key_Item);
						data.Sticker_Set.put(key_Item, json_arr);
					}
					
					STK_exist_list = new ArrayList<String>();
					for (String key : data.Sticker_Set.keySet()) {
						STK_exist_list.add(key);
					}
					if(STK_exist_list.size()>0){
						if(STK_exist_list.size()>(Integer.parseInt(position)-1)){
							S_postion = STK_exist_list.get(Integer.parseInt(position)-1);
							j_arr = data.Sticker_Set.get(S_postion);
						}else{
							S_postion = STK_exist_list.get(0);
							j_arr = data.Sticker_Set.get(S_postion);
						}
					}

					if (j_arr != null) {
						for (int i = 0; i < j_arr.length(); i++) {
							JSONObject json_Value = j_arr.getJSONObject(i);
							data.Sticker_UrlSet.put(
									S_postion + "_"
											+ json_Value.getString("sk_id"),
									json_Value.getString("sk_img"));

						}
					}

				}
			}
			for (final String key : data.Sticker_UrlSet.keySet()) {
				if (data.Sticker_UrlSet.get(key).contains(".gif")) {
					putBitmap(StkBtn,
							data.Sticker_UrlSet.get(key));
				} else {
					Bitmap bit = data.BitMapHash.get(data.Sticker_UrlSet
							.get(key));
					if (bit != null) {
						StkBtn.setImageBitmap(bit);
					} else {
						new DownChatPic().startDownload(mActivity, data.Sticker_UrlSet.get(key), StkBtn, data);
					}
				}
				break;				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void StickViewCall(final String position) {
		try {
			ArrayList<String> STK_exist_list = new ArrayList<String>();
			for (String key : data.Sticker_Set.keySet()) {
				STK_exist_list.add(key);
			}
			
			JSONArray j_arr = null;
			String S_postion = "";
			if(STK_exist_list.size()>0){
				if(STK_exist_list.size()>(Integer.parseInt(position)-1)){
					S_postion = STK_exist_list.get(Integer.parseInt(position)-1);
					j_arr = data.Sticker_Set.get(S_postion);
				}else{
					S_postion = STK_exist_list.get(0);
					j_arr = data.Sticker_Set.get(S_postion);
				}
			}
			
			if (j_arr != null) {
				data.Sticker_UrlSet.clear();
				for (int i = 0; i < j_arr.length(); i++) {
					JSONObject json_Value = j_arr.getJSONObject(i);
					data.Sticker_UrlSet.put(
							S_postion + "_" + json_Value.getString("sk_id"),
							json_Value.getString("sk_img"));

				}
			} else {
				String StickJset = SessionManager.getJsonSession(
						mActivity, "StickerSet");
				if (StickJset != null) {
					JSONObject json_ob = new JSONObject(StickJset);
					data.Sticker_Set.clear();
					data.Sticker_UrlSet.clear();
					for (Iterator<?> league_Item_key = json_ob.keys(); league_Item_key
							.hasNext();) {
						String key_Item = (String) league_Item_key.next();
						JSONArray json_arr = json_ob.getJSONArray(key_Item);
						data.Sticker_Set.put(key_Item, json_arr);
					}
					
					STK_exist_list = new ArrayList<String>();
					for (String key : data.Sticker_Set.keySet()) {
						STK_exist_list.add(key);
					}
					if(STK_exist_list.size()>0){
						if(STK_exist_list.size()>(Integer.parseInt(position)-1)){
							S_postion = STK_exist_list.get(Integer.parseInt(position)-1);
							j_arr = data.Sticker_Set.get(S_postion);
						}else{
							S_postion = STK_exist_list.get(0);
							j_arr = data.Sticker_Set.get(S_postion);
						}
					}

					if (j_arr != null) {
						for (int i = 0; i < j_arr.length(); i++) {
							JSONObject json_Value = j_arr.getJSONObject(i);
							data.Sticker_UrlSet.put(
									S_postion + "_"
											+ json_Value.getString("sk_id"),
									json_Value.getString("sk_img"));

						}
					}

				}
			}
			int ImgV_p = 0;
			for (final String key : data.Sticker_UrlSet.keySet()) {
				ImgV_p++;

				if (data.Sticker_UrlSet.get(key).contains(".gif")) {
					putBitmap(Sticker_ImgVSet.get(String.valueOf(ImgV_p)),
							data.Sticker_UrlSet.get(key));
				} else {
					Bitmap bit = data.BitMapHash.get(data.Sticker_UrlSet
							.get(key));
					if (bit != null) {
						Sticker_ImgVSet.get(String.valueOf(ImgV_p))
								.setImageBitmap(bit);
					} else {
						new DownChatPic().startDownload(mActivity, data.Sticker_UrlSet.get(key),
								Sticker_ImgVSet.get(String.valueOf(ImgV_p)),data );
					}
				}
				Sticker_ImgVSet.get(String.valueOf(ImgV_p)).setEnabled(true);
				Sticker_ImgVSet.get(String.valueOf(ImgV_p)).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Send_Stick(key);
							}
						});
			}
			for (int i = ImgV_p; i < Sticker_ImgVSet.size(); i++) {
				Sticker_ImgVSet.get(String.valueOf(i + 1)).setEnabled(false);
				Sticker_ImgVSet.get(String.valueOf(i + 1)).setImageBitmap(null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void putBitmap(final ImageView imgV, final String key) {
		new DownChatPic().startDownloadGIFCache(mActivity
				,  ControllParameter.CHAT_STK_URL + key
				, imgV);
	}

	public void Create_Stick_view() {
		LayoutInflater factory = LayoutInflater.from(mActivity);
		StikerV = factory.inflate(R.layout.sticker_layout, null);

		StikerV.setVisibility(RelativeLayout.GONE);
		input_layout.addView(StikerV, 0);

		StickerSelectorLayout = (LinearLayout) StikerV
				.findViewById(R.id.StickerSelecterLayout);

		Stick_1 = (ImageView) StikerV.findViewById(R.id.stic_1);
		Stick_2 = (ImageView) StikerV.findViewById(R.id.stic_2);
		Stick_3 = (ImageView) StikerV.findViewById(R.id.stic_3);
		Stick_4 = (ImageView) StikerV.findViewById(R.id.stic_4);
		Stick_5 = (ImageView) StikerV.findViewById(R.id.stic_5);
		Stick_6 = (ImageView) StikerV.findViewById(R.id.stic_6);
		Stick_7 = (ImageView) StikerV.findViewById(R.id.stic_7);
		Stick_8 = (ImageView) StikerV.findViewById(R.id.stic_8);
		Stick_9 = (ImageView) StikerV.findViewById(R.id.stic_9);
		Stick_10 = (ImageView) StikerV.findViewById(R.id.stic_10);
		Stick_11 = (ImageView) StikerV.findViewById(R.id.stic_11);
		Stick_12 = (ImageView) StikerV.findViewById(R.id.stic_12);

		Sticker_ImgVSet.put("1", Stick_1);
		Sticker_ImgVSet.put("2", Stick_2);
		Sticker_ImgVSet.put("3", Stick_3);
		Sticker_ImgVSet.put("4", Stick_4);
		Sticker_ImgVSet.put("5", Stick_5);
		Sticker_ImgVSet.put("6", Stick_6);
		Sticker_ImgVSet.put("7", Stick_7);
		Sticker_ImgVSet.put("8", Stick_8);
		Sticker_ImgVSet.put("9", Stick_9);
		Sticker_ImgVSet.put("10", Stick_10);
		Sticker_ImgVSet.put("11", Stick_11);
		Sticker_ImgVSet.put("12", Stick_12);

		for (final String key : Sticker_ImgVSet.keySet()) {
			Sticker_ImgVSet.get(key).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Send_Stick(Stick_Set + "_" + key);
						}
					});
		}

		Button STK_SHOP_Btn = (Button) StikerV.findViewById(R.id.shop_btn);
		STK_SHOP_Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CallSTKShop();
			}
		});
	}

	public void CallSTKShop() {
		StikerV.setVisibility(RelativeLayout.GONE);
		Intent Shop_intent = new Intent(mActivity, STKShop_Page.class);
		mActivity.startActivity(Shop_intent);
	}

	public void Send_Stick(String id) {
		Msg_Send = id;
		StikerV.setVisibility(RelativeLayout.GONE);
		data.Sticker_Layout_Stat_team = false;
		sticker_Sender();
	}

	public class ImageAdapter extends BaseAdapter {

		private Context mActivity;

		public ImageAdapter(Context context) {
			mActivity = context;
		}

		public int getCount() {
			return data.Chat_Item_list_Team.size();// +League_list.size();
		}

		public Object getItem(int position) {
			return null;// URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (position < data.Chat_Item_list_Team.size()) {
				try {
					LinearLayout retval_Main = new LinearLayout(mActivity);
					retval_Main.setOrientation(LinearLayout.VERTICAL);

					LinearLayout retval = new LinearLayout(mActivity);
					retval.setOrientation(LinearLayout.HORIZONTAL);
					retval.setPadding(0, 5, 0, 5);

					JSONObject txt_Item = null;
					txt_Item = data.Chat_Item_list_Team.get(position);
					int colors = Integer.parseInt("000000", 16) + (0xFF000000);

					LinearLayout txt_layout = new LinearLayout(mActivity);
					txt_layout.setOrientation(LinearLayout.VERTICAL);
					txt_layout.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1));
					txt_layout.setPadding(10, 0, 10, 0);

					LinearLayout name_layout = new LinearLayout(mActivity);
					name_layout.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					name_layout.setOrientation(LinearLayout.HORIZONTAL);
					TextView txt_N = new TextView(mActivity);
					txt_N.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_N.setTypeface(Typeface.DEFAULT_BOLD);

					TextView txt_T = new TextView(mActivity);
					txt_T.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));

					TextView txt_M = new TextView(mActivity);
					txt_M.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_M.setAutoLinkMask(Linkify.ALL);
					txt_M.setTextColor(colors);

					ImageView Sticker = new ImageView(mActivity);
					Sticker.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					Sticker.setMinimumWidth(GetdipSize.dip(mActivity, 50));
					Sticker.setMinimumHeight(GetdipSize.dip(mActivity, 50));

					LinearLayout Profile_layout = new LinearLayout(mActivity);
					Profile_layout
							.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
					Profile_layout.setOrientation(LinearLayout.VERTICAL);
					Profile_layout.setGravity(Gravity.CENTER_HORIZONTAL);
					ImageView Profile_Pic = new ImageView(mActivity);
					Profile_Pic.setLayoutParams(new LinearLayout.LayoutParams(
							GetdipSize.dip(mActivity, 35), GetdipSize.dip(mActivity, 35)));

					Profile_Pic.setImageResource(R.drawable.test_profile_pic);
					Profile_layout.addView(Profile_Pic);

					txt_T.setPadding(5, 0, 5, 0);
					txt_T.setText("(" + txt_Item.getString("ch_time") + ")");
					Profile_layout.addView(txt_T);

					txt_layout.addView(name_layout);
					if (position > 0) {
						if (!txt_Item.getString("ch_date").contains(
								data.Chat_Item_list_Team.get(position - 1)
										.getString("ch_date"))) {
							TextView txt_D = new TextView(mActivity);
							txt_D.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT));
							txt_D.setGravity(Gravity.CENTER);
							String Date = txt_Item.getString("ch_date");
							if(ControllParameter.Laugage_Select==1){
								Date = Date_Covert.Mont_ConV(Date_Covert.Day_ConV(Date));
							}
							txt_D.setText(Date);
							txt_D.setTextColor(Color.BLACK);
							txt_D.setTypeface(Typeface.DEFAULT_BOLD);
							retval_Main.addView(txt_D);
						}
						if (txt_Item.getString("ch_uid").equals(
								data.Chat_Item_list_Team.get(position - 1)
								.getString("ch_uid"))) {
							txt_N.setVisibility(View.GONE);
							Profile_Pic.setVisibility(View.GONE);
						}
					} else {
						TextView txt_D = new TextView(mActivity);
						txt_D.setLayoutParams(new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						txt_D.setGravity(Gravity.CENTER);
						String Date = txt_Item.getString("ch_date");
						if(ControllParameter.Laugage_Select==1){
							Date = Date_Covert.Mont_ConV(Date_Covert.Day_ConV(Date));
						}
						txt_D.setText(Date);
						txt_D.setTextColor(Color.BLACK);
						txt_D.setTypeface(Typeface.DEFAULT_BOLD);
						retval_Main.addView(txt_D);
					}

					name_layout.addView(txt_N);

					if (txt_Item.getString("ch_uid").equals(ControllParameter.ID_Send)) {
						txt_N.setText(SessionManager.getMember(mActivity)
								.getNickname());
						if(ControllParameter.ProFileCache!=null){
							Profile_Pic.setImageBitmap(ControllParameter.ProFileCache);
						}else{
							if (data.BitMapHash.get(txt_Item.getString("m_photo")) != null) {
								Profile_Pic.setImageBitmap(data.BitMapHash
										.get(txt_Item.getString("m_photo")));
							} else {
								new DownChatPic().startDownload(mActivity, txt_Item.getString("m_photo"),
										Profile_Pic, data);
							}
						}
						
						if (txt_Item.getString("ch_type").contains("S")) {
							if (txt_Item.getString("ch_msg").contains(".gif")) {
								new DownChatPic().startDownloadGIFCache(mActivity
										, ControllParameter.CHAT_STK_URL
												+ txt_Item.getString("ch_msg")
										, Sticker);
							} else {
								if (data.BitMapHash.get(txt_Item
										.getString("ch_msg")) != null) {
									Sticker.setImageBitmap(data.BitMapHash
											.get(txt_Item.getString("ch_msg")));
								} else {
									Sticker.setImageResource(R.drawable.soccer_icon);
									new DownChatPic().startDownload(mActivity, txt_Item.getString("ch_msg"),
											Sticker, data);
								}
							}

							txt_layout.setGravity(Gravity.RIGHT);
							txt_layout.addView(Sticker);
							retval.addView(txt_layout);
						} else {
							txt_M.setText(txt_Item.getString("ch_msg") + " ");
							txt_M.setBackgroundResource(R.drawable.bubble_green_n);
							txt_M.setGravity(Gravity.RIGHT);
							txt_layout.setGravity(Gravity.RIGHT);
							txt_layout.addView(txt_M);
							retval.addView(txt_layout);
						}
						retval.setGravity(Gravity.RIGHT);
						retval.addView(Profile_layout);
					} else {
						txt_N.setText(txt_Item.getString("m_nickname"));
						if (data.BitMapHash.get(txt_Item.getString("m_photo")) != null) {
							Profile_Pic.setImageBitmap(data.BitMapHash
									.get(txt_Item.getString("m_photo")));
						} else {
							if(saveModeGet.equals("true")){
								Profile_Pic.setImageResource(R.drawable.ic_menu_view);
							}else{
								Profile_Pic.setImageResource(R.drawable.soccer_icon);
							}
							new DownChatPic().startDownloadNonCache(
									mActivity, txt_Item.getString("m_photo"), Profile_Pic, saveModeGet, data);
						}
						retval.setGravity(Gravity.LEFT);
						retval.addView(Profile_layout);
						if (txt_Item.getString("ch_type").contains("S")) {
							if (txt_Item.getString("ch_msg").contains(".gif")) {
								new DownChatPic().startDownloadGIFCache(mActivity
										, ControllParameter.CHAT_STK_URL
												+ txt_Item.getString("ch_msg")
										, Sticker);
							} else {
								if (data.BitMapHash.get(txt_Item
										.getString("ch_msg")) != null) {
									Sticker.setImageBitmap(data.BitMapHash
											.get(txt_Item.getString("ch_msg")));
								} else {
									Sticker.setImageResource(R.drawable.soccer_icon);
									new DownChatPic().startDownload(mActivity, txt_Item.getString("ch_msg"),
											Sticker, data);
								}
							}

							txt_layout.addView(Sticker);
							retval.addView(txt_layout);
						} else {
							txt_M.setText(" " + txt_Item.getString("ch_msg"));
							txt_M.setBackgroundResource(R.drawable.bubble_yellow);
							txt_layout.addView(txt_M);
							retval.addView(txt_layout);
						}
					}
					retval_Main.addView(retval);
					return retval_Main;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return convertView;
			} else {
				return convertView;
			}
		}

	}
	
	class check_Permit extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", String.valueOf(SessionManager.getMember(mActivity).getUid()) ));
				JSONObject json = jParser
						.makeHttpRequest(ControllParameter.CHECK_CHAT_MEMBER_PERMIT_URL,
								"POST", params);
				if (json != null) {
					String retCode = json.getString("return_code");
					if(retCode.equals("1") && args.length>0){
						Msg_Send = args[0];
						chat_Sender();
					}
					return retCode;
				}else{
					if(args.length>0){
						return "N";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(final String outPut) {
			((Activity) mActivity).runOnUiThread(new Runnable() {
				public void run() {
					Chat_input.setText("");
					if(outPut.equals("1")){
						ControllParameter.BanStatus = true;
						Chat_input.setEnabled(ControllParameter.BanStatus);
						send_Btn.setEnabled(ControllParameter.BanStatus);
					}else if(outPut.equals("0")){
						ControllParameter.BanStatus = false;
						Chat_input.setEnabled(ControllParameter.BanStatus);
						Chat_input.setHint(R.string.chat_ban);
						send_Btn.setBackgroundResource(R.drawable.question_btn);
					}else if(outPut.equals("")){
						ControllParameter.BanStatus = null;
					}
				}
			});
		}
	}

	public void Chat_Loader() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					data.socket_Team = new SocketIO("http://"+ControllParameter.SERVER_URL+":"
							+ data.SocketSelect);
				} catch (MalformedURLException e1) {

					e1.printStackTrace();
				}

				data.socket_Team.connect(new IOCallback() {

					@Override
					public void onMessage(JSONObject json, IOAcknowledge ack) {
						try {
							Log.d("TEST",
									"test::Server said:" + json.toString(2));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onMessage(String data, IOAcknowledge ack) {
						Log.d("TEST", "test::Server said: " + data);
					}

					@Override
					public void onError(SocketIOException socketIOException) {
						Log.d("TEST", "test::an Error occured");
						socketIOException.printStackTrace();
						if (data.Chat_Item_list_Team.size() > 0) {
							data.socket_Team.reconnect();
						} else {
							RefreshView(mActivity.getResources().getString(R.string.pull_to_refresh_tap_label));
						}
					}

					@Override
					public void onDisconnect() {
						data.socket_Team.reconnect();
						data.chat_on_Team = false;
						Log.d("TEST", "chat_on::" + data.chat_on_Team);
					}

					@Override
					public void onConnect() {
						data.chat_on_Team = true;
						Log.d("TEST", "chat_on::" + data.chat_on_Team);
					}

					@Override
					public void on(String event, IOAcknowledge ack,
							Object... args) {
						if (event.equals("updatechat") && args.length >= 1) {
							try {
								JSONObject json_ob = new JSONObject(args[0]
										.toString());
								json_ob.put("ch_uid", json_ob.getString("us"));
								json_ob.put("m_nickname",
										json_ob.getString("nn"));
								json_ob.put("ch_msg", json_ob.getString("ms"));
								json_ob.put("ch_type", json_ob.getString("ty"));
								json_ob.put("m_photo", json_ob.getString("ui"));
								json_ob.put("ch_time", json_ob.getString("ft"));
								json_ob.put("ch_date", json_ob.getString("fd"));

								json_ob.remove("us");
								json_ob.remove("nn");
								json_ob.remove("ms");
								json_ob.remove("ty");
								json_ob.remove("ui");
								json_ob.remove("us");
								json_ob.remove("fd");
								data.Chat_Item_list_Team.add(json_ob);
								chatHandle(!json_ob.getString("ch_uid").equals(ControllParameter.ID_Send));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (event.equals("updateusers")
								&& args.length > 0) {
							if(ControllParameter.Role_ID==1){
								ShowUserCountHandle(args[0].toString());
							}
						} else if (event.equals("updateoldchat")) {
							data.Chat_Item_list_Team.clear();
							for (Object object : args) {
								try {
									JSONArray json_arr = new JSONArray(object
											.toString());

									for (int i = 0; i < json_arr.length(); i++) {
										JSONObject json_ob = json_arr
												.getJSONObject(i);
										if (json_ob.getString("ch_type")
												.equals("S")) {
											json_ob.put(
													"ch_msg",
													json_ob.getString("sk_img")
															+ "."
															+ json_ob
																	.getString("sk_type"));
										}
										data.Chat_Item_list_Team.add(json_ob);
									}

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							chatHandle(false);
						} else if (event.equals("getsticker")) {
							for (Object object : args) {
								try {
									SessionManager.createNewJsonSession(
											mActivity, "StickerSet",
											object.toString());
									JSONObject json_ob = new JSONObject(object
											.toString());
									data.Sticker_Set.clear();
									data.Sticker_UrlSet.clear();
									for (Iterator<?> stk_Item_key = json_ob
											.keys(); stk_Item_key.hasNext();) {
										String key_Item = (String) stk_Item_key
												.next();
										JSONArray json_arr = json_ob
												.getJSONArray(key_Item);
										data.Sticker_Set
												.put(key_Item, json_arr);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});
				data.socket_Team.emit("adduser", ControllParameter.ID_Send,
						data.ProFile_pic,
						SessionManager.getMember(mActivity).getNickname());
			}
		}).start();
	}
	
	public void ShowUserCountHandle(final String numBer){
		handler.post(new Runnable() {
			@Override
			public void run() {
				ControllParameter.UcountChatTeam = numBer;
				UserCountTXT.setText(mActivity.getResources().getString(R.string.user_count)+ ": " +ControllParameter.UcountChatTeam);
			}
		});
	}

	public void RefreshView(final String txt) {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				data.Chat_list_LayOut_Team.removeAllViews();
				TextView RefreshTag = new TextView(mActivity);
				RefreshTag.setPadding(0, 30, 0, 30);
				RefreshTag.setTextColor(Color.GRAY);
				RefreshTag.setText(txt);
				RefreshTag.setGravity(Gravity.CENTER);
				data.Chat_list_LayOut_Team.addView(RefreshTag);
				data.Chat_list_LayOut_Team
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								data.Chat_list_LayOut_Team.removeAllViews();
								ProgressBar progress = new ProgressBar(mActivity);
								data.Chat_list_LayOut_Team.addView(progress);
								(data.Chat_list_LayOut_Team)
										.addView(data.lstViewChatTeam);
								if (data.socket_Team == null) {
									Chat_Loader();
								}else if (!data.socket_Team.isConnected()) {
									Chat_Loader();
								}
								new check_Permit().execute();
							}
						});
			}
		}, data.chatDelay);
	}

	public void chatHandle(final Boolean updateCountAlert) {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (data.Chat_list_LayOut_Team.getChildCount() > 1) {
					data.Chat_list_LayOut_Team.removeViewAt(0);
				}
				data.lstViewChatTeam.setSelection(data.Chat_Item_list_Team
						.size());
				if(updateCountAlert && MainActivity.curPage!=2){
					MainActivity.countNumChat++;
					MainActivity.ChatAlertSetting();
				}
			}
		}, data.chatDelay);
	}

	public void chat_Sender() {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Msg_Send = Msg_Send.replaceAll("'|\"|<|>", "");
				if(ControllParameter.BanStatus!=null){
					if(ControllParameter.BanStatus){
						if (!Msg_Send.equals("")) {
							data.socket_Team.emit("sendchat", Msg_Send);
							Msg_Send = "";
						}
					}else{
						User_Rule.showRuleDialog(mActivity);
					}
				}else{
					new check_Permit().execute(Msg_Send);
				}
			}
		}, data.chatDelay);
	}

	public void sticker_Sender() {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Msg_Send = Msg_Send.replaceAll("'|\"|<|>", "");
				if(ControllParameter.BanStatus!=null){
					if (ControllParameter.BanStatus) {
						if (!Msg_Send.equals("")) {
							data.socket_Team.emit("sendsticker", Msg_Send);
							Msg_Send = "";
						}
					} else {
						User_Rule.showRuleDialog(mActivity);
					}
				}else{
					new check_Permit().execute(Msg_Send);
				}
			}
		}, data.chatDelay);
	}
	
}
