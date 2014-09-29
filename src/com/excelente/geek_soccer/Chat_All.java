package com.excelente.geek_soccer;

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
import com.excelente.geek_soccer.chat_menu.Chat_Menu_LongClick;
import com.excelente.geek_soccer.date_convert.Date_Covert;
import com.excelente.geek_soccer.pic_download.DownChatPic;
import com.excelente.geek_soccer.sideMenu.SideMenuLayout;
import com.excelente.geek_soccer.user_rule.User_Rule;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
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

public class Chat_All extends Activity {

	int width;
	int height;
	private static ControllParameter data;
	Context mContext;
	JSONParser jParser = new JSONParser();
	private Handler handler = new Handler(Looper.getMainLooper());
	String Msg_Send = "";

	String TimeStamp_Send = "0";
	String old_timeStamp = "0";

	EditText Chat_input;
	Button send_Btn;
	Button sendSticker_Btn;

	WindowManager wm;
	LinearLayout input_layout;
	TextView UserCountTXT;
	View StikerV;

	ImageView allRoom;
	ImageView TeamRoom;

	String Stick_Set = "1";
	LinearLayout StickerSelectorLayout;
	ImageView Stick_1;
	ImageView Stick_2;
	ImageView Stick_3;
	ImageView Stick_4;
	ImageView Stick_5;
	ImageView Stick_6;
	ImageView Stick_7;
	ImageView Stick_8;
	ImageView Stick_9;
	ImageView Stick_10;
	ImageView Stick_11;
	ImageView Stick_12;
	static HashMap<String, ImageView> Sticker_ImgVSet = new HashMap<String, ImageView>();
	static HashMap<String, ImageView> Sticker_ButVSet = new HashMap<String, ImageView>();
	SessionManager sesPrefer;
	String saveModeGet;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		data = ControllParameter.getInstance(this);

		setContentView(R.layout.chat_layout);
		mContext = this;
		ControllParameter.ProFileCache = SessionManager.getImageSession(mContext, SessionManager.getMember(mContext).getPhoto());
		saveModeGet = SessionManager.getSetting(mContext,
				SessionManager.setting_save_mode);
		
		UserCountTXT = (TextView)findViewById(R.id.ShowUserCount);
		if(ControllParameter.Role_ID==1){
			UserCountTXT.setText(getResources().getString(R.string.user_count)+ ": " +ControllParameter.UcountChatAll);
		}else{
			UserCountTXT.setVisibility(RelativeLayout.GONE);
		}
		data.lstViewChatAll = new ListView(mContext);
		data.lstViewChatAll.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		data.lstViewChatAll.setClipToPadding(false);
		data.imageAdapterChatAll = new ImageAdapter(
				mContext.getApplicationContext());
		data.lstViewChatAll.setAdapter(data.imageAdapterChatAll);
		data.lstViewChatAll.setDividerHeight(0);
		data.Chat_list_LayOut_All = (LinearLayout) findViewById(R.id.Chat_list_Layout);
		(data.Chat_list_LayOut_All).addView(data.lstViewChatAll);

