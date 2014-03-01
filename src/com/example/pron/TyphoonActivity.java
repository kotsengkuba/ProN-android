package com.example.pron;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class TyphoonActivity extends Activity{

	TextView typhoonTextView;
	//TyphoonActivityFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typhoon);
		
		typhoonTextView = (TextView) findViewById(R.id.header_typhoon);
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		typhoonTextView.setTypeface(font);
/*
		fragment = (TyphoonActivityFragment) getFragmentManager().
				findFragmentById(R.id.typhoon_detail_fragment);
				if (fragment==null || ! fragment.isInLayout()) {
				  // start new Activity
				}
				else {
				  //fragment.update(...);
				} */
	}
}
