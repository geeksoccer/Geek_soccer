package com.excelente.geek_soccer.adapter;

import java.io.File;
import java.util.List;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.model.HilightItemModel;
import com.excelente.geek_soccer.model.HilightModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HilightVdoAdapter extends BaseAdapter{
	
	Context context;
	List<HilightItemModel> hilightItemList;
	HilightModel hilight;
	
	public HilightVdoAdapter(Context context, List<HilightItemModel> hilightItemList, HilightModel hilight) {
		this.context = context;
		this.hilightItemList = hilightItemList;
		this.hilight = hilight;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		HilightItemModel hilightItem = (HilightItemModel) getItem(pos);
		
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.hilight_vdo, parent, false);
		
		File cachImage = ImageLoader.getInstance().getDiscCache().get(hilight.getHilightImage());
		ImageView hilightVdoImageview = (ImageView) convertView.findViewById(R.id.hilight_vdo_imageview);
		Drawable drawImage = Drawable.createFromPath(cachImage.getAbsolutePath());
		hilightVdoImageview.setBackgroundDrawable(drawImage);
		
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
