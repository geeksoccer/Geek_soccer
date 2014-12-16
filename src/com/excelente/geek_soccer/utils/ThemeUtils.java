package com.excelente.geek_soccer.utils;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.MemberModel;
import com.excelente.geek_soccer.model.ThemeModel;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ThemeUtils {
	
	public static final int TYPE_LOGO = 1; 
	public static final int TYPE_BACKGROUND_COLOR = 2;
	public static final int TYPE_TEXT_COLOR = 3;

	public static void setThemeToView(Context act, int type, View view){
		MemberModel member = SessionManager.getMember(act);
		ThemeModel theme = member.getTheme();
		switch (type) {
			case TYPE_LOGO:{
				ImageView logo = (ImageView) view;
				if(member.getTeamId() == 0){
					logo.setImageResource(R.drawable.logo_gs);
				}else{
					logo.setImageBitmap(SessionManager.getImageSession(act, theme.getThemeLogo()));
				}
				break;
			}
			case TYPE_BACKGROUND_COLOR:{
				view.setBackgroundColor(Color.parseColor(theme.getThemeColor()));
				break;
			}
			case TYPE_TEXT_COLOR:{
				TextView text = (TextView) view;
				text.setTextColor(Color.parseColor(theme.getThemeTextColor()));
				break;
			}
		}
	}

}
