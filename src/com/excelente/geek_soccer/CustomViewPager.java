package com.excelente.geek_soccer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean enabled;
    
    public interface onTouchCallbackClass {
		boolean onTouchcallbackReturn(int action, float posX, float posY);
	}

	public static onTouchCallbackClass onTouchCallbackClass;

	public void registerCallback(onTouchCallbackClass callbackClass) {
		onTouchCallbackClass = callbackClass;
	}
    
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
        if (this.enabled) {
        	return super.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
 
    	if (this.enabled) {
    	    return super.onInterceptTouchEvent(event);
        }
        return true;
    }
 
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
