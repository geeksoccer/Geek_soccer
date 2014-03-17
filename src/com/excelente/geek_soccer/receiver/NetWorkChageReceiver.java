package com.excelente.geek_soccer.receiver;

import com.excelente.geek_soccer.utils.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetWorkChageReceiver extends BroadcastReceiver{
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String status = NetworkUtils.getConnectivityStatusString(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}

}
