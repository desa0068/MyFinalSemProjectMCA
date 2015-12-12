package com.bsns.app;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.ClientInfo;
import com.bsns.beans.CountryBean;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.GroupTransactionBean;
import com.bsns.beans.LocationTransactionBean;
import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.internal.db;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import static com.bsns.app.CommonUtilities.SENDER_ID;
import static com.bsns.app.CommonUtilities.SERVER_URL;

public class UserDataRetrieve extends Activity {
	SharedPreferences sp;
	private static String responseJSON;
	private static String[] resp;
	private static int dmlstatus;

	// alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
     
    // Internet detector
    ConnectionDetector cd;
     
    // UI elements
    EditText txtName;
    EditText txtEmail;
     
    // Register button
    Button btnRegister;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdataretrievelayout);
		
		sp=getSharedPreferences("controller_check", MODE_WORLD_READABLE);
		Toast.makeText(UserDataRetrieve.this, "I am here",Toast.LENGTH_SHORT).show();
		ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
		String UserId=sp.getString("UserId","no_data");
		if(!UserId.equals("no_data"))
		{
			sc1.executeDQLSP(UserId);
		}
		
		registerReceiver(br2, new IntentFilter("dataretrieved"));
		
		cd = new ConnectionDetector(getApplicationContext());
				
		final String regId = GCMRegistrar.getRegistrationId(this);
		 
		// Check if regid already presents
		if (regId.equals(""))
		{
		    // Registration is not present, register now with GCM           
		    GCMRegistrar.register(this, SENDER_ID);
		} 
		else
		{
		    // Device is already registered on GCM
		    if (!GCMRegistrar.isRegisteredOnServer(this))
		    {
				GCMRegistrar.setRegisteredOnServer(this, true);
		    }
	    }
	}
	
	final BroadcastReceiver br2=new BroadcastReceiver(){
	   	   @Override
			public void onReceive(Context context, Intent intent) {
		   		SharedPreferences.Editor editor=sp.edit();
				editor.putString("data_fetch", "done");
				editor.commit();
				
				Intent i=new Intent(UserDataRetrieve.this,Controller_redirect.class);
				startActivity(i);
				finish();
		    }
	   	};
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(br2);
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
		public String[] executeDQLSP(String UserId)
		{
			PropertyInfo p1=new PropertyInfo();

			// set which sp to call
			
			// Set Name
			p1.setName("UserId");
			// Set Value
			p1.setValue(UserId);
			// Set dataType
			p1.setType(String.class);
			
			
			final PropertyInfo parameters1=p1;
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
					SoapObject request = new SoapObject(NAMESPACE, "retrieveAllDetails");
					// Property which holds input parameters
					request.addProperty(parameters1);
					// Create envelope
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.dotNet = true;
					// Set output SOAP object
					envelope.setOutputSoapObject(request);
					// Create HTTP call object
					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

					try {
						// Invoke web service
						androidHttpTransport.call(SOAP_ACTION+"retrieveAllDetails", envelope);
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
				protected void onPostExecute(Void result)
				{
					Log.i(TAG, "onPostExecute");
					//Convert 'Countries' JSON response into String array using fromJSON method
					//placelist = gson.fromJson(responseJSON, String[].class);
					resp=gson.fromJson(responseJSON, String[].class);
					int cnt2=0;
					LocalDBHandler2 db1=new LocalDBHandler2(UserDataRetrieve.this);
					
					if(resp!=null)
					{
						for(int i=0;i<resp.length;i++)
						{
							//Log.i(TAG, "Service called with " + resp[i]);							
							StringTokenizer st=new StringTokenizer(resp[i],"?$?");
							int cnt=0;
							while(st.hasMoreTokens())
							{
								String onedata=st.nextElement().toString();
								if(cnt==0)
								{
									switch(onedata)
									{
										case "UserFriends":
											ArrayList<UserMasterBean> userMasterBean=new ArrayList<>();
											UserMasterBean bean1=new UserMasterBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean1.setUserId(detaildata);
													break;
													
													case 1:
														bean1.setFname(detaildata);
													break;
													
													case 2:
														bean1.setMname(detaildata);
													break;
													
													case 3:
														bean1.setLname(detaildata);
													break;
													
													case 4:
														bean1.setEmailId(detaildata);
													break;
													
													case 5:
														bean1.setContactNo(detaildata);
													break;
													
													case 6:
														bean1.setPassword(detaildata);
													break;
													
													case 7:
														bean1.setGender(detaildata);
													break;
													
													case 8:
														bean1.setRegDate(detaildata);
													break;
													
													case 9:
														bean1.setIsDelete(detaildata);
													break;
													
													case 10:
														bean1.setDPUrl(detaildata.substring(detaildata.lastIndexOf("/")+1).substring(0,detaildata.substring(detaildata.lastIndexOf("/")+1).indexOf(".")));
													break;
													
													case 11:
														bean1.setCountryId(detaildata);
													break;
												}
												cnt2++;
											}
											userMasterBean.add(bean1);
											db1.insertUserMaster(userMasterBean);

										break;
											
										case "UserDetails":
											ArrayList<UserMasterBean> userMasterBean2=new ArrayList<>();
											UserMasterBean bean4=new UserMasterBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean4.setUserId(detaildata);
													break;
													
													case 1:
														bean4.setFname(detaildata);
													break;
													
													case 2:
														bean4.setMname(detaildata);
													break;
													
													case 3:
														bean4.setLname(detaildata);
													break;
													
													case 4:
														bean4.setEmailId(detaildata);
													break;
													
													case 5:
														bean4.setContactNo(detaildata);
													break;
													
													case 6:
														bean4.setPassword(detaildata);
													break;
													
													case 7:
														bean4.setGender(detaildata);
													break;
													
													case 8:
														bean4.setRegDate(detaildata);
													break;
													
													case 9:
														bean4.setIsDelete(detaildata);
													break;
													
													case 10:
														bean4.setDPUrl(detaildata.substring(detaildata.lastIndexOf("/")+1).substring(0,detaildata.substring(detaildata.lastIndexOf("/")+1).indexOf(".")));
													break;
													
													case 11:
														bean4.setCountryId(detaildata);
													break;
												}
												cnt2++;
											}
											userMasterBean2.add(bean4);
											db1.insertUserMaster(userMasterBean2);
											
										break;
											
										case "UserTransaction":
											
											ArrayList<UserListTransactionBean> userListTransactionBean=new ArrayList<>();
											UserListTransactionBean bean5=new UserListTransactionBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean5.setUserListId(detaildata);
													break;
													
													case 1:
														bean5.setUserId1(detaildata);
													break;
													
													case 2:
														bean5.setUserId2(detaildata);
													break;
													
													case 3:
														bean5.setSenderRelId(detaildata);
													break;
													
													case 4:
														bean5.setReceiverRelId(detaildata);
													break;
													
													case 5:
														bean5.setIsSharingLocation(detaildata);
													break;
													
													case 6:
														bean5.setIsAccept(detaildata);
													break;
													
													case 7:
														bean5.setIsDelete(detaildata);
													break;
													
													case 8:
														bean5.setCreationDate(detaildata);
													break;
												}
												cnt2++;
											}
											userListTransactionBean.add(bean5);
											db1.insertUserListTransaction(userListTransactionBean);
											
										break;
										
										case "GroupMaster":
											
											ArrayList<GroupMasterBean> groupMasterBean=new ArrayList<>();
											GroupMasterBean bean2=new GroupMasterBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean2.setGroupId(detaildata);
													break;
													
													case 1:
														bean2.setGroupName(detaildata);
													break;
													
													case 2:
														bean2.setUserId(detaildata);
													break;
													
													case 3:
														bean2.setDPUrl(detaildata.substring(detaildata.lastIndexOf("/")+1).substring(0,detaildata.substring(detaildata.lastIndexOf("/")+1).indexOf(".")));
													break;
													
													case 4:
														bean2.setIsDelete(detaildata);
													break;
													
													case 5:
														bean2.setCreationDate(detaildata);
													break;
													
												}
												cnt2++;
											}
											groupMasterBean.add(bean2);
											db1.insertGroupMaster(groupMasterBean);

										break;
										
										case "RelationMaster":
											
											ArrayList<RelationMasterBean> relationMasterBean=new ArrayList<>();
											RelationMasterBean bean7=new RelationMasterBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean7.setRelId(detaildata);
													break;
													
													case 1:
														bean7.setRelName(detaildata);
													break;
													
													case 2:
														bean7.setIsDelete(detaildata);
													break;
												}
												cnt2++;
											}
											relationMasterBean.add(bean7);
											db1.insertRelationMaster(relationMasterBean);
											
										break;
										
											
										case "GroupTransaction":
											
											ArrayList<GroupTransactionBean> groupTransactionBean=new ArrayList<>();
											GroupTransactionBean bean3=new GroupTransactionBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean3.setGtId(detaildata);
													break;
													
													case 1:
														bean3.setGroupId(detaildata);
													break;
													
													case 2:
														bean3.setUserId(detaildata);
													break;
													
													case 3:
														bean3.setIsDelete(detaildata);
													break;
													
													case 4:
														bean3.setUpdationDate(detaildata);
													break;
												}
												cnt2++;
											}
											groupTransactionBean.add(bean3);
											db1.insertGroupTransaction(groupTransactionBean);
										break;	
											
										case "CountryMaster":
											ArrayList<CountryBean> countryBean=new ArrayList<>();
											CountryBean bean6=new CountryBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean6.setCountryId(detaildata);
													break;
													
													case 1:
														bean6.setCountryName(detaildata);
													break;
													
													case 2:
														bean6.setCountryCode(detaildata);
													break;
													
													case 3:
														bean6.setCountryDescription(detaildata);
													break;
													
													case 4:
														bean6.setIsDelete(detaildata);
													break;
												}
												cnt2++;
											}
											countryBean.add(bean6);
											db1.insertCountry(countryBean);
										break;
										
										case "ClientInfo":
											ArrayList<ClientInfo> clientInfo=new ArrayList<>();
											ClientInfo bean8=new ClientInfo();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean8.setPSubId(detaildata);
													break;
													
													case 1:
														bean8.setPSubName(detaildata);
													break;
													
													case 2:
														bean8.setLatitude(detaildata);
													break;
													
													case 3:
														bean8.setLongitude(detaildata);
													break;
													
												}
												cnt2++;
											}
											clientInfo.add(bean8);
											db1.insertClientInfo(clientInfo);
										break;
										
										
										case "LocationTransaction":
											ArrayList<LocationTransactionBean> locationTransactionBean=new ArrayList<>();
											LocationTransactionBean bean9=new LocationTransactionBean();

											cnt2=0;
											while(st.hasMoreTokens())
											{
												String detaildata=st.nextElement().toString();
												if(detaildata==null || detaildata.equals(""))
													detaildata="-";
												switch(cnt2)
												{
													case 0:
														bean9.setUserId(detaildata);
													break;
													
													case 1:
														bean9.setLatitude(detaildata);
													break;
													
													case 2:
														bean9.setLongitude(detaildata);
													break;
													
													case 3:
														bean9.setDeviceName(detaildata);
													break;
													
													case 4:
														bean9.setUpdatedAt(detaildata);
													break;
													
												}
												cnt2++;
											}
											locationTransactionBean.add(bean9);
											db1.insertLocationTransaction(locationTransactionBean);
										break;
									}
															
								}
								cnt++;
							}
						}
					}
					db1.close();
					Intent i=new Intent();
					i.setAction("dataretrieved");
					UserDataRetrieve.this.sendBroadcast(i);
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
	}
}
