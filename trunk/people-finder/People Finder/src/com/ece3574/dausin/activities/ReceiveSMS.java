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
	public final String PF_REQUEST = "PF:REQUEST:";
	public final String EMPTY = "";
	
	@Override
	    public void onReceive(Context context, Intent intent) 
	    {
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
	                str += "SMS from " + msgs[i].getOriginatingAddress();                     
	                str += " :";
	                str += msgs[i].getMessageBody().toString();
	                str += "\n";        
	            }
	            //---display the new SMS message---
	            //Log.e(tag, "before toast and activity launch");
	            //Toast.makeText(context, "You just received a request, in a future build cycle, when you accept this request, this will launch the map on this phone as well.", Toast.LENGTH_LONG).show();
	            
	            
	            //Log.e(tag, "after toast, before activity");
	            if(msgs[0].getMessageBody().toString().contains(PF_REQUEST)){
	            	String id = msgs[0].getMessageBody().toString();
	            	id = id.replace(PF_REQUEST, EMPTY);
	            	Toast.makeText(context, id, Toast.LENGTH_LONG).show();
	            	Intent i = new Intent(context, DialogActivity.class);  
	            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	            	i.putExtra("reqID", id);
	            	context.startActivity(i);
	            
	            	Log.e(tag, "after activity launch");
	            }
	        }

	    }                         
	    
	}
	 