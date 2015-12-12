package com.bsns.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
public class FriendDetailsFragment extends Fragment 
{
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
	Button b;
	ImageButton btnRemoveUser,btnlocationmgt; 
	Spinner spgroupname;
	RadioButton rdb;
	RadioButton rdb1;
	RadioGroup rg;
	String value;
	String UserId;
	String locstatus;
	ProgressDialog pDial;
	String groupid;
	String grpname;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View view=inflater.inflate(R.layout.frienddetailsfragment, container, false);
		//Toast.makeText(getActivity().getBaseContext(),"Called for " + getArguments().getString("FriendId") , Toast.LENGTH_SHORT).show();
		ImageButton btnApply=(ImageButton)view.findViewById(R.id.btnfriendsdetailslocationmanagementapply);
		btnRemoveUser=(ImageButton)view.findViewById(R.id.btnfriendsdetailsremoveuser);
		btnlocationmgt=(ImageButton)view.findViewById(R.id.btnfriendsdetailslocationmanagement);
	
		
		UserMasterBean bean1=new UserMasterBean();
		LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
		Cursor cursor=db1.retrieveUserMasterById(getArguments().getString("FriendId"));
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			cursor.moveToFirst();
			bean1.setUserId(cursor.getString(0));
			bean1.setFname(cursor.getString(1));
			bean1.setMname(cursor.getString(2));
			bean1.setLname(cursor.getString(3));
			bean1.setEmailId(cursor.getString(4));
			bean1.setContactNo(cursor.getString(5));
			bean1.setPassword(cursor.getString(6));
			bean1.setGender(cursor.getString(7));
			bean1.setRegDate(cursor.getString(8));
			bean1.setIsDelete(cursor.getString(9));
			bean1.setDPUrl(cursor.getString(10));
			bean1.setCountryId(cursor.getString(11));
			
			TextView tvname=(TextView)view.findViewById(R.id.tvfrienddetailsname);
			TextView tvrelation=(TextView)view.findViewById(R.id.tvfrienddetailsrelation);
			TextView tvcontactno=(TextView)view.findViewById(R.id.tvfrienddetailscontactno);
			TextView tvemailid=(TextView)view.findViewById(R.id.tvfrienddetailsemailid);
			
			tvname.setText(bean1.getFname() + " " + bean1.getLname());
			
			SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
			UserId=sp.getString("UserId","no_data");
			db1=new LocalDBHandler2(getActivity().getBaseContext());
			String relname=db1.retrieveReceiverRelationId(sp.getString("UserId","no_data"), bean1.getUserId());
			tvrelation.setText("Your " + relname);
			
			tvcontactno.setText("Mobile number: " + bean1.getContactNo());
			tvemailid.setText("Email-Id: " + bean1.getEmailId());
			ImageView img=(ImageView) view.findViewById(R.id.imgvfrienddetailsuserpic);
			img.setImageResource(getResources().getIdentifier(bean1.getDPUrl() , "drawable", getActivity().getPackageName()));
			
			btnlocationmgt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Fragment fragment=new LocationManagement();
					Bundle b=new Bundle();
			        b.putString("FriendId",getArguments().getString("FriendId"));
			        fragment.setArguments(b);
					FragmentManager fragmentManager = getActivity().getFragmentManager();
					FragmentTransaction ft=fragmentManager.beginTransaction();
					ft.replace(R.id.frame_container, fragment,"NewPL");
					if(fragmentManager.findFragmentByTag("NewPL")==null)
					{
						ft.addToBackStack("NewPL");	
					}
					
