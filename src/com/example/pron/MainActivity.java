package com.example.pron;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
	protected LocationManager locationManager;
	TextView locationTextView, httpText;
	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		locationTextView = (TextView) findViewById(R.id.cityTextView);
		httpText = (TextView) findViewById(R.id.tempTextView);
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
	      new ReverseGeocoding().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&sensor=true");
	    } else {
	      locationTextView.setText(provider + "Location not available");
	    }
	    
	    

	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	} 
	
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
		locationTextView.setText(location.toString());
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
    
    private class NetworkThread extends AsyncTask<String,Void,String>{
    	
    	@Override
    	protected void onPreExecute (){
    		locationTextView.setText("loading...");
    	}
    	@Override
		protected String doInBackground(String... params) {
			String s = "";
    		
    	    try {
    	    	URL url = new URL(params[0]);
    	    	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    	    	InputStream in = urlConnection.getInputStream();
    	    	InputStreamReader isw = new InputStreamReader(in);
    	    	int data = isw.read();
    	    	
    	        while (data != -1) {
    	            char current = (char) data;
    	            s = s + current;
    	            data = isw.read();
    	            System.out.print(current);
    	        }

    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    	httpText.setText(e.toString());
    	    }
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
           httpText.setText(s);
        }
    }
    
    private class ReverseGeocoding extends AsyncTask<String,Void,String>{
    	JSONObject jsonobject;
    	
    	@Override
    	protected void onPreExecute (){
    		locationTextView.setText("loading location...");
    	}
    	
    	@Override
		protected String doInBackground(String... params) {
			String s = "";
    		String location = "";
    	    try {
    	    	URL url = new URL(params[0]);
    	    	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    	    	InputStream in = urlConnection.getInputStream();
    	    	InputStreamReader isw = new InputStreamReader(in);
    	    	int data = isw.read();
    	    	
    	        while (data != -1) {
    	            char current = (char) data;
    	            s = s + current;
    	            data = isw.read();
    	            System.out.print(current);
    	        }
    	        
    	        jsonobject = new JSONObject(s);
    	        JSONArray results = jsonobject.getJSONArray("results");
    	        JSONObject j = results.getJSONObject(1);
    	        
    	        location = j.getString("formatted_address");

    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    return location;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
           locationTextView.setText(s);
        }
    }

}
