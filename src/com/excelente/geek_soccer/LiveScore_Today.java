package com.excelente.geek_soccer;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LiveScore_Today extends Activity {
	
	Context mContext;
	JSONParser jParser = new JSONParser();
	JSONObject products = null;
	ArrayList<String> item_Type_list = new ArrayList<String>();
	ArrayList<String> URL_list = new ArrayList<String>();
	ArrayList<String> Match_list = new ArrayList<String>();
	ArrayList<String> Match_list_Sub = new ArrayList<String>();
	HashMap<String, String> League_Map = new HashMap<String, String>();
	HashMap<String, String> League_Map_index = new HashMap<String, String>();
	private ListView lstView;
	private ImageAdapter imageAdapter;
	View myView;
	int dd, yy, mm;
	// String Date_Select;
	String old_date;
	String Cur_Date;
	private static ControllParameter data = ControllParameter.getInstance();
	TextView Date_txt;
	String Last_League_SET = "";
	Integer count_Item = 0;
	static Bitmap bitmap;
	HashMap<String, Bitmap> HomeMap = new HashMap<String, Bitmap>();
	HashMap<String, Bitmap> AwayMap = new HashMap<String, Bitmap>();
	private Handler handler = new Handler();
	Boolean checkScrolling = true;
	Boolean chk_ani = true;
	int last_ItemView = 0;
	LinearLayout layOutlist;
	Boolean chk_D_Stat=false;
	int chk_loaded = 0;
	
	//SocketIO socket = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livescore_today);
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
				if(data.Match_list_c.get(position).contains("\n")){
					Intent Detail_Page = new Intent(mContext,
							Live_Score_Detail.class);
					Detail_Page.putExtra("URL", position);
					Detail_Page.putExtra("TYPE", "c");
					startActivity(Detail_Page);
					
				}
				
			}
		});
		if(data.socket_LiveScore!=null){
			if(data.socket_LiveScore.isConnected()){
				data.socket_LiveScore.disconnect();
				Live_score_Loader();
			}
		}

		if(data.Match_list_c.size()>0){
			layOutlist = (LinearLayout) findViewById(R.id.List_Layout);
			layOutlist.removeAllViews();
			((LinearLayout) layOutlist).addView(lstView);
			chk_ani = false;
			imageAdapter.notifyDataSetChanged();
		}else{
			new Live_score_1stLoader().execute();
		}
    }

    class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return data.Match_list_c.size();// +League_list.size();
		}

		public Object getItem(int position) {
			return null;// URL_News_text.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LinearLayout retval = new LinearLayout(mContext);
			retval.setOrientation(LinearLayout.VERTICAL);

			retval.setMinimumHeight(50);
			String txt_Item = "false";
			if(data.Match_list_c.size()-1>=position){
				txt_Item = data.Match_list_c.get(position);
			}

			int colors = Integer.parseInt("000000", 16) + (0xFF000000);
			TextView txt = new TextView(mContext);
			txt.setTextColor(colors);
			
			if (txt_Item.contains("\n")) {
				txt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				txt.setGravity(Gravity.CENTER);
				String text_Sprite[] = txt_Item.split("\n");

				LinearLayout layOut_Detail = new LinearLayout(mContext);
				layOut_Detail.setOrientation(LinearLayout.HORIZONTAL);
				
				txt.setTextSize(14);
				txt.setText(" "+text_Sprite[1].substring(3));
				txt.setGravity(Gravity.LEFT);

				TextView txt_Home = new TextView(mContext);
				txt_Home.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1f));
				txt_Home.setTextSize(16);
				txt_Home.setText(text_Sprite[3]);
				txt_Home.setTextColor(Color.DKGRAY);
				

				TextView txt_Score = new TextView(mContext);
				txt_Score.setLayoutParams(new LayoutParams(70, 40));
				txt_Score.setTextSize(14);
				txt_Score.setText(text_Sprite[4].replaceAll("&nbsp;", " "));
				txt_Score.setGravity(Gravity.CENTER);
				txt_Score.setBackgroundResource(R.drawable.score_bg_layer);
				if(!text_Sprite[4].replaceAll("&nbsp;", " ").equals("vs")){
					txt_Score.setTextColor(Color.CYAN);
				}

				TextView txt_Away = new TextView(mContext);
				txt_Away.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1f));
				txt_Away.setTextSize(16);
				txt_Away.setGravity(Gravity.RIGHT);
				txt_Away.setText(text_Sprite[5]);
				txt_Away.setTextColor(Color.DKGRAY);
				
				final ImageView image_Home = new ImageView(mContext);
				image_Home.setLayoutParams(new LayoutParams(50, 50));
				final ImageView image_Away = new ImageView(mContext);
				image_Away.setLayoutParams(new LayoutParams(50, 50));
				if (data.get_HomeMap(text_Sprite[6]) != null) {
					image_Home.setImageBitmap(data.get_HomeMap(text_Sprite[6]));
				} else {
					image_Home.setImageResource(R.drawable.soccer_icon);
					startDownload_Home(position, image_Home);
				}
				if (data.get_AwayMap(text_Sprite[7]) != null) {
					image_Away.setImageBitmap(data.get_AwayMap(text_Sprite[7]));
				} else {
					image_Away.setImageResource(R.drawable.soccer_icon);
					startDownload_Away(position, image_Away);
				}
				
				LinearLayout layOut_1 = new LinearLayout(mContext);
				layOut_1.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1f));
				layOut_1.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				layOut_1.setOrientation(LinearLayout.HORIZONTAL);

				LinearLayout layOut_2 = new LinearLayout(mContext);
				layOut_2.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				layOut_2.setGravity(Gravity.CENTER);

				LinearLayout layOut_3 = new LinearLayout(mContext);
				layOut_3.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1f));
				layOut_3.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				layOut_3.setOrientation(LinearLayout.HORIZONTAL);

				layOut_1.setPadding(5, 0, 5, 0);
				image_Home.setPadding(5, 0, 5, 0);
				txt_Home.setPadding(5, 0, 5, 0);
				layOut_1.addView(image_Home);
				layOut_1.addView(txt_Home);
				
				layOut_2.setPadding(5, 0, 5, 0);
				txt_Score.setPadding(5, 0, 5, 0);
				layOut_2.addView(txt_Score);
				
				layOut_3.setPadding(5, 0, 5, 0);
				txt_Away.setPadding(5, 0, 5, 0);
				image_Away.setPadding(5, 0, 5, 0);
				layOut_3.addView(txt_Away);
				layOut_3.addView(image_Away);
				
				LinearLayout layOut_time = new LinearLayout(mContext);
				layOut_time.setOrientation(LinearLayout.HORIZONTAL);
				layOut_time.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				layOut_time.addView(txt);

				if(text_Sprite.length>9){
					TextView txt_Aggregate = new TextView(mContext);
					txt_Aggregate.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));
					txt_Aggregate.setTextSize(14);
					txt_Aggregate.setGravity(Gravity.RIGHT);
					txt_Aggregate.setText("AGGREGATE:"+text_Sprite[9]+" ");
					layOut_time.addView(txt_Aggregate);
				}
				
				layOut_Detail.addView(layOut_1);
				layOut_Detail.addView(layOut_2);
				layOut_Detail.addView(layOut_3);

				retval.addView(layOut_time);
				retval.addView(layOut_Detail);
				if (chk_ani && last_ItemView - 1 < position) {
					layOut_time.setAnimation(AnimationUtils.loadAnimation(
							mContext, R.drawable.listview_anim));
					layOut_Detail.setAnimation(AnimationUtils.loadAnimation(
							mContext, R.drawable.listview_anim));
				}
				retval.setBackgroundColor(Color.GRAY);
				retval.getBackground().setAlpha(200);
			} else {
				txt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				txt.setGravity(Gravity.CENTER);
				txt.setText(txt_Item.substring(txt_Item.lastIndexOf("]") + 1)
						.replaceAll("&lrm;", " "));
				txt.setTextSize(24);
				retval.addView(txt);
				if (chk_ani && last_ItemView - 1 < position) {
					txt.setAnimation(AnimationUtils.loadAnimation(mContext,
							R.drawable.listview_anim));
				}
				retval.setBackgroundColor(Color.DKGRAY);
				retval.getBackground().setAlpha(200);
			}

			return retval;

		}

	}
	
	class Live_score_1stLoader extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(c_page.this);
			 * pDialog.setMessage("Preparing content data. Please wait...");
			 * pDialog.setIndeterminate(true); pDialog.setCancelable(false);
			 * pDialog.show();
			 */
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("_k",
						"592ffdb05020001222c7d024479b028d"));
				params.add(new BasicNameValuePair("_t",
						"get-livescore"));
				params.add(new BasicNameValuePair("_u",
						String.valueOf(MemberSession.getMember().getUid())));
				params.add(new BasicNameValuePair("_d", "c" ));
				
				JSONObject json = jParser
						.makeHttpRequest("http://183.90.171.209//get-livescore/ajax/goal-livescore.php",
								"POST", params);
				
				if (json != null) {
					data.Match_list_c.clear();
					JSONObject json_ob = json;

					JSONArray json_itArr = json_ob.getJSONArray("it");
					for(int i=0; i<json_itArr.length(); i++){
						JSONObject json_it = json_itArr.getJSONObject(i);
						String League = json_it.getString("ln");
						if(League.contains("(ENG)")){
							League = "[1]"+League;
						}else if(League.contains("(ESP)")){
							League = "[2]"+League;
						}else if(League.contains("(GER)")){
							League = "[3]"+League;
						}else if(League.contains("(ITA)")){
							League = "[4]"+League;
						}else if(League.contains("(FRA)")){
							League = "[5]"+League;
						}
						data.Match_list_c.add(League);
						JSONArray json_dtArr = json_it.getJSONArray("dt");
						
						for(int j=0; j<json_dtArr.length(); j++){
							JSONObject json_dt = json_dtArr.getJSONObject(j);
							String Home  = json_dt.getString("ht");
							String Home_img  = json_dt.getString("hl");
							String away  = json_dt.getString("at");
							String away_img  = json_dt.getString("al");
							String link = json_dt.getString("lk");
							String Time = "";//json_dt.getString("tp");
							
							if(json_dt.getString("ty").equals("playing")){
								if(json_dt.getString("pr").equals("ht")){
									Time = "Half Time";
								}else{
									Time = json_dt.getString("tc")+"'";
								}
							}else if(json_dt.getString("ty").equals("played")){
								Time = "Full Time";
							}else if(json_dt.getString("ty").equals("postponed")){
								Time = json_dt.getString("ty");
							}else if(json_dt.getString("ty").equals("fixture")){
								Time = json_dt.getString("tp");
							}else{
								Time = json_dt.getString("tp");
							}
							
							String stat = "[no]";
							String score = json_dt.getString("sc");
							String score_ag = json_dt.getString("ag");
							if(score.equals("")){
								score = "vs";
							}
							if(score_ag==null){
								score_ag="";
							}
							if (away.contains("Arsenal")
									|| Home.contains("Arsenal")) {
								League = "Tag";
								data.Match_list_c.add("[0]" + "Your Team");
								data.Match_list_c.add("[0]" + "Your Team"
										+ "\n" + "[0]" + Time
										+ "\n" + stat + "\n" + Home
										+ "\n" + score + "\n"
										+ away + "\n" + Home_img
										+ "\n" +away_img + "\n" +link+"\n"+score_ag);
							} else {
								data.Match_list_c.add(League + "\n"
										+ "[1]" + Time + "\n"
										+ stat + "\n" + Home + "\n"
										+ score + "\n" + away + "\n" + Home_img
										+ "\n" +away_img + "\n" +link+"\n"+score_ag);
							}
						}
					}
					Collections.sort(data.Match_list_c, new Comparator<String>() {
						@Override
						public int compare(String s1, String s2) {
							return s1.compareToIgnoreCase(s2);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					chk_D_Stat=false;
					layOutlist = (LinearLayout) findViewById(R.id.List_Layout);
					layOutlist.removeAllViews();
					((LinearLayout) layOutlist).addView(lstView);
					chk_ani = false;
					imageAdapter.notifyDataSetChanged();
					
					if(data.Date_Select.equals("c")){
						if(data.liveScore_on==null){
							Live_score_Loader();
						}else{
							if(!data.liveScore_on){
								Live_score_Loader();
							}
						}
					}
				}
			});
		}
	}
	
	public void Live_score_Loader() {

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					data.socket_LiveScore = new SocketIO("http://183.90.171.209:5070");
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				data.socket_LiveScore.connect(new IOCallback() {
		            @Override
		            public void onMessage(JSONObject json, IOAcknowledge ack) {
		                try {
		                	Log.d("TEST","test::Server said:" + json.toString(2));
		                } catch (JSONException e) {
		                    e.printStackTrace();
		                }
		            }

		            @Override
		            public void onMessage(String data, IOAcknowledge ack) {
		            	Log.d("TEST","test::Server said: " + data);
		            }

		            @Override
		            public void onError(SocketIOException socketIOException) {
		            	Log.d("TEST","test::an Error occured");
		                socketIOException.printStackTrace();
		            }

		            @Override
		            public void onDisconnect() {
		            	
		            	data.liveScore_on = false;
		            	Log.d("TEST", "chat_on::"+data.liveScore_on);
		            }

		            @Override
		            public void onConnect() {
		            	
		            	data.liveScore_on = true;
		            	Log.d("TEST", "chat_on::"+data.liveScore_on);
		            }

		            @Override
		            public void on(String event, IOAcknowledge ack, Object... args) {
		            	
		            	if (event.equals("update-score")) {
		            		data.Match_list_c.clear();
		            		
		            		for (Object object : args) {
		                		try {
									JSONObject json_ob = new JSONObject(object.toString());

									JSONArray json_itArr = json_ob.getJSONArray("it");
									for(int i=0; i<json_itArr.length(); i++){
										JSONObject json_it = json_itArr.getJSONObject(i);
										String League = json_it.getString("ln");
										if(League.contains("(ENG)")){
											League = "[1]"+League;
										}else if(League.contains("(ESP)")){
											League = "[2]"+League;
										}else if(League.contains("(GER)")){
											League = "[3]"+League;
										}else if(League.contains("(ITA)")){
											League = "[4]"+League;
										}else if(League.contains("(FRA)")){
											League = "[5]"+League;
										}
										data.Match_list_c.add(League);
										JSONArray json_dtArr = json_it.getJSONArray("dt");
										
										for(int j=0; j<json_dtArr.length(); j++){
											JSONObject json_dt = json_dtArr.getJSONObject(j);
											String Home  = json_dt.getString("ht");
											String Home_img  = json_dt.getString("hl");
											String away  = json_dt.getString("at");
											String away_img  = json_dt.getString("al");
											String stat = "[no]";
											String score = json_dt.getString("sc");
											String link = json_dt.getString("lk");
											String Time = "";
											if(json_dt.getString("ty").equals("playing")){
												if(json_dt.getString("pr").equals("ht")){
													Time = "Half Time";
												}else{
													Time = json_dt.getString("tc")+"'";
												}
											}else if(json_dt.getString("ty").equals("played")){
												Time = "Full Time";
											}else if(json_dt.getString("ty").equals("postponed")){
												Time = json_dt.getString("ty");
											}else if(json_dt.getString("ty").equals("fixture")){
												Time = json_dt.getString("tp");
											}else{
												Time = json_dt.getString("tp");
											}
											String score_ag = json_dt.getString("ag");
											if(score.equals("")){
												score = "vs";
											}
											if(score_ag==null){
												score_ag="";
											}
											if (away.contains("Arsenal")
													|| Home.contains("Arsenal")) {
												data.Match_list_c.add("[0]" + "Your Team");
												data.Match_list_c.add("[0]" + "Your Team"
														+ "\n" + "[0]" + Time
														+ "\n" + stat + "\n" + Home
														+ "\n" + score + "\n"
														+ away + "\n" + Home_img
														+ "\n" +away_img + "\n" +link+"\n"+score_ag);
											} else {
												data.Match_list_c.add(League + "\n"
														+ "[1]" + Time + "\n"
														+ stat + "\n" + Home + "\n"
														+ score + "\n" + away + "\n" + Home_img
														+ "\n" +away_img + "\n" +link+"\n"+score_ag);
											}
										}
									
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
		            		Collections.sort(data.Match_list_c, new Comparator<String>() {
								@Override
								public int compare(String s1, String s2) {
									return s1.compareToIgnoreCase(s2);
								}
							});
		            		if(data.fragement_Section_get()==1){
		            			handler.post(new Runnable() {

			    					@Override
			    					public void run() {
			    						chk_D_Stat=false;
			    						Toast.makeText(mContext, "Load_End", Toast.LENGTH_LONG)
			    								.show();

			    						layOutlist = (LinearLayout) findViewById(R.id.List_Layout);
			    						layOutlist.removeAllViews();
			    						((LinearLayout) layOutlist).addView(lstView);
			    						chk_ani = false;
			    						imageAdapter.notifyDataSetChanged();
			    					}
			    				});
		            		}
		    				
		                }
		            }
		        });
				//socket.emit("adduser", Name_Send);
			}
		};

		new Thread(runnable).start();
	}
	
	public static Bitmap loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		Bitmap bitmap = null;
		try {

			m = new URL(url);
			URLConnection conexion = m.openConnection();
			conexion.setConnectTimeout(20000);
			conexion.connect();

			conexion.getContentLength();

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
		}
		return bitmap;
	}

	public void startDownload_Home(final int position, final ImageView img_H) {

		Runnable runnable = new Runnable() {
			public void run() {
				String txt_Item = data.Match_list_c.get(position);
				
				if (txt_Item.contains("\n")) {
					
					if (data.get_HomeMap(txt_Item.split("\n")[6]) != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								img_H.setImageBitmap(data.get_HomeMap(String
										.valueOf(position)));
							}
						});
					} else {
						if (!txt_Item.split("\n")[6]
								.contains("/images/placeholder-64x64.png")) {
							final Bitmap pic;
							pic = loadImageFromUrl(txt_Item.split("\n")[6]);
							data.set_HomeMap(txt_Item.split("\n")[6], pic);
							handler.post(new Runnable() {
								@Override
								public void run() {
									if(pic==null){
										img_H.setImageResource(R.drawable.soccer_icon);
									}else{
										img_H.setImageBitmap(pic);
									}
								}
							});
						}
					}
			}
			}
		};

		new Thread(runnable).start();
	}
	
	public void startDownload_Away(final int position, final ImageView img_A) {

		Runnable runnable = new Runnable() {
			public void run() {
				String txt_Item = data.Match_list_c.get(position);
				
				if (txt_Item.contains("\n")) {
					
					if (data.get_AwayMap(txt_Item.split("\n")[7]) != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								img_A.setImageBitmap(data.get_AwayMap(String
										.valueOf(position)));
							}
						});
					} else {
						if (!txt_Item.split("\n")[7]
								.contains("/images/placeholder-64x64.png")) {
							final Bitmap pic;
							pic = loadImageFromUrl(txt_Item.split("\n")[7]);
							data.set_AwayMap(txt_Item.split("\n")[7], pic);
							
							handler.post(new Runnable() {
								@Override
								public void run() {
									if(pic==null){
										img_A.setImageResource(R.drawable.soccer_icon);
									}else{
										img_A.setImageBitmap(pic);
									}
								}
							});
						}
					}

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

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
}
