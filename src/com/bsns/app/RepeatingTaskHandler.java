package com.bsns.app;

import java.util.ArrayList;

import com.bsns.beans.MylocationTransactionBean;
import com.bsns.dbhandler.LocalDBHandler1;
import com.bsns.dbhandler.LocalDBHandler4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.sax.StartElementListener;
import android.widget.Toast;

public class RepeatingTaskHandler extends BroadcastReceiver
{
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// For our recurring task, we'll just display a message
		int AmPiId=Integer.parseInt(intent.getStringExtra("AmPiId").toString());
		GPSTracker gpsTracker=new GPSTracker(arg0);
		if(gpsTracker.canGetLocation())
		{
			Toast.makeText(arg0, "You are at " + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude() + " " + AmPiId, Toast.LENGTH_SHORT).show();
			
			// Here, store location in MyLocationTransaction table 
			
			if(gpsTracker.getLatitude()!=0.0 || gpsTracker.getLongitude()!=0.0)
			{
				LocalDBHandler4 db4=new LocalDBHandler4(arg0);
				ArrayList<MylocationTransactionBean> mylocationTransactionBean=new ArrayList<>();
				MylocationTransactionBean bean1=new MylocationTransactionBean();
				
				Cursor cursor=db4.retrieveMyPlanLocationByAmPiId(AmPiId);
				if(cursor.isBeforeFirst() && cursor.getCount()>0)
				{
					while(cursor.moveToNext())
					{
						bean1.setUserId(cursor.getString(1).toString());
					}
				}
				bean1.setLatitude("" + gpsTracker.getLatitude());
				bean1.setLongtitude("" + gpsTracker.getLongitude());
				bean1.setDeviceName();
				mylocationTransactionBean.add(bean1);
				db4.insertMylocationTransaction(mylocationTransactionBean);
				
				//start internet service to upload location
				arg0.startService(new Intent(arg0,InternetStartService.class));
				gpsTracker.stopUsingGPS();
			}
		}
	}
}