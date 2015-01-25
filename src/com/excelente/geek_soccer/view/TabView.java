package com.excelente.geek_soccer.view;

import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabView extends LinearLayout{
	
	private List<View> itemList;
	private OnTabChangedListener onTabChangedListener;
	private int currentTab;
	private LinearLayout tabLinearHorizontal;
	private HorizontalScrollView horizontalScrollView;
	
	public interface OnTabChangedListener{
		public void onTabChanged(int position);
	}
	
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		itemList = new ArrayList<View>();
		createLinearHoriZontal(context);
		Log.e("TabView", "TabView");
	}
	
	private void createHorizontalScroll(Context context) {
		removeAllViews();
		
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    horizontalScrollView = new HorizontalScrollView(context); 
	    horizontalScrollView.setLayoutParams(params);
	    horizontalScrollView.setFillViewport(true);
	    horizontalScrollView.setSmoothScrollingEnabled(true);
	    horizontalScrollView.setHorizontalScrollBarEnabled(false);
		addView(horizontalScrollView);
	
		params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tabLinearHorizontal = new LinearLayout(context);
		tabLinearHorizontal.setLayoutParams(params);
		tabLinearHorizontal.setOrientation(LinearLayout.HORIZONTAL);
		horizontalScrollView.addView(tabLinearHorizontal);
		
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) convertDpToPixel(1.5f, context));
		View line = new View(context);
		line.setLayoutParams(params);
		ThemeUtils.setThemeToView(context, ThemeUtils.TYPE_BACKGROUND_COLOR, line);
		addView(line);
		
		params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < getItemList().size(); i++) {
			View view = getItemList().get(i);
			view.setLayoutParams(params);
		}
	}
	
	private void createLinearHoriZontal(Context context) {
	
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tabLinearHorizontal = new LinearLayout(context);
		tabLinearHorizontal.setLayoutParams(params);
		tabLinearHorizontal.setOrientation(LinearLayout.HORIZONTAL);
		addView(tabLinearHorizontal);
		
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) convertDpToPixel(1.5f, context));
		View line = new View(context);
		line.setLayoutParams(params);
		ThemeUtils.setThemeToView(context, ThemeUtils.TYPE_BACKGROUND_COLOR, line);
		addView(line);
	}
	
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	@SuppressLint("InflateParams") 
	public void addTab(String label){
		final int position = itemList.size();
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		if(getCountItem() == 4){
			params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		View tab = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
		tab.setLayoutParams(params);
	    ImageView image = (ImageView) tab.findViewById(R.id.icon);
	    TextView text = (TextView) tab.findViewById(R.id.text);
	    text.setTypeface(Typeface.DEFAULT_BOLD);
	    text.setTextColor(Color.parseColor(getContext().getResources().getString(R.color.gray)));
	    View viewSelected = tab.findViewById(R.id.selected);
	    View viewLine = tab.findViewById(R.id.view_line);
	    ThemeUtils.setThemeToView(getContext(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewSelected);
	    ThemeUtils.setThemeToView(getContext(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewLine);
	    
	    if(label.equals("")){
	    	text.setVisibility(View.GONE);
	    	
	    	final float scale = getContext().getResources().getDisplayMetrics().density;
	    	int pixels = (int) (40 * scale + 0.5f);
	    	image.getLayoutParams().width=pixels;
	    	image.getLayoutParams().height=pixels;
	    }
	    
	    viewLine.setVisibility(View.GONE);
	    image.setVisibility(View.GONE);
	    text.setText(label);
	    tab.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View v) {
				if(onTabChangedListener!=null){
					onTabChangedListener.onTabChanged(position);
				}
				setSelectedTab(position);
			}
		});
	    
	    tabLinearHorizontal.addView(tab);
	    itemList.add(tab);
	    chkMoreItem();
	}
	
	private void chkMoreItem() {
		if(getCountItem() == 4){
			createHorizontalScroll(getContext());
		}
	}

	public void setSelectedTab(int index){
		for (int i = 0; i < getItemList().size(); i++) {
			View v = getItemList().get(i).findViewById(R.id.selected);
			TextView text = (TextView) getItemList().get(i).findViewById(R.id.text);
			
			if(i == index){
				text.setTextColor(Color.parseColor(getContext().getResources().getString(R.color.black)));
				v.setVisibility(View.VISIBLE);
				if(getCountItem() > 3){
					focusAndScrollView(v, index);
				}
			}else{
				text.setTextColor(Color.parseColor(getContext().getResources().getString(R.color.gray)));
				v.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void focusAndScrollView(View v, int index) {
		int width = v.getWidth();
		int scollX = 0;
		if(index > 2){
        	scollX = (width/2) * (index+1);
		}
		horizontalScrollView.smoothScrollTo(scollX, 0);
	}
	
	public void setCurrentTab(int position) {
		currentTab = position;
		if(onTabChangedListener!=null){
			onTabChangedListener.onTabChanged(position);
		}
		setSelectedTab(position);
	}
	
	public int getCurrentTab() {
		return currentTab;
	}

	public OnTabChangedListener getOnTabChangedListener() {
		return onTabChangedListener;
	}

	public void setOnTabChangedListener(OnTabChangedListener onTabChangedListener) {
		this.onTabChangedListener = onTabChangedListener;
	}
	
	public List<View> getItemList() {
		return itemList;
	}

	public void setItemList(List<View> itemList) {
		this.itemList = itemList;
	}

	public int getCountItem() {
		return itemList.size();
	}

}
