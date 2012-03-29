/*To Add:
 * Progress Dialog
 */
package com.ece3574.dausin.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ece3574.dausin.R;
import com.ece3574.dausin.appengine.XMLParser;
import com.ece3574.dausin.async.HttpUtils;
import com.ece3574.dausin.async.HttpCallback;
import com.ece3574.dausin.facebook.BaseRequestListener;
import com.ece3574.dausin.facebook.SessionStore;
import com.ece3574.dausin.global.Friend;
import com.ece3574.dausin.global.Globals;
import com.ece3574.dausin.qsort.MyQsort;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
//import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class PeopleFinderActivity extends Activity implements HttpCallback{
    /** Called when the activity is first created. */
	
	private HashMap<String, String> putMap, ParsedXML;
	private ImageView profilePhoto, addFriendPhoto;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    static ArrayList<Friend> appFriends = new ArrayList<Friend>();
    private LinearLayout friendsLayout_, addFriendLayout_;
    private String FILENAME = "PeopleFinder_data";
    private int MAX_HEIGHT = 100;
    private SharedPreferences prefs_;
    private ProgressDialog pDialog_;
    private Facebook facebook;
    private String profileID, selectedId, profileName;
    private TextView profileName_;
    private Boolean firstTime_, firstDone_;
	AsyncFacebookRunner mAsyncRunner;
    
    private static final int SHORT_PRESS_ALERT = 1;
    private static final int LONG_PRESS_ALERT = 2;
    private static final int FRIEND_PRESS_ALERT = 3;
    private static final int MAP_REQUEST_ALERT = 4;
    String numberFromCDialog;
    static int breakCustomLoop = 0;
    static String currentTag;
    public static int ReceiveRequestFlag;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        
        
	    facebook = new Facebook("288898474509243");
	    
        profileName_ = (TextView) findViewById(R.id.profileName);
	    
	    SessionStore.restore(facebook, this);
	    
	    if(facebook.isSessionValid()) {
	    	profileName_.setText("");
	    }
	    else {
	    	profileName_.setText("Session is not valid.");
	    }
        
		friendsLayout_ = (LinearLayout) findViewById(R.id.friendsLayout);
        
		addFriendPhoto = (ImageView) findViewById(R.id.addFriendPicture);
	    addFriendPhoto.setAdjustViewBounds(true);
	    addFriendPhoto.setMaxHeight(50);
	    addFriendPhoto.setMaxWidth(50);
	    
	    profilePhoto = (ImageView) findViewById(R.id.profilePicture);
	    profilePhoto.setAdjustViewBounds(true);
	    profilePhoto.setMaxHeight(50);
	    profilePhoto.setMaxWidth(50);
	    
	    addFriendLayout_ = (LinearLayout) findViewById(R.id.addFriendLayout);
	    
	    profileID = "";
	    
	    //End of Setting up GUI
	    
		prefs_ = getSharedPreferences(FILENAME, 0);
		Globals.uid = prefs_.getString("profileid", "");
		Globals.name = prefs_.getString("profilename", "");

		profileID = Globals.uid;
		profileName = Globals.name;
		
		firstDone_ = false;
		
	    pDialog_ = new ProgressDialog(this);
	    pDialog_.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    pDialog_.setMessage("Retrieving Profile Information...");
	    pDialog_.setCancelable(false);
	    pDialog_.show();
		
		if(profileID.length() == 0) {
			//User id has not been set, so we have a first time use.
		    mAsyncRunner = new AsyncFacebookRunner(facebook);
		    mAsyncRunner.request("me", new meRequestListener());
		    mAsyncRunner.request("me/friends", new FriendRequestListener());
		    firstTime_ = true;
		}
		else {
			//User id has been set, so we can do multiquery immediately.
		    mAsyncRunner = new AsyncFacebookRunner(facebook);
		    mAsyncRunner.request("me", new meRequestListener());
		    mAsyncRunner.request("me/friends", new FriendRequestListener());
		    firstTime_ = false;
		    doFQLMultiquery();
		    
		}
	    /*
	    mAsyncRunner = new AsyncFacebookRunner(facebook);
	    mAsyncRunner.request("me", new meRequestListener());
	    mAsyncRunner.request("me/friends", new FriendRequestListener());
	    */
	    addFriendLayout_.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				makeDialog(FRIEND_PRESS_ALERT, "");
			}
	    	
	    });
	   
	    
	   // tempFillContainer();
        
    }
    

    
	///////////////////
    //DELICIOUS TOAST//
    ///////////////////
    
	public void makeToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	//////////////////////
	//TEXT MESSAGE STUFF//
	//////////////////////
	
	private  String makeMessage(){
		if(Globals.uid != null){
			return "PF:" + "REQUEST:" + Globals.uid;
		}
		else {
			return "";
		}
	}

	private void findFriend(){
		AlertDialog.Builder builder =new AlertDialog.Builder(this);
		AlertDialog alertDialog = builder.create();

		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.customdialog,
		                               (ViewGroup) findViewById(R.id.layout_root));

		final TextView text = (TextView) layout.findViewById(R.id.textView1);
		text.setText("This person's phone number was not found, please enter your facebook friends number:");

		final EditText edit = (EditText) layout.findViewById(R.id.txtPhoneNo);
		//String fone = edit.getText().toString();
		
		builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() { 
	        public void onClick(DialogInterface dialog, int whichButton) { 
	           String fone = edit.getText().toString(); 
	            //Toast.makeText(getApplicationContext(), fone + "  :is the number I'm requesting ", Toast.LENGTH_LONG).show();
	            
	            String requestMessage = makeMessage();
				numberFromCDialog = fone;
	            
	            SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(fone, null, requestMessage, null, null);
				
				//This launch has been moved to the receiver
				//Intent i = new Intent(PeopleFinderActivity.this, mapFinderActivity.class);
	        	//startActivity(i);
	            
	        } 
	        }); 
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
	        public void onClick(DialogInterface dialog, int whichButton) { 
	        	dialog.cancel();
	           
	            Intent i = new Intent(PeopleFinderActivity.this, CompassActivity.class);
	            startActivity(i);
	            
	        } 
	        }); 

		builder.setView(layout);
		alertDialog = builder.create();
		builder.show();
		
		
		/*
		 * //for (int i=0; i< appFriends.size(); i++){
			//if(appFriends.get(i).id == currentTag)
			//{
					//String temp = appFriends.get(i).phoneNumber;
					//if(temp == null)
					//{

						final Dialog dialog = new Dialog(PeopleFinderActivity.this);

						dialog.setContentView(R.layout.customdialog);
						dialog.setTitle("Phone number not found");
						dialog.setCancelable(true);

						TextView text = (TextView) dialog.findViewById(R.id.textView1);
						text.setText("Please enter the phone number of your facebook friend:");
					

					
						Button button1 = (Button) dialog.findViewById(R.id.button1);
						button1.setOnClickListener(new OnClickListener(){
							public void onClick(View v){
								EditText edit = (EditText) dialog.findViewById(R.id.txtPhoneNo);
								String fone = edit.getText().toString();
								
								Toast.makeText(getApplicationContext(), fone + "  :is the number I'm requesting ", Toast.LENGTH_SHORT).show();
								
								String requestMessage = makeMessage();
								numberFromCDialog = fone;
								
								SmsManager sms = SmsManager.getDefault();
								sms.sendTextMessage(fone, null, requestMessage, null, null);
																
								finish();
							}
						});
					
						Button button2 = (Button) dialog.findViewById(R.id.button2);
						button1.setOnClickListener(new OnClickListener(){
							public void onClick(View v){
							
								finish();
	
							}
						});
					
						dialog.show();

					//}
					//else{
					//	numberFromCDialog = appFriends.get(i).phoneNumber;
					//}

				
					return;*/
			//}
		//}
		//numberFromCDialog = "";
		//return;
	}
	
	
	//////////////////
	//DIALOG BUILDER//
	//////////////////
	
	public void makeDialog(int type, String tag) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(PeopleFinderActivity.this);
		AlertDialog dialog;
		String name = "";
		selectedId = "";
		for(Friend f : appFriends){
			if(f.id.equals(tag)){
				name = f.name;
				selectedId = f.id;
			}
		}
		
		
		switch(type){
		case SHORT_PRESS_ALERT:
			builder.setTitle(name);
			builder.setMessage("Send request to search for this person?");

			builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which)  {
					
					findFriend();
					//String phoneNo;
					
					//for (int i=0; i< appFriends.size(); i++){
						//if (appFriends.get(i).id == currentTag)
						//{
						//	appFriends.get(i).phoneNumber = numberFromCDialog; 
						//}
					//}
					
					//dialog.cancel();
					//Do Something Here.
					
					//Intent i = new Intent(PeopleFinderActivity.this, mapFinderActivity.class);
		        	//startActivity(i);
				}
				//private void sendMapRequest(String phone, String message) {
				//	SmsManager sms = SmsManager.getDefault();
				//	sms.sendTextMessage(phone, null, message, null, null);
				//}
				
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					//Do something else here.
					
				}
				
			});
			
			dialog = builder.create();
			dialog.show();
			break;
		case LONG_PRESS_ALERT:
			builder.setTitle(name);
			builder.setMessage("Long Press");
			builder.setPositiveButton("Open Compass", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					currentTag = selectedId;
					Intent i = new Intent(PeopleFinderActivity.this, CompassActivity.class);
					startActivity(i);
					
					
				}
				
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					
					dialog.cancel();
					//Do something else here.
					
				}
				
			});
			
			dialog = builder.create();
			dialog.show();
			break;
			
		//ADD FRIEND DIALOG.  YAY.	
		case FRIEND_PRESS_ALERT:
			builder.setTitle("Add a Friend");
			CharSequence[] addFriendArray = new CharSequence[friends.size()];
			for(int i=0; i<friends.size(); i++){
				addFriendArray[i] = friends.get(i).name;
			}
			builder.setItems(addFriendArray, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					
					makeToast(friends.get(which).name + " has been sent a request.");
					sendFriendRequest(which);
					
				}
				
			});
			
			dialog = builder.create();
			dialog.show();
			break;
		case MAP_REQUEST_ALERT:
			builder.setTitle(tag);
			builder.setMessage("You just got a map request from another user!");
			builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which)  {
					
					//String phoneNo = findFriend();
					//String requestMessage = makeMessage(); //
					
					//send text message back to accept the request
				}
			});
			
			builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					//send text back to say you ignore
					dialog.cancel();
				}
				
			});
			
			dialog = builder.create();
			dialog.show();
			break;
		}
	}
	
	/////////////////////
	//WALLPOST RESPONSE//
	/////////////////////
	
    public class LinkUploadListener extends BaseRequestListener {

		public void onComplete(final String response, Object state) {
			Log.d("Link", "Response: " + response.toString());
			try {
				JSONObject json = Util.parseJson(response);
				final String src = json.getString("src");

				PeopleFinderActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						Log.d("Link Post", src);
					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}
    }
	
    ///////////////////
    //FRIEND RESPONSE//
    ///////////////////
    
    public class FriendRequestListener extends BaseRequestListener {
    	public void onComplete(String response, Object state) {
    		try {
    			friends.clear();
    			// process the response here: executed in background thread
    			Log.d("Facebook-Example-Friends Request", "response.length(): " + 
                                                            response.length());
    			Log.d("Facebook-Example-Friends Request", "Response: " + response);

    			final JSONObject json = new JSONObject(response);
    			JSONArray d = json.getJSONArray("data");
    			int l = (d != null ? d.length() : 0);
    			Log.d("Facebook-Example-Friends Request", "d.length(): " + l);

    			for (int i=0; i<l; i++) {
    				JSONObject o = d.getJSONObject(i);
    				String n = o.getString("name");
        		    String id = o.getString("id");
        		    Friend f = new Friend();
        		    f.id = id;
        		    f.name = n;
        		    //Log.d("hey", n);
        		    friends.add(f);
    			}
            	friends = MyQsort.sortArrayList(friends);

                PeopleFinderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	//makeToast("friends list populated.  " + Integer.toString(friends.size()));
                    	
                    	if(firstDone_ == false && firstTime_ == true){
                    		firstDone_ = true;
                    		pDialog_.setProgress(33);
                    	}
                    	else if(firstDone_ == true && firstTime_ == true){
                    		pDialog_.setProgress(66);
                    	}
                    	else if(firstDone_ == false && firstTime_ == false){
                    		firstDone_ = true;
                    		pDialog_.setProgress(50);
                    	}
                    	else{
                    		pDialog_.dismiss();
                    	}
                    }
                });
            	
    			//sendFriendRequest();
    			
    		} catch (JSONException e) {
    			Log.w("Facebook-Example", "JSON Error in response");
    		}
    	}
    }
	
    ////////////////////
    //PROFILE RESPONSE//
    ////////////////////
    
    public class meRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
        	
            try {
                // process the response here: executed in background thread

                Log.d("User Request", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                profileName = json.getString("name");
                profileID = json.getString("id");
                Globals.uid = profileID;
                Globals.name = profileName;
                
                SharedPreferences.Editor editor = prefs_.edit();
                editor.putString("profileid", profileID);
                editor.putString("profilename", profileName);
                editor.commit();
          
        	    URL img_value = null;
        	    img_value = new URL("http://graph.facebook.com/"+profileID+"/picture?type=square");
        	    final Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
        	   
        	    
                PeopleFinderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        profileName_.setText(profileName);
                        profilePhoto.setImageBitmap(mIcon1);
                        
                    	if(firstDone_ == false && firstTime_ == true){
                    		firstDone_ = true;
                    		pDialog_.setProgress(33);
                    	}
                    	else if(firstDone_ == true && firstTime_ == true){
                    		pDialog_.setProgress(66);
                    	}
                    	else if(firstDone_ == false && firstTime_ == false){
                    		firstDone_ = true;
                    		pDialog_.setProgress(50);
                    	}
                    	else{
                    		pDialog_.dismiss();
                    	}
                        if(firstTime_ == true){
                        	doFQLMultiquery();
                        	pDialog_.dismiss();
                        }
                    }
                });
                
                   
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            } catch (MalformedURLException e) {
				Log.w("Facebook-Example", "URL Error: " + e.getMessage());
			} catch (IOException e) {
				Log.w("Facebook-Example", "IO Error: " + e.getMessage());
			}
			
        }
    }
    
    ///////////////////////
    //FQL MULTIQUERY CODE//
    /////////////////////// 
    
	public void doFQLMultiquery() {
	    String fql_multiquery = "%7B%22query1%22%3A%22SELECT+uid+FROM+user+WHERE+uid+IN+%28SELECT+uid2+FROM+friend+WHERE+uid1+%3D+" + profileID + "%29+AND+is_app_user+%3D+1%22%2C%22query2%22%3A%22SELECT+name%2C+id%2C+pic+FROM+profile+WHERE+id+IN+%28SELECT+uid+FROM+%23query1%29%22%7D&access_token=" + facebook.getAccessToken() + "&format=json";
	    String url_ = "https://api.facebook.com/method/fql.multiquery?queries="+ fql_multiquery;
		HttpUtils.get().doGet(url_, PeopleFinderActivity.this);
	}

	public void onResponse(HttpResponse resp) {
		try {
			appFriends.clear();
			String response = HttpUtils.get().responseToString(resp);
			Log.e("multiquery", response);
	        JSONArray json;
			json = new JSONArray(response);
			JSONObject d = json.getJSONObject(1);
			JSONArray json2 = d.getJSONArray("fql_result_set");
				
			int l = (json2 != null ? json2.length() : 0);
			Log.d("Facebook-Example-Friends Request", "d.length(): " + l);

			for (int i=0; i<l; i++) {
				JSONObject o = json2.getJSONObject(i);
				String n = o.getString("name");
	    		String id = o.getString("id");
	    		String pic = o.getString("pic"); 
    		    Friend f = new Friend();
    		    f.id = id;
    		    f.name = n;
    		    f.pictureURL = pic;
    		    Log.d("Friends", n);
    		    appFriends.add(f);	
			}
			
			appFriends = MyQsort.sortArrayList(appFriends);
			
			tempFillContainer();

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void onError(Exception e) {
		// TODO Auto-generated method stub
		
	}
	
	//This will need to be reworked to integrate the style of the tempfillcontainer() method.
	public void parseAppFriends() {
		for(int i=0; i<appFriends.size(); i++) {
			LinearLayout appFriendLayout_ = new LinearLayout(getBaseContext());
    	    URL img_value = null;
    	    try {
				img_value = new URL("http://graph.facebook.com/"+appFriends.get(i).id+"/picture?type=square");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    Bitmap mIcon1 = null;
			try {
				mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    hlp.setMargins(0, 0, 10, 0);
    	    ImageView photo = new ImageView(getBaseContext());
    	    photo.setImageBitmap(mIcon1);
    	    photo.setLayoutParams(hlp);
    	    appFriendLayout_.addView(photo);
			TextView t = new TextView(getBaseContext());
			if(!ParsedXML.get(appFriends.get(i).id).equals("")){
				t.setText(appFriends.get(i).name + " - " + ParsedXML.get(appFriends.get(i).id));
			}
			else {
				t.setText(appFriends.get(i).name);
			}
			t.setTextColor(Globals.textColor);
			t.setTextSize(18);
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			llp.setMargins(0, 0, 0, 0);
			t.setLayoutParams(llp);
			appFriendLayout_.addView(t);
			LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			plp.setMargins(0, 10, 0, 10);
			appFriendLayout_.setLayoutParams(plp);
			friendsLayout_.addView(appFriendLayout_);
		}
	}
	
        public static String getPractice(String id){
          String testURL = null;
          testURL = ("http://graph.facebook.com/"+id+"/picture?type=square");
          return testURL;
  }
   
    public void tempFillContainer() {
    	friendsLayout_.removeAllViews();
    	for(int i=0; i<appFriends.size(); i++) {
    		
    	    URL img_value = null;
    	    try {
    	    	
    	    	// This is where the profile picture is on the web
    	    	// -set id+"/picture?type=square"
    	    	// 
    	    	
				img_value = new URL("http://graph.facebook.com/"+appFriends.get(i).id+"/picture?type=large");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    
    	    	// This actually is the image that you use
    	    Bitmap mIcon1 = null;
			try {
				mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		LinearLayout appFriendLayout_ = new LinearLayout(getBaseContext());
    		appFriendLayout_.setOrientation(1);
			String name = appFriends.get(i).name;
    		LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	    hlp.setMargins(0, 0, 10, 0);
    	    ImageView photo = new ImageView(getBaseContext());
    	    photo.setImageBitmap(mIcon1);
    	    appFriends.get(i).pictureBitmap = mIcon1;
    	    photo.setMaxHeight(MAX_HEIGHT);
    	    photo.setMaxWidth(MAX_HEIGHT);
    	    photo.setLayoutParams(hlp);
    	    appFriendLayout_.addView(photo);
			TextView t = new TextView(getBaseContext());
			t.setText(name);
			/*
			if(!ParsedXML.get(appFriends.get(i).id).equals("")){
				t.setText(appFriends.get(i).name + " - " + ParsedXML.get(appFriends.get(i).id));
			}
			else {
				t.setText(appFriends.get(i).name);
			}
			*/
			t.setTextColor(0xFF800000);
			t.setTextSize(18);
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			llp.setMargins(0, 0, 0, 0);
			t.setLayoutParams(llp);
			appFriendLayout_.addView(t);
			LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			plp.setMargins(10, 10, 10, 10);
			appFriendLayout_.setLayoutParams(plp);
			appFriendLayout_.setTag(appFriends.get(i).id);
			friendsLayout_.addView(appFriendLayout_);
			appFriendLayout_.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String tag = v.getTag().toString();
					currentTag = tag;
					//makeToast(tag + " pressed.");
					makeDialog(SHORT_PRESS_ALERT, tag);
				}
				
			});
			appFriendLayout_.setOnLongClickListener(new OnLongClickListener() {

				public boolean onLongClick(View arg0) {
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 300 milliseconds
					v.vibrate(50);
					String tag = arg0.getTag().toString();
					currentTag = tag;
					//makeToast(tag + " long pressed.");
					makeDialog(LONG_PRESS_ALERT, tag);
					return true;
				}
				
			});
    	}
    }
	
	//////////////////////
	//FACEBOOK WALL-POST//
	//////////////////////
	
	public void sendFriendRequest(int num) {			
	    		Bundle params = new Bundle();
            
				params.putString("caption","People Finder is a group project for some silly class.");
				params.putString("link", "https://www.google.com");
				params.putString("message", "Hey, check out People Finder!");
				params.putString("description", "This links to google for now.  Woops.");
				//params.putString("picture", "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/372851_163863397043367_832581163_n.jpg");
			                
				// an AsyncRunner to handle the dispatching the post
				mAsyncRunner.request(friends.get(num).id + "/feed", params, "POST", new LinkUploadListener(), null);

	}







//	public static void requestMapDialog(int Type, String str) {
		//
//	}
	

}