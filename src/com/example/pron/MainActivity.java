package com.example.pron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener,GestureDetector.OnGestureListener{
	
	TextView locationTextView, tempTextView, timeTextView, rainTextView1, rainTextView2, rainTextView3, dayTextView, rainLabelTextView;
	Wheel wheelView;
	WebView webview;
	
	protected LocationManager locationManager;
	private String provider;
	Geocoder geocoder;
	String DEBUG_TAG = "touch event";
	
	String [] time_array = new String[8];
	String [] temp_array = new String[8];
	String [] rain_array = new String[3];
	
	// default values
	String currentCity = "Manila";
	String day = "Today";
	int dayIndex = 0;
	
	JSONObject cityData = new JSONObject();
	JSONObject rainData = new JSONObject();
	
	String fourdaydata;
	
	GestureDetectorCompat dayGDetector;
	
	HtmlParser html_parser = new HtmlParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);
		
		locationTextView = (TextView) findViewById(R.id.cityTextView);
		tempTextView = (TextView) findViewById(R.id.tempTextView);	
		rainTextView1 = (TextView) findViewById(R.id.rainTextView1);
		rainTextView2 = (TextView) findViewById(R.id.rainTextView2);
		rainTextView3 = (TextView) findViewById(R.id.rainTextView3);
		timeTextView = (TextView) findViewById(R.id.timeTextView);
		dayTextView = (TextView) findViewById(R.id.dayTextView);
		rainLabelTextView = (TextView) findViewById(R.id.rainLabelTextView);		
		wheelView = (Wheel) findViewById(R.id.wheelView);
		
		//Get the typeface from assets
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		//Set the TextView's typeface (font)
		locationTextView.setTypeface(font);
		tempTextView.setTypeface(font);
		rainTextView1.setTypeface(font);
		rainTextView2.setTypeface(font);
		rainTextView3.setTypeface(font);
		timeTextView.setTypeface(font);
		dayTextView.setTypeface(font);
		rainLabelTextView.setTypeface(font);
		
		init_location();
		new Filer().saveFile("Hello World!", "test.txt");
		
		//geocoder = new Geocoder(this);
		
	    //new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML", "fourday");
		//new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/rain-forecast.KML", "rainchance");
		
		// Get city kung galing sa search city activity
		Bundle b = getIntent().getExtras();
		if(b != null){
			String value = b.getString("key");
			currentCity = value;
		}
		
		//readJSON(0, fileToString("sample.json"));
		cityData = readJSON(0, 0, new Filer().fileToString("fourdaylive.json"));
		rainData = readJSON(1, 0, new Filer().fileToString("rainchancelive.json"));
		if(rainData == null)
			Log.d("jsoup", "Rain data from file: NULL");
		setDataStrings(0);
		reset_values();
		
		// Gesture Detector for dayTextView
        dayGDetector = new GestureDetectorCompat(this, new GestureListener(){

			@Override
			public void flinged() {
				Toast.makeText(MainActivity.this, "FLINGED",Toast.LENGTH_LONG).show();
			}

			@Override
			public void onRightToLeft() {
				dayIndex = (dayIndex+1)%4;
				setDataStrings(dayIndex);
				reset_values();
			}

			@Override
			public void onLeftToRight() {
				dayIndex = (dayIndex+3)%4;
				setDataStrings(dayIndex);
				reset_values();
			}

			@Override
			public void onTopToBottom() {
				viewMap();
			}

			@Override
			public void onBottomToTop() {
				
			}});
		
		dayTextView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dayGDetector.onTouchEvent(event);
				return true;
			}});
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
		// Set text for location textview
		new SetAddressFromLocation().execute(location);
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

    public void searchCity(View view) {
        Intent intent = new Intent(this, SearchViewActivity.class);
        startActivity(intent);
    }
    
    public void viewMap() {
        //Intent intent = new Intent(this, MapActivity.class);
        //startActivity(intent);
    	final Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML"));
    	startActivity(myIntent);
    }
    
    /*
     *  Get location from gps
     * */
	public void init_location(){
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
	
	public void reset_values(){
		setLocationText();
		setDayText();
		setTimeText(time_array[0]);
		setTempText(temp_array[0]);
		setRainText(rain_array);
	}
	
	public void setTimeText(String s){
		timeTextView.setText(s);
	}
	
	public void setTempText(String s){
		tempTextView.setText(s);
	}
	
	public void setRainText(String [] s){
		rainTextView1.setText(s[0]);
		rainTextView2.setText(s[1]);
		rainTextView3.setText(s[2]);
	}
	public void setLocationText(){
		locationTextView.setText(currentCity);
	}
	public void setDayText(){
		if(dayIndex == 0)
			dayTextView.setText("Today");
		else if(dayIndex == 1)
			dayTextView.setText("Tomorrow");
		else if(dayIndex == 2)
			dayTextView.setText("Next Next day");
		else if(dayIndex == 3)
			dayTextView.setText("Next NExt Next day");
	}
	
	/* set string arrays depende sa day */
	public void setDataStrings(int day){
    	try{
	    	JSONArray dates = cityData.getJSONArray("dates");
	    	JSONObject first = dates.getJSONObject(day);
			JSONArray data = first.getJSONArray("data");
			for(int j = 0; j < data.length(); j++){
				JSONObject o = data.getJSONObject(j);
				//time_array[j] = o.getString("time");
				//temp_array[j] = o.getString("temp")+"°";
				//rain_array[j] = o.getString("rain")+"%";
				
				time_array[j] = o.getString("Time");
				temp_array[j] = o.getString("Temperature")+"°";
			}
			
			data = rainData.getJSONArray("data");
			for(int j = 0; j < data.length(); j++){
				JSONObject o = data.getJSONObject(j);
				rain_array[j] = o.getString("Rain");
			}
    	}catch(Exception e){}
    }
	
	/* Check network connection */
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
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
    
    private class XMLparser extends AsyncTask<String,Void,String>{
    	SAXParserFactory factory;
    	SAXParser saxParser;
    	FourDayXMLParser fourday_handler;
    	RainChanceXMLParser rainchance_handler;
    	
    	@Override
    	protected void onPreExecute (){
    	}
    	@Override
		protected String doInBackground(String... params) {
			String s = "";
    		
    	    try {
    	        factory = SAXParserFactory.newInstance();
    			saxParser = factory.newSAXParser();
    			if(params[1].equals("fourday")){
    				fourday_handler = new FourDayXMLParser();
	                saxParser.parse(params[0], fourday_handler);
	                s = fourday_handler.get_json_string();
	                new Filer().saveFile(s,"fourdaylive.json");
    			}
    			else if(params[1].equals("rainchance")){
    				rainchance_handler = new RainChanceXMLParser();
    				Log.i("kml","Starting (rainchance) parse... "+params[0]);
	                saxParser.parse(params[0], rainchance_handler);
	                s = rainchance_handler.get_json_string();
	                Log.i("kml","s = "+s);
	                new Filer().saveFile(s,"rainchancelive.json");
    			}

    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    	  Log.i("kml","End parse...");
        }
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
    
    /* set json object */
    public JSONObject readJSON(int x, int index, String s){
    	JSONObject jsonobject;
    	try{
    		jsonobject = new JSONObject(s);
    		
    		// 0 for fourday file
    		// 1 for rain file
    		
    		if(x == 0){
	    		if(index == 0){
	    			JSONArray jsonarray = jsonobject.getJSONArray("places");
	        		for(int i = 0; i < jsonarray.length(); i++){
	        			JSONObject place = jsonarray.getJSONObject(i);
	        			if(place.getString("name").equals(currentCity)){
	        				return place;
						}
	        		}
	        	}
    		}
    		else if(x == 1){
    			JSONArray jsonarray = jsonobject.getJSONArray("places");
        		for(int i = 0; i < jsonarray.length(); i++){
        			JSONObject place = jsonarray.getJSONObject(i);
        			if(place.getString("name").indexOf(currentCity) == 0){
        				return place;
					}
        		}
    			//Log.d("jsoup", jsonarray.getJSONObject(0).toString());
    			//return jsonarray.getJSONObject(0);
    		}
    	} catch(Exception e){}
    	
    	return null;
    }

    
    /* Gestures */
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        float i;

        switch(action) {
            case MotionEvent.ACTION_DOWN:
            	Log.d(DEBUG_TAG,"onDown: " + event.getX() + event.getY());
            	wheelView.lasty = event.getY();
            	if(event.getX() > wheelView.width*0.8)
            		wheelView.onWheelArea = false;
            	else
            		wheelView.onWheelArea = true;
            	
                if(wheelView.mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                	wheelView.mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                	wheelView.mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                wheelView.mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
            	wheelView.delta = event.getY() - wheelView.lasty;
            	wheelView.lasty = event.getY();
            	wheelView.snap = false;
            	if(wheelView.onWheelArea)
            		wheelView.invalidate();
            	
            	wheelView.mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call 
                // computeCurrentVelocity(). Then call getXVelocity() 
                // and getYVelocity() to retrieve the velocity for each pointer ID. 
            	wheelView.mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                Log.d(DEBUG_TAG, "X velocity: " + 
                        VelocityTrackerCompat.getXVelocity(wheelView.mVelocityTracker, 
                        pointerId));
                Log.d(DEBUG_TAG, "Y velocity: " + 
                        VelocityTrackerCompat.getYVelocity(wheelView.mVelocityTracker,
                        pointerId));
                break;
            case MotionEvent.ACTION_UP:
            	wheelView.snap = true;
            	wheelView.invalidate();
            	i = -wheelView.rad;
            	while(0.0f>i || i>(float)(2*Math.PI)){
            		if(i<0)
            			i+=(float) 2*Math.PI;
            		else
            			i-=(float) 2*Math.PI;
            	}
            	setTimeText(time_array[(int) (Math.round(4*i/Math.PI)%8)]);
            	setTempText(temp_array[(int) (Math.round(4*i/Math.PI)%8)]);
            	
            	Log.d("snap", ""+wheelView.rad);
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
            	wheelView.mVelocityTracker.recycle();
                break;
        }
    	Log.d(DEBUG_TAG,"onTouch: " + event.toString());
        // Be sure to call the superclass implementation
        return true;
    }

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
