package com.bsns.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bsns.beans.RelationMasterBean;
import com.bsns.beans.UserMasterBean;
import com.bsns.dbhandler.LocalDBHandler2;
import com.bsns.dbhandler.LocalDBHandler4;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class UserHomeFragment extends Fragment 
{
	List<UserMasterBean> userMasterBean=new ArrayList<UserMasterBean>();
	MapView mapView;
	GoogleMap googleMap;
	TextView tvtemp;
	String s;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.userhomefragmentlayout, container, false);
		
		s=getArguments().getString("GroupName");
		MapsInitializer.initialize(getActivity().getBaseContext());
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        
        if(mapView!=null)
        {
            googleMap = mapView.getMap();	
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            /*MarkerOptions m1=new MarkerOptions();
            Geocoder g=new  Geocoder(getActivity().getBaseContext());
            List<Address> list1=null;
			try {
				list1 = g.getFromLocationName("Anand",1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            m1.position(new LatLng(list1.get(0).getLatitude(), list1.get(0).getLongitude()));
            googleMap.addMarker(m1);*/
        }
        
        userMasterBean.clear();
        populateUserList();
        ArrayAdapter<UserMasterBean> adapter=new MyAdapter();
        ListView list=(ListView) view.findViewById(R.id.lvuserhomefriendlist);
        list.setAdapter(adapter);
        setCustomerMasters();
        
        return view;
    }
	
	
	private class MyAdapter extends ArrayAdapter<UserMasterBean>
	{
		public MyAdapter() {
			super(getActivity().getBaseContext(),R.layout.userhomefriendlistview,userMasterBean);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View itemview=convertView;
			if(itemview==null)
			{
				itemview=getActivity().getLayoutInflater().inflate(R.layout.userhomefriendlistview,parent,false);
			}
		 	
			final UserMasterBean currentUser=userMasterBean.get(position);
			TextView tvusername=(TextView) itemview.findViewById(R.id.tvuserhomefriendlistname);
			TextView tvuserrelation=(TextView) itemview.findViewById(R.id.tvuserhomefriendlistrelation);
			ImageView img=(ImageView) itemview.findViewById(R.id.imgvuserhomefriendlist);
			ImageButton btnfrienddetail=(ImageButton)itemview.findViewById(R.id.btnsettingsuserhomefriendlist);
			ImageButton btnfriendlocation=(ImageButton)itemview.findViewById(R.id.btnuserhometracklocation);
			
			img.setImageResource(getResources().getIdentifier(currentUser.getDPUrl() , "drawable", getActivity().getPackageName()));
			tvusername.setText(currentUser.getFname() + " " + currentUser.getLname());
			
			btnfrienddetail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Fragment fragment=new FriendDetailsFragment();
					Bundle b=new Bundle();
			        b.putString("FriendId",currentUser.getUserId());
			        fragment.setArguments(b);
					FragmentManager fragmentManager = getActivity().getFragmentManager();
					FragmentTransaction ft=fragmentManager.beginTransaction();
					ft.replace(R.id.frame_container, fragment,"UserBack");
					if(fragmentManager.findFragmentByTag("UserBack")==null)
					{
						ft.addToBackStack("UserBack");	
					}
					
					ft.commit();
					//fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				}
			});
			
			btnfriendlocation.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LocalDBHandler2 db2=new LocalDBHandler2(getActivity().getBaseContext());
					Cursor cursor=db2.retrieveLocationTransactionByUserId(currentUser.getUserId());
					if(cursor.isBeforeFirst() && cursor.getCount()>0)
					{
						googleMap.clear();
						while(cursor.moveToNext())
						{
							MarkerOptions markerOp1 = new MarkerOptions();
							markerOp1.position(new LatLng(Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2))));
							markerOp1.title(currentUser.getFname() + " " + currentUser.getLname());
							googleMap.addMarker(markerOp1);
						}
					}
					db2.close();
				}
			});
			
			SharedPreferences sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
			sp.getString("UserId","no_data");
			
			LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
			String relname=db1.retrieveReceiverRelationId(sp.getString("UserId","no_data"), currentUser.getUserId());
			tvuserrelation.setText("Your " + relname);
			db1.close();
			return itemview;
		}
	}
	
	public void setCustomerMasters()
	{
		LocalDBHandler2 db2=new LocalDBHandler2(getActivity().getBaseContext());
		Cursor cursor=db2.retrieveClientInfo();

		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			googleMap.clear();
			while(cursor.moveToNext())
			{
				MarkerOptions markerOp1 = new MarkerOptions();
				markerOp1.position(new LatLng(Double.parseDouble(cursor.getString(2)), Double.parseDouble(cursor.getString(3))));
				markerOp1.title(cursor.getString(1)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				googleMap.addMarker(markerOp1);
				googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
					
					@Override
					public boolean onMarkerClick(Marker arg0) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?daddr=" + arg0.getPosition().latitude + "," + arg0.getPosition().longitude));
						getActivity().startActivity(intent);
						return false;
					}
				});
			}
		}
		db2.close();
	}

	private void populateUserList()
	{
		LocalDBHandler2 db1=new LocalDBHandler2(getActivity().getBaseContext());
        SharedPreferences sp;
        sp=getActivity().getSharedPreferences("controller_check", getActivity().MODE_WORLD_READABLE);
        String UserId=sp.getString("UserId","no_data");
        Cursor cursor=db1.retrieveFriends(UserId,s);
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			while(cursor.moveToNext())
			{
				UserMasterBean bean1=new UserMasterBean();
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
				userMasterBean.add(bean1);
			}
		}
		db1.close();
	}
	
	@Override
    public void onResume() 
    {
		mapView.onResume();
        super.onResume();
    }
	/*
    @Override
    public void onDestroy() 
    {
        super.onDestroy();

        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() 
    {
        super.onLowMemory();

        mapView.onLowMemory();
    }
  */
}
