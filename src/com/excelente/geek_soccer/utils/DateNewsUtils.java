package com.excelente.geek_soccer.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.excelente.geek_soccer.R;

@SuppressLint("SimpleDateFormat")
public class DateNewsUtils {
	
	public static final String FORMAT_DATETIME_1 = "yyyy-MM-dd HH:mm:ss";
	public final static String FORMAT_DATETIME_2 = "dd/MM/yyyy HH:mm";
	public final static String FORMAT_DATE_1 = "dd/MM/yyyy"; 
	public final static String FORMAT_TIME_1 = "HH:mm"; 
	
	public static String convertDateToUpdateNewsStr(Context context, Date dateUpdate) {
		if(dateUpdate != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			Calendar today = Calendar.getInstance();
			
			Calendar yesterday = Calendar.getInstance();
			yesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 1);
			
			if(sdf.format(dateUpdate).equals(sdf.format(today.getTime()))){
				
				sdf = new SimpleDateFormat(FORMAT_TIME_1);
				String todayNewsStr = context.getResources().getString(R.string.str_today_news) + " " + sdf.format(dateUpdate);
				return todayNewsStr;
				
			}else if(sdf.format(dateUpdate).equals(sdf.format(yesterday.getTime()))){
				
				sdf = new SimpleDateFormat(FORMAT_TIME_1);
				String yesterdayNewsStr = context.getResources().getString(R.string.str_yesterday_news) + " " + sdf.format(dateUpdate);
				return yesterdayNewsStr;
				
			}else{
				
				sdf = new SimpleDateFormat(FORMAT_DATETIME_2);
				return sdf.format(dateUpdate);
				
			}
		}else{
			return "";
		}
		
	}
	
	public static Date convertStrDateTimeDate(String strDateTime){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME_1);
		Date dateTime = null;
		try {
			dateTime = sdf.parse(strDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateTime;
	}
}
