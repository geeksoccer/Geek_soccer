package com.excelente.geek_soccer.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class AnimUtil {
	
	private static Animation fadeIn;
	private static Animation fadeOut;
	
	public static Animation getFadeIn() {
		if(fadeIn == null){
			fadeIn = new AlphaAnimation(0, 1);
			fadeIn.setInterpolator(new DecelerateInterpolator());
		}
		return fadeIn;
	}
	
	public static Animation getFadeOut() {
		if(fadeOut == null){
			fadeOut = new AlphaAnimation(0, 1);
		}
		return fadeOut;
	}
	
	
}
