//This Activity is finished.

package com.ece3574.dausin.activities;

import com.ece3574.dausin.R;
import com.ece3574.dausin.activities.PeopleFinderActivity;
import com.ece3574.dausin.facebook.SessionStore;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class LoginActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String FACEBOOK_ID = "288898474509243"; //app id which facebook needs to sign up for developers
	private static final String ACCESS_TOKEN = "access_token";
	private static final String ACCESS_EXPIRES ="access_expires";
	
    Facebook facebook = new Facebook(FACEBOOK_ID);	
    
    private SharedPreferences mPrefs;	// preferences will last after app closes (still secretly exist) with preferences
    private Button authButton_;			//login button

    
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);		
        setContentView(R.layout.login);
        
        authButton_ = (Button) findViewById(R.id.facebookAuthButton);	//ties login button
        
        authButton_.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
			        if(!facebook.isSessionValid()) {					//sees is session is valid. invalid means never logged in before
			            facebook.setAccessToken(null);					
			            facebook.setAccessExpires(0);					//will never have to re-log in unless you log out
			            facebook.authorize(LoginActivity.this, new String[] {"publish_stream", "publish_actions", "offline_access"}, new DialogListener() { 
			                public void onComplete(Bundle values) {								//after authorization we get bundle back
			                    SharedPreferences.Editor editor = mPrefs.edit();				//have preferences stored (not re-logging in)
			                    editor.putString(ACCESS_TOKEN, facebook.getAccessToken());		//permissions
			                    editor.putLong(ACCESS_EXPIRES, facebook.getAccessExpires());	//token won't expire
			                    editor.commit();												//pushes these preferences in memory
			                    SessionStore.save(facebook, getBaseContext());					//stores session in login activity for other activities
			                    Intent i = new Intent(LoginActivity.this, PeopleFinderActivity.class);	// moving to other activity
			                    startActivity(i);							
			                }
			                
			                //Exceptions
			                public void onFacebookError(FacebookError error) {
			                	Log.e("fberror", error.getMessage());
			                }
			    
			                public void onError(DialogError e) {
			                	Log.e("DialogError", e.getMessage());
			                }
			    
			                public void onCancel() {
			                	Log.e("Cancel", "Authorization Cancelled");
			                }
			                //end exceptions
			                
			            });
			        }
			        
			        else {
			        	//if already logged in store session and change activity
			        	SessionStore.save(facebook, getBaseContext());
						Intent i = new Intent(LoginActivity.this, PeopleFinderActivity.class);
						startActivity(i);
			        }
				
			}
			
		});
        	   
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString(ACCESS_TOKEN, null);
        long expires = mPrefs.getLong(ACCESS_EXPIRES, 0);
        if(access_token != null) {
        	facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
        	facebook.setAccessExpires(expires);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);	//needs to be here
    }
}
