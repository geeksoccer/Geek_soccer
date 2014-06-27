package com.excelente.geek_soccer.utils;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;

public class DialogUtil {

	public static void showRateAppDialog(final Context mContext) { 
		ThemeUtils.setThemeByTeamId(mContext, SessionManager.getMember(mContext).getTeamId());
		
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.rate_app_title));
		Drawable img = mContext.getResources().getDrawable( R.drawable.news_likes_selected );
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(mContext.getResources().getString(R.string.rate_app_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doRateApp(mContext);
				confirmDialog.dismiss(); 
			}

		});
		
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}
	
	protected static void doRateApp(Context mContext) {
		if(NetworkUtils.isNetworkAvailable(mContext)){
			Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				mContext.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
			}
		}else{ 
			Toast.makeText(mContext, mContext.getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void showShareAppDialog(final Context mContext) {
		ThemeUtils.setThemeByTeamId(mContext, SessionManager.getMember(mContext).getTeamId());
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.share_app_title));
		Drawable img = mContext.getResources().getDrawable(R.drawable.ic_action_share);
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(mContext.getResources().getString(R.string.share_app_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doShareApp(mContext);
				confirmDialog.dismiss(); 
			}

		});
		 
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}

	protected static void doShareApp(Context mContext) {
		if(NetworkUtils.isNetworkAvailable(mContext)){
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+ mContext.getPackageName());
			mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share_by))); 
		}else{ 
			Toast.makeText(mContext, mContext.getResources().getString(R.string.warning_internet), Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void showSaveModeAppDialog(final Context mContext, final View saveMode_btn) {
		ThemeUtils.setThemeByTeamId(mContext, SessionManager.getMember(mContext).getTeamId());
		final Dialog confirmDialog = new Dialog(mContext);  
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_3, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		
		RelativeLayout btComfirmOK = (RelativeLayout) view.findViewById(R.id.button_confirm_ok);
		RelativeLayout btComfirmNO = (RelativeLayout) view.findViewById(R.id.button_confirm_no);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.save_mode_title));
		Drawable img = mContext.getResources().getDrawable(R.drawable.ic_menu_view);
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(mContext.getResources().getString(R.string.select_mode_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirmOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSaveMode(mContext, true, saveMode_btn);
				confirmDialog.dismiss();  
			}

		});
		
		btComfirmNO.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSaveMode(mContext, false, saveMode_btn);
				confirmDialog.dismiss();  
			}

		});
		 
		confirmDialog.setCancelable(false);
		confirmDialog.show();
	}
	 
	public static void doSaveMode(Context mContext, boolean b, View saveMode_btn) {
		String saveMode = SessionManager.getSetting(mContext, SessionManager.setting_save_mode);
		if(saveMode.equals("true")){
			saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode);
			SessionManager.setSetting(mContext, SessionManager.setting_save_mode, "false");
		}else{
			saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode_selected);
			SessionManager.setSetting(mContext, SessionManager.setting_save_mode, "true");
		}
		SessionManager.setSetting(mContext, SessionManager.setting_save_mode, String.valueOf(b));
	}
	
}
