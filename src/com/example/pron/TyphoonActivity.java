package com.example.pron;

import android.app.Activity;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageView;

public class TyphoonActivity extends Activity {

	TextView typhoonTextView;
	GoogleMap gmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
