package com.bsns.app;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.app.JoinUserFragment.ServiceCaller;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FriendGroupFragment extends Fragment implements OnItemClickListener{
		
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
	ListView lstvwrequest;
	Button b;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.friendgroupfragment, container, false);
		lstvwrequest=(ListView)view.findViewById(R.id.lstvwfriendgroupfragment);
		
		this.context=getActivity().getApplicationContext();
		ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
		String[][] l=new String[4][2];
		l[0][0]="@mode";
		l[0][1]="viewfriends";
		l[1][0]="@UserId1";
		l[1][1]="5002";
		l[2][0]="@GroupName";
		l[2][1]="Friends";
		sc1.executeDQLSP("spUserListTransaction",l);
		lstvwrequest.setOnItemClickListener(this);	
		
		return view;
		
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
					SoapObject request = new SoapObject(NAMESPACE, "executeDQLSP");
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
						androidHttpTransport.call(SOAP_ACTION+"executeDQLSP", envelope);
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
					
					Log.i(TAG, "Service called");
					if(resp!=null)
					{
						String list[]=new String[resp.length];
					
						for(int i=0;i<resp.length;i++)
						{
							for(int j=0;j<resp[i].length;j++)
							{
								Log.i(TAG, "Service called with " + resp[i][j]);
								list[i]=resp[i][j];
							}
							
						}
						ArrayAdapter<String> adapter =  new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
						lstvwrequest.setAdapter(adapter);
					}
					else
						{
						ArrayList<String> myStringArray=new ArrayList<String>();
						myStringArray.add("No friends");
						ArrayAdapter<String> adapter =  new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, myStringArray);
						lstvwrequest.setAdapter(adapter);
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


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// selected item 
      Fragment fragment=null;
      fragment=new FriendDetailsFragment();
      if(fragment!=null)
  		{	
     	 FragmentManager fragmentManager = getFragmentManager();
     	 fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
      
  		}
      
   
	}


	
		
	}


	
	


	
	
	

		
	

