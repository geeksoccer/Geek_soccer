package com.excelente.geek_soccer.sideMenu;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.Fixtures_Page;
import com.excelente.geek_soccer.MainActivity;
import com.excelente.geek_soccer.Profile_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SelectTeamPage;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.Setting_Page;
import com.excelente.geek_soccer.Sign_In_Page;
import com.excelente.geek_soccer.MainActivity.OnSelectPageListener;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.utils.DialogUtil;
import com.excelente.geek_soccer.utils.HttpConnectUtils;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.utils.asynctask.GetImageUriTask;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SlidingMenuBar implements OnClickListener, OnClosedListener{
	Activity activity;
	
	private SlidingMenu menu;
	
	private LinearLayout newsBtn;
	private LinearLayout LivscoreBtn;
	private LinearLayout ChatBtn;
	private LinearLayout scoreBoardBtn;
	private LinearLayout HilightBtn;
	private LinearLayout FixturesBtn;
	private View FixturesLine;
	private LinearLayout SelectTeamBtn;
	private View SelectTeamLine;
	private LinearLayout profileBtn;
	private LinearLayout rateBtn;
	private LinearLayout shareBtn;
	private LinearLayout settingBtn;
	private LinearLayout logoutBtn;

	private TextView profileName;
	private TextView profileEmail;
	private ImageView profileIcon;

	private boolean animFirst;
	
	public SlidingMenuBar(Activity activity) {
		this.activity = activity;
		this.setAnimFirst(true);
	}
	
	public void createMenu() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int widthScreen = displaymetrics.widthPixels;
		int widthSideMenu = (int) convertDpToPixel(240, activity);
		int widthOffset = widthScreen - widthSideMenu;
		
		menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffset(widthOffset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu_bar);
        menu.setBackgroundColor(Color.BLACK);
        menu.getBackground().setAlpha(150);
        initView();
 
        ((MainActivity) activity).setOnSelectPageListener(new OnSelectPageListener() {
			
			@Override
			public void onSelect(int position) {
				Log.e("onSelect", "SideBar onSelect: " + position);
				if(menu.isMenuShowing()){
					menu.toggle(true);
				}
			}
		});
	}
	
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	private void initView() {
		profileName = (TextView) menu.findViewById(R.id.profile_name);
		profileEmail = (TextView) menu.findViewById(R.id.profile_email);
		profileIcon = (ImageView) menu.findViewById(R.id.ProfileIcon);
		
		profileBtn = (LinearLayout) menu.findViewById(R.id.Profile);
		if(SessionManager.hasMember(activity)){
			String name = SessionManager.getMember(activity).getNickname();
			String email = SessionManager.getMember(activity).getEmail();
			String photo = SessionManager.getMember(activity).getPhoto();
			profileName.setText(name);
			profileEmail.setText(email);
			if(SessionManager.hasKey(activity, photo)){ 
				Bitmap bitmapPhoto = SessionManager.getImageSession(activity, photo);
				profileIcon.setImageBitmap(Profile_Page.resizeBitMap(bitmapPhoto));
			}else{
				new GetImageUriTask(activity, profileIcon, photo).doLoadImage(true);
			} 
			ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, profileBtn);
		}else{
			profileBtn.setVisibility(View.GONE);
		}
		
		newsBtn = (LinearLayout) menu.findViewById(R.id.News);
		LivscoreBtn = (LinearLayout) menu.findViewById(R.id.LiveScore);
		ChatBtn = (LinearLayout) menu.findViewById(R.id.Chat);
		scoreBoardBtn = (LinearLayout) menu.findViewById(R.id.ScoreBoard);
		HilightBtn = (LinearLayout) menu.findViewById(R.id.Hilight);
		FixturesBtn = (LinearLayout) menu.findViewById(R.id.Fixtures);
		FixturesLine = menu.findViewById(R.id.Fixtures_Line);
		SelectTeamBtn = (LinearLayout) menu.findViewById(R.id.SelectTeamLayout);
		SelectTeamLine = menu.findViewById(R.id.SelectTeam_Line);

		newsBtn.setOnClickListener(this);
		LivscoreBtn.setOnClickListener(this);
		ChatBtn.setOnClickListener(this);
		scoreBoardBtn.setOnClickListener(this);
		HilightBtn.setOnClickListener(this);
		FixturesBtn.setOnClickListener(this);
		SelectTeamBtn.setOnClickListener(this);

		rateBtn = (LinearLayout) menu.findViewById(R.id.Rate);
		shareBtn = (LinearLayout) menu.findViewById(R.id.Share);
		settingBtn = (LinearLayout) menu.findViewById(R.id.Setting);
		logoutBtn = (LinearLayout) menu.findViewById(R.id.logINOUT);

		profileBtn.setOnClickListener(this);
		rateBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		settingBtn.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);

		if (SessionManager.getMember(activity).getTeamId() == 0) {
			FixturesBtn.setVisibility(View.GONE);
			FixturesLine.setVisibility(View.GONE);
		}
		
		if(SessionManager.getMember(activity).getRole() == 1 || SessionManager.getMember(activity).getTeamId() == 0){
			SelectTeamBtn.setVisibility(View.VISIBLE);
			SelectTeamLine.setVisibility(View.VISIBLE);
		}else{
			SelectTeamBtn.setVisibility(View.GONE);
			SelectTeamLine.setVisibility(View.GONE);
		}
	}
	
	public void doSelectPage(int position){
		if(ControllParameter.getInstance(activity).mViewPager.getCurrentItem() == position){
			menu.toggle(true);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.News: {
			doSelectPage(0);
			MainActivity.Page_Select(0, true, activity);
			break;

		}
		case R.id.LiveScore: {
			doSelectPage(1);
			MainActivity.Page_Select(1, true, activity);
			break;

		}
		case R.id.Chat: {
			doSelectPage(2);
			MainActivity.Page_Select(2, true, activity);
			break;
		}
		case R.id.ScoreBoard: {
			doSelectPage(3);
			MainActivity.Page_Select(3, true, activity);
			break;
		}
		case R.id.Hilight: {
			doSelectPage(4);
			MainActivity.Page_Select(4, true, activity);
			break;
		}
		case R.id.Fixtures: {
			Intent gotoFixtures = new Intent(activity, Fixtures_Page.class);
			activity.startActivity(gotoFixtures);
			break;
		}
		case R.id.SelectTeamLayout: {
			
			if(SessionManager.getMember(activity).getRole() == 1 || SessionManager.getMember(activity).getTeamId() == 0){
				Intent selectTeamIntent = new Intent(activity, SelectTeamPage.class);
				activity.startActivityForResult(selectTeamIntent, Sign_In_Page.REQUEST_CODE_SELECT_TEAM);
			}
			break;
			
		}
		case R.id.Profile: {

			Intent gotoProfile = new Intent(activity, Profile_Page.class);
			activity.startActivity(gotoProfile);
			break;

		}
		case R.id.Rate: {
			showRateAppDialog();
			break;

		}
		case R.id.Share: {
			showShareAppDialog();
			break;
		}
		case R.id.Setting: {
			Intent gotoSetting = new Intent(activity, Setting_Page.class);
			activity.startActivityForResult(gotoSetting, 2);
			break;
		}
		case R.id.logINOUT: {
			showLogOutAppDialog();
			break;
		}
		case R.id.Save_Mode_btn: {
			DialogUtil.showSaveModeAppDialog(activity, v);
			break;
		}
		}
	}

	protected void showRateAppDialog() {
		final Dialog confirmDialog = new Dialog(activity);

		View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm, null);
		RelativeLayout main_action_bar = (RelativeLayout) view.findViewById(R.id.main_action_bar);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, main_action_bar);
		
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_TEXT_COLOR, title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, btComfirm);
		
		TextView button_confirm_ok = (TextView) view.findViewById(R.id.button_confirm_ok);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_TEXT_COLOR, button_confirm_ok);

		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);

		title.setText(activity.getResources()
				.getString(R.string.rate_app_title));
		Drawable img = activity.getResources().getDrawable(
				R.drawable.star_white);
		img.setBounds(0, 0, 60, 60);
		title.setCompoundDrawables(img, null, null, null);

		question.setText(activity.getResources().getString(
				R.string.rate_app_question));

		closeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		});

		btComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doRateApp(activity);

				confirmDialog.dismiss();
			}

		});

		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}

	protected void doRateApp(Context activity) {
		if (NetworkUtils.isNetworkAvailable(activity)) {
			Uri uri = Uri.parse("market://details?id="
					+ activity.getPackageName());
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				activity.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://play.google.com/store/apps/details?id="
								+ activity.getPackageName())));
			}
		} else {
			Toast.makeText(
					activity,
					activity.getResources()
							.getString(R.string.warning_internet),
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void showShareAppDialog() {
		final Dialog confirmDialog = new Dialog(activity);

		View view = LayoutInflater.from(activity).inflate(
				R.layout.dialog_confirm, null);
		RelativeLayout main_action_bar = (RelativeLayout) view.findViewById(R.id.main_action_bar);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, main_action_bar);
		
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_TEXT_COLOR, title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, btComfirm);
		
		TextView button_confirm_ok = (TextView) view.findViewById(R.id.button_confirm_ok);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_TEXT_COLOR, button_confirm_ok);

		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);

		title.setText(activity.getResources().getString(
				R.string.share_app_title));
		Drawable img = activity.getResources().getDrawable(
				R.drawable.ic_action_share);
		img.setBounds(0, 0, 60, 60);
		title.setCompoundDrawables(img, null, null, null);

		question.setText(activity.getResources().getString(
				R.string.share_app_question));

		closeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		});

		btComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doShareApp(activity);
				confirmDialog.dismiss();
			}

		});

		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}

	protected void doShareApp(Context activity) {
		if (NetworkUtils.isNetworkAvailable(activity)) {
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(
					android.content.Intent.EXTRA_TEXT,
					"https://play.google.com/store/apps/details?id="
							+ activity.getPackageName());
			activity.startActivity(Intent.createChooser(intent, activity
					.getResources().getString(R.string.share_by)));
		} else {
			Toast.makeText(
					activity,
					activity.getResources()
							.getString(R.string.warning_internet),
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void showLogOutAppDialog() {
		final Dialog confirmDialog = new Dialog(activity);

		View view = LayoutInflater.from(activity).inflate(
				R.layout.dialog_confirm, null);
		RelativeLayout main_action_bar = (RelativeLayout) view.findViewById(R.id.main_action_bar);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, main_action_bar);
		
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_TEXT_COLOR, title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_BACKGROUND_COLOR, btComfirm);
		
		TextView button_confirm_ok = (TextView) view.findViewById(R.id.button_confirm_ok);
		ThemeUtils.setThemeToView(activity, ThemeUtils.TYPE_TEXT_COLOR, button_confirm_ok);

		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);

		title.setText(activity.getResources().getString(
				R.string.logout_app_title));
		Drawable img = activity.getResources().getDrawable(R.drawable.log_out);
		img.setBounds(0, 0, 60, 60);
		title.setCompoundDrawables(img, null, null, null);

		question.setText(activity.getResources().getString(
				R.string.logout_app_question));

		closeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		});

		btComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doLogOutApp(activity);
				confirmDialog.dismiss();
			}

		});

		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}

	protected void doLogOutApp(Activity activity) {
		if (NetworkUtils.isNetworkAvailable(activity)) {
			new doSignOutTask(activity).execute(SessionManager
					.getMember(activity));
		} else {
			Toast.makeText(activity,
					NetworkUtils.getConnectivityStatusString(activity),
					Toast.LENGTH_SHORT).show();
		}
	}

	class doSignOutTask extends AsyncTask<MemberModel, Void, Boolean> {

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
			mConnectionProgressDialog.setMessage(mActivity.getResources().getString(R.string.signout_app));
			mConnectionProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(MemberModel... params) {
			MemberModel member = params[0];

			List<NameValuePair> memberParam = new ArrayList<NameValuePair>();

			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_UID,
					String.valueOf(member.getUid())));
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_TOKEN,
					member.getToken()));

			String dev_id = Secure.getString(mActivity.getContentResolver(),
					Secure.ANDROID_ID);
			memberParam.add(new BasicNameValuePair(MemberModel.MEMBER_DEVID,
					dev_id));

			String memberStr = HttpConnectUtils.getStrHttpPostConnect(
					ControllParameter.MEMBER_SIGN_OUT_URL, memberParam);

			if (memberStr.trim().equals("updated token")) {
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean memberToken) {
			super.onPostExecute(memberToken);

			mConnectionProgressDialog.dismiss();

			if (memberToken) {
				SessionManager.clearMember(mActivity);
				if (MainActivity.getServiceIntent() != null)
					mActivity.stopService(MainActivity.getServiceIntent());
				mActivity.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			} else {
				Toast.makeText(mActivity, "Sign Out Failed", Toast.LENGTH_SHORT).show();
			}
		}

	}

	public SlidingMenu getMenu() {
		return menu;
	}

	public void setMenu(SlidingMenu menu) {
		this.menu = menu;
	}

	public boolean isAnimFirst() {
		return animFirst;
	}

	public void setAnimFirst(boolean animFirst) {
		this.animFirst = animFirst;
	}

	@Override
	public void onClosed() {
		
	}

}
