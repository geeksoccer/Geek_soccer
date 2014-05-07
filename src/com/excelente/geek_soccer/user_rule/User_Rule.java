package com.excelente.geek_soccer.user_rule;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.excelente.geek_soccer.R;

public class User_Rule {
	public static void showRuleDialog(Context mContext) {
		final Dialog confirmDialog = new Dialog(mContext); 
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null);
		TextView title = (TextView)view.findViewById(R.id.dialog_title);
		TextView question = (TextView)view.findViewById(R.id.dialog_question);
		ImageView closeBt = (ImageView) view.findViewById(R.id.close_icon);
		RelativeLayout btComfirm = (RelativeLayout) view.findViewById(R.id.button_confirm);
		
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		confirmDialog.setContentView(view);
		
		title.setText(mContext.getResources().getString(R.string.rule_title));
		Drawable img = mContext.getResources().getDrawable( R.drawable.question_btn );
		img.setBounds( 0, 0, 60, 60 );
		title.setCompoundDrawables( img, null, null, null );
		
		question.setText(mContext.getResources().getString(R.string.rule_detal));
		
		closeBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}

		}); 
		
		btComfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss(); 
			}

		});
		
		confirmDialog.setCancelable(true);
		confirmDialog.show();
	}
}
