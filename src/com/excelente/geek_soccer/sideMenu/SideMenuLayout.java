package com.excelente.geek_soccer.sideMenu;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.MainActivity;
import com.excelente.geek_soccer.Profile_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.Setting_Page;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.utils.DialogUtil;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SideMenuLayout implements OnClickListener{
	
	private static final String MEMBER_SIGN_OUT_URL = "http://183.90.171.209/gs_member/member_sign_out.php";
	
	Activity mContext;

	private Button profileBtn;

	private Button rateBtn;

	private Button shareBtn;

	private Button logoutBtn;

	private Button settingBtn;

	private ImageView menuBtn;

	private ImageView saveMode_btn;
	
	private static ControllParameter data;
	public LinearLayout CreateMenu(LinearLayout MainLayout, final Activity mContext) {
		
		data = ControllParameter.getInstance(mContext);
		
		this.mContext = mContext;
		
		LayoutInflater factory = LayoutInflater.from(mContext);
		View MenuLayV = factory.inflate(R.layout.menu_layout, null);
		data.Menu_Layout = (LinearLayout)MenuLayV.findViewById(R.id.Main_Layout);
		data.Menu_Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		data.Menu_Layout.setGravity(Gravity.RIGHT);
		data.Menu_Layout.setBackgroundColor(Color.BLACK);
		data.Menu_Layout.getBackground().setAlpha(180);
		data.Menu_View = factory.inflate(R.layout.menu_view, null);
		data.Menu_Layout.addView(data.Menu_View);
		data.Menu_View.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
		data.Menu_View.startAnimation(AnimationUtils.loadAnimation(mContext
	               , R.anim.side_menu_animation));
		
		data.Menu_title = (TextView)MenuLayV.findViewById(R.id.Title_bar);
		
		data.Menu_title.setText(data.PageNameSelected);
		
		data.Menu_Layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideMenu(mContext);
			}
		});
		
		menuBtn = (ImageView) data.Menu_Layout.findViewById(R.id.Menu_btn);
		if(SessionManager.getMember(mContext).getRole() == 2){
			menuBtn.setVisibility(View.GONE);
		}else{
			menuBtn.setVisibility(View.VISIBLE);
		}
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu(mContext);
			}
		});
		
		saveMode_btn = (ImageView)MenuLayV.findViewById(R.id.Save_Mode_btn);
		String saveMode = SessionManager.getSetting(mContext, SessionManager.setting_save_mode);
		if(saveMode.equals("true")){
			saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode_selected);
		}else{
			saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode);
		}
		saveMode_btn.setOnClickListener(this);
		
		profileBtn = (Button)data.Menu_View.findViewById(R.id.Profile);
		profileBtn.setOnClickListener(this);
		
		rateBtn = (Button)data.Menu_View.findViewById(R.id.Rate);
		rateBtn.setOnClickListener(this);
		
		shareBtn = (Button)data.Menu_View.findViewById(R.id.Share);
		shareBtn.setOnClickListener(this); 
		 
		settingBtn = (Button)data.Menu_View.findViewById(R.id.Setting);
		settingBtn.setOnClickListener(this); 
		 
		logoutBtn = (Button)data.Menu_View.findViewById(R.id.logINOUT);
		logoutBtn.setOnClickListener(this); 
		
		return data.Menu_Layout;
	}
	
	public void hideMenu(Context mContext){
		Animation out = AnimationUtils.loadAnimation(mContext
	               , R.anim.side_menu_ani_out);
		out.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				data.Menu_Layout.setVisibility(RelativeLayout.GONE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						data.wm.removeView(data._Menu_Layout);
					}
				}, 10);
				
			}
		});
		data.Menu_View.startAnimation(out);
	}
	
	public void hideMenuNoAni() {
		data.Menu_Layout.setVisibility(RelativeLayout.GONE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				data.wm.removeView(data._Menu_Layout);
			}
		}, 10);
	}
	
	public void showMenu(Context mContext){
		data.Menu_Layout.setVisibility(RelativeLayout.ABOVE);
		data.Menu_title.setText(data.PageNameSelected);
		Animation in = AnimationUtils.loadAnimation(mContext
	               , R.anim.side_menu_animation);
		in.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
		data.Menu_View.startAnimation(in);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.Profile:{
				
				Intent gotoProfile = new Intent(mContext, Profile_Page.class);
				mContext.startActivity(gotoProfile);
				hideMenuNoAni();
				break;
	
			}
			case R.id.Rate:{
				hideMenuNoAni();
				showRateAppDialog();
				break;
	
			}
			case R.id.Share:{
				hideMenuNoAni();
				showShareAppDialog();
				break;
			}
			case R.id.Setting:{
				Intent gotoSetting = new Intent(mContext, Setting_Page.class);
				mContext.startActivityForResult(gotoSetting, 2);
				hideMenuNoAni();
				break;
			}
			case R.id.logINOUT:{
				hideMenuNoAni();
				showLogOutAppDialog();
				break;
			}
			case R.id.Save_Mode_btn:{
				hideMenuNoAni();
				DialogUtil.showSaveModeAppDialog(mContext, v);
				break;
			}
		}
	}
	
	protected void showRateAppDialog() {
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
	
	protected void doRateApp(Context mContext) {
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

	protected void showShareAppDialog() {
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

	protected void doShareApp(Context mContext) {
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
	
	protected void showLogOutAppDialog() {
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.logout_app_title));
		Drawable img = mContext.getResources().getDrawable(R.drawable.log_out);
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(mContext.getResources().getString(R.string.logout_app_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doLogOutApp(mContext); 
				confirmDialog.dismiss(); 
			}

		});
		
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}

	protected void doLogOutApp(Activity mContext) {
		if(NetworkUtils.isNetworkAvailable(mContext)){
			new doSignOutTask(mContext).execute(SessionManager.getMember(mContext));
		}else{
			Toast.makeText(mContext, NetworkUtils.getConnectivityStatusString(mContext), Toast.LENGTH_SHORT).show();
		}
	}
	
	class doSignOutTask extends AsyncTask<MemberModel, Void, Boolean>{
		
		Activity mActivity; 
		ProgressDialog mConnectionProgressDialog;
		
		public doSignOutTask(Activity context) {
			mActivity = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mConnectionProgressDialog = new ProgressDialog(mActivity);
	        mConnectionProgressDialog.setCancelable(false);
			mConnectionProgressDialog.setMessage("Signing out...");
			mConnectionProgressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(MemberModel... params) {
			MemberModel member = params[0];
			
			List<NameValuePair> memberParam = new ArrayList<NameValuePair>();

			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_UID, String.valueOf(member.getUid())));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN, member.getToken()));
			
			String dev_id = Secure.getString(mActivity.getContentResolver(),Secure.ANDROID_ID);
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID, dev_id));
				
			String memberStr = HttpConnectUtils.getStrHttpPostConnect(MEMBER_SIGN_OUT_URL, memberParam);
				
			if(memberStr.trim().equals("updated token")){ 
				return true;
			}
				
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean memberToken) {
			super.onPostExecute(memberToken);
			
			mConnectionProgressDialog.dismiss();
			
			if(memberToken){
				SessionManager.clearMember(mActivity);
				if(MainActivity.getServiceIntent()!=null)
					mActivity.stopService(MainActivity.getServiceIntent());
				mActivity.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}else{
				Toast.makeText(mActivity, "Sign Out Failed", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
}