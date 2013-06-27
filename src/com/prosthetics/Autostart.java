package com.prosthetics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import android.util.Log;

/**
 * This class listens for the intent sent when the phone has been
 * booted. If the intent is received the {@code ProstheticsMonitoringActivity}
 * is started.
 * @author aumar11
 * @version 1.0
 * @see ProstheticsMonitoringActivity
 */
public class Autostart extends BroadcastReceiver
{

	/** Tag for Log statements in this class. */
	public static final String TAG = "Autostart";
	/** Boolean to manage logging statements (Debugging)*/
	private static final boolean D = true;

	@Override
	public void onReceive(Context context, Intent intent)
	{
	  if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
	  {  	
		    Intent pushIntent = new Intent(context, ProstheticsMonitoringActivity.class); 
		    pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(pushIntent);
	  }
	}

}