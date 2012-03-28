package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DialogActivity extends Activity {
	
	private TextView header, body;
	private String reqID = "reqID";
	private String dBody = "This person has requested to seek you using People Finder." +
			"Would you like to let them?";
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.receiverdialog);
		 Intent i = getIntent();
		 String id = i.getExtras().getString(reqID);
		 header = (TextView) findViewById(R.id.header);
		 header.setText(id);
		 
		 body = (TextView) findViewById(R.id.body);
		 body.setText(dBody);

		 
		 
		 
	}

}
