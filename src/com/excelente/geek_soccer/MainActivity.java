package com.excelente.geek_soccer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.excelente.geek_soccer.chat_page.Chat_AllView;
import com.excelente.geek_soccer.chat_page.Chat_PageByView;
import com.excelente.geek_soccer.chat_page.Chat_TeamView;
import com.excelente.geek_soccer.live_score_page.Live_Score_PageByView;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.TeamModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.sideMenu.SlidingMenuBar;
import com.excelente.geek_soccer.utils.DialogUtil;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;
import com.excelente.geek_soccer.view.Boast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
	
	boolean customTitleSupported;
	public static CustomViewPager mViewPager;
	public static PagerAdapter mPagerAdapter;

	public static Button news_tab, live_tab, chat_tab, score_board_tab, game_tab;
	
	ImageView logout_btn;
	ImageView news_btn;
	LinearLayout TeamLogo;
	
	ImageView saveMode_btn;
	
	LinearLayout Content_view;
	Activity mContext;
	private static TextView title_bar;
	private static Intent serviceIntent; 
	private static ControllParameter data;

	Boolean Moved = false;
	float MenuWidth;
	float TenPerScreenWidth;
	float originSideMenuX = 0;
	float startTouchX = 0;
	float startTouchY = 0;
	
	private RelativeLayout Header_Layout;
	private ImageView Team_Logo;
	private LinearLayout Tab_Layout;
	private ImageView Update_App_btn;
	private SlidingMenuBar menu;
	private ImageView menuImg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!SessionManager.hasMember(MainActivity.this)){
			finish();
		}
		
		String saveModeAsk = SessionManager.getSetting(this, SessionManager.setting_save_mode);
		if(saveModeAsk == null || saveModeAsk.equals("null")){
			SessionManager.setSetting(this, SessionManager.setting_save_mode, "false");
		}
		//SessionManager.setSetting(this, SessionManager.setting_save_mode_ask, "false");
		if(saveModeAsk != null && saveModeAsk.equals("true")){
			doCreate();
		}else{
			askMode();
		}
	}
	
	private void doCheckVersionApp() {
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pInfo.versionName;
			final int versionCode = pInfo.versionCode;
			MemberModel member = SessionManager.getMember(getApplicationContext());
			
			RequestParams params = new RequestParams();
			params.put("versionName", versionName);
			params.put("versionCode", versionCode);
			params.put("m_uid", member.getUid());
			params.put("m_token", member.getToken());
			params.put("time", new Date().getTime());
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(getApplicationContext(), ControllParameter.MEMBER_CHECK_VERSION_APP_URL, params, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					String response = new String(arg2);
					try {
						JSONObject res = new JSONObject(response);
						String result = res.getString("result");
						int lastVersionCode = res.getInt("last_version_code");
						if(result.equals("old version")){
							if(SessionManager.hasKey(mContext, "old_version_code")){
								int oldVersionCode = Integer.valueOf(SessionManager.getSetting(mContext, "old_version_code"));
								if(versionCode >= lastVersionCode){
									Update_App_btn.setVisibility(View.GONE);
								}else if(oldVersionCode == versionCode){
									Update_App_btn.setVisibility(View.VISIBLE);
								}else{
									Update_App_btn.setVisibility(View.VISIBLE);
									SessionManager.setSetting(mContext, "old_version_code", String.valueOf(versionCode));
									DialogUtil.showUpdateAppDialog(mContext);
								}
							}else{
								Update_App_btn.setVisibility(View.VISIBLE);
								SessionManager.setSetting(mContext, "old_version_code", String.valueOf(versionCode));
								DialogUtil.showUpdateAppDialog(mContext);
							}
						}else{
							Update_App_btn.setVisibility(View.GONE);
						}
					} catch (JSONException e) {
						Update_App_btn.setVisibility(View.GONE);
					}
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					
				}
			});
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		Log.e("onResume", "Main onResume");
		super.onResume();
		if(saveMode_btn!=null){ 
			String saveMode = SessionManager.getSetting(this, SessionManager.setting_save_mode);
			if(saveMode.equals("true")){
				saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode_selected);
			}else{
				saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode);
			}
		}
		
		doDataChangeProfile();
	}
	
	private void doDataChangeProfile() {
		if(menu!=null){
			LinearLayout sideMenu = (LinearLayout) menu.getMenu().findViewById(R.id.SideMenu);
			ProgressBar progressbar = (ProgressBar) menu.getMenu().findViewById(R.id.ProgressBar);
			ScrollView scrollView = (ScrollView) menu.getMenu().findViewById(R.id.ScrollView);
			
			TextView profileName = (TextView) menu.getMenu().findViewById(R.id.profile_name);
			TextView profileEmail = (TextView) menu.getMenu().findViewById(R.id.profile_email);
			ImageView profileIcon = (ImageView) menu.getMenu().findViewById(R.id.ProfileIcon);
			if(!menu.getMenu().isMenuShowing()){
				//sideMenu.setVisibility(View.INVISIBLE);
				scrollView.setVisibility(View.INVISIBLE);
				profileName.setVisibility(View.INVISIBLE);
				profileEmail.setVisibility(View.INVISIBLE);
				profileIcon.setVisibility(View.INVISIBLE);
				progressbar.setVisibility(View.VISIBLE);
				menu.setAnimFirst(true);
			}
			
			LinearLayout profileBtn = (LinearLayout) menu.getMenu().findViewById(R.id.Profile);
			
			if(profileBtn!=null){
				profileBtn = (LinearLayout) menu.getMenu().findViewById(R.id.Profile);
				profileName = (TextView) menu.getMenu().findViewById(R.id.profile_name);
				profileEmail = (TextView) menu.getMenu().findViewById(R.id.profile_email);
				profileIcon = (ImageView) menu.getMenu().findViewById(R.id.ProfileIcon);
				
				if(SessionManager.hasMember(this)){
					String name = SessionManager.getMember(this).getNickname();
					String email = SessionManager.getMember(this).getEmail();
					String photo = SessionManager.getMember(this).getPhoto();
					profileName.setText(name);
					profileEmail.setText(email);
					if(SessionManager.hasKey(this, photo)){ 
						Bitmap bitmapPhoto = SessionManager.getImageSession(this, photo);
						profileIcon.setImageBitmap(Profile_Page.resizeBitMap(bitmapPhoto));
					}else{
						profileIcon.setImageResource(R.drawable.ic_action_person);
					}
				}else{
					profileBtn.setVisibility(View.GONE);
				}
			}
		}
	}

	private void doCreate() {
		mContext = this;
		data = ControllParameter.getInstance(this);
		vidateAskRateApp();
		
		serviceIntent = new Intent(this, UpdateService.class);
		serviceIntent.putExtra(MemberModel.MEMBER_KEY, (Serializable)SessionManager.getMember(MainActivity.this)); 
		startService(serviceIntent);
		
		setContentView(R.layout.main);
		
		this.intialiseViewPager();
		
		menu_setting();
		tab_setting();
		
		Calendar c = Calendar.getInstance();
		Time now = new Time();
		now.setToNow();

	    if(c.get(Calendar.HOUR_OF_DAY) < 6){
	    	 c.add(Calendar.DAY_OF_YEAR, -1);
	    }
		//int yy = c.get(Calendar.YEAR);
		//int mm = c.get(Calendar.MONTH);
		//int dd = c.get(Calendar.DAY_OF_MONTH);
		//String Date_Select = String.valueOf(yy)+"-"+set_DateMonth_format(mm+1) +"-"+set_DateMonth_format(dd);
		//new Load_LiveScore_Data().data(mContext, Date_Select);
	    setPageFromNotification();
		SideMenuStandBy();
		Team_LogoSetting();
		
		setThemeToView();
		
		if(NetworkUtils.isNetworkAvailable(this)){
			doCheckVersionApp();
		}
		
	}

	private void askMode() {
		if(NetworkUtils.getConnectivityStatus(this) == NetworkUtils.TYPE_MOBILE){
			String saveMode = SessionManager.getSetting(this, SessionManager.setting_save_mode);
			if(saveMode == null || saveMode.equals("null") || saveMode.equals("false")){
				showSaveModeAppDialog(this);
			}else{
				doCreate();
			}
		}else{
			doCreate();
		}
		
		//showSaveModeAppDialog(this);
	}

	private void vidateAskRateApp() {
		String askRate = SessionManager.getSetting(mContext, SessionManager.setting_ask_rateapp);
		if(askRate == null || askRate.equals("null")){
			SessionManager.setSetting(mContext, SessionManager.setting_ask_rateapp, "false");
			SessionManager.setSetting(mContext, SessionManager.setting_count_use, "0");
		}else if(askRate.equals("false")){
			String countUseStr = SessionManager.getSetting(mContext, SessionManager.setting_count_use);
			if(countUseStr.equals("null")){ 
				SessionManager.setSetting(mContext, SessionManager.setting_count_use, "0");
				return;
			}
			
			long installed;
			int countUse = Integer.parseInt(countUseStr);
			try {
				installed = this.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).firstInstallTime;
				
				if(countUse > 30 || System.currentTimeMillis() - installed > 7*24*60*60*1000){
					DialogUtil.showRateAppDialog(mContext);
					SessionManager.setSetting(mContext, SessionManager.setting_ask_rateapp, "true");
				}
				
			} catch (NameNotFoundException e) {
				if(countUse > 30){
					DialogUtil.showRateAppDialog(mContext);
					SessionManager.setSetting(mContext, SessionManager.setting_ask_rateapp, "true");
				}
			}
			
			countUse++;
			SessionManager.setSetting(mContext, SessionManager.setting_count_use, String.valueOf(countUse));
		}
	}

	private void setPageFromNotification() {
		if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 1000){
			Page_Select(0, true, this);
		}else if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 2000){
			Page_Select(4, true, this);
		}else if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 4600){
			Page_Select(1, true, this);
		}
	}

	//----------------------Ched: (For Admin Member)-----------------------------

	private void setThemeToView() {
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, Header_Layout);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, Tab_Layout);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_LOGO, Team_Logo);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, title_bar);
	}
	
	private void Team_LogoSetting(){
		menuImg = (ImageView) findViewById(R.id.menu_img);
		menuImg.setImageResource(R.drawable.ic_drawer);
		Team_Logo = (ImageView) findViewById(R.id.Team_Logo);
		TeamLogo = (LinearLayout)findViewById(R.id.Up_btn);
		TeamLogo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				menu.getMenu().toggle(true);
			}
		});
	}
	
	public void SideMenuStandBy(){
		menu = new SlidingMenuBar(this);
		menu.createMenu();
		data.Sliding_Menu_Bar = menu;
	}
	
	private void menu_setting() {
		Header_Layout = (RelativeLayout) findViewById(R.id.Header_Layout);
		 
		title_bar = (TextView)findViewById(R.id.Title_bar);
		
		saveMode_btn = (ImageView) findViewById(R.id.Save_Mode_btn);
		String saveMode = SessionManager.getSetting(mContext, SessionManager.setting_save_mode);
		if(saveMode.equals("true")){
			saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode_selected);
		}else{
			saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode);
		}
		
		saveMode_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogUtil.showSaveModeAppDialog(mContext, v);
			}
		});
		
		Update_App_btn = (ImageView) findViewById(R.id.Update_App_btn);
		Update_App_btn.setVisibility(View.GONE);
		Update_App_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogUtil.showUpdateAppDialog(mContext);
			}
		});
	}
	
	private void doRefeshPage(int indexPage) {
		/*Fragment currentFragment = mPagerAdapter.getItem(indexPage);
	    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
	    fragTransaction.detach(currentFragment);
	    fragTransaction.attach(currentFragment);
	    fragTransaction.commit();*/
		Intent re = new Intent(mContext, Sign_In_Page.class);
		startActivity(re);
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	//----------------------Ched:(For Admin Member) -----------------------------

	private void intialiseViewPager() {
		List<Fragment> fragments = new Vector<Fragment>();
		
		fragments.add(Fragment.instantiate(this, News_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Live_Score_PageByView.class.getName()));
		fragments.add(Fragment.instantiate(this, Chat_PageByView.class.getName()));
		fragments.add(Fragment.instantiate(this, Table_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Hilight_Page.class.getName()));
		
		mPagerAdapter = new PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		mViewPager = (CustomViewPager) super.findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setPagingEnabled(true);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(4);
		onSelectPageListenerList = new ArrayList<MainActivity.OnSelectPageListener>();
	}
	
	public void tab_setting(){
		Tab_Layout = (LinearLayout) findViewById(R.id.Tab_Layout);
		news_tab = (Button)findViewById(R.id.News);
		live_tab = (Button)findViewById(R.id.Live_Score);
		chat_tab = (Button)findViewById(R.id.Chats);
		score_board_tab = (Button)findViewById(R.id.Score_Board);
		game_tab = (Button)findViewById(R.id.Game);
		
		chatAlertV = (RelativeLayout)findViewById(R.id.chatAlertV);
		chatAlertTextCount = (TextView)findViewById(R.id.chatAlertTextCount);
		
		//Content_view = (LinearLayout)findViewById(R.id.Contain_Layout);
		Page_Select(0, true, this);
		ChatAlertSetting();
		news_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(0, true, MainActivity.this);
			}
		});
		live_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(1, true, MainActivity.this);
			}
		});
		chat_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(2, true, MainActivity.this);
			}
		});
		score_board_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(3, true, MainActivity.this);
			}
		});
		game_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(4, true, MainActivity.this);
			}
		});
		
		chatAlertV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(2, true, MainActivity.this);
			}
		});
		
	}
	
	public interface OnSelectPageListener{
		public void onSelect(int position);
	}
	
	List<OnSelectPageListener> onSelectPageListenerList;
	
	public static void Page_Select(int index, boolean by_Selected, Context mContext){
		//Content_view.removeAllViews();
		data.fragement_Section_set(index);
		//mViewPager.setPagingEnabled(true);
		//mViewPager.setOffscreenPageLimit(4);
		
		if(index==0){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_news);
			//news_tab.setBackgroundResource(R.drawable.news_h);
			//live_tab.setBackgroundResource(R.drawable.livescore);
			//chat_tab.setBackgroundResource(R.drawable.chat);
			//score_board_tab.setBackgroundResource(R.drawable.board);
			//game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==1){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_live_score);
			//news_tab.setBackgroundResource(R.drawable.news);
			//live_tab.setBackgroundResource(R.drawable.livescore_h);
			//chat_tab.setBackgroundResource(R.drawable.chat);
			//score_board_tab.setBackgroundResource(R.drawable.board);
			//game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==2){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_chat);
			//news_tab.setBackgroundResource(R.drawable.news);
			//live_tab.setBackgroundResource(R.drawable.livescore);
			//chat_tab.setBackgroundResource(R.drawable.chat_h);
			//score_board_tab.setBackgroundResource(R.drawable.board);
			//game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==3){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_score_broads);
			//news_tab.setBackgroundResource(R.drawable.news);
			//live_tab.setBackgroundResource(R.drawable.livescore);
			//chat_tab.setBackgroundResource(R.drawable.chat);
			//score_board_tab.setBackgroundResource(R.drawable.board_h);
			//game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==4){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_hilight);
			//news_tab.setBackgroundResource(R.drawable.news);
			//live_tab.setBackgroundResource(R.drawable.livescore);
			//chat_tab.setBackgroundResource(R.drawable.chat);
			//score_board_tab.setBackgroundResource(R.drawable.board);
			//game_tab.setBackgroundResource(R.drawable.hilight_icon_select);
		}
		curPage = index;
		if(index==2){
			countNumChat=0;
		}
		ChatAlertSetting();

		title_bar.setText(data.PageNameSelected);
		if(by_Selected){
			mViewPager.setCurrentItem(index);
		}
		
	}

	public void setOnSelectPageListener(OnSelectPageListener onSelectPageListener) {
		this.onSelectPageListenerList.add(onSelectPageListener);
	}

	public static RelativeLayout chatAlertV;
	public static TextView chatAlertTextCount;
	public static int curPage=0;
	public static int countNumChat=0;
	
	public static void ChatAlertSetting(){
		if(countNumChat<10){
			chatAlertTextCount.setText(""+countNumChat);
		}else{
			chatAlertTextCount.setText("10+");
		}
		
		if(curPage==2 || countNumChat==0){
			chatAlertV.setVisibility(View.INVISIBLE);
		}else{
			chatAlertV.setVisibility(View.VISIBLE);
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)chatAlertV.getLayoutParams();
		chatAlertV.setLayoutParams(params);
	}
	
	int OldState = 0;
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(OldState==1 && arg0==0 && mViewPager.getCurrentItem()==0 ){
			//new SideMenuMain().showMenu(mContext);
			data.Sliding_Menu_Bar.getMenu().toggle(true);
		}
		OldState = arg0;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		Page_Select(arg0, false, this);
		if(onSelectPageListenerList!=null&&onSelectPageListenerList.size()>0){
			for (OnSelectPageListener onSelectPageListener : onSelectPageListenerList) {
				onSelectPageListener.onSelect(arg0);
			}
		}
	}
	
	public String  set_DateMonth_format(int value) {
		if(value<10){
			return "0" + String.valueOf(value);
		}else{
			return String.valueOf(value);
		}		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(data.Sliding_Menu_Bar!=null){
				Log.e("ttt","onKeyDown");
				if(data.Sliding_Menu_Bar.getMenu().isMenuShowing()){
					data.Sliding_Menu_Bar.getMenu().toggle(true);
				}else{
					if((data.Sticker_Layout_Stat_team || data.Sticker_Layout_Stat_All) && data.fragement_Section_get()==2){
						if(data.Sticker_Layout_Stat_team){
							data.Sticker_Layout_Stat_team = false;
						}else if(data.Sticker_Layout_Stat_All){
							data.Sticker_Layout_Stat_All = false;
						}
					}else{
						showCloseAppDialog(this);
					}
				}
				return false;
			}else{
				if((data.Sticker_Layout_Stat_team || data.Sticker_Layout_Stat_All) && data.fragement_Section_get()==2){
					if(data.Sticker_Layout_Stat_team){
						Chat_TeamView.StikerV.setVisibility(RelativeLayout.GONE);
						data.Sticker_Layout_Stat_team = false;
					}else if(data.Sticker_Layout_Stat_All){
						Chat_AllView.StikerV.setVisibility(RelativeLayout.GONE);
						data.Sticker_Layout_Stat_All = false;
					}
				}else{
					data.app_Status=false;
					showCloseAppDialog(this);
				}
				return false;
			}
		}
		
		return false;
	}
	
	public static void showCloseAppDialog(final Activity mContext) { 
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
		RelativeLayout main_action_bar = (RelativeLayout) view.findViewById(R.id.main_action_bar);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, main_action_bar);
		
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, btComfirm);
		
		TextView button_confirm_ok = (TextView) view.findViewById(R.id.button_confirm_ok);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, button_confirm_ok);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.close_app_title));
		/*Drawable img = mContext.getResources().getDrawable(R.drawable.ic_action_share);
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );*/
		
		question.setText(mContext.getResources().getString(R.string.close_app_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
				//SessionManager.clearMemberOnly(mContext);
				mContext.finish(); 
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		});
		
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}

	public static Intent getServiceIntent() {
		return serviceIntent;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==2 && data!=null){  
           String message = data.getStringExtra("COMMAND_APP");   
           if(message.equals("Restart App")){
        	   Intent i = new Intent(getApplicationContext(), Sign_In_Page.class);
   			   startActivity(i);
   			   finish();
   			   android.os.Process.killProcess(android.os.Process.myPid());
           }
        }else if(requestCode == Sign_In_Page.REQUEST_CODE_SELECT_TEAM && data!=null){
        	TeamModel team = (TeamModel) data.getSerializableExtra("SELECT_TEAM");
        	if(SessionManager.getMember(mContext).getTeamId() != team.getTeamId()){
        		if (NetworkUtils.isNetworkAvailable(this)){
        			doChangeTeam(team);
        		}else{
        			Boast.makeText(this, NetworkUtils.getConnectivityStatusString(this), Toast.LENGTH_SHORT).show();
        		}
        		
        	}
        }
	}
	
	private void doChangeTeam(final TeamModel team) {
		
		final ProgressDialog mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setCancelable(false);
        mConnectionProgressDialog.setMessage(getResources().getString(R.string.restarting_app));
        mConnectionProgressDialog.show();
        
		final MemberModel member = SessionManager.getMember(mContext);
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(MemberModel.MEMBER_UID, String.valueOf(member.getUid()));
		paramsMap.put(MemberModel.MEMBER_TOKEN, member.getToken());
		paramsMap.put(MemberModel.MEMBER_TEAM_ID, String.valueOf(team.getTeamId()));
		RequestParams params = new RequestParams(paramsMap);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(mContext, ControllParameter.SELECT_TEAM_UPDATE_URL, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String response = new String(arg2);
				//Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
				if(response.equals("OK Success")){
					member.setTeamId(team.getTeamId());
					SessionManager.setMember(mContext, member);
					doRefeshPage(0);
				}
				
				mConnectionProgressDialog.dismiss();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Boast.makeText(MainActivity.this, NetworkUtils.getConnectivityStatusString(MainActivity.this), Toast.LENGTH_SHORT).show();
				mConnectionProgressDialog.dismiss();
			}
			
		});
		
	}

	public void showSaveModeAppDialog(final Context mContext) {
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_2, null);
		
		RelativeLayout main_action_bar = (RelativeLayout) view.findViewById(R.id.main_action_bar);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, main_action_bar);
		
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		
		RelativeLayout btComfirmOK = (RelativeLayout) view.findViewById(R.id.button_confirm_ok);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, btComfirmOK);
		TextView btComfirmOKTxt = (TextView) view.findViewById(R.id.button_confirm_ok_txt);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, btComfirmOKTxt);
		RelativeLayout btComfirmNO = (RelativeLayout) view.findViewById(R.id.button_confirm_no);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_BACKGROUND_COLOR, btComfirmNO);
		TextView btComfirmNoTxt = (TextView) view.findViewById(R.id.button_confirm_no_txt);
		ThemeUtils.setThemeToView(mContext, ThemeUtils.TYPE_TEXT_COLOR, btComfirmNoTxt);
		
		CheckBox checkBoxAsk = (CheckBox) view.findViewById(R.id.chk_ask);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.save_mode_title));
		Drawable img = mContext.getResources().getDrawable(R.drawable.ic_action_network_cell);
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(mContext.getResources().getString(R.string.save_mode_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSaveMode(mContext, false);
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirmOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSaveMode(mContext, true);
				confirmDialog.dismiss();  
			}

		});
		
		btComfirmNO.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSaveMode(mContext, false);
				confirmDialog.dismiss();  
			}

		});
		
		checkBoxAsk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SessionManager.setSetting(mContext, SessionManager.setting_save_mode_ask, String.valueOf(isChecked));
			}
			
		});
		 
		confirmDialog.setCancelable(false);
		confirmDialog.show();
	}
	
	public void doSaveMode(Context mContext, boolean b) {
		SessionManager.setSetting(mContext, SessionManager.setting_save_mode, String.valueOf(b)); 
		doCreate();
	}
	
}
