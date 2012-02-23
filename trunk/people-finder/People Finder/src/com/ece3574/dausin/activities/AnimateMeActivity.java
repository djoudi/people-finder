package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
//import android.view.MotionEvent;

public class AnimateMeActivity extends Activity {
    /** Called when the activity is first created. */
    
	AnimationDrawable searchAnimation;
	AnimationDrawable loadAnimation;
	
	@Override
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animate);
        
        ImageView loader = (ImageView) findViewById(R.id.myLoader);
        ImageView findMe = (ImageView) findViewById(R.id.myAnimation);
        findMe.setBackgroundResource(R.drawable.search_light);
        loader.setBackgroundResource(R.drawable.load_img);
        searchAnimation = (AnimationDrawable) findMe.getBackground();
        loadAnimation = (AnimationDrawable) loader.getBackground();
        
        
        //Attempt to play sound
        MediaPlayer startSound = MediaPlayer.create(getApplicationContext(), R.raw.startup); 
        startSound.start();
        
        findMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	        	Intent i = new Intent(AnimateMeActivity.this, LoginActivity.class);
	        	startActivity(i);
			}
        	
        });
    }

	
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus == true){
			searchAnimation.start();
			loadAnimation.start();
        	//Intent i = new Intent(AnimateMeActivity.this, LoginActivity.class);
        	//startActivity(i);
		}
		return;
	}
	
}
