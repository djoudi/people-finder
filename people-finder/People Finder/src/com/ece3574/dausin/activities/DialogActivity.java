package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity {
	
	private TextView header, body;
	private Button accept, ignore;
	private String reqID = "reqID";
	private String dBody = "This person has requested to seek you using People Finder." +
			"Would you like to let them?";
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.receiverdialog);
		 Intent i = getIntent();
		 String id = i.getExtras().getString(reqID);
		 String name = id.substring(id.indexOf(":")+1, id.length());
		 id = id.substring(0, id.indexOf(":"));
		 header = (TextView) findViewById(R.id.header);
		 header.setText(name);
		 
		 body = (TextView) findViewById(R.id.body);
		 body.setText(dBody);
		 
		 accept = (Button) findViewById(R.id.receiverAccept);
		 accept.setText("Accept");
		 accept.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				
			}
			 
		 });
		 
		 ignore = (Button) findViewById(R.id.receiverIgnore);
		 ignore.setText("Ignore");
		 ignore.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				
				
			}
			 
		 });

		 
		 
		 
		 
	}

}
