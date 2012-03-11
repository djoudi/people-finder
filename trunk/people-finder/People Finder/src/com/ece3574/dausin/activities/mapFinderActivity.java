package com.ece3574.dausin.activities;

import java.util.List;

import com.ece3574.dausin.R;
import com.ece3574.dausin.maps.TheItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

//import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class mapFinderActivity extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Creates the MapView for the Activity
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.robinh);
        TheItemizedOverlay itemizedoverlay = new TheItemizedOverlay(drawable, this);
    
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
    
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
    }
    // Don't display routes on the map
    @Override
    protected boolean isRouteDisplayed(){
    	return false;
    }
}