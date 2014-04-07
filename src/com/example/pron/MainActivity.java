package com.example.pron;
/*
 * Minnie was here.
 */
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
	OpenWeatherMapHandler owmh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_screen);
		
		owmh = new OpenWeatherMapHandler();
		
		if((new Filer().fileExists("fourdaylive.json"))){
			File file = new File (new File(Environment.getExternalStorageDirectory().toString() + "/weatherwheel/saved_files"), "fourdaylive.json");
			if(file.lastModified()-System.currentTimeMillis()<240000000){
				loaded = true;
				geocoder = new Geocoder(this);
				initLocation();
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
	    	Toast.makeText(this, "Finding your location...", Toast.LENGTH_SHORT).show();
	    	gpsfinder = new SetAddressFromLocation();
	    	onLocationChanged(location);
	    } else {
	    	Toast.makeText(this, "Unable to get your location", Toast.LENGTH_LONG).show();
	    }
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(gpsfinder.getStatus() == AsyncTask.Status.FINISHED || gpsfinder.getStatus() == AsyncTask.Status.PENDING){
			if(!gpsfinder.isCancelled())
				gpsfinder.cancel(true);
			gpsfinder = new SetAddressFromLocation();
			gpsfinder.execute(location);
		}
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
		Typeface font = Typeface.createFromAsset(getAssets(), "REGULAR.TTF");
		locationTextView.setTypeface(font);
		
		Log.d("OUT", "locationtextview"+locationTextView.getText());
		
		typhoonButton = (ImageView) findViewById(R.id.typhoonButton);
		typhoonButton.setVisibility(View.INVISIBLE);
		
		new StormParser().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML");
		
		setLocationText();
		
		fragment = (MainWeatherFragment) getFragmentManager().
				  findFragmentById(R.id.weather_detail_fragment);
	}
	
	public void setLocationText(){
		int l = currentCity.length();
		//for regular font
		if(l <= 14)
			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.biggertext));
		else if(l>14 && l<18)
			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.bigtext));
		else
			locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medtext));
		
		locationTextView.setText(currentCity);
	}
	
	public String getCurrentCity(){
		return currentCity;
	}
	
	public void gpsclick(View v){
		initLocation();
	}
	
	public void refreshclick(View v){
		Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
		fragment.updateData(1);
	}
	
	public void loadclick(View v){
		Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
		loadNOAH();
	}
	
	public OpenWeatherMapHandler getOWMH(){
		return owmh;
	}
	
	private class SetAddressFromLocation extends AsyncTask<Location,Void,String>{

    	@Override
    	protected void onPreExecute (){
    		Log.d("OUT", "Finding location...");
    	}
    	
    	@Override
		protected String doInBackground(Location... params) {
    		String location = "";
    	    try {
    	    	List<Address> addresses = geocoder.getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 10);

    	    	int index = 2;
    	    	if(addresses.size() != 0) {
		    		   Address returnedAddress = addresses.get(index);
		    		   String s = "";
		    		   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
		    			   s = returnedAddress.getAddressLine(i);
		    			   Log.i("address", returnedAddress.getAddressLine(i));
		    		   }
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
        	   owmh = new OpenWeatherMapHandler();
        	   setCurrentCity(s);
        	   setLocationText();
        	   showGPSToast("GPS location found: "+s);
           }
           else{
        	   if(owmh.IsNull()){
        		   owmh = new OpenWeatherMapHandler();
	        	   setCurrentCity("Manila");
	        	   setLocationText();
        	   }
        	   
        	   if(s.length()==0)
        		   showGPSToast("Unable to get location");
        	   else
        		   showGPSToast("Unable to get data for "+s+". Default location loaded.");
           }
        }
    }
	
	public void showGPSToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onResume() {
	  super.onResume();  
	}
	
	/* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
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
		typhoonButton.setVisibility(View.VISIBLE);
	}
    
    public void loadErrorFrag(){
		DialogFragment errorFragment = new ErrorFragment();
	    errorFragment.show(this.getFragmentManager(), "errorFrag");    
	}
    
    public void doPositiveClick() {
        // Do stuff here.
        Log.d("OUT", "Positive click!");
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("jsoup", "REQUEST CODE:" + requestCode);
        switch (requestCode) {
        case 0:
            if (resultCode == RESULT_OK) {
            	// data is from noah
            	owmh = new OpenWeatherMapHandler();
            	if(data.getStringExtra("owmJSON") != null && owmh.loadFromJSONString(data.getStringExtra("owmJSON"))){
            		// Get city from owmh
            		setCurrentCity(owmh.getLocation());
            	}
            	else{
	            	// Get city from key
	    			setCurrentCity(data.getStringExtra("key"));
            	}
            	setLocationText();            	
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
    	  if(loaded)
    		  loadMain();
    	  else{
    		 loadErrorFrag(); 
    	  }
    		  
        }
    }
}
    
