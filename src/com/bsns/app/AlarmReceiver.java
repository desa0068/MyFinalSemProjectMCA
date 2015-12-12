package com.bsns.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// For our recurring task, we'll just display a message
		if(intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,false))
		{
			Toast.makeText(arg0, "You are in that location", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(arg0, "Exiting...", Toast.LENGTH_SHORT).show();
		}
	}
}