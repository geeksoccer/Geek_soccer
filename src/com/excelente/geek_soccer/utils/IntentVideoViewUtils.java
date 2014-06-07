package com.excelente.geek_soccer.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class IntentVideoViewUtils {
	
	public static void playYoutubeVideo(Context context, String url) {
		IntentVideoViewUtils.viewWithPackageName(context, url, "com.google.android.youtube");
    }
	
	public static void playFacebookVideo(Context context, String videoUrl) {  
		String packageName = "com.android.browser"; 
		//String className = "com.android.browser.BrowserActivity"; 
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//intent.addCategory(Intent.CATEGORY_LAUNCHER); 
		//intent.setClassName(packageName, className);
		
	    try {
	        if (videoUrl.startsWith("https")) {
	            videoUrl = "http" + videoUrl.substring(5);
	        }
	        
	        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(videoUrl);
	        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	        
	        if (isAppInstalled(context, packageName)) {
				intent.setPackage(packageName);
	        }
	        
	        intent.setDataAndType(Uri.parse(videoUrl), mimetype);
	        context.startActivity(intent);
	    } catch (ActivityNotFoundException e) {
	        intent.setData(Uri.parse(videoUrl));
	        context.startActivity(intent);
	    }

	}


    public static void viewWithPackageName(Context context, String url, String packageName) {
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (isAppInstalled(context, packageName)) {
                viewIntent.setPackage(packageName);
            }
            context.startActivity(viewIntent);
        } catch (Exception e) {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(viewIntent);
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }
}
