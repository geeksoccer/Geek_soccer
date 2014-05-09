package com.excelente.geek_soccer;

import android.content.Context;

public class GetdipSize {
	public static int dip(Context mContext, int SizeInDip) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		int pixels = (int) (SizeInDip * scale + 0.5f);
		return pixels;
	}
}
