package com.bsns.app;

import java.io.IOException;
import java.util.List;

import com.bsns.dbhandler.LocalDBHandler3;

import android.app.Fragment;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocationAlertFragment extends Fragment {
	Spinner spname;
	String username[]=null;
	TextView tvaddress;
	TextView tvlat;
	TextView tvlong;
	TextView tvupdationdate;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.locationalertsfragment, container, false);
		spname=(Spinner)view.findViewById(R.id.spfriendname);
		tvaddress=(TextView)view.findViewById(R.id.tvaddress);
		tvlat=(TextView)view.findViewById(R.id.tvLatitude);
		tvlong=(TextView)view.findViewById(R.id.tvLongitude);
		tvupdationdate=(TextView)view.findViewById(R.id.tvdatetime);
		LocalDBHandler3 db3=new LocalDBHandler3(getActivity().getBaseContext());
		Cursor cursor=db3.retrievePLFriendName();
		
		if(cursor.isBeforeFirst() && cursor.getCount()>0)
		{
			username=new String[cursor.getCount()];
			int cnt=0;
			while(cursor.moveToNext())
			{
				username[cnt]=cursor.getString(0);
				cnt++;
			}
			ArrayAdapter<String> adapter =  new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item, username);
			spname.setAdapter(adapter);
		
		}
		else
		{
			Toast.makeText(getActivity().getBaseContext(), "You haven't sert any plan location", Toast.LENGTH_SHORT).show();
		}

		spname.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LocalDBHandler3 db3=new LocalDBHandler3(getActivity().getBaseContext());
				Cursor c=db3.retrieveLocationTransactionByName(spname.getSelectedItem().toString());
				if(c!=null && c.isBeforeFirst() && c.getCount()>0)
				{
					c.moveToFirst();
					tvlat.setText(c.getString(0));
					tvlong.setText(c.getString(1));
					tvupdationdate.setText(c.getString(2));
					
					Geocoder geocoder=new Geocoder(getActivity().getBaseContext());
	        		List<Address> addresses=null;
					try {
						addresses = geocoder.getFromLocation(Double.parseDouble(tvlat.getText().toString()),Double.parseDouble(tvlong.getText().toString()), 1);
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
					
					tvaddress.setText(loctitle);

					
				}
				else
				{
					Toast.makeText(getActivity().getBaseContext(), "You haven't sert any plan location", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return view;
	}
	
}
