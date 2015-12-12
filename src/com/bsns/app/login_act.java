package com.bsns.app;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;

public class login_act extends Activity 
{	
	private static String responseJSON;
	private static String[][] resp;
	ProgressDialog pDial;	
	String UserId="";
	EditText uname,pass;
	ImageButton login;

	SharedPreferences sp;
	String regid;
	TextView txtsignup;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		login=(ImageButton) findViewById(R.id.btn_login);
		uname=(EditText)findViewById(R.id.et_login_uname);
		pass=(EditText)findViewById(R.id.et_login_pwd);
		sp=getSharedPreferences("controller_check", MODE_WORLD_READABLE);
	
		txtsignup=(TextView)findViewById(R.id.txtsignup);
		
		  txtsignup.setText(
			        Html.fromHtml(
			            "<a href=\"http://www.google.com\">Google</a> "));
			    txtsignup.setMovementMethod(LinkMovementMethod.getInstance());
			    
			    stripUnderlines(txtsignup);
		
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				String u,p;
				u=uname.getText().toString();
				UserId=u;
				p=pass.getText().toString();
				//pass.setError(Html.fromHtml("<font color='red'>Please enter pasword"));
				if(!(u.equals("") || p.equals("")))
				{
					ServiceCaller sc1=new ServiceCaller(getString(R.string.serviceurl));
					String[][] l=new String[3][2];
					
					l[0][0]="@mode";
					l[0][1]="androidlogincheck";
					
					l[1][0]="@ContactNo";
					l[1][1]=u;
					
					l[2][0]="@Password";
					l[2][1]=p;
					
					sc1.executeDQLSP("spUserMaster", l);
				}
				else if(u.equals(""))
				{
					uname.setError(Html.fromHtml("<font color='red'>Please enter mobile number"));
				}
				else if(p.equals(""))
				{
					pass.setError(Html.fromHtml("<font color='red'>Please enter password"));
				}
			}
		});
		
		
	
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
				protected void onPreExecute() 
		    	{
		    		pDial = new ProgressDialog(login_act.this);
					pDial.setCancelable(false);
					pDial.setMessage("Please wait..!!");
					pDial.show();
					Log.i(TAG, "onPreExecute");
					//Display progress bar
				}
		    	
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
					
					Log.i(TAG, "Service called we got " + responseJSON);
					int cnt=0;
					if(resp!=null)
					{
						Log.e("we got ", responseJSON);
						for(int i=0;i<resp.length;i++)
						{
							for(int j=0;j<resp[i].length;j++)
							{
								if((resp[i][j].equals("nodata")))
								{
									cnt=1;
								}
								else
								{
									UserId=resp[i][j];
									Toast.makeText(login_act.this, "I am here with " + UserId,Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
					else
					{
						Toast.makeText(login_act.this, "Can't connect to services", Toast.LENGTH_SHORT).show();
						cnt=1;
					}
					
					if(cnt==0)
					{
						SharedPreferences.Editor editor=sp.edit();
						editor.putString("login_check", "done");
						editor.putString("UserId", UserId);
						editor.commit();
						
						Intent intent=new Intent(login_act.this,Controller_redirect.class);
						startActivity(intent);
						finish();
					}
					else
					{
						uname.setError(Html.fromHtml("<font color='red'>Verify mobile number"));
						pass.setText("");
						Toast.makeText(login_act.this, "invalid inputs", Toast.LENGTH_SHORT).show();
					}
					
					pDial.cancel();
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
	private void stripUnderlines(TextView textView) {
	    Spannable s = new SpannableString(textView.getText());
	    URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
	    for (URLSpan span: spans) {
	        int start = s.getSpanStart(span);
	        int end = s.getSpanEnd(span);
	        s.removeSpan(span);
	        span = new URLSpanNoUnderline(span.getURL());
	        s.setSpan(span, start, end, 0);
	    }
	    textView.setText(s);
	}
}