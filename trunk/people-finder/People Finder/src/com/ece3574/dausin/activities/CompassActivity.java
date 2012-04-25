package com.ece3574.dausin.activities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Criteria;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;

public class CompassActivity extends Activity implements SensorEventListener,
		LocationListener {

	// camera
	private Preview mPreview;
	Camera mCamera;
	// int numberOfCameras;
	int cameraCurrentlyLocked;

	// The first rear facing camera
	// int defaultCameraId;
	// end camera
	private Handler handler_ = new Handler();
	private LocationManager mlocManager;
	private String provider, putId, theirGPS, theirLat, theirLong;
	private Handler handler = new Handler();
	private HashMap<String, String> putMap, ParsedXML;

	private double[] coordBufferX;
	private double[] coordBufferY;
	private int bufferElement_;

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
		Log.e("onCreate", "begin");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/*---------------------------------------------------------JACOB*/
		// Setting up screen layout to display arrow
		/*---------------------------------------------------------JACOB*/
		setContentView(R.layout.compassarrow);

		/*---------------------------------------------------------JACOB*/
		// Initialize Arrow Picture
		/*---------------------------------------------------------JACOB*/
		// fill buffer with default 0
		coordBufferX = new double[10];
		coordBufferY = new double[10];
		for (int i = 0; i < 10; ++i) {
			coordBufferX[i] = 0;
			coordBufferY[i] = 0;
		}

		myView = (ImageView) findViewById(R.id.arrowPic);

		bmpOriginal = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.arrow);
		bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(),
				bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
		tempCanvas = new Canvas(bmResult);

		tempCanvas.rotate(degree, bmpOriginal.getWidth() / 2,
				bmpOriginal.getHeight() / 2);
		tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
		myView.setImageBitmap(bmResult);

		// Initialize The persons photo - Kyle
		myPhoto = (ImageView) findViewById(R.id.compassPic);
		for (Friend f : PeopleFinderActivity.appFriends) {
			if (f.id == PeopleFinderActivity.currentTag) {
				myPhoto.setImageBitmap(f.pictureBitmap);
			}
		}
		// Set full screen view
		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 * requestWindowFeature(Window.FEATURE_NO_TITLE);
		 */
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = this;
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0,
				mlocListener); // checks ofr updates every 30 seconds
		// end Jake's added onCreate

		Criteria criteria = new Criteria();
		provider = mlocManager.getBestProvider(criteria, false);
		Location location = mlocManager.getLastKnownLocation(provider);

		// get sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// CAMERA

		// Hide the window title.
		Log.d("onCreate", "cameraBegin");

		// Create a RelativeLayout container that will hold a SurfaceView,
		// and set it as the content of our activity.
		mPreview = new Preview(this);
		//setContentView(mPreview);
		FrameLayout bar = (FrameLayout)findViewById(R.id.cameraContainer);
		//foo.addView(bar);
		//setContentView(foo);
		bar.addView(mPreview);
		
		bar.setVisibility(View.VISIBLE);

	}

	public void onLocationChanged(final Location loc) {

		handler.post(new Runnable() {

			public void run() {

				if (bufferElement_ == 10)
					bufferElement_ = 0;
				coordBufferY[bufferElement_] = loc.getLatitude();
				coordBufferX[bufferElement_] = loc.getLongitude();
				++bufferElement_;

				mapFinderActivity.myLat = 0;
				mapFinderActivity.myLong = 0;
				for (int i = 0; i < 10; ++i) {
					mapFinderActivity.myLat = mapFinderActivity.myLat
							+ coordBufferY[i];
					mapFinderActivity.myLong = mapFinderActivity.myLong
							+ coordBufferX[i];

					// Log.e("array:",
					// "x: "+coordBufferX[i]+" y: "+coordBufferY[i]);
				}
				Log.e("After using buffer", " ");

				final String coordinates = mapFinderActivity.myLat + "|"
						+ mapFinderActivity.myLong; // creates coordinates
													// string seperated by |

				// /////////////////////////
				// PUSHING STRING
				// /////////////////////////
				Map<String, String> args_ = new HashMap<String, String>();
				args_.put("app", coordinates); // posts coordinates to app
				args_.put("uid", Globals.uid);

				HttpUtils.get().doPost(
						"http://www.peoplefinderredevs.appspot.com/"
								+ "uidpackagepairs", args_, new HttpCallback() {

							public void onResponse(HttpResponse resp) {
								// TODO Auto-generated method stub
								try {
									Log.i("GamesActivity",
											"Succesful post of "
													+ coordinates
													+ " "
													+ HttpUtils.get()
															.responseToString(
																	resp));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							public void onError(Exception e) {
								// TODO Auto-generated method stub

							}

						});

				// //////////////////////////
				// PULLING STRING
				// //////////////////////////

				// the account that we are recieving from should be the facebook
				// in the
				// PeopleFinderActivity.currentTag string
				// so then based on
				// putMap was changed to static in PeopleFinderActivity to
				// access it here.
				// app friends was made public to be used in here

				String accountName = PeopleFinderActivity.currentTag; // since
																		// apparently
																		// we
																		// change
																		// currentTag
																		// below
				putMap = new HashMap<String, String>();
				putId = accountName;
				PeopleFinderActivity.currentTag = "the";
				int i = 0;
				while (i < PeopleFinderActivity.appFriends.size()) {
					putMap.put("uid" + Integer.toString(i + 1),
							PeopleFinderActivity.appFriends.get(i).id);
					++i;
				}

				putMap.put("uid" + Integer.toString(i + 1), accountName);

				HttpUtils.get().doPut(Globals.uidPackagePairsUrl, putMap,
						new HttpCallback() {

							public void onResponse(HttpResponse resp) {

								try {

									// ParsedXML has also been changed to become
									// static
									String response = HttpUtils.get()
											.responseToString(resp);
									ParsedXML = XMLParser
											.parseUidPackagePairsXML(response); // ParsedXML
																				// should
																				// now
																				// have
																				// both
																				// strings
									theirGPS = ParsedXML.get(putId);
									int index = theirGPS.indexOf("|");
									theirLat = theirGPS.substring(0, index);
									theirLong = theirGPS.substring(index + 1,
											theirGPS.length());

									mapFinderActivity.yourLat = Double
											.valueOf(theirLat);
									mapFinderActivity.yourLong = Double
											.valueOf(theirLong);

									Log.e("Test Message", response);
									// friendsLayout_.removeAllViews();
									// parseAppFriends();
									// progressDialog.dismiss();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							public void onError(Exception e) {
								// Log.e("Appengine error",
								// e.printStackTrace());

							}

						});

				// ///////////////////////////
				// End Pull String. It should exist in XML Parser. I dont'
				// understand how to access it though.
				// How do we set something like String coordinatesRecieved =
				// ParsedXML<I_DONT_CARE, coordinates_I_Want>
				// coordinatesRecieved should then be toasted.
				// ///////////////////////////

			}
		});

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	// register to listen to sensors
	@Override
	public void onResume() {
		Log.e("onResume", " ");
		super.onResume();

		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);

		// sensorManager.registerListener(this, sensor);

		// camera
		// Open the default i.e. the first rear facing camera.
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
		
		// cameraCurrentlyLocked = defaultCameraId;
		mPreview.setCamera(mCamera);
		
	
		// end camera
	}

	// unregister
	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);

		// camera
		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
		// end camera
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

	private double calcAngleFromNorth(double x, double y) {
		double angle = 0;
		;
		double hypot = 0;

		hypot = Math.pow(x, 2) + Math.pow(y, 2);
		hypot = Math.pow(hypot, 0.5);
		angle = x / hypot;
		angle = Math.asin(angle);

		return angle;
	}

	public void onSensorChanged(final SensorEvent event) {
		Runnable r = new Runnable() {
			public void run() {

				double myX = mapFinderActivity.myLong;
				double myY = mapFinderActivity.myLat;
				double yourX = mapFinderActivity.yourLong;
				double yourY = mapFinderActivity.yourLat;

				double diffX;
				double diffY;
				double angle = 0;
				double radianConversion = 57.2957795;
				double pi = 3.14159265;

				if (myX >= yourX) {
					if (myY >= yourY) {
						diffX = myX - yourX;
						diffY = myY - yourY;
						angle = calcAngleFromNorth(diffX, diffY);
						angle = angle + pi;

					} else {
						diffX = myX - yourX;
						diffY = yourY - myY;
						angle = calcAngleFromNorth(diffY, diffX);
						angle = angle + 1.5 * pi;
					}

				} else {
					if (myY >= yourY) {
						diffX = yourX - myX;
						diffY = myY - yourY;
						angle = calcAngleFromNorth(diffY, diffX);
						angle = angle + pi / 2;

					} else {
						diffX = yourX - myX;
						diffY = yourY - myY;
						angle = calcAngleFromNorth(diffX, diffY);
					}
				}

				// convert angle from radian to degree
				angle = angle * radianConversion;
				int intAngle = (int) (angle);

				// Log.d("angleFromNorth:", (" "+intAngle));

				if (event.sensor.getType() != sensor) {
					return;
				}

				// get angle in relation to north 0 is north, 1 is right 1
				// degree, 359 is left one degree
				int orientation = (int) event.values[0];
				// Log.d("North: ", (" "+orientation));

				if (intAngle >= orientation) {
					intAngle = intAngle - orientation;
				} else {
					intAngle = orientation - intAngle;
					intAngle = 360 - intAngle;
				}
				//Log.d("Difference: ", (" " + intAngle));

				degree = intAngle;

				/*---------------------------------------------------------JACOB*/
				// Update arrow picture
				/*---------------------------------------------------------JACOB*/

				tempCanvas
						.rotate(degree - prevDegree,
								bmpOriginal.getWidth() / 2,
								bmpOriginal.getHeight() / 2);
				
				//tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
				//myView.setImageBitmap(bmResult);
				myView.draw(tempCanvas);
				prevDegree = degree;
			}
		};

		handler_.post(r);
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

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered
 * preview of the Camera to the surface. We need to center the SurfaceView
 * because not all devices have cameras that support preview sizes at the same
 * aspect ratio as the device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {
	private final String TAG = "Preview";

	SurfaceView mSurfaceView;
	SurfaceHolder mHolder;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;
	Camera mCamera;

	Preview(Context context) {

		super(context);

		mSurfaceView = new SurfaceView(context);
		addView(mSurfaceView);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			requestLayout();
		}
	}

	public void switchCamera(Camera camera) {
		setCamera(camera);
		try {
			camera.setPreviewDisplay(mHolder);
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		requestLayout();

		camera.setParameters(parameters);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
					height);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height
						/ previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		if (mCamera != null) {
			mCamera.stopPreview();
		}
		//Toast.makeText(this, Float.toString(degree), Toast.LENGTH_LONG).show();
		
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		requestLayout();

		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}

}