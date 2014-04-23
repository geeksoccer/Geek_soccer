package com.excelente.geek_soccer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.koushikdutta.ion.Ion;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	private static ControllParameter data;
	private Handler handler = new Handler(Looper.getMainLooper());
	static HashMap<String, ImageView> Sticker_ImgVSet = new HashMap<String, ImageView>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this).getTeamId());
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(R.layout.stk_shop_layout, null);
		setContentView(myView);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		
		data = ControllParameter.getInstance(this);
		mContext = this;
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
		
		new stk_list_Loader().execute();
	}
	
	public void Detail_STK_Dialog(int position){
		final Dialog dialog = new Dialog(mContext);
		
		LayoutInflater factory = LayoutInflater.from(this);
		View DialogV = factory.inflate(R.layout.stk_detail_layout, null);
		ImageView Preview_img = (ImageView)DialogV.findViewById(R.id.Preview_Image);
		TextView Stk_name = (TextView)DialogV.findViewById(R.id.stk_name);
		TextView Stk_by = (TextView)DialogV.findViewById(R.id.stk_by);
		TextView Stk_detail = (TextView)DialogV.findViewById(R.id.stk_detail);
		Button but_price = (Button)DialogV.findViewById(R.id.download);
		but_price.setTypeface(Typeface.DEFAULT_BOLD);
		try {
			JSONObject STK_Item = STK_list.get(position);
			JSONArray STK_Item_arr = STK_Item.getJSONArray("data");
			String ImgTxt = STK_Item_arr.getJSONObject(0).getString("sk_img") + "." + STK_Item_arr.getJSONObject(0).getString("sk_type");
			if(ImgTxt.contains(".gif")){
				Ion.with(Preview_img)
					.placeholder(R.drawable.soccer_icon)
					.load("http://183.90.171.209/chat/stk/"+ImgTxt);
			}else{
				if (data.BitMapHash.get(ImgTxt) != null) {
					Preview_img.setImageBitmap(data.BitMapHash
							.get(ImgTxt));
				} else {
					Preview_img.setImageResource(R.drawable.soccer_icon);
					startDownloadNonCache(ImgTxt,
							Preview_img);
				}
			}

			Stk_name.setText(STK_Item.getString("sk_bname"));
			Stk_by.setText("By " + STK_Item.getString("sk_creator_name"));
			Stk_detail.setText(STK_Item.getString("sk_detail"));
			if(STK_Item.getString("sk_price_set").equals("0")){
				but_price.setText("Price: " + "Free");
			}else{
				but_price.setText("Price: " + STK_Item.getString("sk_price_set") + " $");
			}
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
					Ion.with(Sticker_ImgVSet.get(String.valueOf(i+1)))
						.placeholder(R.drawable.soccer_icon)
						.load("http://183.90.171.209/chat/stk/"+ImgTxt);
				}else{
					if (data.BitMapHash.get(ImgTxt) != null) {
						Sticker_ImgVSet.get(String.valueOf(i+1)).setImageBitmap(data.BitMapHash
								.get(ImgTxt));
					} else {
						Sticker_ImgVSet.get(String.valueOf(i+1)).setImageResource(R.drawable.soccer_icon);
						startDownloadNonCache(ImgTxt,
								Sticker_ImgVSet.get(String.valueOf(i+1)));
					}
				}
				
			}
			
			dialog.setTitle(STK_Item.getString("sk_bname"));
			dialog.setContentView(DialogV);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
			return STK_list.size();//+League_list.size();
		}

		public Object getItem(int position) {
			return null;//URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			LinearLayout retval = new LinearLayout(mContext);
			try {
				retval.setOrientation(LinearLayout.HORIZONTAL);
				//retval.setGravity(Gravity.CENTER);
				retval.setPadding(5, 0, 5, 0);
				retval.setMinimumHeight(50);
				JSONObject STK_Item = STK_list.get(position);
				JSONArray STK_Item_arr = STK_Item.getJSONArray("data");
				String ImgTxt = STK_Item_arr.getJSONObject(0).getString("sk_img") + "." + STK_Item_arr.getJSONObject(0).getString("sk_type");
				
				ImageView Sticker = new ImageView(mContext);
				Sticker.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
				
				
				if(ImgTxt.contains(".gif")){
					Ion.with(Sticker)
						.placeholder(R.drawable.soccer_icon)
						.load("http://183.90.171.209/chat/stk/"+ImgTxt);
				}else{
					if (data.BitMapHash.get(ImgTxt) != null) {
						Sticker.setImageBitmap(data.BitMapHash
								.get(ImgTxt));
					} else {
						Sticker.setImageResource(R.drawable.soccer_icon);
						startDownloadNonCache(ImgTxt,
								Sticker);
					}
				}
				
				LinearLayout Detail_Layout = new LinearLayout(mContext);
				Detail_Layout.setOrientation(LinearLayout.VERTICAL);
				Detail_Layout.setPadding(5, 5, 5, 5);
				
				int colors = Integer.parseInt("000000", 16) + (0xFF000000);
				TextView txt_name = new TextView(mContext);
				txt_name.setTypeface(Typeface.DEFAULT_BOLD);
				txt_name.setText(STK_Item.getString("sk_bname"));
				txt_name.setTextColor(colors);
				
				TextView txt_artist_name = new TextView(mContext);
				txt_artist_name.setTypeface(Typeface.DEFAULT_BOLD);
				txt_artist_name.setText("By " + STK_Item.getString("sk_creator_name"));
				txt_artist_name.setTextColor(colors);
				
				TextView txt_price = new TextView(mContext);
				txt_price.setTypeface(Typeface.DEFAULT_BOLD);
				if(STK_Item.getString("sk_price_set").equals("0")){
					txt_price.setText("Price: " + "Free");
				}else{
					txt_price.setText("Price: " + STK_Item.getString("sk_price_set") + " $");
				}
				
				txt_price.setTextColor(colors);
				
				Detail_Layout.addView(txt_name);
				Detail_Layout.addView(txt_artist_name);
				Detail_Layout.addView(txt_price);
				
				LinearLayout arrow_layout = new LinearLayout(mContext);
				arrow_layout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
				arrow_layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				ImageView img = new ImageView(mContext);
				img.setImageResource(R.drawable.arrow_btn);
				img.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
				arrow_layout.addView(img);
				
				retval.addView(Sticker);
				retval.addView(Detail_Layout);
				retval.addView(arrow_layout);
				
				if(position%2==0){
					retval.setBackgroundColor(Color.GRAY);
					retval.getBackground().setAlpha(200);
				}
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
						.makeHttpRequest("http://183.90.171.209/gs_stk_permission/get_stk_list.php",
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
	
	public static Bitmap loadImageFromUrl(String url) {
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		Bitmap bitmap=null;
		
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

			double screenWidth = options.outWidth/2;
			double screenHeight = options.outHeight/2;

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
		}else{
			return null;
		}
		
	}
	
	public void startDownloadNonCache(final String url, final ImageView img_H) {
		Runnable runnable = new Runnable() {
			public void run() {
				String _Url = "";
				if (url.contains("googleusercontent.com") || url.contains("/gs_member/member_images/")) {
					_Url = url;
				} else {
					_Url = "http://183.90.171.209/chat/stk/" + url;
				}
				Bitmap pic = null;
				pic = loadImageFromUrl(_Url);
				if(pic!=null){
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
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		finish();
	}
}
