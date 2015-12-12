package com.bsns.app;

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
import com.google.gson.Gson;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class AddGroupFragment extends Fragment {
	private static String responseJSON;
	private static String[][] resp;
	private static int dmlstatus;
	private static Context context;
	String picname;
	String UserId;
	String groupname;
	String picname1;
	ProgressDialog pDial;
	
	Integer[] imageIDs = {
			 R.drawable.group1,
			 R.drawable.group2,
			 R.drawable.group3,
			 R.drawable.group4,
			 R.drawable.group5,
			 R.drawable.group6
			
			 };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.addgroupfragment, container, false);
		ImageButton btncreategroup=(ImageButton)view.findViewById(R.id.addgroupbtncreategroup);
		
		final EditText edtgroupname=(EditText)view.findViewById(R.id.addgroupedtgroupname);
		
		Gallery gallery = (Gallery)view.findViewById(R.id.groupimages);
		final ImageView imageView = (ImageView)view.findViewById(R.id.image1);
		gallery.setAdapter(new ImageAdapter(getActivity().getBaseContext()));
		
		 gallery.setOnItemClickListener(new OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> parent, View v, int position,long id)
		 {
			
		 Toast.makeText(getActivity().getBaseContext(),"pic" + (position + 1) + " selected",
		 Toast.LENGTH_SHORT).show();
		 // display the images selected
		 if(position==1)
		 {
			 picname="group1.9.png";
			 picname1="group1";
		 }
		 else if(position==2)
		 {
			 picname="group2.9.png";
			 picname1="group2";
		 }
		 else if(position==3)
		 {
			 picname="group3.9.png";
			 picname1="group3";
		 }
		 else if(position==4)
		 {
			 picname="group4.9.png";
			 picname1="group4";
		 }
		 else if(position==5)
		 {
			 picname="group5.9.png";
			 picname1="group5";
		 }
		 else if(position==6)
		 {
			 picname="group6.9.png";
			 picname1="group6";
		 }
		 else
		 {
			 picname="group1.9.png";
			 picname1="group1";
		 }
		
		 
		 imageView.setImageResource(imageIDs[position]);
		 
		 }
		 });
		 
		 btncreategroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!(edtgroupname.getText().toString().equals("") ))
				{
				ServiceCaller scaddgroup=new ServiceCaller(getString(R.string.serviceurl));
				String[][] paramaddgroup=new String[7][2];
				
				paramaddgroup[0][0]="@mode";
				paramaddgroup[0][1]="insertgroup";
				
				paramaddgroup[1][0]="@GroupName";
				paramaddgroup[1][1]=edtgroupname.getText().toString();
				groupname=edtgroupname.getText().toString();
				
				paramaddgroup[2][0]="@UserId";
				SharedPreferences sp;
		        sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
		        UserId=sp.getString("UserId","no_data");
				paramaddgroup[2][1]=UserId;
				
				paramaddgroup[3][0]="@CreationDate";
				LocalDBHandler3 dbcreationdate=new LocalDBHandler3(getActivity().getBaseContext());
				paramaddgroup[3][1]=dbcreationdate.getDateTime();
				
				paramaddgroup[4][0]="@DPUrl";
				paramaddgroup[4][1]="~/Image/groupimages/"+	picname;	
				
				
				
				scaddgroup.executeDQLSP("spAndroid", paramaddgroup);
				
				}
				else
				{
					Toast.makeText(getActivity().getBaseContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
				}
			}
		});
		 return view;
	}
	
	
	 public class ImageAdapter extends BaseAdapter {
		 private Context context;
		 private int itemBackground;
		 public ImageAdapter(Context c)
		 {
				 context = c;
				 // sets a grey background; wraps around the images
				 TypedArray a =c.obtainStyledAttributes(R.styleable.MyGallery);
				 itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground,0);
				 a.recycle();
				 }
				 // returns the number of images
				 public int getCount() {
				 return imageIDs.length;
				 }
				 // returns the ID of an item
				 public Object getItem(int position) {
				 return position;
				 }
				 // returns the ID of an item
				 public long getItemId(int position) {
				 return position;
				 }
				 // returns an ImageView view
				 public View getView(int position, View convertView, ViewGroup parent) {
				 ImageView imageView = new ImageView(context);
				 imageView.setImageResource(imageIDs[position]);
				 imageView.setLayoutParams(new Gallery.LayoutParams(300, 300));
				 imageView.setBackgroundResource(itemBackground);
				 return imageView;
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
						
						if(responseJSON!=null)
						{
							if(resp!=null)
							{
								GroupMasterBean gpbean=new GroupMasterBean();
								gpbean.setGroupId(resp[0][0]);
								gpbean.setGroupName(groupname);
								gpbean.setDPUrl(picname1);
								gpbean.setUserId(UserId);
								LocalDBHandler3 dbcreationdate=new LocalDBHandler3(getActivity().getBaseContext());
								gpbean.setCreationDate(dbcreationdate.getDateTime());
								gpbean.setIsDelete("False");
								ArrayList<GroupMasterBean> arrgpbean=new ArrayList<GroupMasterBean>();
								arrgpbean.add(gpbean);
								LocalDBHandler2 dbinsertgroupdata=new LocalDBHandler2(getActivity().getBaseContext());
								dbinsertgroupdata.insertGroupMaster(arrgpbean);
								Toast.makeText(getActivity().getBaseContext(), "Group Created..", Toast.LENGTH_SHORT).show();
							
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
			
	 }

}
