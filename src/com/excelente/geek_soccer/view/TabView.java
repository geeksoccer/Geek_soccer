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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabView extends LinearLayout{
	
	private List<View> itemList;
	private List<View> itemList2;
	private OnTabChangedListener onTabChangedListener;
	private int currentTab;
	private LinearLayout tabLinearHorizontal;
	private LinearLayout tabLinearHorizontalInHScroll;
	private HorizontalScrollView horizontalScrollView;
	
	public interface OnTabChangedListener{
		public void onTabChanged(int position);
	}
	
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		itemList = new ArrayList<View>();
		itemList2 = new ArrayList<View>();
		createLinearHoriZontal(context);
		createHorizontalScroll(context);
		createLine(context);
		Log.e("TabView", "TabView");
	}

	private void createHorizontalScroll(Context context) {
		 
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    horizontalScrollView = new HorizontalScrollView(context); 
	    horizontalScrollView.setLayoutParams(params);
	    horizontalScrollView.setFillViewport(true);
	    horizontalScrollView.setSmoothScrollingEnabled(true);
	    horizontalScrollView.setHorizontalScrollBarEnabled(false);
		addView(horizontalScrollView);
	
		params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tabLinearHorizontalInHScroll = new LinearLayout(context);
		tabLinearHorizontalInHScroll.setLayoutParams(params);
		tabLinearHorizontalInHScroll.setOrientation(LinearLayout.HORIZONTAL);
		horizontalScrollView.addView(tabLinearHorizontalInHScroll);
	}
	
	private void createLinearHoriZontal(Context context) {
	
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tabLinearHorizontal = new LinearLayout(context);
		tabLinearHorizontal.setLayoutParams(params);
		tabLinearHorizontal.setOrientation(LinearLayout.HORIZONTAL);
		addView(tabLinearHorizontal);
	}
	
	private void createLine(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) convertDpToPixel(1.5f, context));
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

	public void addTab(String label){
		final int position = itemList.size();
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		View tab1 = createViewTab(position, label, params);
		tabLinearHorizontal.addView(tab1);
		itemList2.add(tab1);
		
		LinearLayout.LayoutParams paramsInHScroll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		View tab = createViewTab(position, label, paramsInHScroll);
	    tabLinearHorizontalInHScroll.addView(tab);
	    itemList.add(tab);
	
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed){
			if(isWidthMoreScreen()){
				horizontalScrollView.setVisibility(View.VISIBLE);
				tabLinearHorizontal.setVisibility(View.GONE);
			}else{
				horizontalScrollView.setVisibility(View.GONE);
	    		tabLinearHorizontal.setVisibility(View.VISIBLE);
			}
			
			setSelectedTab(getCurrentTab());
		}
	}
	
	@SuppressLint("InflateParams") 
	private View createViewTab(final int position, String label, LayoutParams params) {
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
	    
	    return tab;
	}

	private boolean isWidthMoreScreen() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay(); 
		int widthScreen = display.getWidth();
		int widthHorizontalScrollView = getRightXIndex(getCountItem()-1);
		Log.e("widthScreen", widthScreen + " " + widthHorizontalScrollView);
		if(widthHorizontalScrollView > widthScreen){
			return true;
		}
		
		return false;
	}

	public void setSelectedTab(int index){
		List<View> viewList = new ArrayList<View>();
		if(isWidthMoreScreen()){
			viewList = itemList;
		}else{
			viewList = itemList2;
		}
		
		for (int i = 0; i < viewList.size(); i++) {
			View v = viewList.get(i).findViewById(R.id.selected);
			TextView text = (TextView) viewList.get(i).findViewById(R.id.text);
			
			if(i == index){
				text.setTextColor(Color.parseColor(getContext().getResources().getString(R.color.black)));
				v.setVisibility(View.VISIBLE);
				if(isWidthMoreScreen()){
					focusAndScrollView(v, index);
				}
			}else{
				text.setTextColor(Color.parseColor(getContext().getResources().getString(R.color.gray)));
				v.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void focusAndScrollView(View v, int index) {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay(); 
		int widthScreen = display.getWidth();
		int widthHorizontalScrollView = horizontalScrollView.getWidth();
		int scollX = 0;
		if(index < 1){
			scollX = 0;
		}else if(index > (getCountItem()-1)-1){
        	scollX = getRightXIndex(index);
		}else{
			int halfScreen = widthScreen/2;
			int halfXIndex = getHalfXIndex(index);
			if(halfXIndex > halfScreen){
				scollX = getHalfXIndex(index) - halfScreen;
			}else{
				if(halfXIndex > (widthHorizontalScrollView/2)){
					scollX = getRightXIndex(index);
				}else{
					scollX = 0;
				}
			}
        	
		}
		horizontalScrollView.smoothScrollTo(scollX, 0);
	}
	
	public int getHalfXIndex(int position) {
		int width = 0;
		for (int i = 0; i < position+1; i++) {
			View v = getItemList().get(i).findViewById(R.id.selected);
			if(i==position){
				width += (v.getWidth()/2);
			}else{
				width += v.getWidth();
			}
		}
		return width;
	}
	
	public int getRightXIndex(int position) {
		int width = 0;
		for (int i = 0; i < position+1; i++) {
			View v = getItemList().get(i).findViewById(R.id.selected);
			width += v.getWidth();
		}
		return width;
	}
	
	public int getLeftXIndex(int position) {
		int width = 0;
		for (int i = 0; i < position+1; i++) {
			View v = getItemList().get(i).findViewById(R.id.selected);
			if(i!=position){
				width += v.getWidth();
			}
		}
		return width;
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
