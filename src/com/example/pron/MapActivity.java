package com.example.pron;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Activity{
	MapViewFragment fragment;
	
	int latitude, longitude;
	GoogleMap mMap;
	LatLng currLocation;
	ArrayList<Double> PARcoor = new ArrayList<Double>();
	PolylineOptions PAROptions;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OUT", "MapActivity");
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        currLocation = new LatLng(intent.getDoubleExtra("Latitude", 14.5833), intent.getDoubleExtra("Longitude", 121));
        
        new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML", "fourday");
        setupMap();
	}
	
	public void setPAR(){
		PAROptions = new PolylineOptions();
		for(int i = 0; i<PARcoor.size()/2; i++){
			PAROptions.add(new LatLng(PARcoor.get(i*2+1), PARcoor.get(i*2)));
		}
		// Get back the mutable Polyline
        mMap.addPolyline(PAROptions);
	}
	
	public void setupMap(){
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        //LatLng sydney = new LatLng(-33.867, 151.206);
        

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 5));

        mMap.addMarker(new MarkerOptions()
                .title("X")
                .snippet("You are here.")
                .position(currLocation));
        

	}
	private class XMLparser extends AsyncTask<String,Void,String>{
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
	            stormtrack_handler = new StormTrackXMLParser();
	            saxParser.parse(params[0], stormtrack_handler);
                Log.d("OUT", "PARcoor "+stormtrack_handler.getPARcoor());
                PARcoor = stormtrack_handler.getPARcoor();
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
			return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    		setPAR();
        }
    }
	
}
