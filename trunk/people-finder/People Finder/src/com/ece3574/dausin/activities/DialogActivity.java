package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity {
	
	private String nameFrom;
	private TextView requestFrom, request;
	private Button accepting;
	private Button ignoring;
    private String[] str;
    private String delimiter = ":";
    private String body = "This person has sent a request to find you using People Finder.";
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.receiverdialog);

		 Bundle extras = getIntent().getExtras();
		 String idAndReturnNumber = extras.getString("ID_RNUM");

         str = idAndReturnNumber.split(delimiter);
         
         
		 
		 requestFrom = (TextView) findViewById(R.id.header);
		 request= (TextView) findViewById(R.id.body);
		 accepting = (Button) findViewById(R.id.receiverAccept);
		 ignoring = (Button) findViewById(R.id.receiverIgnore);

		 
         requestFrom.setText(str[1] + ":");
         request.setText(body);
         
         
		 Toast.makeText(getApplicationContext(), str[0]+ "\n" + str[1] +"\n" , Toast.LENGTH_LONG).show();
		 accepting.setText("Accept");
		 accepting.setOnClickListener(new View.OnClickListener()
		 {
			 public void onClick(View v){
			        SmsManager sms = SmsManager.getDefault();
			        sms.sendTextMessage(str[2], null, "PF:ACCEPT_R", null, null);  
					Intent i = new Intent(DialogActivity.this, mapFinderActivity.class);
		        	startActivity(i);
			 }
		 });
		 ignoring.setText("Ignore");
		 ignoring.setOnClickListener(new View.OnClickListener()
		 {
			 public void onClick(View v){
			        SmsManager sms = SmsManager.getDefault();
			        sms.sendTextMessage(str[2], null, "PF:IGNORE_R", null, null);  
					Intent i = new Intent(DialogActivity.this, PeopleFinderActivity.class);
		        	startActivity(i);
			 }
		 });
		 
		 
	}

}
