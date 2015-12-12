package com.bsns.app;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class GpsLocationReceiver extends BroadcastReceiver
{
	private GoogleMap googleMap;
	GPSTracker gps;
	Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context=context;
		if(intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
        { 
            // react on GPS provider change action
			//Toast.makeText(context,"gps started", Toast.LENGTH_LONG).show();
			
			//here we start service to track locagtion
			GPSTracker g=new GPSTracker(context);
        }
	}
}
