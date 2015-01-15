package com.excelente.geek_soccer.view;

import java.util.ArrayList;
import java.util.List;

import com.google.analytics.tracking.android.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.ViewFlipper;

public class PageView extends ViewFlipper{
	
	Adapter adapter;
	private List<Integer> tempPosition;

	public PageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setAdapter(Adapter adapter) {
		this.adapter = adapter;
		tempPosition = new ArrayList<Integer>();
		if(this.adapter!=null && this.adapter.getCount()>0){
			View child = this.adapter.getView(0, null, this);
			addView(child);
			tempPosition.add(0);
		}
	}
	
	public void setSelection(int position){
		//Log.e(""+position);
		if(!tempPosition.contains(position)){
			View child = adapter.getView(position, null, this);
			addView(child);
			tempPosition.add(position);
		}
		
		setDisplayedChild(tempPosition.indexOf(position));
	}
}
