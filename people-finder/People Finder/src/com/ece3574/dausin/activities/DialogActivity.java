package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class DialogActivity extends Activity {
	
	private Button accepting;
	private Button ignoring;
	private TextView header, body;
    private String[] str;
    private String delimiter = ":";
    private String dBody = "This person has requested to seek you using People Finder." +
			"Would you like to let them?";
	
	public void onCreate(Bundle savedInstanceState){

		

		super.onCreate(savedInstanceState);
		 setContentView(R.layout.receiverdialog);
		 //final String id = compound.substring(0, compound.indexOf(":"));
		 //header = (TextView) findViewById(R.id.header);
		 //header.setText(name);


		accepting = (Button) findViewById(R.id.receiverAccept);
		accepting.setText("Accept");
		
		ignoring = (Button) findViewById(R.id.receiverIgnore);
		ignoring.setText("Ignore");
		


		 Bundle extras = getIntent().getExtras();
		 String idAndNumberAndName = extras.getString("ID_RNUM");
		 //String returnNumber = extras.getString("RETURN_NUMBER");

         str = idAndNumberAndName.split(delimiter);

		 //body = (TextView) findViewById(R.id.body);
		 //body.setText(dBody);
         
 		header = (TextView) findViewById(R.id.header);
 		header.setText(str[3]);
 		
 		body = (TextView) findViewById(R.id.body);
 		body.setText(dBody);

		 

		 Toast.makeText(getApplicationContext(), str[1], Toast.LENGTH_LONG).show();
		 
		 accepting.setOnClickListener(new View.OnClickListener()
		 {
			 public void onClick(View v){
			        SmsManager sms = SmsManager.getDefault();
			        sms.sendTextMessage(str[1], null, "PF:ACCEPT_R", null, null);  
					Intent i = new Intent(DialogActivity.this, mapFinderActivity.class);
		        	startActivity(i);
			 }
		 });
		 
		 
		 
	}

		 //accept = (Button) findViewById(R.id.receiverAccept);
		 //accept.setText("Accept");
		 //accept.setOnClickListener(new OnClickListener(){
}

/*
			@Override
			public void onClick(View arg0) {
				
				PeopleFinderActivity.currentTag = id;
				Intent i = new Intent(DialogActivity.this, mapFinderActivity.class);
	        	startActivity(i);
				
			}
			 
		 });
		 
		 ignore = (Button) findViewById(R.id.receiverIgnore);
		 ignore.setText("Ignore");
		 ignore.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
			}
			 
		 });

		 
		 
		 
		 
	}

}
*/

