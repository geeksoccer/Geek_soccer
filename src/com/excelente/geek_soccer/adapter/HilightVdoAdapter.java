package com.excelente.geek_soccer.adapter;

import java.util.List;

import com.excelente.geek_soccer.Hilight_Item_Page;
import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.model.HilightItemModel;
import com.excelente.geek_soccer.model.HilightModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HilightVdoAdapter extends BaseAdapter{
	
	Hilight_Item_Page context;
	List<HilightItemModel> hilightItemList;
	HilightModel hilight;
	
	public HilightVdoAdapter(Hilight_Item_Page context, List<HilightItemModel> hilightItemList, HilightModel hilight) {
		this.context = context;
		this.hilightItemList = hilightItemList;
		this.hilight = hilight;
	}
	
	@SuppressLint("ViewHolder") 
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		HilightItemModel hilightItem = (HilightItemModel) getItem(pos);
		
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.hilight_vdo, parent, false);
		
		final ImageView hilightVdoImageview = (ImageView) convertView.findViewById(R.id.hilight_vdo_imageview);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final Bitmap bm = SessionManager.getImageSession(context, hilight.getHilightImage().replace(".gif", ".png"));
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(bm!=null){
							Drawable drawable = new BitmapDrawable(bm);
							hilightVdoImageview.setBackgroundDrawable(drawable);
						}
					}
				});
			}
		}).start();
		
		TextView hilightVdoTextview = (TextView) convertView.findViewById(R.id.hilight_vdo_textview);
		hilightVdoTextview.setText(hilightItem.getHilightItemTopic());
		
		return convertView;
	}

	@Override
	public int getCount() {
		return hilightItemList.size();
	}

	@Override
	public Object getItem(int pos) {
		return hilightItemList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return hilightItemList.indexOf(getItem(pos));  
	}

}
