package com.excelente.geek_soccer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.DialogUtil;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
	
	boolean customTitleSupported;
	private CustomViewPager mViewPager;
	private PagerAdapter mPagerAdapter;

	Button news_tab;
	Button live_tab;
	Button chat_tab;
	Button score_board_tab;
	Button game_tab;
	
	ImageView menu_btn;  
	ImageView logout_btn;
	ImageView news_btn;
	LinearLayout TeamLogo;
	
	LinearLayout Content_view;
	Activity mContext;
	private TextView title_bar;
	private static Intent serviceIntent; 
	private static ControllParameter data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!SessionManager.hasMember(MainActivity.this)){
			finish();
		}
		
		data = ControllParameter.getInstance(this);
		vidateAskRateApp();
		
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(MainActivity.this).getTeamId());
		
		serviceIntent = new Intent(this, UpdateService.class);
		serviceIntent.putExtra(MemberModel.MEMBER_KEY, (Serializable)SessionManager.getMember(MainActivity.this)); 
		startService(serviceIntent);
		
		setContentView(R.layout.main);
		mContext = this;
		this.intialiseViewPager();
		
		//------------Ched: ทำไว้เพื่อ ทดลองกด ดูข่าวทีมอื่นจากเมนู  (For View News Anyone)-----------------------------
				menu_setting();
		//------------Ched: ทำไว้เพื่อ ทดลองกด ดูข่าวทีมอื่นจากเมนู  (For View News Anyone)-----------------------------
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
			Page_Select(0, true);
		}else if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 2000){
			Page_Select(4, true);
		}else if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 4600){
			Page_Select(1, true);
		}
	}

	//----------------------Ched: (For Admin Member)-----------------------------

	private void Team_LogoSetting(){
		TeamLogo = (LinearLayout)findViewById(R.id.Up_btn);
		TeamLogo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				if(data.Menu_Layout==null){
					final LinearLayout MainLayout = (LinearLayout)findViewById(R.id.Main_Layout);
					data._Menu_Layout = new SideMenuLayout().CreateMenu(MainLayout, mContext);
					WindowManager.LayoutParams params = new WindowManager.LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
							WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
							WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
									| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
									| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
							PixelFormat.TRANSLUCENT);

					params.gravity = Gravity.LEFT | Gravity.CENTER_HORIZONTAL;
					data.wm = (WindowManager) getSystemService(WINDOW_SERVICE);
					data.wm.addView(data._Menu_Layout, params);
				}else{
					
					if(data.Menu_Layout.getVisibility()==0){
						new SideMenuLayout().hideMenu(mContext);
						//data.wm.removeView(data._Menu_Layout);
					}else if(data.Menu_Layout.getVisibility()==8){
						new SideMenuLayout().showMenu(mContext);
					}
				}
				*/
				final LinearLayout MainLayout = (LinearLayout)findViewById(R.id.Main_Layout);
				data._Menu_Layout = new SideMenuLayout().CreateMenu(MainLayout, mContext);
				WindowManager.LayoutParams params = new WindowManager.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
							| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
							| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
						PixelFormat.TRANSLUCENT);

				params.gravity = Gravity.LEFT | Gravity.CENTER_HORIZONTAL;
				data.wm = (WindowManager) getSystemService(WINDOW_SERVICE);
				data.wm.addView(data._Menu_Layout, params);
			}
		});
	}
	private void menu_setting() {
		menu_btn = (ImageView) findViewById(R.id.Menu_btn);
		
		if(SessionManager.getMember(MainActivity.this).getRole() == 1){
			menu_btn.setVisibility(View.VISIBLE);
		}else{
			menu_btn.setVisibility(View.INVISIBLE);
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
	}
	//----------------------Ched:(For Admin Member) -----------------------------

	private void intialiseViewPager() {
		List<Fragment> fragments = new Vector<Fragment>();
		
		fragments.add(Fragment.instantiate(this, News_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Live_Score_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Chat_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Table_Page.class.getName()));
		fragments.add(Fragment.instantiate(this, Hilight_Page.class.getName()));
		
		this.mPagerAdapter = new PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		this.mViewPager = (CustomViewPager) super.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
		
	}
	
	public void tab_setting(){
		news_tab = (Button)findViewById(R.id.News);
		live_tab = (Button)findViewById(R.id.Live_Score);
		chat_tab = (Button)findViewById(R.id.Chats);
		score_board_tab = (Button)findViewById(R.id.Score_Board);
		game_tab = (Button)findViewById(R.id.Game);
		
		//Content_view = (LinearLayout)findViewById(R.id.Contain_Layout);
		Page_Select(0, true);
		news_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(0, true);
			}
		});
		live_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(1, true);
			}
		});
		chat_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(2, true);
			}
		});
		score_board_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(3, true);
			}
		});
		game_tab.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Page_Select(4, true);
			}
		});
	}
	
	public void Page_Select(int index, boolean by_Selected){
		//Content_view.removeAllViews();
		data.fragement_Section_set(index);
		this.mViewPager.setPagingEnabled(true);
		this.mViewPager.setOffscreenPageLimit(4);
		
		if(index==0){
			data.PageNameSelected = getResources().getString(R.string.title_bar_news);
			news_tab.setBackgroundResource(R.drawable.news_h);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==1){
			data.PageNameSelected = getResources().getString(R.string.title_bar_live_score);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore_h);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==2){
			data.PageNameSelected = getResources().getString(R.string.title_bar_chat);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat_h);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==3){
			data.PageNameSelected = getResources().getString(R.string.title_bar_score_broads);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board_h);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==4){
			data.PageNameSelected = getResources().getString(R.string.title_bar_hilight);
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon_select);
		}
		title_bar.setText(data.PageNameSelected);
		if(by_Selected){
			MainActivity.this.mViewPager.setCurrentItem(index);
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		Page_Select(arg0, false);
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
					new SideMenuLayout().hideMenu(mContext);
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
				//android.os.Process.killProcess(android.os.Process.myPid());
				mContext.finish(); 
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
	
}
