package com.excelente.geek_soccer.receiver;


import com.excelente.geek_soccer.SessionManager;
import com.excelente.geek_soccer.service.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartReceiver extends BroadcastReceiver {
    public void onReceive(Context arg0, Intent arg1) {
    	if(SessionManager.hasMember(arg0)){
    		Intent intent = new Intent(arg0, UpdateService.class);
    		arg0.startService(intent);
    	}
    }
}
