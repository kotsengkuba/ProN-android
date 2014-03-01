package com.example.pron;

import android.app.Activity;
import java.util.List;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageView;

public class TyphoonActivity extends Activity implements OnClickListener{

	TextView typhoonTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typhoon);
		
		typhoonTextView = (TextView) findViewById(R.id.header_typhoon);
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		typhoonTextView.setTypeface(font);
	   
		ImageView img = (ImageView) findViewById(R.id.icon_map);
		img.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		       // your code here
		    }
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
