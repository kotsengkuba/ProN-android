package com.example.pron;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class TyphoonActivity extends Activity{

	TextView typhoonTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typhoon);
		
		typhoonTextView = (TextView) findViewById(R.id.textView1);
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		typhoonTextView.setTypeface(font);
	}
}
