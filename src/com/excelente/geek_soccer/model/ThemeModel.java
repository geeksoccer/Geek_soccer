package com.excelente.geek_soccer.model;

import java.io.Serializable;

import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.pic_download.DownLiveScorePic;

public class ThemeModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String THEME_ID = "theme_id";
	public static final String THEME_NAME = "theme_name";
	public static final String THEME_NAME_TH = "theme_name_th";
	public static final String THEME_LOGO = "theme_logo";
	public static final String THEME_COLOR = "theme_color";
	public static final String THEME_TEXT_COLOR = "theme_text_color";
	public static final String THEME_CREATE = "theme_create"; 
	
	private int themeId;
	private String themeName;
	private String themeNameTH;
	private String themeColor;
	private String themeTextColor;
	private String themeLogo;
	private String themeCreate;
	
	public ThemeModel() {
	}
	
	public int getThemeId() {
		return themeId;
	}
	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public String getThemeNameTH() {
		return themeNameTH;
	}
	public void setThemeNameTH(String themeNameTH) {
		this.themeNameTH = themeNameTH;
	}
	public String getThemeColor() {
		return themeColor;
	}
	public void setThemeColor(String themeColor) {
		this.themeColor = themeColor;
	}
	public String getThemeTextColor() {
		return themeTextColor;
	}
	public void setThemeTextColor(String themeTextColor) {
		this.themeTextColor = themeTextColor;
	}
	public String getThemeLogo() {
		return themeLogo;
	}
	public void setThemeLogo(String themeLogo) {
		this.themeLogo = themeLogo;
	}
	public String getThemeCreate() {
		return themeCreate;
	}
	public void setThemeCreate(String themeCreate) {
		this.themeCreate = themeCreate;
	}
}