		data.lstViewChatAll
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int position, long arg3) {
						String m_photo;
						try {
							m_photo = data.Chat_Item_list_All.get(position).getString("m_photo");
							new Chat_Menu_LongClick().ChatMenu(mContext,
									data.Chat_Item_list_All.get(position), m_photo, saveModeGet, data);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						return false;
					}
				});

		if (data.Chat_Item_list_All.size() > 0) {
			if (data.Chat_list_LayOut_All.getChildCount() > 1) {
				data.Chat_list_LayOut_All.removeViewAt(0);
			}
			data.imageAdapterChatAll.notifyDataSetChanged();
			data.lstViewChatAll.setSelection(data.Chat_Item_list_All.size());
		}

		if (data.socket_All == null) {
			Chat_Loader();
		} else if (!data.socket_All.isConnected()) {
			Chat_Loader();
		}
		new check_Permit().execute();

		Chat_input = (EditText) findViewById(R.id.Chat_input);
		send_Btn = (Button) findViewById(R.id.send_btn);

		Chat_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (data.Sticker_Layout_Stat_All) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_All = false;
				}
			}
		});

		Chat_input.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (data.Sticker_Layout_Stat_All) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_All = false;
				}
			}
		});

		Chat_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (Chat_input.getLineCount() > 1) {
					Chat_input.setLayoutParams(new LinearLayout.LayoutParams(0,
							(GetdipSize.dip(mContext, 40) * 3) / 2, 1));
				} else {
					Chat_input.setLayoutParams(new LinearLayout.LayoutParams(0,
							GetdipSize.dip(mContext, 40), 1));
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
		input_layout = (LinearLayout) findViewById(R.id.input_Layout);
		sendSticker_Btn = (Button) findViewById(R.id.sendSticker_btn);
		Create_Stick_view();

		sendSticker_Btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (data.Sticker_Layout_Stat_All) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_All = false;
				} else {
					StikerV.setVisibility(RelativeLayout.ABOVE);
					data.Sticker_Layout_Stat_All = true;
					StickViewClear();
					StickerPrepare();
					StickViewCall(Stick_Set);
					StickerSelectorLayout.removeAllViews();
					LayoutParams paramsBtn = new LinearLayout.LayoutParams(GetdipSize.dip(mContext, 40), GetdipSize.dip(mContext, 40));
					LayoutParams paramsLine = new LinearLayout.LayoutParams(GetdipSize.dip(mContext, 1), LayoutParams.MATCH_PARENT);
					((MarginLayoutParams) paramsBtn).setMargins(5, 0, 5, 0);
					View line = new View(mContext);
					line.setBackgroundColor(Color.GRAY);
					line.getBackground().setAlpha(100);
					line.setLayoutParams(paramsLine);
					StickerSelectorLayout.addView(line);
					for (int i = 0; i < data.Sticker_Set.size(); i++) {
						final ImageView StickSet_1 = new ImageView(mContext);
						StickSet_1.setLayoutParams(paramsBtn);
						final int StickPosition = i + 1;
						StickBTNPrepare(String
								.valueOf(StickPosition), StickSet_1);
						if (String.valueOf(StickPosition).equals(Stick_Set)) {
							StickSet_1.setEnabled(false);
							StickSet_1.setBackgroundResource(SessionManager.getTeamColor(mContext));
						} else {
							StickSet_1.setEnabled(true);
							StickSet_1.setBackgroundResource(R.color.tran);
						}
						StickSet_1
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View arg0) {
										StickViewClear();
										Stick_Set = String
												.valueOf(StickPosition);
										
										for (int j = 0; j < data.Sticker_Set
												.size(); j++) {
											if (String.valueOf(j + 1).equals(
													Stick_Set)) {
												Sticker_ButVSet.get(
														String.valueOf(j))
														.setBackgroundResource(SessionManager.getTeamColor(mContext));
												Sticker_ButVSet.get(
														String.valueOf(j))
														.setEnabled(false);
											} else {
												Sticker_ButVSet.get(
														String.valueOf(j))
														.setBackgroundResource(R.color.tran);
												Sticker_ButVSet.get(
														String.valueOf(j))
														.setEnabled(true);
											}
										}
										
										StickViewCall(String
												.valueOf(StickPosition));
									}

								});
						Sticker_ButVSet.put(String.valueOf(i), StickSet_1);
						StickerSelectorLayout.addView(StickSet_1);
						line = new View(mContext);
						line.setBackgroundColor(Color.GRAY);
						line.getBackground().setAlpha(100);
						line.setLayoutParams(paramsLine);
						StickerSelectorLayout.addView(line);
					}
				}
			}
		});

		data.lstViewChatAll.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (data.Sticker_Layout_Stat_All) {
					StikerV.setVisibility(RelativeLayout.GONE);
					data.Sticker_Layout_Stat_All = false;
				}
			}
		});

		Chat_input.setEnabled(ControllParameter.BanStatus);
		if (!ControllParameter.BanStatus) {
			Chat_input.setHint(R.string.chat_ban);
			send_Btn.setBackgroundResource(R.drawable.question_btn);
		}
	}

	public void StickViewClear() {
		for (final String key : Sticker_ImgVSet.keySet()) {
			Sticker_ImgVSet.get(key).setImageResource(R.drawable.livescore_h);
		}
	}

	public void StickerPrepare() {
		String StickJset = SessionManager.getJsonSession(Chat_All.this,
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
						Chat_All.this, "StickerSet");
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
						new DownChatPic().startDownload(mContext, data.Sticker_UrlSet.get(key), StkBtn, data);
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
			if (STK_exist_list.size() > 0) {
				if (STK_exist_list.size() > (Integer.parseInt(position) - 1)) {
					S_postion = STK_exist_list
							.get(Integer.parseInt(position) - 1);
					j_arr = data.Sticker_Set.get(S_postion);
				} else {
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
				String StickJset = SessionManager.getJsonSession(Chat_All.this,
						"StickerSet");
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
					if (STK_exist_list.size() > 0) {
						if (STK_exist_list.size() > (Integer.parseInt(position) - 1)) {
							S_postion = STK_exist_list.get(Integer
									.parseInt(position) - 1);
							j_arr = data.Sticker_Set.get(S_postion);
						} else {
							S_postion = STK_exist_list.get(0);
							j_arr = data.Sticker_Set.get(S_postion);
						}
					}

					if (j_arr != null) {
						for (int i = 0; i < j_arr.length(); i++) {
							JSONObject json_Value = j_arr.getJSONObject(i);
							data.Sticker_UrlSet.put(S_postion + "_"
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
						new DownChatPic().startDownload(mContext, data.Sticker_UrlSet.get(key),
								Sticker_ImgVSet.get(String.valueOf(ImgV_p)), data );
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
		new DownChatPic().startDownloadGIFCache(mContext
				, ControllParameter.CHAT_STK_URL + key
				, imgV);
	}

	public void Create_Stick_view() {
		LayoutInflater factory = LayoutInflater.from(mContext);
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
		Intent Shop_intent = new Intent(mContext, STKShop_Page.class);
		startActivity(Shop_intent);
	}

	public void Send_Stick(String id) {
		Msg_Send = id;
		StikerV.setVisibility(RelativeLayout.GONE);
		data.Sticker_Layout_Stat_All = false;
		sticker_Sender();
	}

	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return data.Chat_Item_list_All.size();// +League_list.size();
		}

		public Object getItem(int position) {
			return null;// URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (position < data.Chat_Item_list_All.size()) {
				try {
					LinearLayout retval_Main = new LinearLayout(mContext);
					retval_Main.setOrientation(LinearLayout.VERTICAL);

					LinearLayout retval = new LinearLayout(mContext);
					retval.setOrientation(LinearLayout.HORIZONTAL);
					retval.setPadding(0, 5, 0, 5);

					JSONObject txt_Item = null;
					txt_Item = data.Chat_Item_list_All.get(position);
					int colors = Integer.parseInt("000000", 16) + (0xFF000000);

					LinearLayout txt_layout = new LinearLayout(mContext);
					txt_layout.setOrientation(LinearLayout.VERTICAL);
					txt_layout.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1));
					txt_layout.setPadding(10, 0, 10, 0);

					LinearLayout name_layout = new LinearLayout(mContext);
					name_layout.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					name_layout.setOrientation(LinearLayout.HORIZONTAL);
					TextView txt_N = new TextView(mContext);
					txt_N.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_N.setTypeface(Typeface.DEFAULT_BOLD);

					TextView txt_T = new TextView(mContext);
					txt_T.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));

					TextView txt_M = new TextView(mContext);
					txt_M.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_M.setTextColor(colors);

					ImageView Sticker = new ImageView(mContext);
					Sticker.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					Sticker.setMinimumWidth(GetdipSize.dip(mContext, 50));
					Sticker.setMinimumHeight(GetdipSize.dip(mContext, 50));

					LinearLayout Profile_layout = new LinearLayout(mContext);
					Profile_layout
							.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
					Profile_layout.setOrientation(LinearLayout.VERTICAL);
					Profile_layout.setGravity(Gravity.CENTER_HORIZONTAL);

					ImageView Profile_Pic = new ImageView(mContext);
					Profile_Pic.setLayoutParams(new LinearLayout.LayoutParams(
							GetdipSize.dip(mContext, 35), GetdipSize.dip(mContext, 35)));
					Profile_Pic.setImageResource(R.drawable.test_profile_pic);
					Profile_layout.addView(Profile_Pic);

					LinearLayout Logo_layout = new LinearLayout(mContext);
					Logo_layout.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.MATCH_PARENT));
					Logo_layout.setOrientation(LinearLayout.HORIZONTAL);
					Logo_layout.setGravity(Gravity.CENTER_HORIZONTAL);
					Profile_layout.addView(Logo_layout);

					txt_T.setPadding(5, 0, 5, 0);
					txt_T.setText("(" + txt_Item.getString("ch_time") + ")");
					Logo_layout.addView(txt_T);

					txt_layout.addView(name_layout);

					if (position > 0) {
						if (!txt_Item.getString("ch_date").equals(
								data.Chat_Item_list_All.get(position - 1)
										.getString("ch_date"))) {
							TextView txt_D = new TextView(mContext);
							txt_D.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT));
							txt_D.setGravity(Gravity.CENTER);
							String Date = txt_Item.getString("ch_date");
							if (ControllParameter.Laugage_Select == 1) {
								Date = Date_Covert.Mont_ConV(Date_Covert
										.Day_ConV(Date));
							}
							txt_D.setText(Date);
							txt_D.setTextColor(Color.BLACK);
							txt_D.setTypeface(Typeface.DEFAULT_BOLD);
							retval_Main.addView(txt_D);
						}
					} else {
						TextView txt_D = new TextView(mContext);
						txt_D.setLayoutParams(new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						txt_D.setGravity(Gravity.CENTER);
						String Date = txt_Item.getString("ch_date");
						if (ControllParameter.Laugage_Select == 1) {
							Date = Date_Covert.Mont_ConV(Date_Covert
									.Day_ConV(Date));
						}
						txt_D.setText(Date);
						txt_D.setTextColor(Color.BLACK);
						txt_D.setTypeface(Typeface.DEFAULT_BOLD);
						retval_Main.addView(txt_D);
					}
					name_layout.addView(txt_N);

					if (txt_Item.getString("ch_uid").equals(ControllParameter.ID_Send)) {
						txt_N.setText(SessionManager.getMember(Chat_All.this)
								.getNickname());
						if(ControllParameter.ProFileCache!=null){
							Profile_Pic.setImageBitmap(ControllParameter.ProFileCache);
						}else{
							if (data.BitMapHash.get(txt_Item.getString("m_photo")) != null) {
								Profile_Pic.setImageBitmap(data.BitMapHash
										.get(txt_Item.getString("m_photo")));
							} else {
								new DownChatPic().startDownload(mContext, txt_Item.getString("m_photo"),
										Profile_Pic, data);
							}
						}
						if (txt_Item.getString("ch_type").contains("S")) {
							if (txt_Item.getString("ch_msg").contains(".gif")) {
								new DownChatPic().startDownloadGIFCache(mContext
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
									new DownChatPic().startDownload(mContext, txt_Item.getString("ch_msg"),
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
									mContext, txt_Item.getString("m_photo"), Profile_Pic, saveModeGet, data);
						}
						TextView txt_TeamMate = new TextView(mContext);
						txt_TeamMate
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT));
						txt_TeamMate.setTypeface(Typeface.DEFAULT_BOLD);
						txt_TeamMate.setText("");
						name_layout.addView(txt_TeamMate);

						retval.setGravity(Gravity.LEFT);
						retval.addView(Profile_layout);
						if (txt_Item.getString("ch_type").contains("S")) {
							if (txt_Item.getString("ch_msg").contains(".gif")) {
								new DownChatPic().startDownloadGIFCache(mContext
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
									new DownChatPic().startDownload(mContext, txt_Item.getString("ch_msg"),
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
				params.add(new BasicNameValuePair("id", String
						.valueOf(SessionManager.getMember(mContext).getUid())));
				JSONObject json = jParser
						.makeHttpRequest(
								ControllParameter.CHECK_CHAT_MEMBER_PERMIT_URL,
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
			// pDialog.dismiss();
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					Chat_input.setText("");
					if (outPut.equals("1")) {
						ControllParameter.BanStatus = true;
						Chat_input.setEnabled(ControllParameter.BanStatus);
						send_Btn.setEnabled(ControllParameter.BanStatus);
					} else if(outPut.equals("0")){
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
					if (data.socket_All != null) {
						if (data.socket_All.isConnected()) {
							data.socket_All.disconnect();
						}
					}
					data.socket_All = new SocketIO("http://" + ControllParameter.SERVER_URL + ":5000");
				} catch (MalformedURLException e1) {

					e1.printStackTrace();
				}
				data.socket_All.connect(new IOCallback() {
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
						if (data.Chat_Item_list_All.size() > 0) {
							data.socket_All.reconnect();
						} else {
							RefreshView(mContext.getResources().getString(R.string.pull_to_refresh_tap_label));
						}
					}

					@Override
					public void onDisconnect() {
						data.socket_All.reconnect();
						data.chat_on_All = false;
						Log.d("TEST", "chat_on::" + data.chat_on_All);
					}

					@Override
					public void onConnect() {
						data.chat_on_All = true;
						Log.d("TEST", "chat_on::" + data.chat_on_All);
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

								data.Chat_Item_list_All.add(json_ob);
								chatHandle();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (event.equals("updateusers")
								&& args.length > 0) {
							if(ControllParameter.Role_ID==1){
								ShowUserCountHandle(args[0].toString());
							}
						} else if (event.equals("updateoldchat")) {
							data.Chat_Item_list_All.clear();
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
										data.Chat_Item_list_All.add(json_ob);
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							chatHandle();
						} else if (event.equals("getsticker")) {
							for (Object object : args) {
								try {
									SessionManager.createNewJsonSession(
											Chat_All.this, "StickerSet",
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
							// chatHandle();
						}
					}
				});
				data.socket_All.emit("adduser", ControllParameter.ID_Send, data.ProFile_pic,
						SessionManager.getMember(Chat_All.this).getNickname());
			}
		}).start();

	}
	
	public void ShowUserCountHandle(final String numBer){
		handler.post(new Runnable() {
			@Override
			public void run() {
				ControllParameter.UcountChatAll = numBer;
				UserCountTXT.setText(getResources().getString(R.string.user_count)+ ": " +ControllParameter.UcountChatAll);
			}
		});
	}

	public void RefreshView(final String txt) {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				data.Chat_list_LayOut_All.removeAllViews();
				TextView RefreshTag = new TextView(mContext);
				RefreshTag.setPadding(0, 30, 0, 30);
				RefreshTag.setTextColor(Color.GRAY);
				RefreshTag.setText(txt);
				RefreshTag.setGravity(Gravity.CENTER);
				data.Chat_list_LayOut_All.addView(RefreshTag);
				data.Chat_list_LayOut_All
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								data.Chat_list_LayOut_All.removeAllViews();
								ProgressBar progress = new ProgressBar(mContext);
								data.Chat_list_LayOut_All.addView(progress);
								(data.Chat_list_LayOut_All)
										.addView(data.lstViewChatAll);
								if (data.socket_All == null) {
									Chat_Loader();
								}else if (!data.socket_All.isConnected()) {
									Chat_Loader();
								}
								new check_Permit().execute();
							}
						});
			}
		}, data.chatDelay);
	}

	public void chatHandle() {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (data.Chat_list_LayOut_All.getChildCount() > 1) {
					data.Chat_list_LayOut_All.removeViewAt(0);
				}
				data.lstViewChatAll.setSelection(data.Chat_Item_list_All.size());
			}
		}, data.chatDelay);
	}

	public void chat_Sender() {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Msg_Send = Msg_Send.replaceAll("'|/|\"|<|>", "");
				if(ControllParameter.BanStatus!=null){
					if (ControllParameter.BanStatus) {
						if (!Msg_Send.equals("")) {
							data.socket_All.emit("sendchat", Msg_Send);
							Msg_Send = "";
						}
					} else {
						User_Rule.showRuleDialog(mContext);
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
				Msg_Send = Msg_Send.replaceAll("'|/|\"|<|>", "");
				if(ControllParameter.BanStatus!=null){
					if (ControllParameter.BanStatus) {
						if (!Msg_Send.equals("")) {
							data.socket_All.emit("sendsticker", Msg_Send);
							Msg_Send = "";
						}
					} else {
						User_Rule.showRuleDialog(mContext);
					}
				}else{
					new check_Permit().execute(Msg_Send);
				}
				
			}
		}, data.chatDelay);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (data.Menu_Layout != null) {
				if (data.Menu_Layout.getVisibility() == 0) {
					new SideMenuLayout().hideMenu(mContext);
					return false;
				} else {
					if (data.Sticker_Layout_Stat_All) {
						StikerV.setVisibility(RelativeLayout.GONE);
						return false;
					} else {
						return false;
					}
				}
			} else {
				if (data.Sticker_Layout_Stat_All) {
					StikerV.setVisibility(RelativeLayout.GONE);
					return false;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
