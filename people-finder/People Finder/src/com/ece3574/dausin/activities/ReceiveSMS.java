package com.ece3574.dausin.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class ReceiveSMS extends BroadcastReceiver
	{
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
	            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	        }
	/*
	            //--make a dialog to let the user know they got a text
                //Create alert dialog
                AlertDialog alert;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setTitle("Request");
 
                //Give Alert Dialog custom view and create close button
                alertBuilder.setMessage("You just got a map request from another user!");
 
                //Create positive button
                alertBuilder.setPositiveButton("I accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                       // Toast.makeText(context, "accepted", Toast.LENGTH_LONG).show();
                    }
                });
 
                //Create negative button
                alertBuilder.setNegativeButton("Ignore ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(PeopleFinderActivity.this, "Ignored", Toast.LENGTH_LONG).show();
                    }
                });
 
                //Display alert dialog
                alert = alertBuilder.create();
                alert.show();
                */
	        }                         
	    
	}
	 