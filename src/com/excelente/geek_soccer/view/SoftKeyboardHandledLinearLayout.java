package com.excelente.geek_soccer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SoftKeyboardHandledLinearLayout extends LinearLayout {

	  public SoftKeyboardHandledLinearLayout(final Context context, final AttributeSet attrs) {
	    super(context, attrs);
	  }

	  public SoftKeyboardHandledLinearLayout(Context context) {
	    super(context);
	  }

	  private OnSoftKeyboardListener onSoftKeyboardListener;

	  @Override
	  protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
	    if (onSoftKeyboardListener != null) {
	      final int newSpec = MeasureSpec.getSize(heightMeasureSpec); 
	      final int oldSpec = getMeasuredHeight();
	      // If layout became smaller, that means something forced it to resize. Probably soft keyboard :)
	      if (oldSpec > newSpec){
	        onSoftKeyboardListener.onShown();
	      } else if(oldSpec < newSpec) {
	        onSoftKeyboardListener.onHidden();
	      }else{
	    	onSoftKeyboardListener.onNoEvent();
	      }
	    }
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	  }

	  public final void setOnSoftKeyboardListener(final OnSoftKeyboardListener listener) {
	    this.onSoftKeyboardListener = listener;
	  }

	  // Simplest possible listener :)
	  public interface OnSoftKeyboardListener {
	    public void onShown();
	    public void onHidden();
	    public void onNoEvent();
	  }
	}
