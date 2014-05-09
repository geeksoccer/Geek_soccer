package com.excelente.geek_soccer.chat_menu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.JSONParser;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Chat_Menu_LongClick {
	JSONObject jsonOb;
	Context mContext;
	JSONParser jParser = new JSONParser();
	Dialog C_dialog;
	LinearLayout MainLayout;
	
	public void ChatMenu(final Context mContext, final JSONObject jsonOb) {
		this.jsonOb = jsonOb;
		this.mContext = mContext;
		Boolean isMenu = false;
		
		C_dialog = new Dialog(mContext);
		C_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MainLayout = new LinearLayout(mContext);
		MainLayout.setOrientation(LinearLayout.VERTICAL);
		MainLayout.setGravity(Gravity.CENTER);
		C_dialog.setContentView(MainLayout);
		C_dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		C_dialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		try {
			if (jsonOb.getString("ch_type").contains("T")) {
				TextView Copy_txt = new TextView(mContext);
				Copy_txt.setBackgroundResource(R.drawable.bg_press);
				Copy_txt.setTextSize(18);
				Copy_txt.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				Copy_txt.setGravity(Gravity.CENTER);
				Copy_txt.setText("Copy");
				MainLayout.addView(Copy_txt);
				Copy_Setup(Copy_txt, C_dialog);
				
				View line = new View(mContext);
				line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1));
				line.setBackgroundColor(color.gray);
				line.setPadding(5, 0, 5, 0);
				MainLayout.addView(line);
				isMenu=true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if(!String.valueOf(SessionManager.getMember(mContext).getUid()).equals(jsonOb.getString("ch_uid"))){
				TextView Report_txt = new TextView(mContext);
				Report_txt.setBackgroundResource(R.drawable.bg_press);
				Report_txt.setTextSize(18);
				Report_txt.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				Report_txt.setGravity(Gravity.CENTER);
				Report_txt.setText("Ban Report");
				MainLayout.addView(Report_txt);
				Report_Setup(Report_txt, C_dialog);
				isMenu=true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(isMenu){
			C_dialog.show();
		}
	}

	public void Copy_Setup(TextView Copy_txt, final Dialog C_dialog) {
		Copy_txt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
						android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext
								.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(jsonOb.getString("ch_msg"));
					} else {
						android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext
								.getSystemService(Context.CLIPBOARD_SERVICE);
						android.content.ClipData clip = android.content.ClipData
								.newPlainText("Copied",
										jsonOb.getString("ch_msg"));
						clipboard.setPrimaryClip(clip);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				C_dialog.dismiss();
				Toast.makeText(mContext, "Copied", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void Report_Setup(TextView Report_txt, final Dialog C_dialog) {
		Report_txt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				try {
					MainLayout.removeAllViews();
					ProgressBar proGress = new ProgressBar(mContext);
					TextView txt = new TextView(mContext);
					txt.setTextSize(18);
					txt.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					txt.setGravity(Gravity.CENTER);
					txt.setText("Reporting user:"+jsonOb.getString("m_nickname"));
					
					MainLayout.addView(proGress);
					MainLayout.addView(txt);
					
					new member_Report_Call().execute(jsonOb.getString("ch_uid"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	class member_Report_Call extends AsyncTask<String, String, String> {

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
				params.add(new BasicNameValuePair("u_id", args[0]));
				params.add(new BasicNameValuePair("token",
						md5Digest(String.valueOf(SessionManager.getMember(mContext).getUid())+args[0]+"acpt46") ));
				JSONObject json = jParser
						.makeHttpRequest("http://183.90.171.209/gs_member_permission/chat_permission.php",
								"POST", params);
				if (json != null) {
					return json.getString("return_code");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(final String outPut) {
			//pDialog.dismiss();
			((Activity) mContext).runOnUiThread(new Runnable() {
				public void run() {
					if(outPut.equals("0")){
						Toast.makeText(mContext, "Report fail!", Toast.LENGTH_LONG).show();
					}else if(outPut.equals("1")){
						try {
							Toast.makeText(mContext, "Report \"" + jsonOb.getString("m_nickname") + "\" success", Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(outPut.equals("2")){
						try {
							Toast.makeText(mContext, "You used to report \""+jsonOb.getString("m_nickname") +"\"", Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					C_dialog.dismiss();
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
}