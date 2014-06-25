package com.excelente.geek_soccer.utils;

import com.excelente.geek_soccer.R;

import android.content.Context;

public class ThemeUtils {
	
	public static final String THEME_DEFAULT = "DEFAULT"; 
	public static final String THEME_ARSENAL = "Aresenal"; 
	public static final String THEME_CHELSEA = "Chelsea"; 
	public static final String THEME_LIVERPOOL = "LIVERPOOL"; 
	public static final String THEME_MANU = "MANU"; 

	public static void setThemeToActivity(Context act, String theme){
		if(theme.equalsIgnoreCase(THEME_DEFAULT)){
			act.setTheme(R.style.Theme_DefaultTheme);
		}else if(theme.equalsIgnoreCase(THEME_ARSENAL)) {
			act.setTheme(R.style.Theme_Aresenal);
	    }else if(theme.equalsIgnoreCase(THEME_CHELSEA)){
	    	act.setTheme(R.style.Theme_Chelsea);
	    }else if(theme.equalsIgnoreCase(THEME_LIVERPOOL)){
	    	act.setTheme(R.style.Theme_Liverpool);
	    }else if(theme.equalsIgnoreCase(THEME_MANU)){
	    	act.setTheme(R.style.Theme_Manu);
	    }
	}
	
	public static void setThemeByTeamId(Context act, int teamId){
		switch (teamId) {
			case 1:
				setThemeToActivity(act, THEME_ARSENAL);
				break;
			case 2:
				setThemeToActivity(act, THEME_CHELSEA);
				break;
			case 3:
				setThemeToActivity(act, THEME_LIVERPOOL);
				break;
			case 4:
				setThemeToActivity(act, THEME_MANU);
				break;
			default:
				setThemeToActivity(act, THEME_DEFAULT);
				break;
		}
	}

}
