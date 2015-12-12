package com.bsns.app;

import android.R.array;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import com.bsns.beans.GroupTransactionBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.google.gson.Gson;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class JoinUserFragment extends Fragment {
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
	ListView lstvwrequest;
	UserMasterBean accepteduser;
	private List<UserMasterBean> ult=new ArrayList<UserMasterBean>();
	ProgressDialog pDial;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.joinuserfragment, container, false);
		
		
	
		ult.clear();
		populateUserList();
		ArrayAdapter<UserMasterBean> adapter=new MyAdapter();
        ListView list=(ListView) view.findViewById(R.id.lstvwjoinuserrequestlist);
        list.setAdapter(adapter);
        
		return view;
	}
	
	private class MyAdapter extends ArrayAdapter<UserMasterBean>
	{
		public MyAdapter() {
			super(getActivity().getBaseContext(),R.layout.frienditem_listview,ult);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemview=convertView;
			if(itemview==null)
			{
				itemview=getActivity().getLayoutInflater().inflate(R.layout.frienditem_listview,parent,false);
			}
		 	
			final UserMasterBean currentUser=ult.get(position);
			accepteduser=ult.get(position);
			TextView tvusername=(TextView) itemview.findViewById(R.id.tvfrienditemlistname);
			TextView tvuserrelation=(TextView) itemview.findViewById(R.id.tvfrienditemrelation);
			ImageView img=(ImageView) itemview.findViewById(R.id.imgvfrienditemlist);
			ImageButton btnfriendaccept=(ImageButton)itemview.findViewById(R.id.btnfrienditemaccept);
			ImageButton btnfriendreject=(ImageButton)itemview.findViewById(R.id.btnfrienditemreject);
			
			img.setImageResource(getResources().getIdentifier(currentUser.getDPUrl() , "drawable", getActivity().getPackageName()));
			tvusername.setText(currentUser.getFname() + " " + currentUser.getLname());
			
			SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
			sp.getString("UserId","no_data");
			
			btnfriendaccept.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
					
					ServiceCaller scacceptrequest=new ServiceCaller(getString(R.string.serviceurl));
					String[][] paramacceptreq=new String[12][2];
					
					paramacceptreq[0][0]="@mode";
					paramacceptreq[0][1]="acceptrequest";
					
					paramacceptreq[1][0]="@UserId1";
					paramacceptreq[1][1]=currentUser.getUserId();

					paramacceptreq[2][0]="@UserId2";
					paramacceptreq[2][1]=sp.getString("UserId","no_data");
					
					paramacceptreq[3][0]="@UserId11";
					paramacceptreq[3][1]=sp.getString("UserId","no_data");
					
					paramacceptreq[4][0]="@UserId22";
					paramacceptreq[4][1]=currentUser.getUserId();
					
					paramacceptreq[5][0]="@SenderRelId";
					LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
					String relname=db1.retrieveReceiverRelationId(currentUser.getUserId(),sp.getString("UserId","no_data"));
					LocalDBHandler3 dbrelationid1=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor cursorrelationid1=dbrelationid1.retrieveRelationId(relname);
					if(cursorrelationid1.isBeforeFirst() && cursorrelationid1.getCount()>0)
					{
						cursorrelationid1.moveToFirst();
						paramacceptreq[5][1]=cursorrelationid1.getString(0);
					}
					
					paramacceptreq[6][0]="@ReceiverRelId";
					LocalDBHandler2 db3=new LocalDBHandler2(getActivity().getBaseContext());
					String relnamereciver=db3.retrieveReceiverRelationId(currentUser.getUserId(),sp.getString("UserId","no_data"));
					LocalDBHandler3 dbrelationid=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor cursorrelationid=dbrelationid.retrieveRelationId(relnamereciver);
					if(cursorrelationid.isBeforeFirst() && cursorrelationid.getCount()>0)
					{
						cursorrelationid.moveToFirst();
						paramacceptreq[6][1]=cursorrelationid.getString(0);
					}
					
					paramacceptreq[7][0]="@IsSharingLocation";
					paramacceptreq[7][1]="1";
					
					paramacceptreq[8][0]="@CreationDate";
					paramacceptreq[8][1]=dbrelationid1.getDateTime();
					
					paramacceptreq[9][0]="@IsDelete";
					paramacceptreq[9][1]="0";
					
					paramacceptreq[9][0]="@IsAccept";
					paramacceptreq[9][1]="1";
					
					paramacceptreq[10][0]="@UpdationDate";
					paramacceptreq[10][1]=dbrelationid1.getDateTime();
					
					scacceptrequest.executeDQLSP("spAndroid", paramacceptreq);
					
					
					
				

				}
			});
			
			btnfriendreject.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
					ServiceCaller screjectrequest=new ServiceCaller(getString(R.string.serviceurl));
					String[][] paramrejectreq=new String[3][2];
					
					paramrejectreq[0][0]="@mode";
					paramrejectreq[0][1]="rejectrequest";
					
					paramrejectreq[1][0]="@UserId1";
					paramrejectreq[1][1]=currentUser.getUserId();

					paramrejectreq[2][0]="@UserId2";
					paramrejectreq[2][1]=sp.getString("UserId","no_data");
					
					screjectrequest.executeDQLSP1("spAndroid", paramrejectreq);
					LocalDBHandler3 dbdelete=new LocalDBHandler3(getActivity().getBaseContext());
					
					sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
				    String UserId=sp.getString("UserId","no_data");
					dbdelete.removeUserFromUserListTransaction(accepteduser.getUserId(),UserId,UserId,accepteduser.getUserId());
					dbdelete.removeUserFromUserMaster(currentUser.getUserId());
					Toast.makeText(getActivity().getBaseContext(), "Request Rejected", Toast.LENGTH_SHORT).show();
				}
			});
			
			LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
			String relname=db1.retrieveReceiverRelationId(currentUser.getUserId(),sp.getString("UserId","no_data"));
			tvuserrelation.setText("Wants to add you as " + relname);
			db1.close();
			
			
			return itemview;
		}
	}
	
	private void populateUserList() 
	{
		// TODO Auto-generated method stub
		LocalDBHandler2 db2=new LocalDBHandler2(getActivity().getBaseContext());
		SharedPreferences sp;
		 sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
	     String UserId=sp.getString("UserId","no_data");
	     Cursor cursorfriend=db2.retrieveFriendRequests(UserId);
	     if(cursorfriend.isBeforeFirst() && cursorfriend.getCount()>0){
	    	 while(cursorfriend.moveToNext())
	    		 {
		    		 UserMasterBean bean1=new UserMasterBean();
		    		 bean1.setUserId(cursorfriend.getString(0));
		    		 bean1.setFname(cursorfriend.getString(1));
		    		 bean1.setLname(cursorfriend.getString(3));
		    		 bean1.setDPUrl(cursorfriend.getString(10));
		    		 ult.add(bean1);
		    		 
	    		 }
	    	 
	    	 }
	     else
	     {
	    	 Toast.makeText(getActivity().getBaseContext(), "No Requests", Toast.LENGTH_LONG).show();
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
					SoapObject request = new SoapObject(NAMESPACE, "executeFriendAccept");
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
						androidHttpTransport.call(SOAP_ACTION+"executeFriendAccept", envelope);
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
					String[] stringlist=new String[2];
					if(resp!=null)
					{
						
					
						stringlist[0]=resp[0][0];
						stringlist[1]=resp[0][1];
					
					
						
						Log.i(TAG, "Service called with " + responseJSON);
						SharedPreferences sp;
						 sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
						LocalDBHandler3 dbacceptreq=new LocalDBHandler3(getActivity().getBaseContext());
						dbacceptreq.setAcceptRequest(accepteduser.getUserId(),sp.getString("UserId","no_data"));
						UserListTransactionBean ubean=new UserListTransactionBean();
					ubean.setUserListId(stringlist[0]);
					ubean.setUserId1(sp.getString("UserId","no_data"));
					ubean.setUserId2(accepteduser.getUserId());
					
					LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
					String relname=db1.retrieveReceiverRelationId(accepteduser.getUserId(),sp.getString("UserId","no_data"));
					LocalDBHandler3 dbrelationid1=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor cursorrelationid1=dbrelationid1.retrieveRelationId(relname);
					if(cursorrelationid1.isBeforeFirst() && cursorrelationid1.getCount()>0)
					{
						cursorrelationid1.moveToFirst();
						ubean.setSenderRelId(cursorrelationid1.getString(0));
					}
					
					LocalDBHandler2 db3=new LocalDBHandler2(getActivity().getBaseContext());
					String relnamereciver=db3.retrieveReceiverRelationId(accepteduser.getUserId(),sp.getString("UserId","no_data"));
					LocalDBHandler3 dbrelationid=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor cursorrelationid=dbrelationid.retrieveRelationId(relnamereciver);
					if(cursorrelationid.isBeforeFirst() && cursorrelationid.getCount()>0)
					{
						cursorrelationid.moveToFirst();
						ubean.setReceiverRelId(cursorrelationid.getString(0));
					
						
					}
					
					ubean.setIsSharingLocation("True");
					ubean.setIsAccept("True");
					ubean.setIsDelete("False");
					LocalDBHandler3 dbdatetime=new LocalDBHandler3(getActivity().getBaseContext());
					ubean.setCreationDate(dbdatetime.getDateTime());
					ArrayList<UserListTransactionBean> arrayult =new  ArrayList<UserListTransactionBean>();
					LocalDBHandler2 db2=new LocalDBHandler2(getActivity().getBaseContext());
					arrayult.add(ubean);
					db2.insertUserListTransaction(arrayult);

					
					
					
					GroupTransactionBean gtbean=new GroupTransactionBean();
					gtbean.setGtId(stringlist[1]);
					Cursor cgpid= dbdatetime.retrivegroupid();
					if(cgpid.isBeforeFirst() && cgpid.getCount()>0)
					{
						cgpid.moveToFirst();
						gtbean.setGroupId(cgpid.getString(0));
					
						
					}
					
					gtbean.setUserId(accepteduser.getUserId());
					gtbean.setIsDelete("False");
					gtbean.setUpdationDate(dbdatetime.getDateTime());
					
					ArrayList<GroupTransactionBean> arrayult1 =new  ArrayList<GroupTransactionBean>();
					LocalDBHandler2 db4=new LocalDBHandler2(getActivity().getBaseContext());
					arrayult1.add(gtbean);
					db4.insertGroupTransaction(arrayult1);
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
		public String[][] executeDQLSP1(String spName,String[][] allParams)
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
					if(responseJSON!=null)
					{
						
						/*if(responseJSON.equals("Send"))
						{
							Toast.makeText(getActivity().getBaseContext(),"Your request is accepted", Toast.LENGTH_SHORT).show();
							
							Log.i(TAG, "I am done");
						}
						else
						{
							Toast.makeText(getActivity().getBaseContext(),"Your request is not accepted", Toast.LENGTH_SHORT).show();
						} */
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
						pDial.show();
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
	

	     
}

	
	