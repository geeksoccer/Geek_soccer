package com.excelente.geek_soccer;

import java.util.ArrayList;
import java.util.List;

import com.excelente.geek_soccer.adapter.SettingAdapter;
import com.excelente.geek_soccer.model.SettingModel;
import com.excelente.geek_soccer.utils.ThemeUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Setting_Page extends Activity implements OnClickListener, OnItemClickListener{
	
	private LinearLayout backBtn;
	private ListView lvSetting;
	private SettingAdapter settingAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ThemeUtils.setThemeByTeamId(this, SessionManager.getMember(this).getTeamId());
		setContentView(R.layout.setting_page);
		overridePendingTransition(R.anim.in_trans_left_right, R.anim.out_trans_right_left);
		initView();
	}

	private void initView() {
		backBtn = (LinearLayout) findViewById(R.id.Up_btn); 
		backBtn.setOnClickListener(this);
		
		lvSetting = (ListView) findViewById(R.id.lv_setting);
		settingAdapter = new SettingAdapter(this, getListSettingModel());
		lvSetting.setAdapter(settingAdapter);
		lvSetting.setOnItemClickListener(this);
	}

	private List<SettingModel> getListSettingModel() {
		List<SettingModel> settingList = new ArrayList<SettingModel>();
		
		String[] tags = getResources().getStringArray(R.array.setting_tag_list);
		String[] topics = getResources().getStringArray(R.array.setting_topic_list);
		String[] details = getResources().getStringArray(R.array.setting_detail_list);
		int[] showcbs = getResources().getIntArray(R.array.setting_showcb_list);
		
		for (int i = 0; i < topics.length; i++) {
			String strVal = SessionManager.getSetting(this, tags[i]);
			if(strVal.equals("null")){
				if(i==0){
					strVal = "0"; 
					topics[i] = getResources().getStringArray(R.array.lang_list)[Integer.valueOf(strVal)];
				}else
					strVal = "true"; 
				
				SessionManager.setSetting(this, tags[i], strVal);
			}else{
				if(i==0){
					topics[i] = getResources().getStringArray(R.array.lang_list)[Integer.valueOf(strVal)];
				}
			}
			
			SettingModel sm = new SettingModel(tags[i], topics[i], details[i], strVal, showcbs[i]);
			settingList.add(sm);
		}
		
		return settingList;
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.Up_btn:
				onBackPressed();
				break;
		}
		
	}
	
	private void showChoise(final SettingModel sm) { 
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		dialog.setSingleChoiceItems(getResources().getStringArray(R.array.lang_list), Integer.valueOf(sm.getVal()), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(Integer.valueOf(sm.getVal()) != which)
					showChangeLangDialog(sm, which);
			}
			
		});
		
		dialog.create();
		dialog.show();
	}
	
	protected void showChangeLangDialog(final SettingModel sm, final int which) {
		final Dialog confirmDialog = new Dialog(this); 
		
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		 
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(this.getResources().getString(R.string.change_lang_title));
		Drawable img = this.getResources().getDrawable( R.drawable.ic_settings_language );
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(this.getResources().getString(R.string.change_lang_question));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				switch (which) {
					case 0:
						SessionManager.setSetting(getApplicationContext(), sm.getTag(), "0");
						sm.setTopic(getResources().getStringArray(R.array.lang_list)[0]);
						sm.setVal("0");
						SessionManager.setLangApp(getApplicationContext()); 
						settingAdapter.notifyDataSetChanged();
						break;
					case 1:
						SessionManager.setSetting(getApplicationContext(), sm.getTag(), "1");
						sm.setTopic(getResources().getStringArray(R.array.lang_list)[1]);
						sm.setVal("1");
						SessionManager.setLangApp(getApplicationContext());
						settingAdapter.notifyDataSetChanged();
						break;
				}
				
				confirmDialog.dismiss();
				Intent intent=new Intent();  
                intent.putExtra("COMMAND_APP", "Restart App");  
                setResult(2,intent);  
                finish();
			}

		});
		
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_trans_right_left, R.anim.out_trans_left_right);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position==0){
			SettingModel sm = (SettingModel) settingAdapter.getItem(position);
			showChoise(sm);
		}
	}
}
