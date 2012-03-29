package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity {
	
	private String nameFrom;
	private TextView requestFrom;
	private Button accepting;
	private Button ignoring;
    private String[] str;
    private String delimiter = ":";
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.dialogactivity);

		 Bundle extras = getIntent().getExtras();
		 String idAndReturnNumber = extras.getString("ID_RNUM");

         str = idAndReturnNumber.split(delimiter);
         
			for (int i=0; i< PeopleFinderActivity.appFriends.size(); i++){
				if (PeopleFinderActivity.appFriends.get(i).id == str[0])
				{
					nameFrom = PeopleFinderActivity.appFriends.get(i).name; 
				}
			}
		 
		 requestFrom = (TextView) findViewById(R.id.textView1);
		 accepting = (Button) findViewById(R.id.button2);
		 ignoring = (Button) findViewById(R.id.button1);

		 
         requestFrom.setText(nameFrom);
         
		 //Toast.makeText(getApplicationContext(), str[1], Toast.LENGTH_LONG).show();
		 
		 accepting.setOnClickListener(new View.OnClickListener()
		 {
			 public void onClick(View v){
			        SmsManager sms = SmsManager.getDefault();
			        sms.sendTextMessage(str[1], null, "PF:ACCEPT_R", null, null);  
					Intent i = new Intent(DialogActivity.this, mapFinderActivity.class);
		        	startActivity(i);
			 }
		 });
		 ignoring.setOnClickListener(new View.OnClickListener()
		 {
			 public void onClick(View v){
			        SmsManager sms = SmsManager.getDefault();
			        sms.sendTextMessage(str[1], null, "PF:IGNORE_R", null, null);  
					Intent i = new Intent(DialogActivity.this, PeopleFinderActivity.class);
		        	startActivity(i);
			 }
		 });
		 
		 
	}

}
