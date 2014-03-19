package com.example.pron;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Fragment{
	MapViewFragment fragment;
	
	int latitude, longitude;
	GoogleMap mMap;
	LatLng currLocation;
	ArrayList<Double> PARcoor = new ArrayList<Double>();
	ArrayList<Double> actualTrack = new ArrayList<Double>();
	ArrayList<Double> forecastTrack = new ArrayList<Double>();
	PolylineOptions PAROptions;
	
	View view;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
  	      Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OUT", "MapActivity");
//        setContentView(R.layout.activity_map);
        view = inflater.inflate(R.layout.activity_map,
		        container, false);
//        Intent intent = getIntent();
//        currLocation = new LatLng(intent.getDoubleExtra("Latitude", 14.5833), intent.getDoubleExtra("Longitude", 121));
        
        currLocation = new LatLng(14.5833,121.0);
        
        new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML", "storm");
        setupMap();
        
        return view;
	}
	
	public GoogleMap getMap(){
		return mMap;
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
	
	public void setTrack(ArrayList<Double> a){
		for(int i = 0; i<a.size()/2; i++){
			mMap.addMarker(new MarkerOptions().position(new LatLng(a.get(i*2+1), a.get(i*2))));
		}
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
//	            saxParser.parse(params[0], stormtrack_handler);
	            File testfile = new Filer().getFile("storm-track-YOLANDA-4.KML");
	            saxParser.parse(testfile, stormtrack_handler);
	            Log.d("OUT", "kml testfile: "+new Filer().fileExists("storm-track-YOLANDA-4.KML"));
                Log.d("OUT", "PARcoor "+stormtrack_handler.getPARcoor());
                PARcoor = stormtrack_handler.getPARcoor();
                actualTrack = stormtrack_handler.getActualTrack();
                forecastTrack = stormtrack_handler.getForecastTrack();
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    	Log.d("OUT", "stormparse exception: "+e);
    	    }
			return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    		setPAR();
    		Log.d("OUT", "Track: "+actualTrack);
    		if(actualTrack.size()>0)
    			setTrack(actualTrack);
    		if(forecastTrack.size()>0)
    			setTrack(forecastTrack);
        }
    }
	
}
