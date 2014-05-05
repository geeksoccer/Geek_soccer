package com.excelente.geek_soccer.adapter;

import java.util.List;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.TeamModel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TeamAdapter extends BaseAdapter implements OnTouchListener{
	
	Activity activity;
	List<TeamModel> teamList;
	
	public TeamAdapter(Activity activity, List<TeamModel> teamList) {
		this.activity = activity;
		this.teamList = teamList;
	}
	
	class ViewHolder{
		TextView teamName;
		ImageView teamLogo;
		RelativeLayout teamColor;
		RelativeLayout teamPress;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		if(convertView==null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.dialog_select_team_item, parent, false);
			view = new ViewHolder();
			view.teamName = (TextView) convertView.findViewById(R.id.team_name);
			view.teamLogo = (ImageView) convertView.findViewById(R.id.team_logo);
			view.teamColor = (RelativeLayout) convertView.findViewById(R.id.team_color);
			view.teamPress = (RelativeLayout) convertView.findViewById(R.id.team_press);
			
			convertView.setTag(view);
		}else{
			view = (ViewHolder) convertView.getTag(); 
		}
		 
		TeamModel team = (TeamModel) getItem(position);
		
		view.teamName.setText(team.getTeamName());
		view.teamLogo.setImageResource(team.getTeamLogo());
		view.teamColor.setBackgroundResource(team.getTeamColor());
		view.teamPress.setOnTouchListener(this);
		
		return convertView;
	}

	@Override
	public int getCount() {
		return teamList.size();
	}

	@Override
	public Object getItem(int position) {
		return teamList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return teamList.indexOf(teamList.get(position)); 
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}
	
}
