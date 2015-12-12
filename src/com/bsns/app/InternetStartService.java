package com.bsns.app;

import com.bsns.dbhandler.LocalDBHandler1;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.widget.Toast;

public class InternetStartService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		LocalDBHandler1 db1=new LocalDBHandler1(InternetStartService.this);
		Cursor cursor=db1.retrieveMyLocationTransaction();
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			LocationUploader lc1=new LocationUploader(getString(R.string.serviceurl),getApplicationContext());
			lc1.startUploading();
		}
		else
		{
			stopSelf();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
	}

}
