package com.bsns.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.CountryBean;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.google.gson.Gson;
public class AddUserFragment extends Fragment implements OnItemSelectedListener {
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
	Spinner spcountry,spgroupnames,spsenderrelation,spreceiverrelation;
	String UserId;
	List<CountryBean> countryBean=new ArrayList<>();
	ImageButton btnsendrequest;
	EditText txtcontactno;
	ProgressDialog pDial;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.adduserfragment, container, false);
		spcountry=(Spinner)view.findViewById(R.id.spinneraddusercountry);
		
		this.context=getActivity().getApplicationContext();
		spcountry=(Spinner)view.findViewById(R.id.spinneraddusercountry);
		LocalDBHandler2 dbcountry=new LocalDBHandler2(getActivity().getBaseContext());
		
		Cursor cursorcountry=dbcountry.retrieveAllCountry();
		String strcountry[]=new String[cursorcountry.getCount()];
		if(cursorcountry.isBeforeFirst() && cursorcountry.getCount()>0)
		{
			//cursorcountry.moveToFirst();
			int i=0;
			
			while(cursorcountry.moveToNext())
			{
				CountryBean bean1=new CountryBean();
				bean1.setCountryCode(cursorcountry.getString(0));
				bean1.setCountryName(cursorcountry.getString(1));
				strcountry[i]=cursorcountry.getString(1);
				bean1.setCountryCode(cursorcountry.getString(2));
				bean1.setCountryDescription(cursorcountry.getString(3));
				bean1.setIsDelete(cursorcountry.getString(4));
				countryBean.add(bean1);
				i++;
			}
			
		}
		dbcountry.close();
		
		
		ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity().getBaseContext(),android.R.layout.simple_spinner_dropdown_item, strcountry);
		spcountry.setAdapter(adapter);
		
		
		LocalDBHandler3 dbgroupname=new LocalDBHandler3(getActivity().getBaseContext());
		SharedPreferences sp;
        sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
        UserId=sp.getString("UserId","no_data");
		/*Cursor cursorgroupname=dbgroupname.loadGroupNames(UserId);
		spgroupnames=(Spinner)view.findViewById(R.id.spinnerselectgroup);
		GroupMasterBean gpmasterBean=new GroupMasterBean();
		String list1[]=new String[cursorgroupname.getCount()];
		if(cursorgroupname.isBeforeFirst() && cursorgroupname.getCount()>0)
		{
			int cnt=0;
			while(cursorgroupname.moveToNext())
			{
				gpmasterBean.setGroupName(cursorgroupname.getString(0));
						
				list1[cnt]=cursorgroupname.getString(0);
				cnt++;
			}
		}
		ArrayAdapter<String> groupadapter =  new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, list1);
		spgroupnames.setAdapter(groupadapter);*/
		
		LocalDBHandler3 dbrelation=new LocalDBHandler3(getActivity().getBaseContext());
		spsenderrelation=(Spinner)view.findViewById(R.id.spinneryourrelation);
		spreceiverrelation=(Spinner)view.findViewById(R.id.spinnerreciverrelation);
		Cursor cursorrelation=dbrelation.retrieveRelation();
		RelationMasterBean relationbean=new RelationMasterBean();
		String list2[]=new String[cursorrelation.getCount()];
		if(cursorrelation.isBeforeFirst() && cursorrelation.getCount()>0)
		{
			int cnt=0;
			while(cursorrelation.moveToNext())
			{
				relationbean.setRelId(cursorrelation.getString(0));
				relationbean.setRelName(cursorrelation.getString(1));
				list2[cnt]=cursorrelation.getString(1);
				cnt++;
			}
		}
		ArrayAdapter<String> relationadapter =  new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, list2);
		spsenderrelation.setAdapter(relationadapter);
		spreceiverrelation.setAdapter(relationadapter);
		
		btnsendrequest=(ImageButton)view.findViewById(R.id.btnaddusersendrequest);
		txtcontactno=(EditText)view.findViewById(R.id.txtaddusercontactno);
		btnsendrequest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!(txtcontactno.getText().toString().equals("")))
				{
				ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
				String[][] paramsendrequest=new String[11][2];
				
				paramsendrequest[0][0]="@mode";
				paramsendrequest[0][1]="sendrequest";
				
				paramsendrequest[1][0]="@ContactNo";
				paramsendrequest[1][1]=txtcontactno.getText().toString();
				
				paramsendrequest[2][0]="@UserId1";
				paramsendrequest[2][1]=UserId;
				
				paramsendrequest[3][0]="@UserId11";
				paramsendrequest[3][1]=UserId;
				
				//paramsendrequest[4][0]="@UserId2";
				//paramsendrequest[4][1]=getArguments().getString("FriendId");
				
				paramsendrequest[4][0]="@SenderRelId";
				LocalDBHandler3 dbrelationid=new LocalDBHandler3(getActivity().getBaseContext());
				Cursor cursorrelationid=dbrelationid.retrieveRelationId(spsenderrelation.getSelectedItem().toString());
				if(cursorrelationid.isBeforeFirst() && cursorrelationid.getCount()>0)
				{
					cursorrelationid.moveToFirst();
					paramsendrequest[4][1]=cursorrelationid.getString(0);
				}
				
				paramsendrequest[5][0]="@ReceiverRelId";
				LocalDBHandler3 dbrelationid1=new LocalDBHandler3(getActivity().getBaseContext());
				Cursor cursorrelationid1=dbrelationid.retrieveRelationId(spsenderrelation.getSelectedItem().toString());
				if(cursorrelationid1.isBeforeFirst() && cursorrelationid1.getCount()>0)
				{
					cursorrelationid1.moveToFirst();
					paramsendrequest[5][1]=cursorrelationid1.getString(0);
				}
			
				paramsendrequest[6][0]="@IsSharingLocation";
				paramsendrequest[6][1]="1";
				
				paramsendrequest[7][0]="@CreationDate";
				paramsendrequest[7][1]=dbrelationid1.getDateTime();
				
				paramsendrequest[8][0]="@IsDelete";
				paramsendrequest[8][1]="0";
				
				paramsendrequest[9][0]="@IsAccept";
				paramsendrequest[9][1]="0";
				
				
				
				sc1.executeDQLSP("spAndroid", paramsendrequest);
				}
				else
				{
					Toast.makeText(getActivity().getBaseContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		

		
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
					SoapObject request = new SoapObject(NAMESPACE, "executeFriendRequest");
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
						androidHttpTransport.call(SOAP_ACTION+"executeFriendRequest", envelope);
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
						
						if(responseJSON.equals("Send"))
						{
							Toast.makeText(getActivity().getBaseContext(),"Your request is sent", Toast.LENGTH_SHORT).show();
							
							Log.i(TAG, "I am done");
						}
						else
						{
							Toast.makeText(getActivity().getBaseContext(),"Your request is not sent", Toast.LENGTH_SHORT).show();
						} 
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
		    AsyncWSCallDML a=new AsyncWSCallDML();
		    a.execute();
		    return dmlstatus;
		}
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		/*String text=spcountry.getSelectedItem().toString();
		//Toast.makeText(context,text, Toast.LENGTH_LONG).show();
		ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
		String[][] l=new String[4][2];
		l[0][0]="@mode";
		l[0][1]="selectcountrycode";
		l[1][0]="@CountryName";
		l[1][1]=text;
		sc1.executeDQLSP("spCountry",l);
		// TODO Auto-generated method stub
		 lblcode.setText(cd);*/
		
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	public void onBackPressed() {
		
	}
	
}