					ft.commit();
				}
			});
			
			btnApply.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					ServiceCaller sc=new ServiceCaller(getString(R.string.serviceurl));
					String[][] param=new String[6][2];
					param[0][0]="@mode";
					param[0][1]="applyupdation";
					
					param[1][0]="@IsSharingLocation";
					 rg = (RadioGroup)view.findViewById(R.id.rdgfrienddetailssharelocation);
				    value =((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
				    Toast.makeText(getActivity().getBaseContext(),value, Toast.LENGTH_SHORT).show();;
				    if(value.equals("On"))
					{
						param[1][1]="1";
					}
					if(value.equals("Off"))
					{
						param[1][1]="0";
					}
					
					param[2][0]="@UserId2";
					param[2][1]=getArguments().getString("FriendId");
					
					SharedPreferences sp;
			        sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
			        String UserId=sp.getString("UserId","no_data");
					param[3][0]="@UserId1";
					param[3][1]=UserId;

					param[4][0]="@GroupId";
					LocalDBHandler3 db4=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor cgrp=db4.loadGroupId(spgroupname.getSelectedItem().toString(),UserId);
					String grpid;
					if(cgrp.isBeforeFirst() && cgrp.getCount()>0)
					{
						cgrp.moveToFirst();
						grpid=cgrp.getString(0);
						param[4][1]=grpid;
						
					}
					
					param[5][0]="@UserId";
					param[5][1]=getArguments().getString("FriendId");
					
					sc.executeDMLSP("spAndroid",param);

					
				}
			} );
			
			btnRemoveUser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					ServiceCaller scremoveuser=new ServiceCaller(getString(R.string.serviceurl));
					String[][] paramRemoveUser=new String[8][2];
					
					paramRemoveUser[0][0]="@mode";
					paramRemoveUser[0][1]="removeuserfromfriendlist";
					
					paramRemoveUser[1][0]="@UserId";
					paramRemoveUser[1][1]=getArguments().getString("FriendId");
					
					paramRemoveUser[2][0]="@GroupId";
					LocalDBHandler3 dbremoveuser=new LocalDBHandler3(getActivity().getBaseContext());
					Cursor cgroupid=dbremoveuser.loadGroupId(spgroupname.getSelectedItem().toString(), UserId);
					if(cgroupid.isBeforeFirst() && cgroupid.getCount()>0)
					{
						cgroupid.moveToFirst();
						Toast.makeText(getActivity().getBaseContext(),cgroupid.getString(0), Toast.LENGTH_SHORT);
						paramRemoveUser[2][1]=cgroupid.getString(0);
						
						
					}
					
					paramRemoveUser[3][0]="@UserId1";
					paramRemoveUser[3][1]=UserId;
					
					paramRemoveUser[4][0]="@UserId44";
					paramRemoveUser[4][1]=UserId;
					
					paramRemoveUser[5][0]="@UserId2";
					paramRemoveUser[5][1]=getArguments().getString("FriendId");
					
					paramRemoveUser[6][0]="@UserId11";
					paramRemoveUser[6][1]=getArguments().getString("FriendId");
					
					paramRemoveUser[7][0]="@UserId22";
					paramRemoveUser[7][1]=UserId;
					
					scremoveuser.executeDMLSP2("spAndroid", paramRemoveUser);
				}
			});
		}
		 
		db1.close();
		SharedPreferences sp;
        sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
        UserId=sp.getString("UserId","no_data");
        UserListTransactionBean ulbean=new UserListTransactionBean();
		LocalDBHandler3 db3=new LocalDBHandler3(getActivity().getBaseContext());
		Cursor cursor1=db3.retrieveUserListTransactionDetails(UserId, getArguments().getString("FriendId"));
		if(cursor1.isBeforeFirst() && cursor1.getCount()>0)
		{
			cursor1.moveToFirst();
			ulbean.setIsSharingLocation(cursor.getString(0));
			//RadioGroup rdgfrienddetails=(RadioGroup)view.findViewById(R.id.rdgfrienddetailssharelocation);
			if(cursor1.getString(0).equals("True"))
			{
				 rdb=(RadioButton)view.findViewById(R.id.rdrdgfrienddetailslocationon);
				rdb.setChecked(true);
				
			}
			else
			{
				rdb1=(RadioButton)view.findViewById(R.id.rdrdgfrienddetailslocationoff);
				rdb1.setChecked(true);
			}
			
			
			
		}
		GroupMasterBean gpmasterBean=new GroupMasterBean();
		Cursor cursorgrpname=db3.loadGroupNames(UserId);
		String list1[]=new String[cursorgrpname.getCount()];
		spgroupname=(Spinner)view.findViewById(R.id.spfrienddetailsselectgroup);
		
		if(cursorgrpname.isBeforeFirst() && cursorgrpname.getCount()>0)
		{
			int cnt=0;
			while(cursorgrpname.moveToNext())
			{
				gpmasterBean.setGroupName(cursorgrpname.getString(0));
				
				
				
				list1[cnt]=cursorgrpname.getString(0);
				cnt++;
			}
			
		}
		
		ArrayAdapter<String> adapter =  new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, list1);
		spgroupname.setAdapter(adapter);
		LocalDBHandler3 dbgpid=new LocalDBHandler3(getActivity().getBaseContext());
		Cursor cgpid=dbgpid.retrievegroupid(getArguments().getString("FriendId"));
		if(cgpid.isBeforeFirst() && cgpid.getCount()>0)
		{
			cgpid.moveToFirst();
			groupid=cgpid.getString(0);
		}
		
		LocalDBHandler3 dbgp=new LocalDBHandler3(getActivity().getBaseContext());
		Cursor cgname=dbgp.retrievegroupname(groupid);
		if(cgname.isBeforeFirst() && cgname.getCount()>0)
		{
			cgname.moveToFirst();
			grpname=cgname.getString(0);
		}
		for(int i=0;i<list1.length;i++)
		{
			if(list1[i].toString().equals(cgname.getString(0)))
			{
			spgroupname.setSelection(i);
			}
		}
		
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
				
					
					Log.i(TAG, "Service called with " + responseJSON);
					
					if(resp!=null)
					{
						
					
						for(int i=0;i<resp.length;i++)
						{
							for(int j=0;j<resp[i].length;j++)
							{
								Log.i(TAG, "Service called with " + resp[i][j]);
								
								
								
							}
							
						}
						Log.i(TAG, "I am done");
						
						 
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
						//Toast.makeText(getActivity().getBaseContext(),""+responseJSON + "& you choose " + value, Toast.LENGTH_SHORT).show();
						dmlstatus=Integer.parseInt(responseJSON);
						LocalDBHandler3 dbupdate=new LocalDBHandler3(getActivity().getBaseContext());
						if(value.equals("On"))
						{
							locstatus="True";
						}
						if(value.equals("Off"))
						{
							locstatus="False";
						}
								
						dbupdate.updateUserListTransactionDetails(UserId, getArguments().getString("FriendId"), locstatus);
						Cursor cgroupid=dbupdate.loadGroupId(spgroupname.getSelectedItem().toString(), UserId);
						if(cgroupid.isBeforeFirst() && cgroupid.getCount()>0)
						{
							cgroupid.moveToFirst();
							cgroupid.getString(0);
							dbupdate.updateGroupTransaction(cgroupid.getString(0),getArguments().getString("FriendId"));
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
					
					//Log.i(TAG, "onPreExecute");
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
		public int executeDMLSP2(String spName,String[][] allParams)
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
					SoapObject request = new SoapObject(NAMESPACE, "executeFriendRemove");
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
						androidHttpTransport.call(SOAP_ACTION+"executeFriendRemove", envelope);
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
						//Toast.makeText(getActivity().getBaseContext(),""+responseJSON + "& you choose " + value, Toast.LENGTH_SHORT).show();
						LocalDBHandler3 dbremove1=new LocalDBHandler3(getActivity().getBaseContext());
						dbremove1.removeUserFromGroupTransaction(getArguments().getString("FriendId"));
						dbremove1.removeUserFromUserListTransaction(UserId, getArguments().getString("FriendId"), getArguments().getString("FriendId"), UserId);
						dbremove1.removeUserFromUserMaster(getArguments().getString("FriendId"));
						Toast.makeText(getActivity().getBaseContext(),"Friend removed", Toast.LENGTH_SHORT).show();
						
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
					pDial.show();

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
