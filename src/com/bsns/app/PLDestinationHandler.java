package com.bsns.app;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.MylocationTransactionBean;
import com.bsns.dbhandler.LocalDBHandler4;
import com.google.gson.Gson;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

public class PLDestinationHandler extends BroadcastReceiver
{
	String userid,msg;
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// For our recurring task, we'll just display a message
		int PlPiId=Integer.parseInt(intent.getStringExtra("PlPiId").toString());
		int AmPiId=1;
		
		// check user reaches to targeted location
		if(intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,false))
		{
			//now we need AmPiId which we can get using PlPiId to stop repeating task 
			LocalDBHandler4 db4=new LocalDBHandler4(arg0);
			Toast.makeText(arg0, "You reached that location for " + PlPiId, Toast.LENGTH_SHORT).show();
			Cursor cursor=db4.retrieveMyPlanLocationMasterByPlPiId(PlPiId);
			if(cursor.isBeforeFirst() && cursor.getCount()>0)
			{
				while(cursor.moveToNext())
				{
					AmPiId=Integer.parseInt(cursor.getString(6));
				}
			}
							
			// Here, we stop our repeatingtask
			PendingIntent pendingIntent;
			AlarmManager manager;
			Intent alarmIntent = new Intent(arg0,RepeatingTaskHandler.class);
			pendingIntent = PendingIntent.getBroadcast(arg0,AmPiId, alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
			manager = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
			manager.cancel(pendingIntent);
			
			// here we will remove planlocationhandler
			Intent planlocationintent = new Intent(arg0,PLDestinationHandler.class);
	        PendingIntent pendingIntent2=PendingIntent.getBroadcast(arg0,PlPiId,planlocationintent, PendingIntent.FLAG_CANCEL_CURRENT);
			GPSTracker gpsTracker=new GPSTracker(arg0);
			gpsTracker.locationManager.removeProximityAlert(pendingIntent2);			
			Log.e("Stop PM","Rquested to stop with " + PlPiId);
			
			
			
			// below we will insert the latlong lasttime for confirming user's last location in targeted area 
			if(gpsTracker.canGetLocation())
			{
				Toast.makeText(arg0, "You are at " + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude() + " " + AmPiId, Toast.LENGTH_SHORT).show();
				
				// Here, store location in MyLocationTransaction table 
				
				if(gpsTracker.getLatitude()!=0.0 || gpsTracker.getLongitude()!=0.0)
				{
					db4=new LocalDBHandler4(arg0);
					ArrayList<MylocationTransactionBean> mylocationTransactionBean=new ArrayList<>();
					MylocationTransactionBean bean1=new MylocationTransactionBean();
					
					Cursor cursor2=db4.retrieveMyPlanLocationByAmPiId(AmPiId);
					if(cursor2.isBeforeFirst() && cursor2.getCount()>0)
					{
						while(cursor2.moveToNext())
						{
							bean1.setUserId(cursor2.getString(1).toString());
							userid=cursor2.getString(1).toString();
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
			
			msg="ReachedPlanlocation?$?user reached to your set plan location";
			ServiceCaller sc1=new ServiceCaller(arg0.getString(R.string.serviceurl));
			sc1.executeDQLSP();
			// finally, remove such record from database too
			db4.deleteMyPlanLocationMaster(PlPiId);
		}
		else
		{
			Toast.makeText(arg0, "Exiting... for " + PlPiId, Toast.LENGTH_SHORT).show();
		}
	}
	
	class ServiceCaller {
		private final String NAMESPACE = "http://tempuri.org/";
		private String URL;
		private final String SOAP_ACTION = "http://tempuri.org/";
		private String TAG = "PGGURU";
		
		Gson gson = new Gson();
		
		public ServiceCaller(String URL) {
			this.URL=URL;
		}
		
		// call below method as we do in c#.net
		public String executeDQLSP()
		{
			PropertyInfo p1=new PropertyInfo();

			// set which sp to call
			
			// Set Name
			p1.setName("UserId");
			// Set Value
			p1.setValue(userid);
			// Set dataType
			p1.setType(Integer.class);
			
			PropertyInfo p2=new PropertyInfo();
			
			p2.setName("message");
			//Set Value
			p2.setValue(msg);
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
				protected void onPreExecute() 
		    	{
					Log.i(TAG, "onPreExecute");
					//Display progress bar
				}
		    	
				@Override
				protected Void doInBackground(String... params) {
					// Create request
					SoapObject request = new SoapObject(NAMESPACE, "sendGCMMsg");
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
						androidHttpTransport.call(SOAP_ACTION+"sendGCMMsg", envelope);
						// Get the response
						SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
						// Assign it to static variable		
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
		
				@Override
				protected void onPostExecute(Void result) {
					Log.i(TAG, "onPostExecute");
					//Convert 'Countries' JSON response into String array using fromJSON method
				}
				
				@Override
				protected void onProgressUpdate(Void... values) {
					Log.i(TAG, "onProgressUpdate");
				}
		    }
		    AsyncWSCallDQL a=new AsyncWSCallDQL();
		    a.execute();
		    return "";
		}
	}
}