package com.bsns.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Controller_redirect extends Activity {

	SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("controller_check",MODE_WORLD_READABLE);
		SharedPreferences.Editor ed=sp.edit();
		String login_check=sp.getString("login_check",null);
		String data_fetch=sp.getString("data_fetch", "not_done");
		
		Intent it;
		if(login_check!=null)
		{
			if(login_check.equals("done"))
			{
				if(data_fetch.equals("done"))
				{
					it=new Intent(Controller_redirect.this,UserHomeActivity.class);
					startActivity(it);
					finish();
				}
				else
				{
					it=new Intent(Controller_redirect.this,UserDataRetrieve.class);
					startActivity(it);
					finish();
				}
			}
			else
			{
				it=new Intent(Controller_redirect.this,login_act.class);
				startActivity(it);
				finish();
			}
		}
		else{
			//Intent to redirect user to to Login activity
			it=new Intent(this,login_act.class);
			startActivity(it);
			finish();
		}
	}

}