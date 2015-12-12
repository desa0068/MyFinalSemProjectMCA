package com.bsns.app;

import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsns.app.FriendDetailsFragment.ServiceCaller;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.google.gson.Gson;

public class UserProfileFragment extends Fragment 
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
	String cname;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View view=inflater.inflate(R.layout.userprofile, container, false);
		//Toast.makeText(getActivity().getBaseContext(),"Called for " + getArguments().getString("FriendId") , Toast.LENGTH_SHORT).show();
		ImageButton btnlogout=(ImageButton)view.findViewById(R.id.btnlogout);
		
		btnlocationmgt=(ImageButton)view.findViewById(R.id.btnfriendsdetailslocationmanagement);
		
		
		SharedPreferences sp3=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
		
		
		UserMasterBean bean1=new UserMasterBean();
		LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
		Cursor cursor=db1.retrieveUserMasterById(sp3.getString("UserId","no_data"));
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
			TextView tvmname=(TextView)view.findViewById(R.id.tvusermiddlename);
			TextView tvcontactno=(TextView)view.findViewById(R.id.tvfrienddetailscontactno);
			TextView tvemailid=(TextView)view.findViewById(R.id.tvfrienddetailsemailid);
			TextView tvgender=(TextView)view.findViewById(R.id.tvusergender);
			TextView tvcountry=(TextView)view.findViewById(R.id.tvusercountry);
			TextView tvregistered=(TextView)view.findViewById(R.id.tvuserregistrationdate);
			
			tvname.setText(bean1.getFname() + " " + bean1.getLname());
			tvmname.setText(bean1.getMname());
			if(bean1.getGender().equals("F"))
			{
				tvgender.setText("Female");	
			}
			else
			{
				tvgender.setText("Male");
			}
			
			
			
			LocalDBHandler3 dbcountry=new LocalDBHandler3(getActivity().getBaseContext());
			Cursor ccountry=dbcountry.retrieveCountryName(bean1.getCountryId());
			if(ccountry.isBeforeFirst() && ccountry.getCount()>0)
			{
				ccountry.moveToFirst();
				cname=ccountry.getString(0);
			}
			tvcountry.setText(cname);
			
			
			tvregistered.setText(bean1.getRegDate());
			
			SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
			UserId=sp.getString("UserId","no_data");
			db1=new LocalDBHandler2(getActivity().getBaseContext());
			
			tvcontactno.setText("Mobile number: " + bean1.getContactNo());
			tvemailid.setText("Email-Id: " + bean1.getEmailId());
			ImageView img=(ImageView) view.findViewById(R.id.imgvfrienddetailsuserpic);
			img.setImageResource(getResources().getIdentifier(bean1.getDPUrl() , "drawable", getActivity().getPackageName()));
			
			btnlocationmgt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Fragment fragment=new LocationManagement();
					SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
					Bundle b=new Bundle();
			        b.putString("FriendId",sp.getString("UserId","no_data"));
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
			
			btnlogout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LocalDBHandler3 dbdelete=new LocalDBHandler3(getActivity().getBaseContext());
					dbdelete.deletealldata();
					
					SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
					SharedPreferences.Editor editor=sp.edit();
					editor.putString("data_fetch", "done");
					
					
					editor.remove("login_check");
					editor.remove("data_fetch");
					editor.remove("UserId");
					editor.commit();
					
					Intent i=new Intent(getActivity().getBaseContext(),Controller_redirect.class);
					startActivity(i);
					getActivity().finish();
				}
			});
			
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
