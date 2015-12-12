package com.bsns.app;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.app.UserDataRetrieve.ServiceCaller;
import com.bsns.beans.ClientInfo;
import com.bsns.beans.CountryBean;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.GroupTransactionBean;
import com.bsns.beans.LocationTransactionBean;
import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

public class DataRefersh {
	Context context;
	private static String responseJSON;
	private static String[] resp;
	private static int dmlstatus;
	
	public DataRefersh(Context context) 
	{
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	public void startRetrivingData()
	{
		SharedPreferences sp;
		sp=context.getSharedPreferences("controller_check", context.MODE_WORLD_READABLE);
		Toast.makeText(context, "I am here",Toast.LENGTH_SHORT).show();
		ServiceCaller sc1=new ServiceCaller(context.getString(R.string.serviceurl));
		String UserId=sp.getString("UserId","no_data");
		if(!UserId.equals("no_data"))
		{
			sc1.executeDQLSP(UserId);
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
					LocalDBHandler2 db1=new LocalDBHandler2(context);
					
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
					i.setAction("datarefreshed");
					context.sendBroadcast(i);
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
