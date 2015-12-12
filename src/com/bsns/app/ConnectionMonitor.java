package com.bsns.app;

import com.bsns.dbhandler.LocalDBHandler1;
import com.bsns.dbhandler.LocalDBHandler4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            return;
        }
        boolean noConnectivity = intent.getBooleanExtra(
            ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        NetworkInfo aNetworkInfo = (NetworkInfo) intent
            .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (!noConnectivity) {
            if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                // Handle connected case
            	
            	LocalDBHandler4 db4=new LocalDBHandler4(context);
				Cursor cursor=db4.retrieveMyLocationTransaction();
				
				if(cursor.isBeforeFirst() && cursor.getCount()>0)
				{
					context.startService(new Intent(context,InternetStartService.class));
				}
            }
        } else {
            if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                || (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                // Handle disconnected case
            	context.stopService(new Intent(context,InternetStartService.class));
            }
        }
    }
}