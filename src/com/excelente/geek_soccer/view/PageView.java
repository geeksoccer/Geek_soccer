package com.excelente.geek_soccer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.ViewFlipper;

public class PageView extends ViewFlipper{
	
	Adapter adapter;

	public PageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
		if(this.adapter!=null && this.adapter.getCount()>0){
			View child = adapter.getView(0, null, this);
			addView(child, 0);
		}
	}
	
	public void setSelection(int position){
		if(getChildAt(position) == null){
			View child = adapter.getView(position, null, this);
			addView(child, position);
		}
		setDisplayedChild(position);
	}
}
