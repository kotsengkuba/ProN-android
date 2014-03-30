package com.example.pron;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.app.DialogFragment;
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
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
	TextView locationTextView;
	ImageView typhoonButton;
	String currentCity = null;
	protected LocationManager locationManager;
	private String provider;
	Geocoder geocoder;
	MainWeatherFragment fragment;
	boolean loaded = false;
	SetAddressFromLocation gpsfinder;
	
	
	//Twitter t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_screen);
		Log.d("OUT", "MainAct OnCreate");

		if((new Filer().fileExists("fourdaylive.json"))){
			File file = new File (new File(Environment.getExternalStorageDirectory().toString() + "/weatherwheel/saved_files"), "fourdaylive.json");
			if(file.lastModified()-System.currentTimeMillis()<240000000){
				loaded = true;
				geocoder = new Geocoder(this);
				initLocation();
				Thread waiter = new Thread(){
					int wait = 0;
					@Override
				    public void run() {
			        try {
			        	Log.d("OUT", "wait start. CurrentCity: "+currentCity);
			            super.run();
			            while (wait < 5000) {
			                sleep(100);
			                wait += 100;
			            }
			            Log.d("OUT", "wait end. CurrentCity: "+currentCity);
			        } catch (Exception e) {
			            // ..
			        } finally {
			        	// ..
			        }
			    }};
			    //waiter.start();
			    loadMain();
			}
			else
				loadNOAH();
		} 
		else{
			loadNOAH();
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
	    
	    Log.d("OUT", "Location:"+location);
	    // Initialize the location fields
	    if (location != null) {
	      onLocationChanged(location);
	    } else {
	      //locationTextView.setText(provider + "Location not available");
//	    	setCurrentCity("Manila"); //default
	    }
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// Set text for location textview
//		gpsfinder.cancel(true);
		gpsfinder = new SetAddressFromLocation();
		gpsfinder.execute(location);
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
	
	public void setCurrentCity(String s){
		currentCity = s;
		if(fragment!=null)
			fragment.reset();
	}
	
	public void loadNOAH(){
		new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML", "fourday");
	}
	
	public void loadMain(){
	    if(currentCity == null)
			setCurrentCity("Manila");
		
		setContentView(R.layout.activity_main_v2);
		
		locationTextView = (TextView) findViewById(R.id.cityTextView);
//		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		Typeface font = Typeface.createFromAsset(getAssets(), "REGULAR.TTF");
		locationTextView.setTypeface(font);
		
		typhoonButton = (ImageView) findViewById(R.id.typhoonButton);
		typhoonButton.setVisibility(View.INVISIBLE);
		
		new StormParser().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML");
		
		setLocationText();
		//Log.d("OUT", "Twitter: "+t);
		
		fragment = (MainWeatherFragment) getFragmentManager().
				  findFragmentById(R.id.weather_detail_fragment);
				if (fragment==null || ! fragment.isInLayout()) {
				  // start new Activity
				  }
				else {
				  //fragment.update(...);
				} 
	}
	
	public void setLocationText(){
		int l = currentCity.length();
		
		//for track font:
//		if(l <= 11)
//			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.bigtext));
//		else if(currentCity.length()>11 && currentCity.length()<=14)
//			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medbigtext));
//		else if(currentCity.length()>14)
//			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medtext));
		
		//for regular font
		if(l <= 14)
			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.biggertext));
		else
			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.bigtext));
		
		locationTextView.setText(currentCity);
	}
	
	public String getCurrentCity(){
		return currentCity;
	}
	
	public void gpsclick(View v){
		Toast.makeText(this, "Finding your location...", Toast.LENGTH_SHORT).show();
		initLocation();
	}
	
	private class SetAddressFromLocation extends AsyncTask<Location,Void,String>{

    	@Override
    	protected void onPreExecute (){
    		//locationTextView.setText("loading location...");
    		Log.d("OUT", "Finding location...");
    	}
    	
    	@Override
		protected String doInBackground(Location... params) {
    		String location = "";
    	    try {
//    	    	Log.d("OUT", "adresses: "+geocoder);
    	    	List<Address> addresses = geocoder.getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 10);

    	    	int index = 2;
    	    	if(addresses.size() != 0) {
		    		   Address returnedAddress = addresses.get(index);
		    		   StringBuilder strReturnedAddress = new StringBuilder("");
		    		   String s = "";
		    		   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
//		    			   strReturnedAddress.append(returnedAddress.getAddressLine(i));
		    			   s = returnedAddress.getAddressLine(i);
		    			   Log.i("address", returnedAddress.getAddressLine(i));
		    		   }
//		    		   location = strReturnedAddress.toString();
		    		   location = s;
	    		}
	    		else{
	    		   location = "";
	    		}
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    	Log.d("OUT", "Location find error: "+e.toString());
    	    }
    	    return location;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    	   Log.d("OUT", "Location: "+s);
    	   
           if(s.length()>0 && new WeatherJSONReader(new Filer().fileToString("fourdaylive.json")).getPlaceObject(s)!=null){
        	   setCurrentCity(s);
        	   setLocationText();
        	   showGPSToast("GPS location found: "+s);
           }
           else
        	   setCurrentCity("Manila");
        	   setLocationText();
        	   if(s.length()==0)
        		   showGPSToast("Unable to get location");
        	   else
        		   showGPSToast("Unable to get data for "+s+". Default location loaded.");
        }
    }
	
	public void showGPSToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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
    
    public void openHelp(View view){
    	Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    
    public void openMap(View view){
    	Intent intent = new Intent(this, MapActivity.class);
//    	double lat = locationManager.getLastKnownLocation(provider).getLatitude();
//    	double lon = locationManager.getLastKnownLocation(provider).getLongitude();
    	if(locationManager.getLastKnownLocation(provider) != null){
    		intent.putExtra("Latitude", locationManager.getLastKnownLocation(provider).getLatitude());
        	intent.putExtra("Longitude", locationManager.getLastKnownLocation(provider).getLongitude());
    	}
    	else{
    		intent.putExtra("Latitude", 0);
        	intent.putExtra("Longitude", 0);
    	}
    	
    	 startActivity(intent);
    }
    
    public void openTyphoon(View view){
    	Intent intent = new Intent(this, TyphoonActivity.class);
        startActivity(intent);
    }
    
    public void addTyphoonButton(){
//		final TextView b = new TextView(this);
//		LinearLayout l = (LinearLayout)findViewById(R.id.stormLinearLayout);
//		b.setText("Typhoon");
//		b.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				openTyphoon(b);
//			}});
//		Log.d("OUT", "addTyphoonButton b: "+b+" l: "+l);
//		l.addView(b);
		typhoonButton.setVisibility(View.VISIBLE);
	}
    
    public void loadErrorFrag(){
		DialogFragment errorFragment = new ErrorFragment();
	    errorFragment.show(this.getFragmentManager(), "errorFrag");    
	}
    
    public void doPositiveClick() {
        // Do stuff here.
        Log.d("OUT", "Positive click!");
        loadNOAH();
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("jsoup", "REQUEST CODE:" + requestCode);
        switch (requestCode) {
        case 0:
            if (resultCode == RESULT_OK) {
            	// Get city from search view activity
    			setCurrentCity(data.getStringExtra("key"));
    			setLocationText();
    			fragment.reset();
    			Log.d("jsoup", "intent"); 		
            }
            break;
        default:
            break;
        }
    }
	
	private class StormParser extends AsyncTask<String,Void,String>{
    	SAXParserFactory factory;
    	SAXParser saxParser;
    	StormTrackXMLParser stormtrack_handler;
    	
    	@Override
    	protected void onPreExecute (){
    	}
    	@Override
		protected String doInBackground(String... params) {
			String s = "";
    		
    	    try {
    	        factory = SAXParserFactory.newInstance();
    			saxParser = factory.newSAXParser();
    			
    			Log.d("jsoup", "writing RAW..."+params[0]);
    			URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream());
                
                String path = Environment.getExternalStorageDirectory()
                        + "/weatherwheel/saved_files";
                File file = new File(path);
	            file.mkdirs();
	            File outputFile = new File(file, "stormRAW.txt");
	            OutputStream output = new FileOutputStream(outputFile);
	
	            byte data[] = new byte[1024];
	            int count;
	            
	            while ((count = input.read(data)) != -1) {
	                    output.write(data, 0, count);
	            }
	
	            output.flush();
	            output.close();
	            Log.d("jsoup", "RAW written.");

				stormtrack_handler = new StormTrackXMLParser();
	            saxParser.parse(params[0], stormtrack_handler);
                Log.d("OUT", "storm exists: "+stormtrack_handler.stormExists());
	            if(stormtrack_handler.stormExists()){
	            	addTyphoonButton();
	            }


    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
        }
    }
	
	private class XMLparser extends AsyncTask<String,Void,String>{
    	SAXParserFactory factory;
    	SAXParser saxParser;
    	FourDayXMLParser fourday_handler;
    	boolean loaded = true;
    	
    	@Override
    	protected void onPreExecute (){
    	}
    	@Override
		protected String doInBackground(String... params) {
			String s = "";
    		
    	    try {
    	        factory = SAXParserFactory.newInstance();
    			saxParser = factory.newSAXParser();
    			
    			Log.d("jsoup", "writing RAW..."+params[0]);
    			URL url = new URL(params[0]);
    			Log.d("OUT", "URL: "+url); 
    			
                URLConnection connection = url.openConnection();
                Log.d("OUT", "URLconnection: "+connection);
                connection.connect();

                Log.d("OUT", "url.openStream: "+url.openStream());
                InputStream input = new BufferedInputStream(url.openStream());
                Log.d("OUT", "InputStream: "+input);
                
                
                String path = Environment.getExternalStorageDirectory()
                        + "/weatherwheel/saved_files";
                
                Log.d("OUT", "Path: "+path); 
                
                File file = new File(path);
	            file.mkdirs();
	            File outputFile = new File(file, params[1]+"RAW.txt");
	            OutputStream output = new FileOutputStream(outputFile);
	            
	            Log.d("OUT", "OutputStream: "+output.toString());
	
	            byte data[] = new byte[1024];
	            int count;
	            
	            while ((count = input.read(data)) != -1) {
	                    output.write(data, 0, count);
	            }
	
	            output.flush();
	            output.close();
	            Log.d("jsoup", "RAW written.");
	            
	            
    			if(params[1].equals("fourday")){
    				Log.d("jsoup", "writing PARSED...");
    				fourday_handler = new FourDayXMLParser();
	                saxParser.parse(outputFile, fourday_handler);
	                s = fourday_handler.get_json_string();
	                new Filer().saveFile(s,"fourdaylive.json");
	                Log.d("jsoup", "PARSED written.");
    			}
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    	Log.d("OUT", "Exception"+e.toString()+e.getLocalizedMessage());
    	    	loaded = false;
    	    }
    	    
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    	  Log.i("kml","End parse...");
    	  
    	  // reload displayed data
    	  //setDataFromLocation();
    	  //Toast.makeText(null, "New data downloaded.", dayIndex).show();
    	  if(loaded)
    		  loadMain();
    	  else{
    		 loadErrorFrag(); 
    	  }
    		  
        }
    }
}
    
