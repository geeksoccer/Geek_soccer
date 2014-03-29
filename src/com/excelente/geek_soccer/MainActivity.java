package com.excelente.geek_soccer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.service.UpdateService;
import com.excelente.geek_soccer.utils.NetworkUtils;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
	
	boolean customTitleSupported;
	private CustomViewPager mViewPager;
	private PagerAdapter mPagerAdapter;

	Button news_tab;
	Button live_tab;
	Button chat_tab;
	Button score_board_tab;
	Button game_tab;
	
	Button menu_btn;
	ImageView logout_btn;
	ImageView news_btn;
	
	LinearLayout Content_view;
	Context mContext;
	private TextView title_bar;
	private static ControllParameter data = ControllParameter.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemeUtils.setThemeByTeamId(this, MemberSession.getMember().getTeamId());
		
		Intent serviceIntent = new Intent(this, UpdateService.class);
		serviceIntent.putExtra(MemberModel.MEMBER_KEY, (Serializable)MemberSession.getMember()); 
		startService(serviceIntent);
		
		setContentView(R.layout.main);
		mContext = this;
		this.intialiseViewPager();
		
		//------------Ched: ทำไว้เพื่อ ทดลองกด ดูข่าวทีมอื่นจากเมนู  (For View News Anyone)-----------------------------
				menu_setting();
		//------------Ched: ทำไว้เพื่อ ทดลองกด ดูข่าวทีมอื่นจากเมนู  (For View News Anyone)-----------------------------
				
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
	
	private void setPageFromNotification() {
		if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 1000){
			Page_Select(0, true);
		}else if(getIntent().getIntExtra(UpdateService.NOTIFY_INTENT, 1000) == 1000){
			Page_Select(4, true);
		}
	}

	//----------------------Ched: (For Admin Member)-----------------------------

	private void menu_setting() {
		menu_btn = (Button)findViewById(R.id.Menu_btn);
		menu_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(MemberSession.getMember().getRole() == 1){
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("Select News Team");
					
					CharSequence[] teams = new CharSequence[]{"Arsenal", "Chelsea","Liverpool", "ManU", "Others"};
					dialog.setSingleChoiceItems(teams, MemberSession.getMember().getTeamId()-1, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
								case 0:
									MemberSession.getMember().setTeamId(1);
									break;
								case 1:
									MemberSession.getMember().setTeamId(2);
									break;
								case 2:
									MemberSession.getMember().setTeamId(3);
									break;
								case 3:
									MemberSession.getMember().setTeamId(4);
									break;
								case 4:
									MemberSession.getMember().setTeamId(5);
									break;
							}
							 
							ThemeUtils.setThemeByTeamId(MainActivity.this, MemberSession.getMember().getTeamId());
							doRefeshPage(0);
							dialog.dismiss();
						}
						
					});
					
					dialog.create();
					dialog.show();
				}
			}
		});
		
		logout_btn = (ImageView)findViewById(R.id.Logout_btn);
		logout_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				if(NetworkUtils.isNetworkAvailable(mContext)){
					new Sign_In_Page().new doSignOutTask(MainActivity.this).execute(MemberSession.getMember());
				}else{
					Toast.makeText(mContext, NetworkUtils.getConnectivityStatusString(mContext), Toast.LENGTH_SHORT).show();
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
			title_bar.setText("News");
			news_tab.setBackgroundResource(R.drawable.news_h);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			//game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==1){
			title_bar.setText("Livescore");
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore_h);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==2){
			title_bar.setText("Chat Room");
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat_h);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==3){
			title_bar.setText("Score Board");
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board_h);
			game_tab.setBackgroundResource(R.drawable.hilight_icon);
		}else if(index==4){
			title_bar.setText("Hilight");
			news_tab.setBackgroundResource(R.drawable.news);
			live_tab.setBackgroundResource(R.drawable.livescore);
			chat_tab.setBackgroundResource(R.drawable.chat);
			score_board_tab.setBackgroundResource(R.drawable.board);
			game_tab.setBackgroundResource(R.drawable.hilight_icon_select);
		}

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
	
	@Override
	protected void onResume() {
		super.onResume();
		//this.overridePendingTransition(R.drawable.ani_out, R.drawable.ani_alpha);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			data.app_Status=false;
			if(data.socket_All!=null){
				if(data.socket_All.isConnected()){
					data.socket_All.disconnect();
				}
			}
			if(data.socket_Team!=null){
				if(data.socket_Team.isConnected()){
					data.socket_Team.disconnect();
				}
			}
			if(data.socket_LiveScore!=null){
				if(data.socket_LiveScore.isConnected()){
					data.socket_LiveScore.disconnect();
				}
			}
			finish();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_HOME){
			if(data.socket_All!=null){
				if(data.socket_All.isConnected()){
					data.socket_All.disconnect();
				}
			}
			if(data.socket_Team!=null){
				if(data.socket_Team.isConnected()){
					data.socket_Team.disconnect();
				}
			}
			if(data.socket_LiveScore!=null){
				if(data.socket_LiveScore.isConnected()){
					data.socket_LiveScore.disconnect();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
