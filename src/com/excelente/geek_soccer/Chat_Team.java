package com.excelente.geek_soccer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

public class Chat_Team extends Activity {
	int width;
	int height;
	private static ControllParameter data;
	Context mContext;
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
	static HashMap<String, Button> Sticker_ButVSet = new HashMap<String, Button>();
	String root = Environment.getExternalStorageDirectory().toString();
	ProgressBar progressBar;

	int pixels;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		data = ControllParameter.getInstance(this);
		
		setContentView(R.layout.chat_layout);
		mContext = this;
		data.lstViewChatTeam = new ListView(mContext);
		data.lstViewChatTeam.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		data.lstViewChatTeam.setClipToPadding(false);
		data.imageAdapterChatTeam = new ImageAdapter(
				mContext.getApplicationContext());
		data.lstViewChatTeam.setAdapter(data.imageAdapterChatTeam);
		data.lstViewChatTeam.setDividerHeight(0);
		data.Chat_list_LayOut_Team = (LinearLayout) findViewById(R.id.Chat_list_Layout);
		(data.Chat_list_LayOut_Team).addView(data.lstViewChatTeam);

		data.lstViewChatTeam
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int position, long arg3) {
						try {
							JSONObject txt = data.Chat_Item_list_Team
									.get(position);
							if (txt.getString("ch_type").contains("T")) {
								if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
									android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
									clipboard.setText(txt.getString("ch_msg"));
								} else {
									android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
									android.content.ClipData clip = android.content.ClipData
											.newPlainText("Copied",
													txt.getString("ch_msg"));
									clipboard.setPrimaryClip(clip);
								}

								Toast.makeText(mContext, "Copied",
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return false;
					}
				});

		if (SessionManager.getMember(Chat_Team.this).getTeamId() == 1) {
			data.SocketSelect = "5001";
		} else if (SessionManager.getMember(Chat_Team.this).getTeamId() == 2) {
			data.SocketSelect = "5002";
		} else if (SessionManager.getMember(Chat_Team.this).getTeamId() == 3) {
			data.SocketSelect = "5003";
		} else if (SessionManager.getMember(Chat_Team.this).getTeamId() == 4) {
			data.SocketSelect = "5004";
		}
		if (data.Chat_Item_list_Team.size() > 0) {
			if (data.Chat_list_LayOut_Team.getChildCount() > 1) {
				data.Chat_list_LayOut_Team.removeViewAt(0);
			}
			data.imageAdapterChatTeam.notifyDataSetChanged();
			data.lstViewChatTeam.setSelection(data.imageAdapterChatTeam
					.getCount());
		}
		if (data.socket_Team == null) {
			Chat_Loader();
		}else if (!data.socket_Team.isConnected()) {
			Chat_Loader();
		}

		Chat_input = (EditText) findViewById(R.id.Chat_input);
		send_Btn = (Button) findViewById(R.id.send_btn);

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
		final float scale = getResources().getDisplayMetrics().density;
		pixels = (int) (40 * scale + 0.5f);
		Chat_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (Chat_input.getLineCount() > 1) {
					Chat_input.setLayoutParams(new LinearLayout.LayoutParams(0, (pixels * 3) / 2, 1));
				} else {
					Chat_input.setLayoutParams(new LinearLayout.LayoutParams(0, pixels, 1));
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
					LayoutParams paramsBtn = new LinearLayout.LayoutParams((int) (48 * scale + 0.5f),
							(int)(48 * scale + 0.5f));
					((MarginLayoutParams) paramsBtn).setMargins(5, 0, 5, 0);
					for (int i = 0; i < data.Sticker_Set.size(); i++) {
						final Button StickSet_1 = new Button(mContext);
						StickSet_1
								.setBackgroundResource(R.drawable.stk_btn_set);
						StickSet_1.setLayoutParams(paramsBtn);
						StickSet_1.setTypeface(Typeface.DEFAULT_BOLD);
						StickSet_1.setText("" + (i + 1));
						final int StickPosition = i + 1;
						if (String.valueOf(StickPosition).equals(Stick_Set)) {
							StickSet_1.setEnabled(false);
						} else {
							StickSet_1.setEnabled(true);
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
														.setEnabled(false);
											} else {
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
	}

	public void StickViewClear() {
		for (final String key : Sticker_ImgVSet.keySet()) {
			Sticker_ImgVSet.get(key).setImageResource(R.drawable.livescore_h);
		}
	}

	public void StickerPrepare() {
		String StickJset = SessionManager.getJsonSession(Chat_Team.this,
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
						Chat_Team.this, "StickerSet");
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
						startDownload(data.Sticker_UrlSet.get(key),
								Sticker_ImgVSet.get(String.valueOf(ImgV_p)));
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
		Ion.with(imgV).placeholder(R.drawable.livescore_h).animateGif(true)
				.load("http://183.90.171.209/chat/stk/" + key)
				.setCallback(new FutureCallback<ImageView>() {
					@Override
					public void onCompleted(Exception arg0, ImageView arg1) {
					}
				});
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
		data.Sticker_Layout_Stat_team = false;
		sticker_Sender();
	}

	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
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
					LinearLayout retval_Main = new LinearLayout(mContext);
					retval_Main.setOrientation(LinearLayout.VERTICAL);
					retval_Main.setGravity(Gravity.CENTER);

					LinearLayout retval = new LinearLayout(mContext);
					retval.setOrientation(LinearLayout.HORIZONTAL);
					retval.setPadding(0, 5, 0, 5);

					JSONObject txt_Item = null;
					txt_Item = data.Chat_Item_list_Team.get(position);
					int colors = Integer.parseInt("000000", 16) + (0xFF000000);

					LinearLayout txt_layout = new LinearLayout(mContext);
					txt_layout.setOrientation(LinearLayout.VERTICAL);
					txt_layout.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1));
					txt_layout.setPadding(10, 0, 10, 0);

					LinearLayout name_layout = new LinearLayout(mContext);
					txt_layout.setOrientation(LinearLayout.VERTICAL);
					name_layout.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					name_layout.setOrientation(LinearLayout.HORIZONTAL);
					TextView txt_N = new TextView(mContext);
					txt_N.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_N.setTypeface(Typeface.DEFAULT_BOLD);
					// txt_layout.addView(txt_N);

					TextView txt_T = new TextView(mContext);
					txt_T.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_T.setTypeface(Typeface.DEFAULT_BOLD);

					TextView txt_M = new TextView(mContext);
					txt_M.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					txt_M.setTextColor(colors);

					ImageView Sticker = new ImageView(mContext);
					Sticker.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));

					LinearLayout Profile_layout = new LinearLayout(mContext);
					Profile_layout
							.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.MATCH_PARENT));
					Profile_layout.setOrientation(LinearLayout.VERTICAL);
					Profile_layout.setGravity(Gravity.CENTER_HORIZONTAL);
					ImageView Profile_Pic = new ImageView(mContext);
					Profile_Pic.setLayoutParams(new LinearLayout.LayoutParams(
							50, 50));

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
							TextView txt_D = new TextView(mContext);
							txt_D.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT));
							txt_D.setGravity(Gravity.CENTER);
							txt_D.setText(txt_Item.getString("ch_date"));
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
						txt_D.setText(txt_Item.getString("ch_date"));
						txt_D.setTextColor(Color.BLACK);
						txt_D.setTypeface(Typeface.DEFAULT_BOLD);
						retval_Main.addView(txt_D);
					}

					name_layout.addView(txt_N);

					if (txt_Item.getString("ch_uid").equals(data.ID_Send)) {
						txt_N.setText(SessionManager.getMember(Chat_Team.this)
								.getNickname());
						if (data.BitMapHash.get(txt_Item.getString("m_photo")) != null) {
							Profile_Pic.setImageBitmap(data.BitMapHash
									.get(txt_Item.getString("m_photo")));
						} else {
							startDownload(txt_Item.getString("m_photo"),
									Profile_Pic);
						}
						if (txt_Item.getString("ch_type").contains("S")) {
							if (txt_Item.getString("ch_msg").contains(".gif")) {
								Ion.with(Sticker)
										.placeholder(R.drawable.soccer_icon)
										.load("http://183.90.171.209/chat/stk/"
												+ txt_Item.getString("ch_msg"));
							} else {
								if (data.BitMapHash.get(txt_Item
										.getString("ch_msg")) != null) {
									Sticker.setImageBitmap(data.BitMapHash
											.get(txt_Item.getString("ch_msg")));
								} else {
									Sticker.setImageResource(R.drawable.soccer_icon);
									startDownload(txt_Item.getString("ch_msg"),
											Sticker);
								}
							}

							txt_layout.setGravity(Gravity.RIGHT
									| Gravity.CENTER_VERTICAL);
							txt_layout.addView(Sticker);
							retval.addView(txt_layout);
						} else {
							txt_M.setText(txt_Item.getString("ch_msg") + " ");
							txt_M.setBackgroundResource(R.drawable.bubble_green_n);
							txt_layout.setGravity(Gravity.RIGHT
									| Gravity.CENTER_VERTICAL);
							txt_layout.addView(txt_M);
							retval.addView(txt_layout);
						}
						retval.setGravity(Gravity.RIGHT
								| Gravity.CENTER_VERTICAL);
						retval.addView(Profile_layout);
					} else {
						txt_N.setText(txt_Item.getString("m_nickname"));
						if (data.BitMapHash.get(txt_Item.getString("m_photo")) != null) {
							Profile_Pic.setImageBitmap(data.BitMapHash
									.get(txt_Item.getString("m_photo")));
						} else {
							startDownloadNonCache(
									txt_Item.getString("m_photo"), Profile_Pic);
						}
						retval.setGravity(Gravity.LEFT
								| Gravity.CENTER_VERTICAL);
						retval.addView(Profile_layout);
						if (txt_Item.getString("ch_type").contains("S")) {
							if (txt_Item.getString("ch_msg").contains(".gif")) {
								Ion.with(Sticker)
										.placeholder(R.drawable.soccer_icon)
										.load("http://183.90.171.209/chat/stk/"
												+ txt_Item.getString("ch_msg"));
							} else {
								if (data.BitMapHash.get(txt_Item
										.getString("ch_msg")) != null) {
									Sticker.setImageBitmap(data.BitMapHash
											.get(txt_Item.getString("ch_msg")));
								} else {
									Sticker.setImageResource(R.drawable.soccer_icon);
									startDownload(txt_Item.getString("ch_msg"),
											Sticker);
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

	public void Chat_Loader() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					data.socket_Team = new SocketIO("http://183.90.171.209:"
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
							RefreshView();
						}
					}

					@Override
					public void onDisconnect() {

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
								chatHandle();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (event.equals("updateusers")
								&& args.length > 0) {
							String Name_list[] = args[0].toString().split(",");
							for (String Name_item : Name_list) {
								Name_item = Name_item.split(":")[0].replaceAll(
										"\"", "").replaceAll("\\{|\\}", "");
								if (!Name_item.equals(data.ID_Send)) {
								}
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
							chatHandle();
						} else if (event.equals("getsticker")) {
							for (Object object : args) {
								try {
									SessionManager.createNewJsonSession(
											Chat_Team.this, "StickerSet",
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
				data.socket_Team.emit("adduser", data.ID_Send,
						data.ProFile_pic,
						SessionManager.getMember(Chat_Team.this).getNickname());
			}
		}).start();
	}

	public void RefreshView() {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				data.Chat_list_LayOut_Team.removeAllViews();
				TextView RefreshTag = new TextView(mContext);
				RefreshTag.setText("Tap to refresh");
				RefreshTag.setGravity(Gravity.CENTER);
				data.Chat_list_LayOut_Team.addView(RefreshTag);
				data.Chat_list_LayOut_Team
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								data.Chat_list_LayOut_Team.removeAllViews();
								ProgressBar progress = new ProgressBar(mContext);
								data.Chat_list_LayOut_Team.addView(progress);
								(data.Chat_list_LayOut_Team)
										.addView(data.lstViewChatTeam);
								Chat_Loader();
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
				/*
				 * data.imageAdapterChatTeam.notifyDataSetChanged();
				 * data.lstViewChatTeam.setSelection(data.imageAdapterChatTeam
				 * .getCount());
				 * 
				 * if (data.Chat_list_LayOut_Team.getChildCount() > 1) {
				 * data.Chat_list_LayOut_Team.removeViewAt(0); }
				 */
				if (data.Chat_list_LayOut_Team.getChildCount() > 1) {
					data.Chat_list_LayOut_Team.removeViewAt(0);
				}
				data.lstViewChatTeam.setSelection(data.Chat_Item_list_Team
						.size());
			}
		}, data.chatDelay);
	}

	public void chat_Sender() {
		data.chatDelay = 0;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Msg_Send = Msg_Send.replaceAll("'|/|\"|<|>", "");
				if (!Msg_Send.equals("")) {
					data.socket_Team.emit("sendchat", Msg_Send);
					Msg_Send = "";
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
				if (!Msg_Send.equals("")) {
					data.socket_Team.emit("sendsticker", Msg_Send);
					Msg_Send = "";
				}
			}
		}, data.chatDelay);
	}

	public static Bitmap loadImageFromUrl(String url) {
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		Bitmap bitmap = null;

		try {

			final HttpGet getRequest = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 5000;

			httpParameters.setParameter(CoreProtocolPNames.USER_AGENT,
					System.getProperty("http.agent"));
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = httpClient.execute(getRequest);

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
			}

			final HttpEntity entity = response.getEntity();

			i = entity.getContent();// connection.getInputStream();//(InputStream)
									// m.getContent();//

			bis = new BufferedInputStream(i, 1024 * 8);
			out = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = new FlushedInputStream(bis).read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			bis.close();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("err", "Out of memory error :(");
		}
		// double image_size = lenghtOfFile;
		if (out != null) {
			byte[] data = out.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, options);

			double screenWidth = options.outWidth / 2;
			double screenHeight = options.outHeight / 2;

			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inDither = false; // Disable Dithering mode
			options.inPurgeable = true; // Tell to gc that whether it needs free
										// memory, the Bitmap can be cleared
			options.inInputShareable = true; // Which kind of reference will be
												// used to recover the Bitmap
												// data after being clear, when
												// it will be used in the future
			options.inTempStorage = new byte[32 * 1024];
			options.inSampleSize = calculateInSampleSize(options,
					(int) screenWidth, (int) screenHeight);

			options.inJustDecodeBounds = false;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			return bitmap;
		} else {
			return null;
		}

	}

	public void startDownload(final String url, final ImageView img_H) {

		Runnable runnable = new Runnable() {
			public void run() {
				String _Url = "";
				if (url.contains("googleusercontent.com")
						|| url.contains("/gs_member/member_images/")) {
					_Url = url;
				} else {
					_Url = "http://183.90.171.209/chat/stk/" + url;
				}
				Bitmap pic = null;
				if (SessionManager.getImageSession(Chat_Team.this, url) == null) {
					pic = loadImageFromUrl(_Url);
					if (pic != null) {
						SessionManager.createNewImageSession(Chat_Team.this,
								url, pic);
						data.BitMapHash.put(url, pic);
					}
				} else {
					pic = SessionManager.getImageSession(Chat_Team.this, url);
					data.BitMapHash.put(url, pic);
				}
				final Bitmap _pic = pic;

				if (img_H != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (_pic == null) {
								img_H.setImageResource(R.drawable.soccer_icon);
							} else {
								img_H.setImageBitmap(_pic);
							}
						}
					});
				}

			}
		};

		new Thread(runnable).start();
	}

	public void startDownloadNonCache(final String url, final ImageView img_H) {
		Runnable runnable = new Runnable() {
			public void run() {
				String _Url = "";
				if (url.contains("googleusercontent.com")
						|| url.contains("/gs_member/member_images/")) {
					_Url = url;
				} else {
					_Url = "http://183.90.171.209/chat/stk/" + url;
				}
				Bitmap pic = null;
				pic = loadImageFromUrl(_Url);
				if (pic != null) {
					data.BitMapHash.put(url, pic);
				}
				final Bitmap _pic = pic;

				if (img_H != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (_pic == null) {
								img_H.setImageResource(R.drawable.soccer_icon);
							} else {
								img_H.setImageBitmap(_pic);
							}
						}
					});
				}

			}
		};

		new Thread(runnable).start();
	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (data.Menu_Layout != null) {
				if (data.Menu_Layout.getVisibility() == 0) {
					new SideMenuLayout().hideMenu(mContext);
					return false;
				} else {
					if (data.Sticker_Layout_Stat_team) {
						StikerV.setVisibility(RelativeLayout.GONE);
						return false;
					} else {
						return false;
					}
				}
			} else {
				if (data.Sticker_Layout_Stat_team) {
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
