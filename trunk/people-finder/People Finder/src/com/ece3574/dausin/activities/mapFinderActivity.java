package com.ece3574.dausin.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.util.ArrayList;
import java.util.List;

import com.ece3574.dausin.R;
//import com.ece3574.dausin.global.Friend;
import com.ece3574.dausin.maps.TheItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class mapFinderActivity extends MapActivity {
    /** Called when the activity is first created. */
	
	//Added for Jake's functions
	private Handler handler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Creates the MapView for the Activity
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        
        /* Get picture from facebook to put on map*/
        // TODO Not working correctly fix it UP!!!
        Drawable drawable;
		try {
			drawable = drawableFromUrl(PeopleFinderActivity.getPractice());
	        TheItemizedOverlay itemizedoverlay = new TheItemizedOverlay(drawable, this);
	        
	        GeoPoint point = new GeoPoint(19240000,-99120000);
	        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
	    
	        itemizedoverlay.addOverlay(overlayitem);
	        mapOverlays.add(itemizedoverlay);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Jake's added onCreate method
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new DifferentLocationListener();
        //mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 30000, 0, mlocListener);
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1, 1, mlocListener);
        //end Jake's added onCreate
		
    }
    
    /* Function to convert URL images to drawables */
    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    
    // Don't display routes on the map
    @Override
    protected boolean isRouteDisplayed(){
    	return false;
    }
    
	public class DifferentLocationListener implements LocationListener {
		
		//@Override
		public void onLocationChanged(final Location loc){
			Log.e("Location Loc", "default message");
			handler.post(new Runnable() {
				
				@Override
				public void run() {
			
					loc.getLatitude();
					loc.getLongitude();
					String Text = "Latitude: " + loc.getLatitude() + "  Longitude: " + loc.getLongitude();
					makeToast(Text);
				}
			});

		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
		
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		
		
	}/* End of Class MyLocationListener */
	
	public void makeToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

}
