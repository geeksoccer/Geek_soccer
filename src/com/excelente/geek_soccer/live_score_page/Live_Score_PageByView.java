package com.excelente.geek_soccer.live_score_page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.utils.ThemeUtils;

public class Live_Score_PageByView extends Fragment {
	Context mContext;
	View myView;
	private static ControllParameter data;
	LinearLayout LiveScoreMenu;
	LinearLayout ChatContainV;
	LayoutParams childParam;
	View LiveYesterdayView;
	View LiveTodayView;
	View LiveTomorrowView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		return inflater.inflate(R.layout.chat_page_byview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		data = ControllParameter.getInstance(getActivity());

		myView = getView();
		mContext = myView.getContext();
		
		LiveScoreMenu = (LinearLayout) myView.findViewById(R.id.chatMenuLayout);
		
		setupTab("y", getResources().getString(R.string.str_yesterday_livescore), 0, false);
		setupTab("c", getResources().getString(R.string.str_today_livescore), 0, true);
		setupTab("t", getResources().getString(R.string.str_tomorrow_livescore), 0, false);
		
		ChatContainV = (LinearLayout) myView
				.findViewById(R.id.ContainV);

		childParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		//LiveYesterdayView = new LiveScore_YesterdayView().getView(getActivity());
		LiveTodayView = new LiveScore_TodayView().getView(getActivity());
		//LiveTomorrowView = new LiveScore_TomorrowView().getView(getActivity());

		//ChatContainV.addView(LiveYesterdayView, childParam);
		ChatContainV.addView(LiveTodayView, childParam);
		//ChatContainV.addView(LiveTomorrowView, childParam);

		setCurrentTab(data.liveScore_Cur);
	}

	private void setupTab(final String name, String label, Integer iconId,
			boolean selected) {

		View tab = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_tab, null);
		ImageView image = (ImageView) tab.findViewById(R.id.icon);
		TextView text = (TextView) tab.findViewById(R.id.text);
		View viewSelected = tab.findViewById(R.id.selected);
		View viewLine = tab.findViewById(R.id.view_line);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewSelected);
	    ThemeUtils.setThemeToView(getActivity(), ThemeUtils.TYPE_BACKGROUND_COLOR, viewLine);
		text.setTypeface(null, Typeface.BOLD);
		if (label.equals("")) {
			text.setVisibility(View.GONE);

			final float scale = getActivity().getResources()
					.getDisplayMetrics().density;
			int pixels = (int) (40 * scale + 0.5f);
			image.getLayoutParams().width = pixels;
			image.getLayoutParams().height = pixels;
		}

		if (iconId == 0) {
			image.setVisibility(View.GONE);

			final float scale = getActivity().getResources()
					.getDisplayMetrics().density;
			int pixels = (int) (40 * scale + 0.5f);
			text.getLayoutParams().height = pixels;
		}

		if (selected)
			viewSelected.setVisibility(View.VISIBLE);

		if (iconId != null) {
			image.setImageResource(iconId);
		}
		text.setText(label);

		LayoutParams childParam = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.MATCH_PARENT, 1);

		tab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name.equals("y")) {
					setCurrentTab(0);
				} else if (name.equals("c")) {
					setCurrentTab(1);
				} else if (name.equals("t")) {
					setCurrentTab(2);
				}
			}
		});

		LiveScoreMenu.addView(tab, childParam);
	}

	public void setCurrentTab(int index) {
		for (int i = 0; i < LiveScoreMenu.getChildCount(); i++) {
			if (i == index) {
				LiveScoreMenu.getChildAt(i).findViewById(R.id.selected)
				.setVisibility(View.VISIBLE);
			} else {
				LiveScoreMenu.getChildAt(i).findViewById(R.id.selected)
				.setVisibility(View.INVISIBLE);
			}
		}
		if (index == 0) {
			if(LiveYesterdayView!=null){
				LiveYesterdayView.setVisibility(RelativeLayout.ABOVE);
			}else{
				LiveYesterdayView = new LiveScore_YesterdayView().getView(getActivity());
				ChatContainV.addView(LiveYesterdayView, childParam);
			}
			LiveTodayView.setVisibility(RelativeLayout.GONE);
			if(LiveTomorrowView!=null){
				LiveTomorrowView.setVisibility(RelativeLayout.GONE);
			}
		} else if (index == 1) {
			if(LiveYesterdayView!=null){
				LiveYesterdayView.setVisibility(RelativeLayout.GONE);
			}
			LiveTodayView.setVisibility(RelativeLayout.ABOVE);
			if(LiveTomorrowView!=null){
				LiveTomorrowView.setVisibility(RelativeLayout.GONE);
			}
		}else {
			if(LiveYesterdayView!=null){
				LiveYesterdayView.setVisibility(RelativeLayout.GONE);
			}
			LiveTodayView.setVisibility(RelativeLayout.GONE);
			if(LiveTomorrowView!=null){
				LiveTomorrowView.setVisibility(RelativeLayout.ABOVE);
			}else{
				LiveTomorrowView = new LiveScore_TomorrowView().getView(getActivity());
				ChatContainV.addView(LiveTomorrowView, childParam);
			}
		}
	}
}
