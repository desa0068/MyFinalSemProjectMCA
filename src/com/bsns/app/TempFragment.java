package com.bsns.app;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bsns.beans.LocationRequestBean;
import com.bsns.dbhandler.LocalDBHandler1;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TempFragment extends Fragment{
	
	MapView mapView;
	GoogleMap googleMap;
	Circle mCircle;
	Marker mMarker;
	GPSTracker gps;
	SeekBar seekBar;
	LatLng currenttouch;
	TextView tvdisp;
	LocalDBHandler1 db1;
	Button btnStart,btnview;
	ArrayList<LocationRequestBean> locationRequestBean;
	EditText et1;
	String UserId;
	LocationRequestBean bean1;
	int AmPiId,PlPiId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.templayoutfragment, container, false);

        mapView = (MapView) view.findViewById(R.id.map2);
        seekBar=(SeekBar) view.findViewById(R.id.seekbarlocationmgtradius);
        seekBar.setProgress(20);
        
        db1=new LocalDBHandler1(getActivity().getBaseContext());
		
		et1=(EditText) view.findViewById(R.id.splocationmgttime);
        btnStart=(Button) view.findViewById(R.id.btnlocationmgtstartpl);
        btnview=(Button) view.findViewById(R.id.btndisp);
        tvdisp=(TextView) view.findViewById(R.id.tvdisp);
        
        //whom to send to send GCM message, or who has set plan location
        UserId="" + 2;
        
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if(progress<20)
				{
					seekBar.setProgress(20);
				}
				updateMarkerWithCircle(currenttouch);
			}
		});
        
        mapView.onCreate(savedInstanceState);

        if(mapView!=null)
        {
            googleMap = mapView.getMap();
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            
            //drawMarkerWithCircle(new LatLng(22.33480647, 73.17782778));
            
            googleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng location) {
					// TODO Auto-generated method stub
					LatLng latLng = new LatLng(location.latitude, location.longitude);
					currenttouch=latLng;
					
		            if(mCircle == null || mMarker == null){
		                drawMarkerWithCircle(latLng);
		            }else{
		                updateMarkerWithCircle(latLng);
		            }
				}
			});
        }
        
        
        btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currenttouch!=null)
				{
					locationRequestBean=new ArrayList<>();
			        bean1=new LocationRequestBean();
			        
					bean1.setUserId("" +UserId);
					bean1.setLatitude("" + currenttouch.latitude);
					bean1.setLongtitude("" + currenttouch.longitude);
					bean1.setRadius("" + seekBar.getProgress());
					bean1.setRepeatTime(et1.getText().toString());
					AmPiId=Integer.parseInt(db1.generateUniqueAMPiId());
					PlPiId=Integer.parseInt(db1.generateUniquePlPiId());
					bean1.setAmPiId("" + AmPiId);
					bean1.setPlPiId("" + PlPiId);

					locationRequestBean.add(bean1);
					db1.insertLocationRequest(locationRequestBean);
					
					PendingIntent pendingIntent;
					AlarmManager manager;
					
					Intent alarmIntent = new Intent(getActivity().getApplicationContext(),RepeatingTaskHandler.class);
					alarmIntent.putExtra("AmPiId", "" + AmPiId);
					pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),AmPiId , alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
					
					
					manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
					int interval = Integer.parseInt(et1.getText().toString()); // 10 seconds
					
			        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
			        Toast.makeText(getActivity().getBaseContext(), "Alarm Set", Toast.LENGTH_SHORT).show();

			        
			        // here now we will set proximitylocationalert
			        Intent planlocationintent = new Intent(getActivity().getApplicationContext(),PLDestinationHandler.class);
			        planlocationintent.putExtra("PlPiId", "" + PlPiId);
			        PendingIntent pendingIntent2=PendingIntent.getBroadcast(getActivity().getApplicationContext(),PlPiId,planlocationintent, PendingIntent.FLAG_CANCEL_CURRENT);
			        new GPSTracker(getActivity().getBaseContext()).locationManager.addProximityAlert(currenttouch.latitude,currenttouch.longitude,seekBar.getProgress(),-1,pendingIntent2);
				}
			}
		});
        
        btnview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Cursor cursor=db1.retrieveMyLocationTransaction();
				tvdisp.setText("");
				if(cursor.isBeforeFirst() && cursor.getCount()>0)
				{
					while(cursor.moveToNext())
					{
						tvdisp.append(cursor.getString(0) + " ");
						tvdisp.append(cursor.getString(1) + " ");
						tvdisp.append(cursor.getString(2) + " ");
						tvdisp.append(cursor.getString(3) + " ");
						tvdisp.append(cursor.getString(4) + " ");
						//tvdisp.append(cursor.getString(5) + " ");
						//tvdisp.append(cursor.getString(6) + " ");
						//tvdisp.append(cursor.getString(7) + "\n");
					}
					tvdisp.append("\n");
				}
				
				Cursor cursor2=db1.retrieveLocationRequest();
				tvdisp.append("\n\n");
				if(cursor2.isBeforeFirst() && cursor2.getCount()>0)
				{
					while(cursor2.moveToNext())
					{
						tvdisp.append(cursor2.getString(0) + " ");
						tvdisp.append(cursor2.getString(1) + " ");
						tvdisp.append(cursor2.getString(2) + " ");
						tvdisp.append(cursor2.getString(3) + " ");
						tvdisp.append(cursor2.getString(4) + " ");
						tvdisp.append(cursor2.getString(5) + " ");
						tvdisp.append(cursor2.getString(6) + " ");
						tvdisp.append(cursor2.getString(7) + "\n");
					}
					tvdisp.append("\n");
				}
			}
		});
        return view;
    }
	
	private void updateMarkerWithCircle(LatLng position) {
	    mCircle.setCenter(position);
	    mMarker.setPosition(position);
	    mCircle.setRadius(seekBar.getProgress());
	}

	private void drawMarkerWithCircle(LatLng position){
		
		double radiusInMeters = seekBar.getProgress();
	    int strokeColor = 0xffff0000; //red outline
	    int shadeColor = 0x44ff0000; //opaque red fill

	    CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
	    mCircle = googleMap.addCircle(circleOptions);

	    MarkerOptions markerOptions = new MarkerOptions().position(position);
	    mMarker = googleMap.addMarker(markerOptions);
	}
	
	@Override
    public void onResume() 
    {
        mapView.onResume();

        super.onResume();
    }
    @Override
    public void onDestroy() 
    {
        super.onDestroy();

        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() 
    {
        super.onLowMemory();

        mapView.onLowMemory();
    }
  
}
