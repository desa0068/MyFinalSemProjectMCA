package com.bsns.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.LocationRequestBean;
import com.bsns.beans.PlanLocationMasterBean;
import com.bsns.dbhandler.LocalDBHandler1;
import com.bsns.dbhandler.LocalDBHandler3;
import com.bsns.dbhandler.LocalDBHandler4;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class LocationManagement extends Fragment
{
	MapView mapView;
	GoogleMap googleMap;
	Circle mCircle;
	Marker mMarker;
	GPSTracker gps;
	SeekBar seekBar;
	LatLng currenttouch;
	TextView tvdisp;
	LocalDBHandler1 db1;
	ImageButton btnStart;
	Button btnview;
	ArrayList<LocationRequestBean> locationRequestBean;
	Spinner splocationmgttime;
	String UserId;
	LocationRequestBean bean1;
	int AmPiId,PlPiId;
	CheckBox cblocationmgt;
	TextView tvlocationmgttime;
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	PlanLocationMasterBean planLocationMasterBean;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.locationmanagementfragment, container, false);

        mapView = (MapView) view.findViewById(R.id.map2);
        seekBar=(SeekBar) view.findViewById(R.id.seekbarlocationmgtradius);
        seekBar.setProgress(20);
        
        db1=new LocalDBHandler1(getActivity().getBaseContext());
		
        tvlocationmgttime=(TextView) view.findViewById(R.id.tvlocagtionmgttime);
		splocationmgttime=(Spinner) view.findViewById(R.id.splocationmgttime);
        btnStart=(ImageButton) view.findViewById(R.id.btnlocationmgtstartpl);
        btnview=(Button) view.findViewById(R.id.btndisp);
        tvdisp=(TextView) view.findViewById(R.id.tvdisp);
        cblocationmgt=(CheckBox)view.findViewById(R.id.cblocationmgttime); 
        
        cblocationmgt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					tvlocationmgttime.setVisibility(View.VISIBLE);
					splocationmgttime.setVisibility(View.VISIBLE);
				}
				else
				{
					tvlocationmgttime.setVisibility(View.INVISIBLE);
					splocationmgttime.setVisibility(View.INVISIBLE);
				}
			}
		});
        
        String timearray[]=new String[3];
        timearray[0]="10 Min";
        timearray[1]="30 Min";
        timearray[2]="60 Min";
        
        ArrayAdapter<String> ad1=new ArrayAdapter<>(getActivity().getBaseContext(),android.R.layout.simple_spinner_dropdown_item,timearray);
        splocationmgttime.setAdapter(ad1);
                
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
				if(currenttouch!=null)
				{
					if(progress<20)
					{
						seekBar.setProgress(20);
					}
					updateMarkerWithCircle(currenttouch);
				}
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
					SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
					
					//check wether logged-in user try to put location on him or on other user 
					/*
					if(sp.getString("UserId","no_data").toString().equals(getArguments().getString("FriendId")))
					{
						// set for own.. no need to pass it to central db
						Toast.makeText(getActivity().getBaseContext(),"You set it for ur own",Toast.LENGTH_SHORT).show();
					}
					else
					{*/
						planLocationMasterBean=new PlanLocationMasterBean();
						// set for other user.. so send it to central db
						String[][] paramdata=new String[9][2];
						
						paramdata[0][0]="@mode";
						paramdata[0][1]="insertplanlocationmaster";
						
						paramdata[1][0]="@UserId1";
						paramdata[1][1]=sp.getString("UserId","no_data").toString();
						
						paramdata[2][0]="@UserId2";
						paramdata[2][1]=getArguments().getString("FriendId");
						planLocationMasterBean.setUserId(getArguments().getString("FriendId"));
						
						paramdata[3][0]="@Latitude";
						paramdata[3][1]="" + currenttouch.latitude;
						planLocationMasterBean.setLatitude("" + currenttouch.latitude);
						
						paramdata[4][0]="@Longitude";
						paramdata[4][1]="" + currenttouch.longitude;
						planLocationMasterBean.setLongitude("" + currenttouch.longitude);
						
						paramdata[5][0]="@Radius";
						paramdata[5][1]="" + seekBar.getProgress();
						planLocationMasterBean.setRadius("" + seekBar.getProgress());
						
						paramdata[6][0]="@RepeatTime";
						if(cblocationmgt.isChecked())
						{
							paramdata[6][1]=splocationmgttime.getSelectedItem().toString().split(" ")[0];
							planLocationMasterBean.setRepeatTime(splocationmgttime.getSelectedItem().toString().split(" ")[0]);
						}
						else
						{
							paramdata[6][1]="" + 0;
							planLocationMasterBean.setRepeatTime("" + 0);
						}
						
						
						paramdata[7][0]="@CreationDate";
						paramdata[7][1]=getDateTime();
						planLocationMasterBean.setCreationDate(getDateTime());
						
						paramdata[8][0]="@IsDelete";
						paramdata[8][1]="False";
						planLocationMasterBean.setIsDelete("False");
						
						ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
						sc1.executeDQLSP("spAndroid",paramdata);
						
						PlanLocationMasterBean planLocationMasterBean=new PlanLocationMasterBean();
						planLocationMasterBean.setUserId(getArguments().getString("FriendId"));
					//} 
				}
			}
		});
        
        btnview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				LocalDBHandler4 db4=new LocalDBHandler4(getActivity().getBaseContext());
				db4.close();
				LocalDBHandler3 db3=new LocalDBHandler3(getActivity().getBaseContext());
				db3.close();
				
				Fragment fragment=new ViewRunningPlanLocation();
				SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
				Bundle b=new Bundle();
		        b.putString("FriendId",getArguments().getString("FriendId"));
		        fragment.setArguments(b);
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				FragmentTransaction ft=fragmentManager.beginTransaction();
				ft.replace(R.id.frame_container, fragment,"viewrunning");
				if(fragmentManager.findFragmentByTag("viewrunning")==null)
				{
					ft.addToBackStack("viewrunning");	
				}
				
				ft.commit();

			}
		});
        return view;
    }
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
	}
	
	private void updateMarkerWithCircle(LatLng position) {
	    mCircle.setCenter(position);
	    mMarker.setPosition(position);
	    mCircle.setRadius(seekBar.getProgress());
	}

	private void drawMarkerWithCircle(LatLng position)
	{	
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
    
	class ServiceCaller
	{
		private final String NAMESPACE = "http://tempuri.org/";
		private String URL;
		private final String SOAP_ACTION = "http://tempuri.org/";
		private String TAG = "PGGURU";
		
		Gson gson = new Gson();
		
		public ServiceCaller(String URL) {
			this.URL=URL;
		}
		
		// call below method as we do in c#.net
		public String[][] executeDQLSP(String spName,String[][] allParams)
		{
			PropertyInfo p1=new PropertyInfo();

			// set which sp to call
			
			// Set Name
			p1.setName("StoreProcedureName");
			// Set Value
			p1.setValue(spName);
			// Set dataType
			p1.setType(String.class);
			
			PropertyInfo p2=new PropertyInfo();
			String temp2="";
			for(int i=0;i<allParams.length;i++)
			{
				temp2=temp2 + (allParams[i][0] + "?$?" + allParams[i][1] + "?$?;");
			}
			temp2=temp2.substring(0,temp2.length()-4);
			
			p2.setName("p");
			//Set Value
			p2.setValue(temp2);
			// Set dataType
			p2.setType(String.class);
			
			
			final PropertyInfo parameters1=p1;
			final PropertyInfo parameters2=p2;
			// so finally we have passed single PropertyInfo object 
			// including spName. Now, in service extract this value
			// and call this sp.
			// Also, this single PropertyInfo object must contain
			// @mode value, which determine which query to call.
			
			
		    class AsyncWSCallDQL extends AsyncTask<String, Void, Void>
		    {
				@Override
				protected Void doInBackground(String... params) {
					// Create request
					SoapObject request = new SoapObject(NAMESPACE, "setPlanLocation");
					// Property which holds input parameters
					request.addProperty(parameters1);
					request.addProperty(parameters2);
					// Create envelope
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.dotNet = true;
					// Set output SOAP object
					envelope.setOutputSoapObject(request);
					// Create HTTP call object
					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

					try {
						// Invoke web service
						androidHttpTransport.call(SOAP_ACTION+"setPlanLocation", envelope);
						// Get the response
						SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
						// Assign it to static variable		
						responseJSON = response.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
		
				@Override
				protected void onPostExecute(Void result) {
					Log.i(TAG, "onPostExecute");
					//Convert 'Countries' JSON response into String array using fromJSON method
					//placelist = gson.fromJson(responseJSON, String[].class);
					resp=gson.fromJson(responseJSON, String[][].class);
					
					String response=null;
					Log.i(TAG, "Service called");
					if(resp!=null)
					{
						for(int i=0;i<resp.length;i++)
						{
							for(int j=0;j<resp[i].length;j++)
							{
								Log.i(TAG, "Service called with dd " + resp[i][j]);
								response=resp[i][j];
							}
							//tv1.append("\n");
						}
					}
					
					if(!(response==null || response.equals("")))
					{
						if(!response.equals("0"))
						{
							planLocationMasterBean.setPlanLocId("" + response);
							Toast.makeText(getActivity().getBaseContext(),"Plan location set with " + planLocationMasterBean.getPlanLocId(), Toast.LENGTH_SHORT).show();
							ArrayList<PlanLocationMasterBean> arrayList=new ArrayList<PlanLocationMasterBean>();
							arrayList.add(planLocationMasterBean);
							LocalDBHandler4 db4=new LocalDBHandler4(getActivity().getBaseContext());
							db4.insertPlanLocationMaster(arrayList);
							db4.close();
						}
					}
				}
		
				@Override
				protected void onPreExecute() {
					Log.i(TAG, "onPreExecute");
					//Display progress bar
				}
		
				@Override
				protected void onProgressUpdate(Void... values) {
					Log.i(TAG, "onProgressUpdate");
				}
		    }
		    AsyncWSCallDQL a=new AsyncWSCallDQL();
		    a.execute();
		    return resp;
		}
		
		public int executeDMLSP(String spName,String[][] allParams)
		{
			PropertyInfo p1=new PropertyInfo();
		
			// set which sp to call
			
			// Set Name
			p1.setName("StoreProcedureName");
			// Set Value
			p1.setValue(spName);
			// Set dataType
			p1.setType(String.class);
			
			PropertyInfo p2=new PropertyInfo();
			String temp2="";
			for(int i=0;i<allParams.length;i++)
			{
				temp2=temp2 + allParams[i][0] + "?$?" + allParams[i][1] + "?$?;";
			}
			temp2=temp2.substring(0,temp2.length()-4);
			
			p2.setName("p");
			//Set Value
			p2.setValue(temp2);
			// Set dataType
			p2.setType(String.class);
			
			
			final PropertyInfo parameters1=p1;
			final PropertyInfo parameters2=p2;
			// so finally we have passed single PropertyInfo object 
			// including spName. Now, in service extract this value
			// and call this sp.
			// Also, this single PropertyInfo object must contain
			// @mode value, which determine which query to call.
			
			
		    class AsyncWSCallDML extends AsyncTask<String, Void, Void>
		    {
				@Override
				protected Void doInBackground(String... params) {
					// Create request
					SoapObject request = new SoapObject(NAMESPACE, "executeDMLSP");
					// Property which holds input parameters
					request.addProperty(parameters1);
					request.addProperty(parameters2);
					// Create envelope
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.dotNet = true;
					// Set output SOAP object
					envelope.setOutputSoapObject(request);
					// Create HTTP call object
					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

					try {
						// Invoke web service
						androidHttpTransport.call(SOAP_ACTION+"executeDMLSP", envelope);
						// Get the response
						SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
						// Assign it to static variable		
						responseJSON = response.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
		
				@Override
				protected void onPostExecute(Void result) {
					Log.i(TAG, "onPostExecute");
					//Convert 'Countries' JSON response into String array using fromJSON method
					//placelist = gson.fromJson(responseJSON, String[].class);
					
					Log.i(TAG, "Service called");
					if(responseJSON!=null)
					{
						Log.i(TAG, "Service called with " + responseJSON);
						dmlstatus=Integer.parseInt(responseJSON);
						//tv1.append("dml status is:" + dmlstatus);
					}
				}
		
				@Override
				protected void onPreExecute() {
					Log.i(TAG, "onPreExecute");
					//Display progress bar
				}
		
				@Override
				protected void onProgressUpdate(Void... values) {
					Log.i(TAG, "onProgressUpdate");
				}
		    }
		    AsyncWSCallDML a=new AsyncWSCallDML();
		    a.execute();
		    return dmlstatus;
		}
	}
}
