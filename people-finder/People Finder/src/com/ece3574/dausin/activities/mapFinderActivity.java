package com.ece3574.dausin.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;

import com.ece3574.dausin.R;
import com.ece3574.dausin.appengine.XMLParser;
import com.ece3574.dausin.async.HttpCallback;
import com.ece3574.dausin.async.HttpUtils;
import com.ece3574.dausin.facebook.BaseRequestListener;
import com.ece3574.dausin.global.Globals;
import com.ece3574.dausin.maps.TheItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.ece3574.dausin.async.HttpUtils;

public class mapFinderActivity extends MapActivity {
    /** Called when the activity is first created. */
	
	//Added for Jake's functions
	private Handler handler = new Handler();
	private HashMap<String, String> putMap, ParsedXML;
	private String putId, theirGPS, theirLat, theirLong;
	private int theirLatInt, theirLongInt, newLat, newLng;
	private String provider;
	private LocationManager mlocManager;
	private static final float PROXIMITY_RADIUS = 50;
	private static final int PROXIMITY_EXPIRES = -1;
	
	private static final String PROX_ALERT_INTENT = "com.ece3574.dausin.activities.CompassActivity";
	
	public static final int MICRO_DEGREE = 100000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Creates the MapView for the Activity
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        
        /* Get picture from facebook to put on map*/

        Drawable drawable;

		//Jake's added onCreate method
		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new DifferentLocationListener();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 30000, 0, mlocListener); //checks ofr updates every 30 seconds
        //end Jake's added onCreate
        
        Criteria criteria = new Criteria();
		provider = mlocManager.getBestProvider(criteria, false);
		Location location = mlocManager.getLastKnownLocation(provider);

		int lat = 0;
		int lng = 0;
		
		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			lat = (int) location.getLatitude();
			lng = (int) location.getLongitude();
			lat = lat * MICRO_DEGREE;
			lng = lng * MICRO_DEGREE;
		} else {
		}
		        
        
		try {
			drawable = drawableFromUrl(PeopleFinderActivity.getPractice());
	        TheItemizedOverlay itemizedoverlay = new TheItemizedOverlay(drawable, this);
	        
	        GeoPoint point = new GeoPoint(lat,lng);
	        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
	    
	        itemizedoverlay.addOverlay(overlayitem);
	        mapOverlays.add(itemizedoverlay);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


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
    
    //PeopleActivity.appfriends.get(i).id
    
    //////////////////////////////////////////////////
    //Get GPS coordinates and send/recieve coordinates
    //////////////////////////////////////////////////
    
	public class DifferentLocationListener implements LocationListener {
		public String coordinates;
		// Temporary Method to access GPS Coordinates
		public GeoPoint getCoordinates(final Location loc){
			GeoPoint p1 = new GeoPoint ((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
			return p1;
		}
		//@Override
		public void onLocationChanged (final Location loc){

			handler.post(new Runnable() {
				
				public void run() {
			
					newLat = 0;
					newLng = 0;
					
					newLat = (int)loc.getLatitude()*MICRO_DEGREE;
					newLng = (int)loc.getLongitude()*MICRO_DEGREE;
					
					coordinates = loc.getLatitude()+"|"+loc.getLongitude(); //creates coordinates string seperated by |
					Toast.makeText(getApplicationContext(), coordinates, Toast.LENGTH_SHORT).show();
					
					///////////////////////////
					//PUSHING STRING
					///////////////////////////
					Map<String, String> args_ = new HashMap<String, String>();
					args_.put("app", coordinates); //posts coordinates to app
					args_.put("uid", Globals.uid);

					HttpUtils.get().doPost("http://www.peoplefinderredevs.appspot.com/" + "uidpackagepairs", args_, new HttpCallback() {

						public void onResponse(HttpResponse resp) {
							// TODO Auto-generated method stub
							try {											
								Log.i("GamesActivity", "Succesful post of " + coordinates + " " + HttpUtils.get().responseToString(resp));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						public void onError(Exception e) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
					
					////////////////////////////
					// PULLING STRING
					////////////////////////////
					
					//the account that we are recieving from should be the facebook in the
					//PeopleFinderActivity.currentTag string
					//so then based on
					//putMap was changed to static in PeopleFinderActivity to access it here.
					//app friends was made public to be used in here
					
					String accountName = PeopleFinderActivity.currentTag; //since apparently we change currentTag below
					putMap = new HashMap<String, String>();
					putId = accountName;
					PeopleFinderActivity.currentTag = "the";
					int i = 0;
					while( i<PeopleFinderActivity.appFriends.size()){
						putMap.put("uid"+Integer.toString(i+1), PeopleFinderActivity.appFriends.get(i).id);
						++i;
					}
					
					putMap.put("uid"+Integer.toString(i+1), accountName);

					
					HttpUtils.get().doPut(Globals.uidPackagePairsUrl, putMap, new HttpCallback(){

						public void onResponse(HttpResponse resp) {
							
							try {
								
								//ParsedXML has also been changed to become static
								String response = HttpUtils.get().responseToString(resp);
								ParsedXML = XMLParser.parseUidPackagePairsXML(response); //ParsedXML should now have both strings
								theirGPS = ParsedXML.get(putId);
								int index = theirGPS.indexOf("|");
								theirLat = theirGPS.substring(0, index);
								theirLong = theirGPS.substring(index+1, theirGPS.length());
								makeToast(theirLat + " " + theirLong);
								theirLatInt = Integer.valueOf(theirLat) * MICRO_DEGREE;
								theirLongInt = Integer.valueOf(theirLong) * MICRO_DEGREE;
								
								Log.e("Test Message", response);
								makeToast(response);
								//friendsLayout_.removeAllViews();
								//parseAppFriends();
								//progressDialog.dismiss();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}

						public void onError(Exception e) {
							//Log.e("Appengine error", e.printStackTrace());
							
						}
						
					});
					 
					/////////////////////////////
					//End Pull String. It should exist in XML Parser. I dont' understand how to access it though.
					//How do we set something like String coordinatesRecieved  = ParsedXML<I_DONT_CARE, coordinates_I_Want>
					//coordinatesRecieved should then be toasted.
					/////////////////////////////
					
					
				}
			});
			


		}
		
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
		
		}
		
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		
		
	}/* End of Class MyLocationListener */
	public void updateProximity(int aLat, int aLng){
		
		Intent intent = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		
		//mlocManager.removeProximityAlert(intent);
		mlocManager.addProximityAlert(aLat, aLng, PROXIMITY_RADIUS, PROXIMITY_EXPIRES, proximityIntent);
		
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT); 
		registerReceiver(new ProximityIntentReceiver(), filter);
	}
	
	
	public void makeToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

}
