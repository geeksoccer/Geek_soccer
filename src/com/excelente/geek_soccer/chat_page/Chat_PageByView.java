package com.excelente.geek_soccer.chat_page;

import com.excelente.geek_soccer.ControllParameter;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
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

public class Chat_PageByView extends Fragment {
	Context mContext;
	View myView;
	int teamID;

	private static ControllParameter data;

	LinearLayout chatMenu;
	View chatTeamV;
	View chatAllV;

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
		teamID = SessionManager.getMember(getActivity()).getTeamId();

		myView = getView();
		mContext = myView.getContext();

		chatMenu = (LinearLayout) myView.findViewById(R.id.chatMenuLayout);

		if (teamID != 5) {
			String teamName = "";
			if (teamID == 1) {
				teamName = getResources().getString(R.string.chat_arsenal);
			} else if (teamID == 2) {
				teamName = getResources().getString(R.string.chat_chelsea);
			} else if (teamID == 3) {
				teamName = getResources().getString(R.string.chat_liverpool);
			} else if (teamID == 4) {
				teamName = getResources().getString(R.string.chat_manu);
			}
			setupTab("Team", teamName, 0, true);
		}

		setupTab("All", getResources().getString(R.string.chat_global), 0,
				false);

		LinearLayout ChatContainV = (LinearLayout) myView
				.findViewById(R.id.ContainV);

		LayoutParams childParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		chatTeamV = new Chat_TeamView().getView(getActivity());
		chatAllV = new Chat_AllView().getView(getActivity());

		ChatContainV.addView(chatTeamV, childParam);
		ChatContainV.addView(chatAllV, childParam);

		setCurrentTab(data.chat_Cur);
	}

	private void setupTab(final String name, String label, Integer iconId,
			boolean selected) {

		View tab = LayoutInflater.from(getActivity()).inflate(
				R.layout.custom_tab, null);
		ImageView image = (ImageView) tab.findViewById(R.id.icon);
		TextView text = (TextView) tab.findViewById(R.id.text);
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

		View viewSelected = tab.findViewById(R.id.selected);
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
				if (name.equals("Team")) {
					setCurrentTab(0);
				} else {
					setCurrentTab(1);
				}
			}
		});
		
		chatMenu.addView(tab, childParam);
	}

	public void setCurrentTab(int index) {
		for (int i = 0; i < chatMenu.getChildCount(); i++) {
			if (i == index) {
				chatMenu.getChildAt(i).findViewById(R.id.selected)
				.setVisibility(View.VISIBLE);
			} else {
				chatMenu.getChildAt(i).findViewById(R.id.selected)
				.setVisibility(View.INVISIBLE);
			}
		}
		if (index == 0) {
			chatTeamV.setVisibility(RelativeLayout.ABOVE);
			chatAllV.setVisibility(RelativeLayout.GONE);
		} else {
			chatAllV.setVisibility(RelativeLayout.ABOVE);
			chatTeamV.setVisibility(RelativeLayout.GONE);
		}
	}
}
