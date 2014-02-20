package com.example.pron;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Activity{
	MapViewFragment fragment;
	
	int latitude, longitude;
	GoogleMap mMap;
	LatLng currLocation;
	double [] PARcoor = {120,25,135,25,135,5,115,5,115,15,120,21,120,25};
	PolylineOptions PAROptions;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OUT", "MapActivity");
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        currLocation = new LatLng(intent.getDoubleExtra("Latitude", 14.5833), intent.getDoubleExtra("Longitude", 121));
        
        
        
//        fragment = (MapViewFragment) getFragmentManager().findFragmentById(R.id.map);
//				if (fragment==null || ! fragment.isInLayout()) {
//				  // start new Activity
//				  }
//				else {
//				  //fragment.update(...);
//				} 
        setPAR();
        setupMap();
	}
	
	public void setPAR(){
		PAROptions = new PolylineOptions();
		for(int i = 0; i<PARcoor.length/2; i++){
			PAROptions.add(new LatLng(PARcoor[i*2+1], PARcoor[i*2]));
		}
	}
	
	public void setupMap(){
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        //LatLng sydney = new LatLng(-33.867, 151.206);
        

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 5));

        mMap.addMarker(new MarkerOptions()
                .title("X")
                .snippet("You are here.")
                .position(currLocation));
        
     // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(currLocation.latitude, currLocation.longitude))
                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
                .add(new LatLng(37.35, -122.0)); // Closes the polyline.

        // Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(PAROptions);

	}
}
