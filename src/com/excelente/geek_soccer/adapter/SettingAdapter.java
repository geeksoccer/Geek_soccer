package com.excelente.geek_soccer.adapter;

import java.util.List;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.SettingModel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter{

	Activity activity;
	List<SettingModel> settingList;
	
	public SettingAdapter(Activity activity, List<SettingModel> settingList) {
		this.activity = activity;
		this.settingList = settingList;
	}
	
	@Override
	public int getCount() {
		return settingList.size();
	}

	@Override
	public Object getItem(int position) {
		return settingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return settingList.indexOf(getItem(position)); 
	}
	
	class ViewHolder{
		TextView tvTopic;
		TextView tvDetail;
		CheckBox cbCheck;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewSetting;
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.setting_page_item, parent, false);
			
			viewSetting = new ViewHolder();
			viewSetting.tvTopic = (TextView)convertView.findViewById(R.id.tv_topic);
			viewSetting.tvDetail = (TextView)convertView.findViewById(R.id.tv_detail);
			viewSetting.cbCheck = (CheckBox)convertView.findViewById(R.id.cb_setting);
			
			convertView.setTag(viewSetting);
		}else{
			viewSetting = (ViewHolder) convertView.getTag();
		} 
		
		final SettingModel sm = (SettingModel) getItem(position);
		
		viewSetting.tvTopic.setText(sm.getTopic());
		viewSetting.tvDetail.setText(sm.getDetail());
		
		if(sm.getShowCheckBox() == 0){
			viewSetting.cbCheck.setVisibility(View.GONE);
		}else{
			viewSetting.cbCheck.setVisibility(View.VISIBLE);
		}
		
		if(position>0){
			viewSetting.cbCheck.setChecked(Boolean.valueOf(sm.getVal()));
			if(Boolean.valueOf(sm.getVal())){
				viewSetting.tvDetail.setText(sm.getDetail());
			}else{
				viewSetting.tvDetail.setText(sm.getDetail2());
			}
		}
		
		viewSetting.cbCheck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox chk = (CheckBox)v;
				SessionManager.setSetting(activity, sm.getTag(), String.valueOf(chk.isChecked()));
				if(chk.isChecked()){
					viewSetting.tvDetail.setText(sm.getDetail());
				}else{
					viewSetting.tvDetail.setText(sm.getDetail2());
				}
			}
		});
		
		return convertView;
		
	}

}
