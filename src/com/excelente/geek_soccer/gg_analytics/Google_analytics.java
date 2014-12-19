package com.excelente.geek_soccer.gg_analytics;

import java.util.HashMap;

import com.excelente.geek_soccer.R;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.Application;

public class Google_analytics extends Application{
	/*define your web property ID obtained after profile creation for the app*/
	private String appId = "UA-57798481-1";
	
	/*Analytics instance*/
	GoogleAnalytics analyticsInstance;
	
	/*Analytics tracker instance*/
	Tracker tracker;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//get the singleton tracker instance
		analyticsInstance = GoogleAnalytics.getInstance(getApplicationContext());
		tracker = analyticsInstance.getTracker(appId);
		GAServiceManager.getInstance().setDispatchPeriod(30);

		//your app specific code goes here
	}

	/* This is getter for tracker instance. This is called in activity to get reference to tracker instance.*/
	public Tracker getTracker() {
		return tracker;
	}
    
}
