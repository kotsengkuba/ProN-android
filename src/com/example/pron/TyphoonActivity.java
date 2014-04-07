package com.example.pron;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

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
		Typeface font = Typeface.createFromAsset(getAssets(), "REGULAR.TTF");
		typhoonTextView.setTypeface(font);
		
		mapActivity = (MapActivity) getFragmentManager().
				  findFragmentById(R.id.mapActivity);				
	}
	
	public void displayMap(){

	}

}
