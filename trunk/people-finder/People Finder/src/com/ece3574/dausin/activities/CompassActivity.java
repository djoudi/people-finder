package com.ece3574.dausin.activities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ece3574.dausin.R;
import org.apache.http.HttpResponse;

import com.ece3574.dausin.appengine.XMLParser;
import com.ece3574.dausin.async.HttpCallback;
import com.ece3574.dausin.async.HttpUtils;
import com.ece3574.dausin.global.Friend;
import com.ece3574.dausin.global.Globals;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.location.Criteria;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;

public class CompassActivity extends Activity implements SensorEventListener, LocationListener{
	
	private LocationManager mlocManager;
	private String provider, putId, theirGPS, theirLat, theirLong;
	private Handler handler = new Handler();
	private HashMap<String, String> putMap, ParsedXML;
	
	public static float degree = 0;
	public static float prevDegree = 0;
	SensorManager sensorManager;
	static final int sensor = Sensor.TYPE_ORIENTATION;
	
	public ImageView myView, myPhoto;
    public Bitmap bmpOriginal;
    public Bitmap bmResult;
    public Canvas tempCanvas; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		/*---------------------------------------------------------JACOB*/
		// Setting up screen layout to display arrow
		/*---------------------------------------------------------JACOB*/
		setContentView(R.layout.compassarrow);
		
		/*---------------------------------------------------------JACOB*/
		// Initialize Arrow Picture
		/*---------------------------------------------------------JACOB*/
        myView = (ImageView) findViewById(R.id.arrowPic);
        
        bmpOriginal = BitmapFactory.decodeResource(this.getResources(), R.drawable.arrow);
        bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(bmResult);
        
		tempCanvas.rotate(degree, bmpOriginal.getWidth()/2, bmpOriginal.getHeight()/2);
        tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
        myView.setImageBitmap(bmResult);
        
        //Initialize The persons photo - Kyle
        myPhoto = (ImageView) findViewById(R.id.compassPic);
        for(Friend f : PeopleFinderActivity.appFriends){
        	if(f.id == PeopleFinderActivity.currentTag){
        		myPhoto.setImageBitmap(f.pictureBitmap);
        	}
        }
        
		
		// Set full screen view
		/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		*/
		mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    LocationListener mlocListener = this;
	    mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 30000, 0, mlocListener); //checks ofr updates every 30 seconds
	    //end Jake's added onCreate
	    
	    Criteria criteria = new Criteria();
		provider = mlocManager.getBestProvider(criteria, false);
		Location location = mlocManager.getLastKnownLocation(provider);
		
		// get sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	public void onLocationChanged (final Location loc){

		handler.post(new Runnable() {
			
			public void run() {
				
				mapFinderActivity.myLat = loc.getLatitude();
				mapFinderActivity.myLong= loc.getLongitude();
				
				final String coordinates = loc.getLatitude()+"|"+loc.getLongitude(); //creates coordinates string seperated by |
				Toast.makeText(getApplicationContext(), coordinates, Toast.LENGTH_SHORT).show();
				
				///////////////////////////
				//PUSHING STRING
				///////////////////////////
				/*Map<String, String> args_ = new HashMap<String, String>();
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
					
				});*/
				
				
				////////////////////////////
				// PULLING STRING
				////////////////////////////
				
				//the account that we are recieving from should be the facebook in the
				//PeopleFinderActivity.currentTag string
				//so then based on
				//putMap was changed to static in PeopleFinderActivity to access it here.
				//app friends was made public to be used in here
				
				/*String accountName = PeopleFinderActivity.currentTag; //since apparently we change currentTag below
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
							
							mapFinderActivity.yourLat = Double.valueOf(theirLat);
							mapFinderActivity.yourLong = Double.valueOf(theirLong);
							
							Log.e("Test Message", response);
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
					
				});*/
				 
				/////////////////////////////
				//End Pull String. It should exist in XML Parser. I dont' understand how to access it though.
				//How do we set something like String coordinatesRecieved  = ParsedXML<I_DONT_CARE, coordinates_I_Want>
				//coordinatesRecieved should then be toasted.
				/////////////////////////////

			}
		});

	}
		
	
	// register to listen to sensors
	@Override
	public void onResume() {
		super.onResume();
		
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		
		//sensorManager.registerListener(this, sensor);
	}
	
	// unregister
	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}
	
	// Ignore for now
	public void onAccuracyChanged(int sensor, int accuracy) {
	}
	
	// Listen to sensor and provide output
	public void onSensorChanged(int sensor, float[] values) {

	}
		
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	// TODO Auto-generated method stub	
	}
	
	private double calcAngleFromNorth(double x, double y){
		double angle =0;;
		double hypot = 0;
		
		hypot = Math.pow(x, 2) + Math.pow(y, 2);
		hypot = Math.pow(hypot, 0.5);
		angle = x / hypot;
		angle = Math.asin(angle);
		
		return angle;
	}

	public void onSensorChanged(SensorEvent event) {
		
		double myX 		= mapFinderActivity.myLat;
		double myY 		= mapFinderActivity.myLong;
		double yourX	= mapFinderActivity.yourLat;
		double yourY	= mapFinderActivity.yourLong;
		double diffX;
		double diffY;
		double angle = 0;
		double radianConversion = 57.2957795;
		double pi = 3.14159265;
		
		if(myX>=yourX){
			if(myY>=yourY){
				diffX = myX - yourX;
				diffY = myY - yourY;
				angle = calcAngleFromNorth(diffX, diffY);
				angle = angle+pi;
				
			} else {
				diffX = myX - yourX;
				diffY = yourY- myY;
				angle = calcAngleFromNorth(diffY, diffX);
				angle = angle + 1.5*pi;
			}
			
		}else {
			if(myY>= yourY){
				diffX = yourX - myX;
				diffY = myY - yourY;
				angle = calcAngleFromNorth(diffY, diffX);
				angle = angle+ pi/2;

			} else {
				diffX = yourX - myX;
				diffY = yourY - myY;
				angle = calcAngleFromNorth(diffX, diffY);
			}
		}
		
		//convert angle from radian to degree
		angle = angle*radianConversion;
		int intAngle = (int)(angle);
		
		//Log.d("angleFromNorth:", (" "+intAngle));
	
		if (event.sensor.getType() != sensor){
			return;
		}
		
		//get angle in relation to north 0 is north, 1 is right 1 degree, 359 is left one degree
		int orientation = (int) event.values[0];
		//Log.d("North: ", (" "+orientation));
		
		if(intAngle>=orientation){
			 intAngle = intAngle- orientation;
		} else {
			intAngle = orientation - intAngle;
			intAngle = 360-intAngle;
		}
		Log.d("Difference: ", (" "+intAngle));
		
		degree = intAngle;
		
		/*---------------------------------------------------------JACOB*/
		// Update arrow picture
		/*---------------------------------------------------------JACOB*/
		
        tempCanvas.rotate(degree-prevDegree, bmpOriginal.getWidth()/2, bmpOriginal.getHeight()/2);
        
        tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
        myView.setImageBitmap(bmResult);
		prevDegree = degree;
		
	}


	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
