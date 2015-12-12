package com.bsns.app;

import info.bsns.adapter.NavDrawerListAdapter;
import info.bsns.model.NavDrawerItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
 
































import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bsns.beans.GroupMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler3;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;


















import com.google.gson.Gson;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
 
public class UserHomeActivity extends FragmentActivity {
	ProgressDialog pDial;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    //ServiceCaller variables
    private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private boolean isWarnedToClose = false;
    MapView mapView;
	GoogleMap googleMap;
	String oldgroupname;
	String newgroupname;
	String groupid;
	String removegroupid;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userhomelayout);
        getActionBar().setTitle("HOME");
        mTitle = mDrawerTitle = getTitle();
        
        // load slide menu items
        registerReceiver(br2, new IntentFilter("datarefreshed"));
        
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        // Home
       
        LocalDBHandler2 db1=new LocalDBHandler2(UserHomeActivity.this);
        Cursor cursor=db1.retrieveGroupMaster();
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			while(cursor.moveToNext())
			{
				navDrawerItems.add(new NavDrawerItem(cursor.getString(1),getResources().getIdentifier(cursor.getString(3) , "drawable", getPackageName())));
				//navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
			}
		}
		
        db1.close();
        
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Communities, Will add a counter here
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
        // Communities, Will add a counter here
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        
        
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name// nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
               
            }
 
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
                
            }
        };
        try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
      
        if (savedInstanceState == null) {
            // on first time display view for first nav item
           displayView(0);
           Fragment fragInstance;

           //Calling the Fragment newInstance Static method
          
        }

        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View vv,
                    int index, long arg3) {
                // TODO Auto-generated method stub
                 //Log.e("in onLongClick");
                 //String str=listView.getItemAtPosition(index).toString();
            	Context mContext = getApplicationContext();
                Resources res = mContext.getResources();
        		NavDrawerItem item1=(NavDrawerItem) mDrawerList.getItemAtPosition(index);
        		oldgroupname=item1.getTitle();
    			loadingPopup(item1.getTitle());
                return true;
            }
    	}); 

   }
 
    /**
     * Slide menu item click listener
//     * */
    
    private void loadingPopup(String GroupName) {
    	if(!GroupName.equals("Friends"))
    	{
		    LayoutInflater inflater = this.getLayoutInflater();
		    final View layout = inflater.inflate(R.layout.temp, null);
		    
		    final PopupWindow windows = new PopupWindow(layout ,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
		    TextView tv1=(TextView) windows.getContentView().findViewById(R.id.tvgrouppopuptitle);
		    final EditText etHorseName = (EditText) windows.getContentView().findViewById(R.id.etgrouppopuprenamegroup);
		    
		    etHorseName.clearFocus();
		    
		    tv1.setText(GroupName + " Group Options");
		    //windows.setFocusable(false);
		    windows.setTouchable(true); 
		    //windows.setOutsideTouchable(true);
		    layout.post(new Runnable() {
		        public void run() {
		            windows.showAtLocation(layout,Gravity.CENTER, 0, 0);
		        }
		    });
		    
		    Button btnclose = (Button) windows.getContentView().findViewById(R.id.btngrouppopupclose);
		    btnclose.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					windows.dismiss();
				}
			});
		    
		    ImageButton btnrename = (ImageButton) windows.getContentView().findViewById(R.id.btngrouppopuprename);
		    btnrename.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ServiceCaller screnamegroup=new ServiceCaller(getString(R.string.serviceurl));
					String[][] paramrenamegroup=new String[4][2];
					
					paramrenamegroup[0][0]="@mode";
					paramrenamegroup[0][1]="renamegroup";
					
					paramrenamegroup[1][0]="@GroupName";
					paramrenamegroup[1][1]=etHorseName.getText().toString();
					newgroupname=etHorseName.getText().toString();
					paramrenamegroup[2][0]="@GroupId";
					LocalDBHandler3 db3=new LocalDBHandler3(getApplicationContext());
					SharedPreferences sp;
			        sp=UserHomeActivity.this.getSharedPreferences("controller_check", UserHomeActivity.this.MODE_WORLD_READABLE);
			        String UserId=sp.getString("UserId","no_data");
					Cursor cursorgpid=db3.loadGroupId(oldgroupname, UserId);
					if(cursorgpid.isBeforeFirst() && cursorgpid.getCount()>0)
					{
						cursorgpid.moveToFirst();
						paramrenamegroup[2][1]=cursorgpid.getString(0);
						groupid=cursorgpid.getString(0);
					}
					
					paramrenamegroup[3][0]="@UserId";
					paramrenamegroup[3][1]=UserId;
					
					screnamegroup.executeDMLSP("spAndroid", paramrenamegroup);
					//Toast.makeText(UserHomeActivity.this, "Group renamed..!!", Toast.LENGTH_SHORT).show();
				}
			});
		    
		    ImageButton btnremove = (ImageButton) windows.getContentView().findViewById(R.id.btngrouppopupremovegroup);
		    btnremove.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					ServiceCaller scremovegroup=new ServiceCaller(getString(R.string.serviceurl));
					String[][] paramremovegroup=new String[5][2];
					
					paramremovegroup[0][0]="@mode";
					paramremovegroup[0][1]="removegroup";
					
					paramremovegroup[1][0]="@GroupId";
					LocalDBHandler3 db3=new LocalDBHandler3(getApplicationContext());
					SharedPreferences sp;
			        sp=UserHomeActivity.this.getSharedPreferences("controller_check", UserHomeActivity.this.MODE_WORLD_READABLE);
			        String UserId=sp.getString("UserId","no_data");
					Cursor cursorgpid=db3.loadGroupId(oldgroupname, UserId);
					if(cursorgpid.isBeforeFirst() && cursorgpid.getCount()>0)
					{
						cursorgpid.moveToFirst();
						paramremovegroup[1][1]=cursorgpid.getString(0);
						removegroupid=cursorgpid.getString(0);
					}
					
					paramremovegroup[2][0]="@UserId";
					paramremovegroup[2][1]=UserId;
					
					scremovegroup.executeDMLSP2("spAndroid", paramremovegroup);
					
					
				}
			});
    	}
    	else
    	{
    		Toast.makeText(UserHomeActivity.this,"You can't update 'Friends group'",Toast.LENGTH_SHORT).show();
    	}
	}
    
    
       
    
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener 
        {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position,
	                long id) 
	        {
	            // display view for selected nav drawer item
	        	        	
	        	
	        
	        	displayView(position);        	
	        }
        }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
    	Fragment fragment=null;
    	 getActionBar().setTitle(item.getTitle());
        if (mDrawerToggle.onOptionsItemSelected(item)) {
        	 
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) 
        {
        case R.id.refreshButton:
        	getActionBar().setTitle("Refresh");
        	 ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        	 if (!cd.isConnectingToInternet()) {
                 // Internet Connection is not present
        		 AlertDialogManager alert = new AlertDialogManager();
                 alert.showAlertDialog(UserHomeActivity.this,
                         "Internet Connection Error",
                         "Please connect to working Internet connection", false);
             }
        	 else
        	 {
        		 DataRefersh df=new DataRefersh(getApplicationContext());
        		 df.startRetrivingData();
        		 LocalDBHandler2 db2=new LocalDBHandler2(UserHomeActivity.this);
        		 db2.deleteAllRefreshData();
        		 db2.close();
        		 pDial = new ProgressDialog(UserHomeActivity.this);
				pDial.setCancelable(false);
				pDial.setMessage("Please wait..!!");
				pDial.show();
        	 }
        	
        	Toast.makeText(getApplicationContext(),	"Refresh",Toast.LENGTH_LONG).show();
			return true;
        
      
        case R.id.adduser:
        	
        	fragment=new AddUserFragment();
        	if(fragment!=null)
        	{	
        	 FragmentManager fragmentManager = getFragmentManager();
            
             FragmentTransaction ft=fragmentManager.beginTransaction();
	  			ft.replace(R.id.frame_container, fragment,"Addfriend");
	  			if(fragmentManager.findFragmentByTag("Addfriend")==null)
	  			{
	  				ft.addToBackStack("Addfriend");	
	  			}
	  			
	  			ft.commit(); 
	             
            
        	}
        	return true;
        	
        case R.id.addgroup:
        	fragment=new AddGroupFragment();
        	if(fragment!=null)
        	{	
        	 FragmentManager fragmentManager = getFragmentManager();
             
   
	            FragmentTransaction ft=fragmentManager.beginTransaction();
	  			ft.replace(R.id.frame_container, fragment,"Addgroup");
	  			if(fragmentManager.findFragmentByTag("Addgroup")==null)
	  			{
	  				ft.addToBackStack("Addgroup");	
	  			}
	  			
	  			ft.commit(); 
	             
	      	}
        	Toast.makeText(getApplicationContext(),	"Add group",Toast.LENGTH_LONG).show();
        	
        	return true;
        	
        case R.id.generalalerts:
        	getActionBar().setTitle("General Alerts");
        	fragment=new JoinUserFragment();
        	if(fragment!=null)
        	{	
        	 FragmentManager fragmentManager = getFragmentManager();
             FragmentTransaction ft=fragmentManager.beginTransaction();
	  			ft.replace(R.id.frame_container, fragment,"Addgroup");
	  			if(fragmentManager.findFragmentByTag("Addgroup")==null)
	  			{
	  				ft.addToBackStack("Addgroup");	
	  			}
	  			
	  			ft.commit(); 
        	}
        	Toast.makeText(getApplicationContext(),	"General alerts",Toast.LENGTH_LONG).show();
			return true;
        case R.id.locationalerts:
        	getActionBar().setTitle("Location Alerts");
        	Toast.makeText(getApplicationContext(),	"Location Alerts",Toast.LENGTH_LONG).show();
        	
        	fragment=new LocationAlertFragment();
        	if(fragment!=null)
        	{	
        	 FragmentManager fragmentManager = getFragmentManager();
             

                FragmentTransaction ft=fragmentManager.beginTransaction();
      			ft.replace(R.id.frame_container, fragment,"LocationAlertFragment");
      			if(fragmentManager.findFragmentByTag("LocationAlertFragment")==null)
      			{
      				ft.addToBackStack("LocationAlertFragment");	
      			}
      			
      			ft.commit(); 
                 
          	}

        	
			return true;
			
        case R.id.userprofile:
        fragment=new UserProfileFragment();
    	if(fragment!=null)
    	{	
    	 FragmentManager fragmentManager = getFragmentManager();
         

            FragmentTransaction ft=fragmentManager.beginTransaction();
  			ft.replace(R.id.frame_container, fragment,"Profile");
  			if(fragmentManager.findFragmentByTag("Profile")==null)
  			{
  				ft.addToBackStack("Profile");	
  			}
  			
  			ft.commit(); 
             
      	}
    	Toast.makeText(getApplicationContext(),	"Your Profile",Toast.LENGTH_LONG).show();
    	return true;

        	
       
        }
        return super.onOptionsItemSelected(item);
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */

 
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    final BroadcastReceiver br2=new BroadcastReceiver(){
	   	   @Override
			public void onReceive(Context context, Intent intent) {
	   		pDial.cancel();
	   	   }
	   	};
    
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new UserHomeFragment();
        Bundle b=new Bundle();
        b.putString("GroupName",navDrawerItems.get(position).getTitle());
        getActionBar().setTitle(navDrawerItems.get(position).getTitle());
        fragment.setArguments(b);
        
        /*
        switch (position) {
        case 0:
            b.putString("loads","d");
            fragment.setArguments(b);
            break;
            
            
        case 1:
        	b.putString("loads",navDrawerItems.get(position).getTitle());
            fragment.setArguments(b);
            break;
            
        case 2:
        	b.putString("loads",navDrawerItems.get(position).getTitle());
            fragment.setArguments(b);
            break;
            
        case 3:
        	b.putString("loads",navDrawerItems.get(position).getTitle());
            fragment.setArguments(b);
            break;
       
        case 4:
        	b.putString("loads",navDrawerItems.get(position).getTitle());
            fragment.setArguments(b);
            break;
            
        default:
            break;
        }
 		*/
        
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.replace(R.id.frame_container, fragment,"Home");
            ft.commit();
            //fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            //setTitle(navMenuTitles[position]);
           
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
      
    }
    
   
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	//super.onBackPressed();
    	
    	FragmentManager fm=getFragmentManager();
 
    	
    	if(fm.findFragmentByTag("UserBack")!=null)
    	{
    		mDrawerList.setSelection(0);
    	}
    	if(fm.getBackStackEntryCount()==0)
    	{
    		super.onBackPressed();
    	}
    	else
    	{
			fm.popBackStack();
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
							Toast.makeText(UserHomeActivity.this,"Your request is sent", Toast.LENGTH_SHORT).show();
							
							Log.i(TAG, "I am done");
						}
						else
						{
							Toast.makeText(UserHomeActivity.this,"Your request is not sent", Toast.LENGTH_SHORT).show();
						} 
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
						if(responseJSON!=null)
						{
							
							SharedPreferences sp;
					        sp=UserHomeActivity.this.getSharedPreferences("controller_check", UserHomeActivity.this.MODE_WORLD_READABLE);
					        String UserId=sp.getString("UserId","no_data");
							
					       
					        LocalDBHandler3 dbrenamegroup=new LocalDBHandler3(getApplicationContext());
					        dbrenamegroup.updateGroupName(newgroupname, groupid, UserId);
					        
							
							Toast.makeText(UserHomeActivity.this, "Group renamed..!!", Toast.LENGTH_SHORT).show();

						}
						else
						{	
							Toast.makeText(UserHomeActivity.this, "Group not renamed..!!", Toast.LENGTH_SHORT).show();
						}
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
						if(responseJSON!=null)
						{
							
							SharedPreferences sp;
					        sp=UserHomeActivity.this.getSharedPreferences("controller_check", UserHomeActivity.this.MODE_WORLD_READABLE);
					        String UserId=sp.getString("UserId","no_data");
							
					       
					        LocalDBHandler3 dbremovegroup=new LocalDBHandler3(getApplicationContext());
					        dbremovegroup.removeSelectedGroup(removegroupid, UserId);
					        
							
							Toast.makeText(UserHomeActivity.this, "Group removed..!!", Toast.LENGTH_SHORT).show();

						}
						else
						{	
							Toast.makeText(UserHomeActivity.this, "Group not removed..!!", Toast.LENGTH_SHORT).show();
						}
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(br2);
	}
}