package com.example.pron;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener{
	TextView locationTextView;
	String currentCity = "Manila"; //default
	protected LocationManager locationManager;
	private String provider;
	Geocoder geocoder;
	MainWeatherFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);
		
		locationTextView = (TextView) findViewById(R.id.cityTextView);
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		locationTextView.setTypeface(font);
		
		initLocation();
		
		fragment = (MainWeatherFragment) getFragmentManager().
				  findFragmentById(R.id.weather_detail_fragment);
				if (fragment==null || ! fragment.isInLayout()) {
				  // start new Activity
				  }
				else {
				  //fragment.update(...);
				} 
	}
	
	/*
     *  Get location from gps
     * */
	public void initLocation(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		// Define the criteria how to select the location provider -> use default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);
	    
	    // Initialize the location fields
	    if (location != null) {
	      onLocationChanged(location);
	    } else {
	      //locationTextView.setText(provider + "Location not available");
	    }
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// Set text for location textview
		new SetAddressFromLocation().execute(location);
	}
	
	@Override
	public void onProviderDisabled(String provider){
		//Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderEnabled(String provider){
		//Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){
		
	}
	
	public void setLocationText(){
		locationTextView.setText(currentCity);
	}
	
	public String getCurrentCity(){
		return currentCity;
	}
	
	private class SetAddressFromLocation extends AsyncTask<Location,Void,String>{

    	@Override
    	protected void onPreExecute (){
    		//locationTextView.setText("loading location...");
    	}
    	
    	@Override
		protected String doInBackground(Location... params) {
    		String location = "";
    	    try {
    	    	List<Address> addresses = geocoder.getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 10);
    	    	int index = 2;
    	    	if(addresses.size() != 0) {
		    		   Address returnedAddress = addresses.get(index);
		    		   StringBuilder strReturnedAddress = new StringBuilder("");
		    		   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
		    		    strReturnedAddress.append(returnedAddress.getAddressLine(i));
		    		    Log.i("address", returnedAddress.getAddressLine(i));
		    		   }
		    		   location = strReturnedAddress.toString();
	    		}
	    		else{
	    		   location = "No Address returned!";
	    		}
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    return location;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
           if(s != ""){
    		currentCity = s;
    		setLocationText();
           }
        }
    }
	
	@Override
	protected void onResume() {
	  super.onResume();
	  //locationManager.requestLocationUpdates(provider, 400, 1, this);
	  //setDataFromLocation();	  
	}
	
	/* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
      //locationManager.removeUpdates(this);
    }

    public void searchCity(View view) {
        Intent intent = new Intent(this, SearchViewActivity.class);
        startActivityForResult(intent, 0);
    }
    
    public void openMap(View view){
    	Intent intent = new Intent(this, MapActivity.class);
        //startActivityForResult(intent, 0);
    }
    
    public void openTyphoon(View view){
    	Intent intent = new Intent(this, TyphoonActivity.class);
        startActivity(intent);
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("jsoup", "REQUEST CODE:" + requestCode);
        switch (requestCode) {
        case 0:
            if (resultCode == RESULT_OK) {
            	// Get city from search view activity
    			currentCity = data.getStringExtra("key");
    			setLocationText();
    			fragment.reset();
    			Log.d("jsoup", "intent"); 		
            }
            break;
        default:
            break;
        }
    }
    
}
