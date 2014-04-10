package com.excelente.geek_soccer;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SideMenuLayout{
	private static ControllParameter data = ControllParameter.getInstance();
	public LinearLayout CreateMenu(LinearLayout MainLayout, final Context mContext) {
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
		
		Button btn = (Button)data.Menu_View.findViewById(R.id.Profile);
		data.Menu_Layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideMenu(mContext);
			}
		});
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(mContext, "Menu1", Toast.LENGTH_LONG).show();
			}
		});
		Button menu_btn = (Button)MenuLayV.findViewById(R.id.Menu_btn);
		menu_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu(mContext);
			}
		});
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
			}
		});
		data.Menu_View.startAnimation(out);
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
}
