package com.ece3574.dausin.activities;
//Hello

import com.ece3574.dausin.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.animation.Animation;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
//import android.view.animation.Animation.AnimationListener;
//import android.view.MotionEvent;

public class AnimateMeActivity extends Activity{
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
        
        // Change Activities upon finished animation
        ifAnimationDone(searchAnimation);
    }

	
	public void onWindowFocusChanged(boolean hasFocus){
		if (hasFocus == true){
			//setContentView(R.layout.animate);
			searchAnimation.start();
			loadAnimation.start();
			
			
		}
		return;
	}
	
    /*--------------------------------------------------------JACOB*/
    // Change activities upon animation end
	// Credit goes to Stack Overflow
    /*--------------------------------------------------------JACOB*/
    private void ifAnimationDone(AnimationDrawable anim){
    	final AnimationDrawable a = anim;
    	int timeBetweenChecks = 300;
    	Handler h = new Handler();
    	h.postDelayed(new Runnable(){
    		public void run(){
    			if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() -1)){
    				ifAnimationDone(a);
    			}
    			else{
    				Intent i = new Intent(AnimateMeActivity.this, LoginActivity.class);
    				startActivity(i);
    			}
    		}
    	}, timeBetweenChecks);
    };
    /*--------------------------------------------------------JACOB*/
}
