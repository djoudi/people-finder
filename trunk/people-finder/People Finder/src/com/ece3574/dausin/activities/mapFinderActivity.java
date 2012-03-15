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

//import android.content.Context;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.location.LocationManager;
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
	private String putId, theirGPS;
	
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
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 30000, 0, mlocListener); //checks ofr updates every 30 seconds
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
    
    //PeopleActivity.appfriends.get(i).id
    
    //////////////////////////////////////////////////
    //Get GPS coordinates and send/recieve coordinates
    //////////////////////////////////////////////////
    
	public class DifferentLocationListener implements LocationListener {
		public String coordinates;
		//@Override
		public void onLocationChanged (final Location loc){

			handler.post(new Runnable() {
				
				public void run() {
			
					//loc.getLatitude();
					//loc.getLongitude();
					
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
						@Override
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
	
	public void makeToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

}
