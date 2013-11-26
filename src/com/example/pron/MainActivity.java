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
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener,GestureDetector.OnGestureListener{
	protected LocationManager locationManager;
	TextView locationTextView, tempTextView, timeTextView, rainTextView, dayTextView, rainLabelTextView;
	Wheel wheelView;
	WebView webview;
	FourDayView fourDayView;
	private String provider;
	Geocoder geocoder;
	String DEBUG_TAG = "touch event";
	//String [] time_array = new String[] {"6PM","9PM","12MN","3AM","6AM","9AM","12NN","3PM"};
	//String [] temp_array = new String[] {"34°","36°","36°","35°","33°","30°","30°","31°"};
	//String [] rain_array = new String[] {"45%","47%","54%","33%","80%","80%","90%","0%"};
	String [] time_array = new String[8];
	String [] temp_array = new String[8];
	String [] rain_array = new String[8];
	String currentCity = "Manila";
	
	String fourdaydata;
	public static final String PREFS_NAME = "MyPrefsFile";
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "asd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);
		
		locationTextView = (TextView) findViewById(R.id.cityTextView);
		tempTextView = (TextView) findViewById(R.id.tempTextView);	
		rainTextView = (TextView) findViewById(R.id.rainTextView);
		timeTextView = (TextView) findViewById(R.id.timeTextView);
		dayTextView = (TextView) findViewById(R.id.dayTextView);
		rainLabelTextView = (TextView) findViewById(R.id.rainLabelTextView);
		wheelView = (Wheel) findViewById(R.id.wheelView);
		fourDayView = (FourDayView) findViewById(R.id.fourDayView);
		
		webview = (WebView) findViewById(R.id.webView1);
		
		//preferences
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		editor.putBoolean("silentMode", true);
		// Commit the edits!
	      editor.commit();
		
		//Get the typeface from assets
		Typeface font = Typeface.createFromAsset(getAssets(), "TRACK.OTF");
		//Set the TextView's typeface (font)
		locationTextView.setTypeface(font);
		tempTextView.setTypeface(font);
		rainTextView.setTypeface(font);
		timeTextView.setTypeface(font);
		dayTextView.setTypeface(font);
		rainLabelTextView.setTypeface(font);
		
		init_location();
		saveFile("Hello World!", "test.txt");
		
		//geocoder = new Geocoder(this);
	    //new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML");
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			String value = b.getString("key");
			//Toast.makeText(this, "Value: "+value,Toast.LENGTH_LONG).show();
			currentCity = value;
		}
		
		readJSON(0, fileToString("sample.json"));
		init_values();
		setFourDayView();
		webview.setVisibility(View.INVISIBLE);
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
	
	public void viewNextDay(View view) {
        Intent intent = new Intent(this, WeekViewActivity.class);
        startActivity(intent);
    }

    public void searchCity(View view) {
        Intent intent = new Intent(this, SearchViewActivity.class);
        startActivity(intent);
    }
    
    public void toggleFourDay(View view){
    	if (fourDayView.getVisibility() == View.VISIBLE)
    		fourDayView.setVisibility(View.INVISIBLE);
    	else
    		fourDayView.setVisibility(View.VISIBLE);
    		fourDayView.bringToFront();
    }
    
    
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
	
	public void init_values(){
		setLocationText();
		setTimeText(time_array[0]);
		setTempText(temp_array[0]);
		setRainText(rain_array[0]);
	}
	
	public void setTimeText(String s){
		timeTextView.setText(s);
	}
	
	public void setTempText(String s){
		tempTextView.setText(s);
	}
	
	public void setRainText(String s){
		rainTextView.setText(s);
	}
	public void setLocationText(){
		locationTextView.setText(currentCity);
	}
	public void setFourDayView(){
		fourDayView.setTemp(0, 30);
		fourDayView.setTemp(1, 31);
		fourDayView.setTemp(2, 32);
		fourDayView.setTemp(3, 33);
		
		fourDayView.setImage(0, getResources().getDrawable(R.drawable.clear));
		fourDayView.setImage(1, getResources().getDrawable(R.drawable.cloudy));
		fourDayView.setImage(2, getResources().getDrawable(R.drawable.rainy));
		fourDayView.setImage(3, getResources().getDrawable(R.drawable.clear));
		
		fourDayView.setDay(0, "Tomorrow");
		fourDayView.setDay(1, "Nov 27");
		fourDayView.setDay(2, "Nov 28");
		fourDayView.setDay(3, "Nov 29");
	}
	
	// Check network connection
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

    private class SAXHandler extends DefaultHandler{
    	String xml = "";
    	boolean is_name, found, is_body;
		public void startDocument ()
	    {
			//System.out.println("Start document");
			is_name = false;
			is_body = false;
			found = false;
	    }


	    public void endDocument ()
	    {
	    	//System.out.println("End document");
	    	//webView.loadData(xml, "text/html",null);
	    }


	    public void startElement (String uri, String name, String qName, Attributes atts)
	    {
	    	xml= xml + "Start element: " + qName + "\n";
	    	Log.i("kml","Start: "+qName);
	    	
	    	if(qName.equals("name")){
	    		is_name = true;
	    	}
	    	else if(qName.equals("description")){
	    		is_body = true;
	    	}

	    }


	    public void endElement (String uri, String name, String qName)
	    {
	    	xml= xml + "End element: " + qName + "\n";
	    	Log.i("kml","End: "+qName);
	    	
	    	if(qName.equals("name")){
	    		is_name = false;
	    	}
	    	else if(qName.equals("description")){
	    		is_body = false;
	    		found = false;
	    	}
	    }


	    public void characters (char ch[], int start, int length)
	    {
	    	String s = new String(ch, start, length);
	    	xml= xml + "Characters: " + s + "\n";
	    	Log.i("kml",s);
	    	
	    	if(is_name && s.equals("Quezon City")){
	    		found = true;
	    	}
	    	
	    	if(is_body && found){
	    		xml = xml + s;
	    	}
		}
	    
	    public String get_string(){
	    	return xml;
	    }
    }
    private class XMLparser extends AsyncTask<String,Void,String>{
    	SAXParserFactory factory;
    	SAXParser saxParser;
    	SAXHandler handler;
    	
    	@Override
    	protected void onPreExecute (){
    		webview.loadData("silent mode: "+settings.getBoolean("silentMode", false),"text/html",null);
    	}
    	@Override
		protected String doInBackground(String... params) {
			String s = "";
    		
    	    try {
    	        factory = SAXParserFactory.newInstance();
    			saxParser = factory.newSAXParser();
    			handler = new SAXHandler();
                saxParser.parse(params[0], handler);
                s = handler.get_string();

    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    	  fourdaydata = s;
          webview.loadData("data: "+fourdaydata,"text/html",null);
          saveFile(fourdaydata,"fourday.txt");
        }
    }
    
    private class SetAddressFromLocation extends AsyncTask<Location,Void,String>{

    	@Override
    	protected void onPreExecute (){
    		locationTextView.setText("loading location...");
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
           //locationTextView.setText("Location: "+s);
    		//locationTextView.setText("QUEZON CITY");
    		currentCity = s;
    		setLocationText();
        }
    }
    
    public void saveFile(String s, String filename){
    	String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/pron/saved_files");    
        myDir.mkdirs();
        File file = new File (myDir, filename);
        if (file.exists ()) file.delete (); 
        try {
               FileOutputStream out = new FileOutputStream(file);
               out.write(s.getBytes());
               out.flush();
               out.close();

        } catch (Exception e) {
               e.printStackTrace();
        }
    }
    
    public String fileToString(String filename){
    	String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/pron/saved_files");    
        //myDir.mkdirs();
        File file = new File (myDir, filename);
        String s = "";
        StringBuffer stringBuffer = new StringBuffer();
        try {
        	FileInputStream in = new FileInputStream(file);
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        	String content = "";
        	while((content = reader.readLine()) != null){
	        	//s = s+content;
        		stringBuffer.append(content);
        	}
            in.close();

        } catch (Exception e) {
               e.printStackTrace();
        }
    	return stringBuffer.toString();
    }
    
    public void readJSON(int index, String s){
    	JSONObject jsonobject;
    	try{
    		jsonobject = new JSONObject(s);
    		if(index == 0){
    			JSONArray jsonarray = jsonobject.getJSONArray("places");
        		for(int i = 0; i < jsonarray.length(); i++){
        			JSONObject place = jsonarray.getJSONObject(i);
        			if(place.getString("name").equals(currentCity)){
        				JSONArray dates = place.getJSONArray("dates");
            			JSONObject first = dates.getJSONObject(0);
            			JSONArray data = first.getJSONArray("data");
    					for(int j = 0; j < data.length(); j++){
    						JSONObject o = data.getJSONObject(j);
    						time_array[j] = o.getString("time");
    						temp_array[j] = o.getString("temp")+"°";
    						rain_array[j] = o.getString("rain")+"%";
    					}
    					break;
					}
        		}
        	}
    	} catch(Exception e){}
    	
    	
    }

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
            	setRainText(rain_array[(int) (Math.round(4*i/Math.PI)%8)]);
            	Log.d("snap", ""+wheelView.rad);
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
            	wheelView.mVelocityTracker.recycle();
                break;
        }
        return true;
    }
    
    @Override
    public boolean onDown(MotionEvent event) { 
        Log.d(DEBUG_TAG,"onDown: " + event.toString()); 
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, 
            float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString()); 
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

}
