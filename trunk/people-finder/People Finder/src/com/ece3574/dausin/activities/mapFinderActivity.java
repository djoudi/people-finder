//Jacob's Map API Debug Key: 0mRfB2ZoGSks9TbeplyC8Lcno3yrJMLlKOBPqrA
//Kyle's Map API Dbug Key: 0o92FbCNGOqyOtAyWHGWmlawvB7golYJv7d4oNg

package com.ece3574.dausin.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import com.ece3574.dausin.R;
import com.ece3574.dausin.appengine.XMLParser;
import com.ece3574.dausin.async.HttpCallback;
import com.ece3574.dausin.async.HttpUtils;
import com.ece3574.dausin.facebook.BaseRequestListener;
import com.ece3574.dausin.global.Friend;
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
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

//--------------------------------------------------------------JACOB
// TODO: 
// -> Integrate markers with app engine
//--------------------------------------------------------------JACOB

public class mapFinderActivity extends MapActivity {
    /** Called when the activity is first created. */
	
	//Added for Jake's functions
	private MapView mapView;
	private Drawable drawable, drawable2; 
	private Drawable tDrawable;
	private Handler handler = new Handler();
	private HashMap<String, String> putMap, ParsedXML;
	private String putId, theirGPS, theirLat, theirLong;
	public static double myLat, myLong, yourLat, yourLong;	//Used in location.get, which returns a double.

	private int theirLatInt, theirLongInt, myLatInt, myLongInt;	//Used in GeoPoint, which uses int in the constructor.
	private String provider;
	private LocationManager mlocManager;
	private static final float PROXIMITY_RADIUS = 50;
	private static final int PROXIMITY_EXPIRES = -1;
	
	private static final String PROX_ALERT_INTENT = "com.ece3574.dausin.activities.CompassActivity";
	
	public static final int MICRO_DEGREE = 1000000;
	
	private static OverlayItem overlayitem, overlayitem2;
	private static TheItemizedOverlay itemizedoverlay, itemizedoverlay2;
	private static List<Overlay> mapOverlays;
	private GeoPoint globPoint1;
	
