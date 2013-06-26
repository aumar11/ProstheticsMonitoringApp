package com.prosthetics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import android.util.Log;

public class Autostart extends BroadcastReceiver {

	public static final String TAG = "Autostart";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"Something was received"+intent.getAction());
	    if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {  	
		    Intent pushIntent = new Intent(context, ProstheticsMonitoringActivity.class); 
		    pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(pushIntent);
	    }
	}
}