package com.ece3574.dausin.activities;

//import pack.Compass.Rose;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CompassActivity extends Activity implements SensorEventListener{
	public static int degree = 0;
	SensorManager sensorManager;
	static final int sensor = Sensor.TYPE_ORIENTATION;
	//Rose rose;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*
		// Set full screen view
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		rose = new Rose(this);
		setContentView(rose);
		*/
		
		// get sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
		
	@Override
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

	@Override
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
		//Log.d("Difference: ", (" "+intAngle));
		
		degree = intAngle;
		
	}
	

}
