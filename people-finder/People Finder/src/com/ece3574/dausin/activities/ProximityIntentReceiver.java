package com.ece3574.dausin.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);
        
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }
        
        
		Intent i = new Intent();
		i.setClassName("com.ece3574.dausin.activities.ProximityIntentReceiver", 
				"com.ece3574.dausin.activities.CompassActivity");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
        

        
    }
    

    
}