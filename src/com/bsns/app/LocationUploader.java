package com.bsns.app;

import java.util.StringTokenizer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;








import com.bsns.dbhandler.LocalDBHandler1;
import com.bsns.dbhandler.LocalDBHandler4;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class LocationUploader
{
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private String serviceurl;
	private Context context;
	
	public LocationUploader(String url,Context context) {
		this.serviceurl=url;
		this.context=context;
	}
	
	public void startUploading()
	{
		LocalDBHandler4 db4=new LocalDBHandler4(context);
		Cursor cursor=db4.retrieveMyLocationTransaction();
		
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			ServiceCaller sc1=new ServiceCaller(serviceurl);
			sc1.executeDQLSP();
		}
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
		public String[][] executeDQLSP()
		{
			PropertyInfo p2=new PropertyInfo();
			String temp2="";
			
			final LocalDBHandler4 db4=new LocalDBHandler4(context);
			Cursor cursor=db4.retrieveMyLocationTransaction();
			SharedPreferences sp=context.getSharedPreferences("controller_check",context.MODE_WORLD_READABLE);
			String userid=sp.getString("UserId","no_data");
			if(cursor.isBeforeFirst() && cursor.getCount()>0)
			{
				while(cursor.moveToNext())
				{					
					
					temp2=temp2 + userid +  "?$?";// place the id of the user who logged-in in the app
					temp2=temp2 + cursor.getString(0) + "?$?";
					temp2=temp2 + cursor.getString(1) + "?$?";
					temp2=temp2 + cursor.getString(2) + "?$?";
					temp2=temp2 + cursor.getString(3) + "?$?";
					temp2=temp2 + cursor.getString(4) + "?$?";
					temp2=temp2 + cursor.getString(5) + "?$?;";
				}
			}
						
			temp2=temp2.substring(0,temp2.length()-4);
			
			
			p2.setName("p");
			//Set Value
			p2.setValue(temp2);
			// Set dataType
			p2.setType(String.class);
			
			final PropertyInfo parameters2=p2;
			// so finally we have passed single PropertyInfo object 
			// including spName. Now, in service extract this value
			// and call this sp.
			// Also, this single PropertyInfo object must contain
			// @mode value, which determine which query to call.
			
			
		    class AsyncWSCallDQL extends AsyncTask<String, Void, Void>
		    {
		    	@Override
				protected void onPreExecute() {
					Log.i(TAG, "onPreExecute");
					//Display progress bar
				}
		    	
				@Override
				protected Void doInBackground(String... params) {
					// Create request
					SoapObject request = new SoapObject(NAMESPACE, "storeUserLocation");
					// Property which holds input parameters
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
						androidHttpTransport.call(SOAP_ACTION+"storeUserLocation", envelope);
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
					//resp=gson.fromJson(responseJSON, String.class);
					
					if(responseJSON!=null)
					{
						Log.i(TAG, "Service called with " + responseJSON);
						StringTokenizer st=new StringTokenizer(responseJSON,",");
						while(st.hasMoreTokens())
						{						
							db4.deleteMylocationTransaction(Integer.parseInt((String)st.nextElement()));
						}
					}
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
	}
}
