package com.bsns.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.PlanLocationMasterBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.bsns.dbhandler.LocalDBHandler4;
import com.google.gson.Gson;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRunningPlanLocation extends Fragment {
	
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
	ListView lstplrun;
	PlanLocationMasterBean plbean;
	private List<PlanLocationMasterBean> plb=new ArrayList<PlanLocationMasterBean>();
	ProgressDialog pDial;
	TextView tvdatetime;
	TextView tvlat;
	TextView tvlong;
	ImageButton btnclose;
	TextView tvaddress;
	String pid;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.viewrunninglocationsfragment, container, false);
		
		plb.clear();
		populateuserlist();
		ArrayAdapter<PlanLocationMasterBean> arrplanloc=new MyAdapter();
		ListView lst=(ListView)view.findViewById(R.id.lstvwplanlocationuserlist);
		lst.setAdapter(arrplanloc);
		Toast.makeText(getActivity().getBaseContext(), ""+getArguments().getString("FriendId"), Toast.LENGTH_SHORT).show();
		return view;
	}
	
	private class MyAdapter extends ArrayAdapter<PlanLocationMasterBean>
	{
		public MyAdapter() {
			super(getActivity().getBaseContext(),R.layout.runninguserlistview,plb);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemview=convertView;
			if(itemview==null)
			{
				itemview=getActivity().getLayoutInflater().inflate(R.layout.runninguserlistview,parent,false);
			}
		
			final PlanLocationMasterBean plbean1=plb.get(position);
			
			plbean=plb.get(position);
			
			tvdatetime=(TextView)itemview.findViewById(R.id.tvdatetime);
			tvlat=(TextView)itemview.findViewById(R.id.tvLatitude);
			tvlong=(TextView)itemview.findViewById(R.id.tvLongitude);
			btnclose=(ImageButton)itemview.findViewById(R.id.btnfrienditemreject);
			tvaddress=(TextView)itemview.findViewById(R.id.tvaddress);
			btnclose.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
					String[][] paramloc=new String[4][2];
					paramloc[0][0]="@mode";
					paramloc[0][1]="updatelocdetails";
					paramloc[1][0]="@PlanLocId";
					
					LocalDBHandler3 dbhandler3=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor plid=dbhandler3.retrievePlanLocationId(getArguments().getString("FriendId"));
					if(plid.isBeforeFirst() && plid.getCount()>0)
					{
						plid.moveToFirst();
						paramloc[1][1]=plid.getString(0);
						
					}
					else
					{
						Toast.makeText(getActivity().getBaseContext(), "No Plan Location Set", Toast.LENGTH_SHORT).show();
					}
					
					sc1.executeDQLSP("spAndroid", paramloc);
					
					
					
					
				}
			});
			
			LocalDBHandler4 db4=new LocalDBHandler4(getActivity().getBaseContext());
			
			db4.close();
		
			LocalDBHandler3 dbretrieveplanloc=new LocalDBHandler3(getActivity().getBaseContext());
			Cursor cplloc=dbretrieveplanloc.retrievePlanLocationDetails(getArguments().getString("FriendId"));
			if(cplloc.isBeforeFirst() && cplloc.getCount()>0)
			{
				cplloc.moveToFirst();
				tvdatetime.setText(cplloc.getString(6));
				tvlat.setText(cplloc.getString(2));
				tvlong.setText(cplloc.getString(3));
				
				Geocoder geocoder=new Geocoder(getActivity().getBaseContext());
        		List<Address> addresses=null;
				try {
					addresses = geocoder.getFromLocation(Double.parseDouble(tvlat.getText().toString()),Double.parseDouble(tvlong.getText().toString()), 1);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		String loctitle="";
        		if(!(addresses.get(0).getSubLocality()==null))
				{
					loctitle=loctitle + addresses.get(0).getSubLocality();
					loctitle=loctitle +",";
				}
				if(!(addresses.get(0).getLocality()==null))
				{
					loctitle=loctitle + addresses.get(0).getLocality();
					loctitle=loctitle +",";
				}
				if(!(addresses.get(0).getCountryName()==null))
				{
					loctitle=loctitle + addresses.get(0).getCountryName();
				}
				
				tvaddress.setText(loctitle);

				
			}
			else
			{
		
			}
			return itemview;
					
	}
	
	}
	private void populateuserlist() {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			
			
			LocalDBHandler3 dbretrieveplanloc=new LocalDBHandler3(getActivity().getBaseContext());
			plbean=new PlanLocationMasterBean();
			Cursor cplloc=dbretrieveplanloc.retrievePlanLocationDetails(getArguments().getString("FriendId"));
			if(cplloc.isBeforeFirst() && cplloc.getCount()>0)
			{
				while(cplloc.moveToNext())
				{
					
					plbean.setPlanLocId(cplloc.getString(0));
					pid=cplloc.getString(0);
					plbean.setUserId(getArguments().getString("FriendId"));
					plbean.setLatitude(cplloc.getString(2));
					plbean.setLongitude(cplloc.getString(3));
					plbean.setRadius(cplloc.getString(4));
					plbean.setRepeatTime(cplloc.getString(5));
					plbean.setCreationDate(cplloc.getString(6));
					plbean.setIsDelete(cplloc.getString(7));
					plb.add(plbean);
				}
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
					SoapObject request = new SoapObject(NAMESPACE, "executeDQLSPCancelPLocation");
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
						androidHttpTransport.call(SOAP_ACTION+"executeDQLSPCancelPLocation", envelope);
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
					
					Log.i(TAG, "Service called with " + responseJSON);
					
					if(responseJSON!=null)
					{
						if(responseJSON.equals("Sucess"))
						{
							LocalDBHandler3 dbdel=new LocalDBHandler3(getActivity().getBaseContext());
							dbdel.removefromplanlocationmaster(pid);
							//dbdel.removeMyPlanLocation(pid);
							Toast.makeText(getActivity().getBaseContext(),"Plan Location Removed", Toast.LENGTH_SHORT).show();
							
							Log.i(TAG, "I am done");
						}
						else
						{
							Toast.makeText(getActivity().getBaseContext(),"Plan Location Not Removed", Toast.LENGTH_SHORT).show();
						} 
							//Toast.makeText(getActivity().getBaseContext(), "Plan Location Removed", Toast.LENGTH_SHORT).show();
					
					}
					if(pDial!=null)
					{
						pDial.dismiss();
					}
					
					
				}
			
		
				@Override
				protected void onPreExecute() {
					Log.i(TAG, "onPreExecute");
					//Display progress bar
					pDial = new ProgressDialog(getActivity());
					pDial.setCancelable(false);
					pDial.setMessage("Please wait..!!");
					if(pDial!=null)
					{
						pDial.show();
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
