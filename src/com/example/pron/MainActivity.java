package com.example.pron;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
	protected LocationManager locationManager;
	TextView text;
	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		text = (TextView) findViewById(R.id.cityTextView);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		// Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);
	    
	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
	      text.setText(provider + "Location not available");
	    }
		//text.setText(LocationManager.GPS_PROVIDER+LOCATION_SERVICE+location);
	}
	
	/* Request updates at startup */
	@Override
	protected void onResume() {
	  super.onResume();
	  locationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	
	/* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
    }
	
	@Override
	public void onLocationChanged(Location location) {
		text.setText(location.toString());
	}
	
	@Override
	public void onProviderDisabled(String provider){
		Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderEnabled(String provider){
		Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void viewNextDay(View view) {
        Intent intent = new Intent(this, WeekViewActivity.class);
        startActivity(intent);
    }

    public void searchCity(View view) {
        Intent intent = new Intent(this, SearchViewActivity.class);
        startActivity(intent);
    }

}
