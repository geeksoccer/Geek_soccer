package com.excelente.geek_soccer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.pic_download.DownChatPic;
import com.excelente.geek_soccer.purchase_pack.PuchaseProcessDialog;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class STKShop_Page extends Activity{
	
	Context mContext;
	private ListView lstView;
	private ImageAdapter imageAdapter;
	Boolean loading = false;
	Boolean FirstLoad = true;
	ArrayList<JSONObject> STK_list = new ArrayList<JSONObject>();
	JSONParser jParser = new JSONParser();
	JSONParser jParser_permission = new JSONParser();
	private static ControllParameter data;
	private Handler handler = new Handler(Looper.getMainLooper());
	static HashMap<String, ImageView> Sticker_ImgVSet = new HashMap<String, ImageView>();
	ArrayList<String> STK_exist_list = new ArrayList<String>();
	String StickJset;
	
	ImageView ProfileImg;
	public static final int MAX_IMAGE = 512;
	LinearLayout but_price;
	TextView but_priceTxt;
	Button but_delete;
	ImageView coinImgDialog;
	ProgressBar down_progress;
	String saveModeGet;
	private RelativeLayout Header_Layout;
	private TextView titleBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PuchaseProcessDialog.StartSetup(this);
		
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(R.layout.stk_shop_layout, null);
		setContentView(myView);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		
		LinearLayout Up_btn = (LinearLayout)myView.findViewById(R.id.Up_btn);
		Up_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		data = ControllParameter.getInstance(this);
		mContext = this;
		saveModeGet = SessionManager.getSetting(mContext,
				SessionManager.setting_save_mode);
		StickJset = SessionManager.getJsonSession(mContext, "StickerSet");
		if(StickJset!=null){
			JSONObject json_ob;
			try {
				json_ob = new JSONObject(StickJset);
				for (Iterator<?> league_Item_key = json_ob
						.keys(); league_Item_key.hasNext();) {
					String key_Item = (String) league_Item_key
							.next();
					STK_exist_list.add(key_Item);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		ProfileImg = (ImageView)myView.findViewById(R.id.ProfileImg);
		Bitmap bitmapPhoto = ControllParameter.ProFileCache;
		if(bitmapPhoto!=null){
			ProfileImg.setImageBitmap(resizeBitMap(bitmapPhoto));
		}else{
			ProfileImg.setImageResource(R.drawable.soccer_icon);
		}
		
		Button CoinChargeBtn = (Button)myView.findViewById(R.id.CoinCharge);
		CoinChargeBtn.setVisibility(RelativeLayout.GONE);
		CoinChargeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PuchaseProcessDialog.BuyCoinsDialog(mContext);
			}
		});
		
		lstView = new ListView(mContext);
		lstView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		lstView.setClipToPadding(false);
		imageAdapter = new ImageAdapter(mContext.getApplicationContext());
		lstView.setAdapter(imageAdapter);
		
		lstView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Detail_STK_Dialog(position);
			}
		});
		
		Header_Layout = (RelativeLayout) myView.findViewById(R.id.Header_Layout);
		titleBar = (TextView) myView.findViewById(R.id.textView1);
		setThemetoView();
		new stk_list_Loader().execute();
	}
	
	private void setThemetoView() {
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, Header_Layout);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, titleBar);
	}
	
	private Bitmap resizeBitMap(Bitmap bitmapPhoto) {
		
		float scale = (bitmapPhoto.getWidth()*1.0f)/(1.0f*bitmapPhoto.getHeight());
		int width = MAX_IMAGE;
		int height = MAX_IMAGE;
		if(width > height){
			height = (int) (height * scale);
		}else{
			width = (int) (width * scale);
		}
		bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, width, height, false);
		return bitmapPhoto;
	}
	
	public void Detail_STK_Dialog(final int position){
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		LayoutInflater factory = LayoutInflater.from(this);
		View DialogV = factory.inflate(R.layout.stk_detail_layout, null);
		dialog.setContentView(DialogV);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		RelativeLayout main_action_bar = (RelativeLayout) DialogV.findViewById(R.id.main_action_bar);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, main_action_bar);
		
		TextView dialog_title = (TextView) DialogV.findViewById(R.id.dialog_title);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, dialog_title);
		
		ImageView Preview_img = (ImageView)DialogV.findViewById(R.id.Preview_Image);
		TextView Stk_name = (TextView)DialogV.findViewById(R.id.stk_name);
		TextView Stk_by = (TextView)DialogV.findViewById(R.id.stk_by);
		TextView Stk_detail = (TextView)DialogV.findViewById(R.id.stk_detail);
		ImageView closeBt = (ImageView) DialogV.findViewById(R.id.close_icon);
		closeBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		}); 
		but_price = (LinearLayout)DialogV.findViewById(R.id.download);
		but_priceTxt = (TextView)DialogV.findViewById(R.id.downloadtxt);
		coinImgDialog = (ImageView)DialogV.findViewById(R.id.imgCoin);
		but_delete = (Button)DialogV.findViewById(R.id.remove);
		down_progress = (ProgressBar)DialogV.findViewById(R.id.download_progress);		
		down_progress.setVisibility(RelativeLayout.GONE);
		but_priceTxt.setTypeface(Typeface.DEFAULT_BOLD);
		try {
			final JSONObject STK_Item = STK_list.get(position);
			JSONArray STK_Item_arr = STK_Item.getJSONArray("data");
			String ImgTxt = STK_Item.getString("sk_preview");
			if(ImgTxt.contains(".gif")){
				new DownChatPic().startDownloadGIFCache(mContext
						, ControllParameter.CHAT_STK_URL+ImgTxt
						, Preview_img);
			}else{
				if (data.BitMapHash.get(ImgTxt) != null) {
					Preview_img.setImageBitmap(data.BitMapHash
							.get(ImgTxt));
				} else {
					Preview_img.setImageResource(R.drawable.soccer_icon);
					new DownChatPic().startDownloadNonCache(
							mContext, ImgTxt, Preview_img, saveModeGet, data);
				}
			}
			Stk_name.setText(STK_Item.getString("sk_bname"));
			Stk_name.setTextSize(16);
			Stk_by.setText("By " + STK_Item.getString("sk_creator_name"));
			Stk_by.setTextSize(10);
			Stk_detail.setText(STK_Item.getString("sk_detail"));
			if(STK_exist_list.contains(STK_Item.getString("sk_bid"))){
				coinImgDialog.setVisibility(RelativeLayout.GONE);
				but_priceTxt.setText(" Downloaded ");
				but_price.setEnabled(false);
				but_priceTxt.setTextColor(Color.GREEN);
			}else{
				but_delete.setVisibility(RelativeLayout.GONE);
				but_priceTxt.setTextColor(Color.RED);
				if(STK_Item.getString("sk_price_set").equals("0")){
					coinImgDialog.setVisibility(RelativeLayout.GONE);
					but_priceTxt.setText(" Free ");
				}else{
					but_priceTxt.setText(" " + STK_Item.getString("sk_price_set"));
				}
			}
			but_price.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						down_progress.setVisibility(RelativeLayout.ABOVE);
						but_priceTxt.setText(" Downloading... ");
						coinImgDialog.setVisibility(RelativeLayout.GONE);
						but_price.setEnabled(false);
						new stk_permission_Request().execute(STK_Item.getString("sk_bid"), String.valueOf(position));					
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			but_delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
						down_progress.setVisibility(RelativeLayout.ABOVE);
						but_priceTxt.setText(" Removing... ");
						but_price.setEnabled(false);
						new stk_delete_Request().execute(STK_Item.getString("sk_bid"), String.valueOf(position));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			ImageView Stick_1 = (ImageView) DialogV.findViewById(R.id.stic_1);
			ImageView Stick_2 = (ImageView) DialogV.findViewById(R.id.stic_2);
			ImageView Stick_3 = (ImageView) DialogV.findViewById(R.id.stic_3);
			ImageView Stick_4 = (ImageView) DialogV.findViewById(R.id.stic_4);
			ImageView Stick_5 = (ImageView) DialogV.findViewById(R.id.stic_5);
			ImageView Stick_6 = (ImageView) DialogV.findViewById(R.id.stic_6);
			ImageView Stick_7 = (ImageView) DialogV.findViewById(R.id.stic_7);
			ImageView Stick_8 = (ImageView) DialogV.findViewById(R.id.stic_8);
			ImageView Stick_9 = (ImageView) DialogV.findViewById(R.id.stic_9);
			ImageView Stick_10 = (ImageView) DialogV.findViewById(R.id.stic_10);
			ImageView Stick_11 = (ImageView) DialogV.findViewById(R.id.stic_11);
			ImageView Stick_12 = (ImageView) DialogV.findViewById(R.id.stic_12);
			
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
			
			for(int i=0; i<STK_Item_arr.length(); i++){
				ImgTxt = STK_Item_arr.getJSONObject(i).getString("sk_img") + "." + STK_Item_arr.getJSONObject(i).getString("sk_type");
				if(ImgTxt.contains(".gif")){
					new DownChatPic().startDownloadGIFCache(mContext
							, ControllParameter.CHAT_STK_URL+ImgTxt
							, Sticker_ImgVSet.get(String.valueOf(i+1)));
				}else{
					if (data.BitMapHash.get(ImgTxt) != null) {
						Sticker_ImgVSet.get(String.valueOf(i+1)).setImageBitmap(data.BitMapHash
								.get(ImgTxt));
					} else {
						Sticker_ImgVSet.get(String.valueOf(i+1)).setImageResource(R.drawable.soccer_icon);
						new DownChatPic().startDownloadNonCache(
								mContext, ImgTxt, Sticker_ImgVSet.get(String.valueOf(i+1)), saveModeGet, data);
					}
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		dialog.show();
	}
	
	
	
	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return STK_list.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			LinearLayout retval = new LinearLayout(mContext);
			try {
				retval.setOrientation(LinearLayout.HORIZONTAL);
				retval.setGravity(Gravity.CENTER);
				retval.setMinimumHeight(50);
				
				LinearLayout bg = new LinearLayout(mContext);
				bg.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				bg.setGravity(Gravity.CENTER_VERTICAL);
				bg.setPadding(5, 0, 5, 0);
				bg.setBackgroundResource(R.drawable.card_background_white);
				bg.getBackground().setAlpha(200);
				
				JSONObject STK_Item = STK_list.get(position);
				String ImgTxt = STK_Item.getString("sk_preview");
				
				ImageView Sticker = new ImageView(mContext);
				Sticker.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
				
				
				if(ImgTxt.contains(".gif")){
					new DownChatPic().startDownloadGIFCache(mContext
							, ControllParameter.CHAT_STK_URL+ImgTxt
							, Sticker);
				}else{
					if (data.BitMapHash.get(ImgTxt) != null) {
						Sticker.setImageBitmap(data.BitMapHash
								.get(ImgTxt));
					} else {
						Sticker.setImageResource(R.drawable.soccer_icon);
						new DownChatPic().startDownloadNonCache(
								mContext, ImgTxt, Sticker, saveModeGet, data);
					}
				}
				
				LinearLayout Detail_Layout = new LinearLayout(mContext);
				Detail_Layout.setOrientation(LinearLayout.VERTICAL);
				Detail_Layout.setPadding(5, 5, 5, 5);
				
				int colors = Integer.parseInt("000000", 16) + (0xFF000000);
				TextView txt_name = new TextView(mContext);
				txt_name.setTypeface(Typeface.DEFAULT_BOLD);
				txt_name.setText(STK_Item.getString("sk_bname"));
				txt_name.setTextSize(16);
				txt_name.setTextColor(colors);
				
				TextView txt_artist_name = new TextView(mContext);
				txt_artist_name.setTextSize(10);
				txt_artist_name.setTypeface(Typeface.DEFAULT_BOLD);
				txt_artist_name.setText("By " + STK_Item.getString("sk_creator_name"));
				txt_artist_name.setTextColor(colors);
				
				LinearLayout price_Layout = new LinearLayout(mContext);
				price_Layout.setOrientation(LinearLayout.HORIZONTAL);
				
				TextView txt_price = new TextView(mContext);
				txt_price.setTypeface(Typeface.DEFAULT_BOLD);
				
				price_Layout.addView(txt_price);
				
				Detail_Layout.addView(txt_name);
				Detail_Layout.addView(txt_artist_name);
				Detail_Layout.addView(price_Layout);
				
				if(STK_exist_list.contains(STK_Item.getString("sk_bid"))){
					txt_price.setTextColor(Color.GREEN);
					txt_price.setText("Downloaded");
				}else{
					txt_price.setTextColor(Color.RED);
					if(STK_Item.getString("sk_price_set").equals("0")){
						txt_price.setText("Price: " + "Free");
					}else{
						txt_price.setText("Price: " + STK_Item.getString("sk_price_set"));
						ImageView coinImg = new ImageView(mContext);
						coinImg.setLayoutParams(new LinearLayout.LayoutParams(GetdipSize.dip(mContext, 20), GetdipSize.dip(mContext, 20)));
						coinImg.setImageResource(R.drawable.coin);
						price_Layout.addView(coinImg);
					}
				}
				
				
				LinearLayout arrow_layout = new LinearLayout(mContext);
				arrow_layout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
				arrow_layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				ImageView img = new ImageView(mContext);
				img.setImageResource(R.drawable.arrow_btn);
				img.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
				arrow_layout.addView(img);
				
				bg.addView(Sticker);
				bg.addView(Detail_Layout);
				bg.addView(arrow_layout);
				retval.addView(bg);
				/*
				if(position%2==0){
					retval.setBackgroundColor(Color.GRAY);
					retval.getBackground().setAlpha(200);
				}
				*/
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retval;
		}

	}

	class stk_list_Loader extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				loading = true;
				STK_list.clear();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				JSONObject json = jParser
						.makeHttpRequest(ControllParameter.GET_STK_PER_LIST,
								"POST", params);
				Log.d("TEST", "json_dtArr::"+json);
				if (json != null) {
					JSONArray json_arr = json.getJSONArray("set_stk");
					for (int i = 0; i < json_arr.length(); i++) {
						STK_list.add(json_arr.getJSONObject(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(String file_url) {
			//pDialog.dismiss();
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					if(FirstLoad){
						LinearLayout list_layout = (LinearLayout)findViewById(R.id.list_player_Detail);					
						list_layout.removeAllViews();
						list_layout.addView(lstView);
						FirstLoad = false;
					}else{
						imageAdapter.notifyDataSetChanged();
					}
					loading = false;
				}
			});
		}
	}
	
	class stk_permission_Request extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", String.valueOf(SessionManager.getMember(mContext).getUid()) ));
				params.add(new BasicNameValuePair("stk_id", args[0] ));
				params.add(new BasicNameValuePair("token",
						md5Digest(String.valueOf(SessionManager.getMember(mContext).getUid())+args[0]+"acpt46") ));
				JSONObject json_permission = jParser_permission
						.makeHttpRequest(ControllParameter.SET_STK_PERMIT_ONE,
								"POST", params);
				if (json_permission != null) {
					final JSONObject STK_Item = STK_list.get(Integer.parseInt(args[1]));
					JSONArray stk_request = STK_Item.getJSONArray("data");
					JSONArray stk_requestNew = new JSONArray();
					
					for(int i=0; i<stk_request.length(); i++){
						JSONObject jsonOb_New = new JSONObject();
						jsonOb_New.put("sk_img"
								, stk_request.getJSONObject(i).getString("sk_img")+"."+stk_request.getJSONObject(i).getString("sk_type"));
						jsonOb_New.put("sk_id", stk_request.getJSONObject(i).getString("sk_id"));
						stk_requestNew.put(jsonOb_New);
					}
					
					JSONObject json_ob = null;
					try {
						json_ob = new JSONObject(StickJset);
						json_ob.put(args[0], stk_requestNew);
						SessionManager.createNewJsonSession(mContext,
								"StickerSet", json_ob.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(String file_url) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					StickJset = SessionManager.getJsonSession(mContext, "StickerSet");
					int Size_update_chk = STK_exist_list.size();
					STK_exist_list.clear();
					if(StickJset!=null){
						JSONObject json_ob;
						try {
							json_ob = new JSONObject(StickJset);
							for (Iterator<?> league_Item_key = json_ob
									.keys(); league_Item_key.hasNext();) {
								String key_Item = (String) league_Item_key
										.next();
								STK_exist_list.add(key_Item);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					down_progress.setVisibility(RelativeLayout.GONE);
					but_priceTxt.setTextColor(Color.GREEN);
					if(Size_update_chk!=STK_exist_list.size()){
						but_priceTxt.setText(" Downloaded ");
						but_delete.setVisibility(RelativeLayout.ABOVE);
					}else{
						but_priceTxt.setText(" Download fail tap to try again.. ");
						but_price.setEnabled(true);
					}
					imageAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	class stk_delete_Request extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", String.valueOf(SessionManager.getMember(mContext).getUid()) ));
				params.add(new BasicNameValuePair("stk_id", args[0] ));
				params.add(new BasicNameValuePair("token",
						md5Digest(String.valueOf(SessionManager.getMember(mContext).getUid())+args[0]+"acpt46") ));
				JSONObject json_permission = jParser_permission
						.makeHttpRequest(ControllParameter.DEL_STK_PERMIT_ONE,
								"POST", params);
				Log.d("TEST", "json_permission::"+json_permission);
				if (json_permission != null) {
					JSONObject json_ob = null;
					try {
						json_ob = new JSONObject(StickJset);
						json_ob.remove(args[0]);
						SessionManager.createNewJsonSession(mContext,
								"StickerSet", json_ob.toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return args[1];
		}
		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(final String position) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					StickJset = SessionManager.getJsonSession(mContext, "StickerSet");
					int Size_update_chk = STK_exist_list.size();
					STK_exist_list.clear();
					if(StickJset!=null){
						JSONObject json_ob;
						try {
							json_ob = new JSONObject(StickJset);
							for (Iterator<?> league_Item_key = json_ob
									.keys(); league_Item_key.hasNext();) {
								String key_Item = (String) league_Item_key
										.next();
								STK_exist_list.add(key_Item);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					down_progress.setVisibility(RelativeLayout.GONE);
					but_priceTxt.setTextColor(Color.GREEN);
					if(Size_update_chk!=STK_exist_list.size()){
						but_delete.setVisibility(RelativeLayout.GONE);
						but_priceTxt.setText(" Remove Success ");
						but_price.setEnabled(false);
						data.chatDelay = 2000;
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									final JSONObject STK_Item = STK_list.get(Integer.parseInt(position) );
									
									if(STK_Item.getString("sk_price_set").equals("0")){
										but_priceTxt.setText(" Free ");
									}else{
										coinImgDialog.setVisibility(RelativeLayout.ABOVE);
										but_priceTxt.setText(" " + STK_Item.getString("sk_price_set"));
									}
									but_priceTxt.setTextColor(Color.RED);
									but_price.setEnabled(true);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}								
							}
						}, data.chatDelay);
					}else{
						but_priceTxt.setText(" Remove fail tap to try again.. ");
						but_price.setEnabled(true);
					}
					imageAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	public static final String md5Digest(final String text)
	{
	     try
	     {
	           // Create MD5 Hash
	           MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	           digest.update(text.getBytes());
	           byte messageDigest[] = digest.digest();

	           // Create Hex String
	           StringBuffer hexString = new StringBuffer();
	           int messageDigestLenght = messageDigest.length;
	           for (int i = 0; i < messageDigestLenght; i++)
	           {
	                String hashedData = Integer.toHexString(0xFF & messageDigest[i]);
	                while (hashedData.length() < 2)
	                     hashedData = "0" + hashedData;
	                hexString.append(hashedData);
	           }
	           return hexString.toString();

	     } catch (NoSuchAlgorithmException e)
	     {
	           e.printStackTrace();
	     }
	     return ""; // if text is null then return nothing
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (PuchaseProcessDialog.isSetup) {
			boolean blnResult = PuchaseProcessDialog.mHelper.handleActivityResult(requestCode, resultCode, data);
			Log.i(PuchaseProcessDialog.tag, "onActivityResult() - mHelper.handleActivityResult() = " + blnResult);
 
			if (blnResult) return;
		}
 
		super.onActivityResult(requestCode, resultCode, data);
	}
 
	@Override
	protected void onDestroy() {
		if (PuchaseProcessDialog.isSetup) PuchaseProcessDialog.mHelper.dispose();
		PuchaseProcessDialog.mHelper = null;
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		if(data.socket_Team != null) {
			if (data.socket_Team.isConnected()) {
				data.socket_Team.emit("adduser", data.ID_Send,
						data.ProFile_pic,
						SessionManager.getMember(mContext).getNickname());
				
			}
		}
		
		if(data.socket_All != null) {
			if (data.socket_All.isConnected()) {
				data.socket_All.emit("adduser", data.ID_Send,
						data.ProFile_pic,
						SessionManager.getMember(mContext).getNickname());
			}
		}
		finish();
	}
}
