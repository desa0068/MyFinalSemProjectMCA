package com.bsns.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
 
import android.widget.Toast;

import com.bsns.app.UserDataRetrieve.ServiceCaller;
import com.bsns.beans.CountryBean;
import com.bsns.beans.GroupMasterBean;
import com.bsns.beans.GroupTransactionBean;
import com.bsns.beans.LocationRequestBean;
import com.bsns.beans.LocationTransactionBean;
import com.bsns.beans.MyPlanLocationMasterBean;
import com.bsns.beans.MylocationTransactionBean;
import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserListTransactionBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.bsns.dbhandler.LocalDBHandler4;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;

import static com.bsns.app.CommonUtilities.SENDER_ID;
import static com.bsns.app.CommonUtilities.displayMessage;
 
public class GCMIntentService extends GCMBaseIntentService {
 
	private static String responseJSON;
	private static String[] resp;
	private static int dmlstatus;
	static Builder mNotifyBuilder;
	static NotificationManager mNotificationManager;
    private static final String TAG = "GCMIntentService";
    ProgressDialog pDial;
	String pid;
	private static String[][] resp1;
    static int notifyID = 0;
    public GCMIntentService() {
        super(SENDER_ID);
    }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        ServiceCaller sc2=new ServiceCaller(getString(R.string.serviceurl));
    	String[][] l=new String[3][2];
    	
    	l[0][0]="@GcmId";
		l[0][1]=registrationId;
    	
		l[1][0]="@UserId";
		SharedPreferences sp;
        sp=getSharedPreferences("controller_check", MODE_WORLD_READABLE);
		l[1][1]=sp.getString("UserId","no_data");
		
		l[2][0]="@mode";
		l[2][1]="updategcmid";
		
