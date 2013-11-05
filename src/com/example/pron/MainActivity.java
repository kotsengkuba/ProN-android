package com.example.pron;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{
	protected LocationManager locationManager;
	TextView locationTextView, tempTextView, humTextView, rainTextView;
	WebView webView;
	private String provider;
	Geocoder geocoder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);
		
		locationTextView = (TextView) findViewById(R.id.cityTextView);
		tempTextView = (TextView) findViewById(R.id.tempTextView);	
		humTextView = (TextView) findViewById(R.id.humidTextView);
		rainTextView = (TextView) findViewById(R.id.rainTextView);
		webView = (WebView) findViewById(R.id.webView1);
		
		//Get the typeface from assets
		Typeface font = Typeface.createFromAsset(getAssets(), "Bender-Solid.otf");
		//Set the TextView's typeface (font)
		locationTextView.setTypeface(font);
		rainTextView.setTypeface(font);
	    
		init_location();
		geocoder = new Geocoder(this);
	    //new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML");
	    //new XMLparser().execute("https://dl.dropboxusercontent.com/u/15122106/testkml.kml");
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
		new AddressFromLocation().execute(location);
		//new ReverseGeocoding().execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&sensor=true");
		//locationTextView.setText(location.toString());
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
    
    public void gotoOpenGLView(View view) {
        Intent intent = new Intent(this, OpenGLES20.class);
        startActivity(intent);
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
	    	webView.loadData(xml, "text/html",null);
	    }


	    public void startElement (String uri, String name, String qName, Attributes atts)
	    {
	    	//xml= xml + "Start element: " + qName + "\n";
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
	    	//xml= xml + "End element: " + qName + "\n";
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
	    	//xml= xml + "Characters: " + s + "\n";
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
          //webView.loadData(s,"text/html",null);
        }
    }
    private class NetworkThread extends AsyncTask<String,Void,String>{
    	
    	@Override
    	protected void onPreExecute (){
    		
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
    	    }
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
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
    
    private class AddressFromLocation extends AsyncTask<Location,Void,String>{

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
    	    	if(addresses != null) {
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
           locationTextView.setText(s);
        }
    }

}
