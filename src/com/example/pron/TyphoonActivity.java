package com.example.pron;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TyphoonActivity extends Activity {

	TextView typhoonTextView;
	MapActivity mapActivity;
	LatLng currLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("OUT", "Typhoon Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typhoon);
		
		typhoonTextView = (TextView) findViewById(R.id.header_typhoon);
//		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		Typeface font = Typeface.createFromAsset(getAssets(), "REGULAR.TTF");
		typhoonTextView.setTypeface(font);
		
		mapActivity = (MapActivity) getFragmentManager().
				  findFragmentById(R.id.mapActivity);
				if (mapActivity==null || ! mapActivity.isInLayout()) {
				  // start new Activity
				  }
				else {
				  //fragment.update(...);
				}
				
	}
	
	public void displayMap(){

	}

}
