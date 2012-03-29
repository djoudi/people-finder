package com.ece3574.dausin.activities;

//import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
//import com.ece3574.dausin.activities.PeopleFinderActivity;

public class ReceiveSMS extends BroadcastReceiver
	{
	public static final String tag = ReceiveSMS.class.getName();
	
	@Override
	    public void onReceive(Context context, Intent intent) 
	    {
			this.abortBroadcast(); 
	        //---get the SMS message passed in---
	        Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;
	        String str = "";            
	        if (bundle != null)
	        {
	            //---retrieve the SMS message received---
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length];            
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	                String body = msgs[i].getMessageBody().toString();
	                str = body + ":" + msgs[i].getOriginatingAddress();     
	            }
	            //---display the new SMS message---
	            //Log.e(tag, "before toast and activity launch");
	            //Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	            
	            
	            //Log.e(tag, "after toast, before activity");
	            Log.e(tag, "before text is parced");
	            String[] temp;
	            String delimiter = ":";
	            temp = str.split(delimiter);
	            if(temp[0].equals("PF")){
	            	if(temp[1].equals("REQUEST")){
	            		Log.e(tag, "in Request block");
		            	//Toast.makeText(context, temp[2], Toast.LENGTH_LONG).show();
			            Intent i = new Intent(context, DialogActivity.class);  
			            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			            i.putExtra("ID_RNUM", temp[2] + ":" + temp[3]);
			            //i.putExtra("RETURN_NUMBER", temp[3]);
			            //Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			            context.startActivity(i);
	            	}
	            	else if(temp[1].equals("ACCEPT_R")){
	            		 Intent i = new Intent(context, mapFinderActivity.class);  
				         i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				         context.startActivity(i);
	            		//Intent i = new Intent(PeopleFinderActivity.this, mapFinderActivity.class);
	    	        	//startActivity(i);
	            	}
	            
	            	else if(temp[1].equals("IGNORE_R")){
	            		 Intent i = new Intent(context, PeopleFinderActivity.class);  
				         i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				         context.startActivity(i);
	            	}
	             }
	            else{
	            	this.clearAbortBroadcast();
	            }
	            
	            
	            Log.e(tag, "after activity launch");

	        }
	     
	        	
	        

	    }                         
	    
	}
	 