package com.example.pron;

import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity{
	//MapViewFragment fragment;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
//        fragment = (MapViewFragment) getFragmentManager().findFragmentById(R.id.map);
//				if (fragment==null || ! fragment.isInLayout()) {
//				  // start new Activity
//				  }
//				else {
//				  //fragment.update(...);
//				} 
	}
}
