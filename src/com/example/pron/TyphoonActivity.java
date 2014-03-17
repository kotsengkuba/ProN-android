package com.example.pron;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

public class TyphoonActivity extends Activity {

	TextView typhoonTextView;
	GoogleMap gmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("OUT", "Typhoon Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typhoon);
		
		typhoonTextView = (TextView) findViewById(R.id.header_typhoon);
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		typhoonTextView.setTypeface(font);
	   
	}
	
	public void displayMap(){
		MapViewFragment mvf = (MapViewFragment) getFragmentManager().findFragmentById(R.id.map);
		gmap = mvf.getMap();
		setContentView(R.layout.activity_typhoon);
	}
	
}
