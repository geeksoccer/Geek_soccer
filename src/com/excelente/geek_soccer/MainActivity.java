package com.excelente.geek_soccer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.sideMenu.SideMenuMain;
import com.excelente.geek_soccer.utils.DialogUtil;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
	
	boolean customTitleSupported;
	public static CustomViewPager mViewPager;
	public static PagerAdapter mPagerAdapter;

	public static Button news_tab, live_tab, chat_tab, score_board_tab, game_tab;
	
	ImageView menu_btn;  
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
	
	@Override
	protected void onResume() {
		super.onResume();
		if(saveMode_btn!=null){ 
			String saveMode = SessionManager.getSetting(this, SessionManager.setting_save_mode);
			if(saveMode.equals("true")){
				saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode_selected);
			}else{
				saveMode_btn.setBackgroundResource(R.drawable.bg_save_mode);
			}
		}
	}
	
	private void doCreate() {
		mContext = this;
		data = ControllParameter.getInstance(this);
		vidateAskRateApp(); 
		
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(MainActivity.this).getTeamId());
		
		serviceIntent = new Intent(this, UpdateService.class);
		serviceIntent.putExtra(MemberModel.MEMBER_KEY, (Serializable)SessionManager.getMember(MainActivity.this)); 
		startService(serviceIntent);
		
		setContentView(R.layout.main);
		
		this.intialiseViewPager();
		
		//------------Ched: ��������� ���ͧ�� �٢��Ƿ����蹨ҡ����  (For View News Anyone)-----------------------------
				menu_setting();
		//------------Ched: ��������� ���ͧ�� �٢��Ƿ����蹨ҡ����  (For View News Anyone)-----------------------------
				Team_LogoSetting();
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

	private void Team_LogoSetting(){
		TeamLogo = (LinearLayout)findViewById(R.id.Up_btn);
		TeamLogo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new SideMenuMain().showMenu(MainActivity.this);
			}
		});
	}
	
	public void SideMenuStandBy(){
		final RelativeLayout MainLayout = (RelativeLayout) findViewById(R.id.Main_Layout);
		if(MainLayout!=null){
			data._Menu_Layout = new SideMenuMain().CreateMenu(MainActivity.this);
			data.Menu_Layout.setVisibility(RelativeLayout.GONE);
			MenuWidth = GetdipSize.dip(mContext, 170);
			data.Menu_View.setX(-MenuWidth);
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			float screenWidth = metrics.widthPixels;
			TenPerScreenWidth = (float) (screenWidth*(5.0f/100.0f));
		}
	}
	
	private void menu_setting() {
		menu_btn = (ImageView) findViewById(R.id.Menu_btn);
		
		
		if(SessionManager.getMember(MainActivity.this).getRole() == 1){
			menu_btn.setVisibility(View.VISIBLE);
		}else{
			menu_btn.setVisibility(View.GONE);
		}
		
		menu_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(SessionManager.getMember(MainActivity.this).getRole() == 1){
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle(getResources().getString(R.string.dailog_select_team)); 
					dialog.setSingleChoiceItems(getResources().getStringArray(R.array.team_list), SessionManager.getMember(MainActivity.this).getTeamId()-1, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MemberModel member;
							switch (which) {
								case 0:
									member = SessionManager.getMember(MainActivity.this);
									member.setTeamId(1);
									SessionManager.setMember(MainActivity.this, member);
									break;
								case 1:
									member = SessionManager.getMember(MainActivity.this);
									member.setTeamId(2);
									SessionManager.setMember(MainActivity.this, member);
									break;
								case 2:
									member = SessionManager.getMember(MainActivity.this);
									member.setTeamId(3);
									SessionManager.setMember(MainActivity.this, member);
									break;
								case 3:
									member = SessionManager.getMember(MainActivity.this);
									member.setTeamId(4);
									SessionManager.setMember(MainActivity.this, member);
									break;
								case 4:
									member = SessionManager.getMember(MainActivity.this);
									member.setTeamId(5);
									SessionManager.setMember(MainActivity.this, member);
									break;
							}
							 
							ThemeUtils.setThemeByTeamId(MainActivity.this, SessionManager.getMember(MainActivity.this).getTeamId());
							doRefeshPage(0);
							dialog.dismiss();
						}
						
					});
					
					dialog.create();
					dialog.show();
				}
			}
		});
		 
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
	}
	
	private void doRefeshPage(int indexPage) {
		/*Fragment currentFragment = mPagerAdapter.getItem(indexPage);
	    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
	    fragTransaction.detach(currentFragment);
	    fragTransaction.attach(currentFragment);
	    fragTransaction.commit();*/
		Intent re = new Intent(mContext, MainActivity.class);
		startActivity(re);
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	//----------------------Ched:(For Admin Member) -----------------------------

	private void intialiseViewPager() {
		List<Fragment> fragments = new Vector<Fragment>();
		
		fragments.add(Fragment.instantiate(this, News_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Live_Score_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Chat_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Table_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Hilight_Page.class.getName()));
		
		mPagerAdapter = new PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		mViewPager = (CustomViewPager) super.findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
		
	}
	
	public void tab_setting(){
		news_tab = (Button)findViewById(R.id.News);
		live_tab = (Button)findViewById(R.id.Live_Score);
		chat_tab = (Button)findViewById(R.id.Chats);
		score_board_tab = (Button)findViewById(R.id.Score_Board);
		game_tab = (Button)findViewById(R.id.Game);
		
		//Content_view = (LinearLayout)findViewById(R.id.Contain_Layout);
		Page_Select(0, true, this);
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
	}
	
	public static void Page_Select(int index, boolean by_Selected, Context mContext){
		//Content_view.removeAllViews();
		data.fragement_Section_set(index);
		mViewPager.setPagingEnabled(true);
		mViewPager.setOffscreenPageLimit(4);
		
		if(index==0){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_news);
			news_tab.setBackgroundResource(R.drawable.news_h);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==1){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_live_score);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore_h);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==2){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_chat);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat_h);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==3){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_score_broads);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board_h);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==4){
			data.PageNameSelected = mContext.getResources().getString(R.string.title_bar_hilight);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon_select);
		}
		title_bar.setText(data.PageNameSelected);
		if(by_Selected){
			mViewPager.setCurrentItem(index);
		}
		
	}
	int OldState = 0;
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(OldState==1 
				&& arg0==0
				&& mViewPager.getCurrentItem()==0 ){
			new SideMenuMain().showMenu(mContext);
		}
		OldState = arg0;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		Page_Select(arg0, false, this);
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
			if(data.Menu_Layout!=null){
				if(data.Menu_Layout.getVisibility()==0){
					new SideMenuMain().hideMenu(mContext);
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
				
			}else{
				if((data.Sticker_Layout_Stat_team || data.Sticker_Layout_Stat_All) && data.fragement_Section_get()==2){
					if(data.Sticker_Layout_Stat_team){
						data.Sticker_Layout_Stat_team = false;
					}else if(data.Sticker_Layout_Stat_All){
						data.Sticker_Layout_Stat_All = false;
					}
				}else{
					data.app_Status=false;
					showCloseAppDialog(this);
				}
			}
		}
		
		return true;
	}
	
	public static void showCloseAppDialog(final Activity mContext) { 
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		
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
        }  
	}
	
	public void showSaveModeAppDialog(final Context mContext) {
		ThemeUtils.setThemeByTeamId(mContext, SessionManager.getMember(mContext).getTeamId());
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm_2, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		
		RelativeLayout btComfirmOK = (RelativeLayout) view.findViewById(R.id.button_confirm_ok);
		RelativeLayout btComfirmNO = (RelativeLayout) view.findViewById(R.id.button_confirm_no);
		
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