	DifferentLocationListener locClass;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        
        //-------------------------------------------------JACOB
        // Image is set here
        //-------------------------------------------------JACOB
        for(Friend f : PeopleFinderActivity.appFriends){
        	if(f.id == PeopleFinderActivity.currentTag){
        		try {
					//drawable2 = drawableFromUrl(f.pictureURL+"?type=square");
        			drawable2 = drawableFromUrl("http://graph.facebook.com/"+f.id+"/picture?type=square");
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	else{
        		// Set picture to your own...
        		String response = PeopleFinderActivity.getResponseString();
        		try{
        		JSONObject json = Util.parseJson(response);
        		String profileID = json.getString("id");
        		URL img_value = new URL("http://graph.facebook.com/"+profileID+"/picture?type=square");
        		String urlString = img_value.toString();
        		drawable = drawableFromUrl(urlString);
        		//drawable2 = this.getResources().getDrawable(R.drawable.robinh);
        		}
        		catch (Exception e){
        			
        		} catch (FacebookError e) {
					e.printStackTrace();
				}
        	}
        }
        //-------------------------------------------------JACOB
        
        itemizedoverlay = new TheItemizedOverlay(drawable, this); // Set up for Finder
        itemizedoverlay2 = new TheItemizedOverlay(drawable2, this); // Set up for Findee
        
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        GeoPoint point2 = new GeoPoint(37227578, -80422368);
        overlayitem = new OverlayItem(point, "F YEAH", "AHM IN MESCO BEECH!");
        
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
        
        point = new GeoPoint(18230000, -88320000);
        overlayitem = new OverlayItem(point, "LUV ME", "AHM MESCAN");
        overlayitem2 = new OverlayItem(point2, "DEERTY BEET", "SCYUH MAYH MAH");
        itemizedoverlay.removeOverlay(); // Removes previous marker
        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay2.addOverlay(overlayitem2);
        //itemizedoverlay.removeOverlay();
        mapOverlays.add(itemizedoverlay); // Populates the map with overlays
        mapOverlays.add(itemizedoverlay2);
        // Handling GPS Stuff

    	
   		// Should be used to call functions and crap
        locClass = new DifferentLocationListener(this.getApplicationContext());
        //locClass.doInitStuff();
    }
    
	protected void onResume() {
		super.onResume();
		locClass.doInitStuff();
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

    /*-----------------------------------------------------------JACOB*/
    // Method to change the markers on the map when the location is 
    // updated by the GPS.
    /*-----------------------------------------------------------JACOB*/
    public static void changeMapMarkers(GeoPoint coordinates, GeoPoint coordinates2){
    	Log.e("Jacob","Entered changeMapMarkers method!");
    	overlayitem = new OverlayItem(coordinates, "Position", "Yeap");
    	//GeoPoint testCoord = new GeoPoint(31503629, -99228516);
    	overlayitem2 = new OverlayItem(coordinates2, "Baby Baby Baby", "Justin");
    	itemizedoverlay.removeOverlay();
    	itemizedoverlay.addOverlay(overlayitem);
    	itemizedoverlay2.removeOverlay();
    	itemizedoverlay2.addOverlay(overlayitem2);
    	mapOverlays.add(itemizedoverlay);
    	mapOverlays.add(itemizedoverlay2);
    }
    /*-----------------------------------------------------------JACOB*/
    
    // Don't display routes on the map
    @Override
    protected boolean isRouteDisplayed(){
    	return false;
    }
    
    //PeopleActivity.appfriends.get(i).id
    
    //////////////////////////////////////////////////
    //Get GPS coordinates and send/receive coordinates
    //////////////////////////////////////////////////
    
	public class DifferentLocationListener implements LocationListener {
		public String coordinates;
		// Temporary Method to access GPS Coordinates
		public GeoPoint getCoordinates(final Location loc){
			GeoPoint p1 = new GeoPoint ((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
			return p1;
		}
		
		private LocationManager locationManager;
		private String provider;
		
		public DifferentLocationListener(Context mContext){
			Log.e("Jacob", "Called FindLocation Constructor");
					
			// Get the location manager
	    	locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	    	// Define the criteria how to select the location provider -> use
	    	// default
	    	Criteria criteria = new Criteria();
	    	provider = locationManager.getBestProvider(criteria, false);
	    	Location location = locationManager.getLastKnownLocation(provider);
	   		// Initialize the location fields
	   		if (location != null) {
	   			System.out.println("Provider " + provider + " has been selected.");
	   			int lat = (int) (location.getLatitude());
	   			int lng = (int) (location.getLongitude());
	   		}
		}
		
		//@Override
		public void onLocationChanged (final Location loc){
			Log.d("Jacob", "running method: onLocationChanged");
			int lat = (int) (loc.getLatitude() * 1E6);
			int lng = (int) (loc.getLongitude() * 1E6);
			globPoint1 = new GeoPoint(lat, lng);
			
			//-------------------------------------------------------JACOB
			// Testing a double picture update
			// 	-> This code will be updated in the future but right now
			//	   it's just being used to check double picture updates
			//-------------------------------------------------------JACOB
			//int lat2 = (int) ((loc.getLatitude() * 1E6) + 100);
			//int lng2 = (int) ((loc.getLongitude() * 1E6) + 100);
			//GeoPoint point2 = new GeoPoint(lat2, lng2);
			
			//changeMapMarkers(point, point2);
						
			handler.post(new Runnable() {
				
				public void run() {
			
					myLatInt = 0;
					myLongInt = 0;
					myLat = loc.getLatitude() * MICRO_DEGREE;
					myLong= loc.getLongitude() * MICRO_DEGREE;
					myLatInt = (int) myLat;
					myLongInt = (int) myLong;
					//MAKE YOUR NEW GEOPOINT HERE.
					
					coordinates = loc.getLatitude()+"|"+loc.getLongitude(); //creates coordinates string seperated by |
					//Toast.makeText(getApplicationContext(), coordinates, Toast.LENGTH_SHORT).show();
					
					
					///////////////////////////
					//PUSHING STRING
					///////////////////////////
					Map<String, String> args_ = new HashMap<String, String>();
					args_.put("app", coordinates); //posts coordinates to app
					args_.put("uid", Globals.uid);

					HttpUtils.get().doPost("http://www.peoplefinderredevs.appspot.com/" + "uidpackagepairs", args_, new HttpCallback() {

						public void onResponse(HttpResponse resp) {
							try {											
								Log.i("MapActivity", "Succesful post of " + coordinates + " " + HttpUtils.get().responseToString(resp));
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
						public void onError(Exception e) {
							
						}
						
					});
					
					
					////////////////////////////
					// PULLING STRING
					////////////////////////////
					
					//the account that we are receiving from should be the facebook in the
					//PeopleFinderActivity.currentTag string
					//so then based on
					//putMap was changed to static in PeopleFinderActivity to access it here.
					//app friends was made public to be used in here
					
					String accountName = PeopleFinderActivity.currentTag; //since apparently we change currentTag below
					putMap = new HashMap<String, String>();
					putId = accountName;
					
					putMap.put("uid1", accountName);

					
					HttpUtils.get().doPut(Globals.uidPackagePairsUrl, putMap, new HttpCallback(){

						public void onResponse(HttpResponse resp) {
							
							try {
								
								//ParsedXML has also been changed to become static
								String response = HttpUtils.get().responseToString(resp);
								ParsedXML = XMLParser.parseUidPackagePairsXML(response); //ParsedXML should now have both strings
								theirGPS = ParsedXML.get(putId);
								int index = theirGPS.indexOf("|");
								
								//****************************NOTE****************************
								// Fixed (or at least should be)
								//	- Jacob
								//****************************NOTE****************************
								Log.d("Jacob", "Following is -index-");
								Log.d("Jacob", "=" + index);
								if (index > 1){
									theirLat = theirGPS.substring(0, index);
									theirLong = theirGPS.substring(index+1, theirGPS.length());
								}
								else {
									// Aloha bruddah
									theirLat = "21.309846";
									theirLong = "-157.862549";
								}
								//****************************NOTE****************************
								
								yourLat = Double.valueOf(theirLat) * MICRO_DEGREE;
								yourLong = Double.valueOf(theirLong) * MICRO_DEGREE;
							
								theirLatInt = (int) yourLat;
								theirLongInt = (int) yourLong;
								GeoPoint globPoint2 = new GeoPoint(theirLatInt, theirLongInt);
								
								//MAKE THEIR NEW GEOPOINT HERE.
								changeMapMarkers(globPoint1, globPoint2);
								
								//--------------------------------------------------------------JACOB
								// Removed updateProximity call to test map Marker movement
								//--------------------------------------------------------------JACOB
								updateProximity(theirLatInt, theirLongInt);
								//--------------------------------------------------------------JACOB
								
								//makeToast(response);
								//friendsLayout_.removeAllViews();
								//parseAppFriends();
								//progressDialog.dismiss();
							} catch (IOException e) {
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
		
		public void doInitStuff(){
			Log.e("Jacob", "running method: doInitStuff");
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
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
	
	public void makeGeoPoint(int mLat, int mLng, String mId, int tLat, int tLng, String tId){
        TheItemizedOverlay itemizedoverlay = new TheItemizedOverlay(drawable, this);
        String overlay = "YOU ARE HERE.";
        GeoPoint point = new GeoPoint(mLat, mLng);
        OverlayItem overlayitem = new OverlayItem(point, overlay, "");
        itemizedoverlay.addOverlay(overlayitem);
        for(int i=0; i<PeopleFinderActivity.appFriends.size(); i++){
        	if(PeopleFinderActivity.appFriends.get(i).id.matches(tId)){
        		overlay = PeopleFinderActivity.appFriends.get(i).name;
        	}
        }
        point = new GeoPoint(tLat, tLng);
        overlayitem = new OverlayItem(point, overlay, "");
    
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.clear();
        mapOverlays.add(itemizedoverlay);
        Log.v("GeoPoint Update", "mapOverlay added");
	}

}