		sc2.executeDMLSP("spAndroid", l);
        
        
        //ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
    }
 
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("message");
        String time = intent.getExtras().getString("time"); 
        //displayMessage(context, "Message was:" + message + " and time was" + time);
        // notifies user
        //generateNotification(context, message);
        StringTokenizer st=new StringTokenizer(message, "?$?");
        int cnt=0;
        while(st.hasMoreTokens())
        {
        	String keycheck=st.nextElement().toString();
        	if(cnt==0)
        	{
        		if(keycheck.equals("FriendRequest"))
        		{
        			UserMasterBean userMasterBean=new UserMasterBean();
        			UserListTransactionBean userListTransactionBean=new UserListTransactionBean();
        			int innercnt=1;
        			while(st.hasMoreTokens())
        			{
        				String innervalue=st.nextElement().toString();
        				switch (innercnt) {
							case 1:	userListTransactionBean.setUserListId(innervalue); 
							break;
							
							case 2: userMasterBean.setUserId(innervalue);
									userListTransactionBean.setUserId1(innervalue);
							break;
							
							case 3:userMasterBean.setFname(innervalue);
							break;
							
							case 4:userMasterBean.setMname(innervalue);
							break;
							
							case 5:userMasterBean.setLname(innervalue);
							break;
							
							case 6:userMasterBean.setEmailId(innervalue);
							break;
							
							case 7:userMasterBean.setContactNo(innervalue);
							break;
							
							case 8:userMasterBean.setPassword(innervalue);
							break;
							
							case 9:userMasterBean.setGender(innervalue);
							break;
							
							case 10:userMasterBean.setRegDate(innervalue);
							break;
							
							case 11:userMasterBean.setIsDelete(innervalue);
							break;
							
							case 12:userMasterBean.setDPUrl(innervalue.substring(innervalue.lastIndexOf("/")+1).substring(0,innervalue.substring(innervalue.lastIndexOf("/")+1).indexOf(".")));
							break;
							
							case 13:userMasterBean.setCountryId(innervalue);
							break;
							
							case 14:userListTransactionBean.setUserId2(innervalue);
							
							break;
							
							case 15:userListTransactionBean.setSenderRelId(innervalue);
							break;
							
							case 16:userListTransactionBean.setReceiverRelId(innervalue);
							break;
							
													
						}
        				innercnt++;
        				
        			}
        			userListTransactionBean.setIsSharingLocation("True");
					userListTransactionBean.setIsAccept("False");				
					userListTransactionBean.setIsDelete("False");
					LocalDBHandler3 dbgetdatetime=new LocalDBHandler3(getApplicationContext());
					userListTransactionBean.setCreationDate(dbgetdatetime.getDateTime());
        			ArrayList<UserListTransactionBean> arrayultb=new ArrayList<UserListTransactionBean>();
        			arrayultb.add(userListTransactionBean);
        			
        			ArrayList<UserMasterBean> arrayumb=new ArrayList<UserMasterBean>();
        			arrayumb.add(userMasterBean);
        			
        			LocalDBHandler2 dbinsert=new LocalDBHandler2(getApplication());
        			dbinsert.insertUserMaster(arrayumb);
        			dbinsert.insertUserListTransaction(arrayultb);
        			
        			dbinsert.close();
        			
        			generateNotification(context, "Friend request from " + userMasterBean.getFname() + " " + userMasterBean.getLname());
        		}
        		if(keycheck.equals("AcceptRequest"))
        		{
        			UserMasterBean userMasterBean=new UserMasterBean();
        			UserListTransactionBean userListTransactionBean=new UserListTransactionBean();
        			UserListTransactionBean userListTransactionBean1=new UserListTransactionBean();
        			GroupTransactionBean gpbean=new GroupTransactionBean();
        			
        			int innercnt=1;
        			while(st.hasMoreTokens())
        			{
        				String innervalue=st.nextElement().toString();
        				switch (innercnt) {
							case 1:	userListTransactionBean.setUserListId(innervalue);
							
									
							break;
							
							case 2: userMasterBean.setUserId(innervalue);
									userListTransactionBean.setUserId1(innervalue);
									userListTransactionBean1.setUserId2(innervalue);
									gpbean.setUserId(innervalue);
									break;
							
							case 3:userMasterBean.setFname(innervalue);
							break;
							
							case 4:userMasterBean.setMname(innervalue);
							break;
							
							case 5:userMasterBean.setLname(innervalue);
							break;
							
							case 6:userMasterBean.setEmailId(innervalue);
							break;
							
							case 7:userMasterBean.setContactNo(innervalue);
							break;
							
							case 8:userMasterBean.setPassword(innervalue);
							break;
							
							case 9:userMasterBean.setGender(innervalue);
							break;
							
							case 10:userMasterBean.setRegDate(innervalue);
							break;
							
							case 11:userMasterBean.setIsDelete(innervalue);
							break;
							
							case 12:userMasterBean.setDPUrl(innervalue.substring(innervalue.lastIndexOf("/")+1).substring(0,innervalue.substring(innervalue.lastIndexOf("/")+1).indexOf(".")));
							break;
							
							case 13:userMasterBean.setCountryId(innervalue);
							break;
							
							case 14:userListTransactionBean.setUserId2(innervalue);
									userListTransactionBean1.setUserId1(innervalue);
									
							break;
							
							case 15:userListTransactionBean.setSenderRelId(innervalue);
									userListTransactionBean1.setReceiverRelId(innervalue);
							break;
							
							case 16:userListTransactionBean.setReceiverRelId(innervalue);
									userListTransactionBean1.setSenderRelId(innervalue);
							break;
							
							case 17:userListTransactionBean1.setUserListId(innervalue);
							break;
							
							case 18:gpbean.setGtId(innervalue);
							break;
							
							case 19:gpbean.setGroupId(innervalue);
							break;
													
						}
        				innercnt++;
        				
        			}
        			userListTransactionBean.setIsSharingLocation("True");
        			userListTransactionBean1.setIsSharingLocation("True");
					userListTransactionBean.setIsAccept("True");
					userListTransactionBean1.setIsAccept("True");
					userListTransactionBean.setIsDelete("False");
					userListTransactionBean1.setIsDelete("False");
					gpbean.setIsDelete("False");
				
					LocalDBHandler3 dbgetdatetime=new LocalDBHandler3(getApplicationContext());
					userListTransactionBean.setCreationDate(dbgetdatetime.getDateTime());
					userListTransactionBean1.setCreationDate(dbgetdatetime.getDateTime());
					gpbean.setUpdationDate(dbgetdatetime.getDateTime());
        			ArrayList<UserListTransactionBean> arrayultb=new ArrayList<UserListTransactionBean>();
        			arrayultb.add(userListTransactionBean);
        			arrayultb.add(userListTransactionBean1);
        			
        			ArrayList<GroupTransactionBean> arraygrp=new ArrayList<GroupTransactionBean>();
        			arraygrp.add(gpbean);
        			ArrayList<UserMasterBean> arrayumb=new ArrayList<UserMasterBean>();
        			arrayumb.add(userMasterBean);
        			
        			LocalDBHandler2 dbinsert=new LocalDBHandler2(getApplication());
        			dbinsert.insertUserMaster(arrayumb);
        			dbinsert.insertUserListTransaction(arrayultb);
        			dbinsert.insertGroupTransaction(arraygrp);
        			dbinsert.close();

        			generateNotification(context,  "" + userMasterBean.getFname() + " " + userMasterBean.getLname() + " accepted your friend request");
        		}
        		if(keycheck.equals("RemoveFriend"))
        		{
        			
        			String innervalue=st.nextElement().toString();
        			SharedPreferences sp;
        	        sp=getSharedPreferences("controller_check", MODE_WORLD_READABLE);
        			LocalDBHandler3 db3=new LocalDBHandler3(getApplication().getBaseContext());
        			db3.removeUserFromUserMaster(innervalue);
        			db3.removeUserFromGroupTransaction(innervalue);
        			db3.removeUserFromUserListTransaction(innervalue, sp.getString("UserId","no_data"), sp.getString("UserId","no_data"), innervalue);
        			
        			generateNotification(context,  "One user removed you");
        		}
        	}
        	if(keycheck.equals("NewPlanLocation"))
        	{
        		MyPlanLocationMasterBean myPlanLocationMasterBean=new MyPlanLocationMasterBean();
        		int innercnt=1;
        		while(st.hasMoreTokens())
        		{
        			String innervalue=st.nextElement().toString();
        			switch (innercnt) 
        			{
        				case 1:	myPlanLocationMasterBean.setPlanLocId(innervalue);
        				break;
        				
        				case 2:	myPlanLocationMasterBean.setUserId(innervalue);
        				break;
        				
        				case 3:	myPlanLocationMasterBean.setLatitude(innervalue);
        				break;
        				
        				case 4:	myPlanLocationMasterBean.setLongitude(innervalue);
        				break;
        				
        				case 5:	myPlanLocationMasterBean.setRadius(innervalue);
        				break;
        				
        				case 6:	myPlanLocationMasterBean.setRepeatTime(innervalue);
        				break;
        				
        				case 7:	myPlanLocationMasterBean.setCreationDate(innervalue);
        				break;
        				
        				case 8:	myPlanLocationMasterBean.setIsDelete(innervalue);
        				break;
        			}
        			innercnt++;
        		}
        		
        		LocalDBHandler4 db4=new LocalDBHandler4(getApplication().getBaseContext());
        		int AmPiId=Integer.parseInt(db4.generateUniqueAMPiId());
        		int PlPiId=Integer.parseInt(db4.generateUniquePlPiId());
        		myPlanLocationMasterBean.setAmPiId("" + AmPiId);
        		myPlanLocationMasterBean.setPlPiId("" + PlPiId);
        		
        		ArrayList<MyPlanLocationMasterBean> arrayList=new ArrayList<MyPlanLocationMasterBean>();
        		arrayList.add(myPlanLocationMasterBean);
        		db4.insertMyPlanLocationMaster(arrayList);

        		PendingIntent pendingIntent;
        		AlarmManager manager;

        		
        		if(!myPlanLocationMasterBean.getRepeatTime().equals("0"))
        		{
        			Intent alarmIntent = new Intent(getApplicationContext(),RepeatingTaskHandler.class);
            		alarmIntent.putExtra("AmPiId", "" + AmPiId);
            		pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),AmPiId , alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            		
            		
            		manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            		long interval = Integer.parseInt(myPlanLocationMasterBean.getRepeatTime())*1000;
            		
            		manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            		Toast.makeText(getApplicationContext(), "Alaram set", Toast.LENGTH_SHORT).show();
        		}
        		else
        		{
        			Toast.makeText(getApplicationContext(), "Alaram not set", Toast.LENGTH_SHORT).show();
        		}
        		
        		// here now we will set proximitylocationalert
        		Intent planlocationintent = new Intent(getApplicationContext(),PLDestinationHandler.class);
        		planlocationintent.putExtra("PlPiId", "" + PlPiId);
        		PendingIntent pendingIntent2=PendingIntent.getBroadcast(getApplicationContext(),PlPiId,planlocationintent, PendingIntent.FLAG_CANCEL_CURRENT);
        		new GPSTracker(getApplicationContext()).locationManager.addProximityAlert(Double.parseDouble(myPlanLocationMasterBean.getLatitude()),Double.parseDouble(myPlanLocationMasterBean.getLongitude()),Float.parseFloat(myPlanLocationMasterBean.getRadius()),-1,pendingIntent2);

        		
        		generateNotification(context, "New plan location is set on you");
        	}
        	if(keycheck.equals("PlanLocationData"))
        	{
        		LocationTransactionBean locationTransactionBean=new LocationTransactionBean();
        		int innercnt=1;
        		while(st.hasMoreTokens())
        		{
        			// we create bean so that it makes easy to utilize these values
        			String innervalue=st.nextElement().toString();
        			switch (innercnt) 
        			{
        				case 1:	locationTransactionBean.setUserId(innervalue);
        				break;
        				
        				case 2:	locationTransactionBean.setLatitude(innervalue);
        				break;
        				
        				case 3:	locationTransactionBean.setLongitude(innervalue);
        				break;
        				
        				case 4:	locationTransactionBean.setDeviceName(innervalue);
        				break;
        				
        				case 5:	locationTransactionBean.setUpdatedAt(innervalue);
        				break;
        			
        			}
        			innercnt++;
        		}
        		
        		String gcmmessage="";
        		
        		LocalDBHandler2 db2=new LocalDBHandler2(getApplicationContext());
        		Cursor cursor=db2.retrieveUserMasterById(locationTransactionBean.getUserId());
        		if(cursor.isBeforeFirst() && cursor.getCount()>0)
        		{
        			if(cursor.moveToNext())
        			{
        				gcmmessage=cursor.getString(1) + " " + cursor.getString(3);  
        			}
        		}
        		
        		ArrayList<LocationTransactionBean> arralist1=new ArrayList<>();
        		arralist1.add(locationTransactionBean);
        		db2.insertLocationTransaction(arralist1);
        		db2.close();
        		
        		Geocoder geocoder=new Geocoder(getApplicationContext());
        		List<Address> addresses=null;
				try {
					addresses = geocoder.getFromLocation(Double.parseDouble(locationTransactionBean.getLatitude()),Double.parseDouble(locationTransactionBean.getLongitude()), 1);
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
				
				gcmmessage=gcmmessage + " is located at " + loctitle;
        		generateNotification(context, gcmmessage);
        	}

        	if(keycheck.equals("ReachedPlanlocation"))
        	{
        		generateNotification(context, st.nextElement().toString());
    		}
        	if(keycheck.equals("CancelPlanLocation"))
    		{
        		int ampid=0;
        		int plpid=0;
        		//generateNotification(context, st.nextElement().toString());
        		pid=st.nextElement().toString();
        		LocalDBHandler3 dbloc=new LocalDBHandler3(getApplicationContext());
        		Cursor cloc=dbloc.retrieveAmpIdPlPId(pid);
        		if(cloc.isBeforeFirst() && cloc.getCount()>0)
        		{
        			cloc.moveToFirst();
        			ampid=Integer.parseInt(cloc.getString(0));
        			plpid=Integer.parseInt(cloc.getString(1));
        		}
        		// Here, we stop our repeatingtask
    			PendingIntent pendingIntent;
    			AlarmManager manager;
    			Intent alarmIntent = new Intent(getApplicationContext(),RepeatingTaskHandler.class);
    			pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),ampid, alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
    			manager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    			manager.cancel(pendingIntent);
    			
    			// here we will remove planlocationhandler
    			Intent planlocationintent = new Intent(getApplicationContext(),PLDestinationHandler.class);
    	        PendingIntent pendingIntent2=PendingIntent.getBroadcast(getApplicationContext(),plpid,planlocationintent, PendingIntent.FLAG_CANCEL_CURRENT);
    			GPSTracker gpsTracker=new GPSTracker(getApplicationContext());
    			gpsTracker.locationManager.removeProximityAlert(pendingIntent2);			
    			LocalDBHandler3 dbdel=new LocalDBHandler3(getApplicationContext());
				
				dbdel.removeMyPlanLocation(pid);
				dbdel.close();
    		}
        	cnt++;
        	
        }
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }
 
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        /*int icon = R.drawable.app_logo;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
         
        String title = context.getString(R.string.app_name);
         
        Intent notificationIntent = new Intent(context, UserHomeActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
         
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
         
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      
         */
    	mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	// Sets an ID for the notification, so it can be updated
    	notifyID++;
    	if(notifyID>20000)
    	{
    		notifyID=0;
    	}
    	mNotifyBuilder = new NotificationCompat.Builder(context)
    	    .setContentTitle("Be Secure Navigation System")
    	    .setContentText(message)
    	    .setSmallIcon(R.drawable.app_logo);
    	
    	    mNotifyBuilder.setContentText(message);
    	    // Because the ID remains unchanged, the existing notification is
    	    // updated.
    	    mNotificationManager.notify(
    	            notifyID,
    	            mNotifyBuilder.build());
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
						Toast.makeText(getApplicationContext(),"dml status is:" + dmlstatus , Toast.LENGTH_SHORT).show();
						//lblMessage.append("dml status is:" + dmlstatus);
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


}
