package com.excelente.geek_soccer;

import com.excelente.geek_soccer.utils.NetworkUtils;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
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
	
	Context mContext;

	private Button profileBtn;

	private Button rateBtn;

	private Button shareBtn;
	
	private static ControllParameter data;
	public LinearLayout CreateMenu(LinearLayout MainLayout, final Context mContext) {
		
		data = ControllParameter.getInstance(mContext);
		
		this.mContext = mContext;
		
		LayoutInflater factory = LayoutInflater.from(mContext);
		View MenuLayV = factory.inflate(R.layout.menu_layout, null);
		data.Menu_Layout = (LinearLayout)MenuLayV.findViewById(R.id.Main_Layout);
		data.Menu_Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		data.Menu_Layout.setGravity(Gravity.LEFT);
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
		Button menu_btn = (Button)MenuLayV.findViewById(R.id.Menu_btn);
		menu_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu(mContext);
			}
		});
		
		profileBtn = (Button)data.Menu_View.findViewById(R.id.Profile);
		profileBtn.setOnClickListener(this);
		
		rateBtn = (Button)data.Menu_View.findViewById(R.id.Rate);
		rateBtn.setOnClickListener(this);
		
		shareBtn = (Button)data.Menu_View.findViewById(R.id.Share);
		shareBtn.setOnClickListener(this); 
		
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
}
