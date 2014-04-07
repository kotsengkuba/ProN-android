package com.example.pron;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Fragment{
	int latitude, longitude;
	GoogleMap mMap;
	LatLng default_location;
	ArrayList<Double> PARcoor = new ArrayList<Double>();
	ArrayList<Double> actual_track = new ArrayList<Double>();
	ArrayList<Double> forecast_track = new ArrayList<Double>();
	JSONArray track, forecast_error;
	PolylineOptions PAROptions;
	ArrayList<Marker> marker_array = new ArrayList<Marker>();
	String storm_name = "";
	
	View view;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
  	      Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_map,
		        container, false);
        
        default_location = new LatLng(14.5833,121.0);
        
        new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML", "storm");
        setupMap();
        
        return view;
	}
	
	public GoogleMap getMap(){
		return mMap;
	}
	
	public void setPAR(){
		PAROptions = new PolylineOptions().color(Color.WHITE);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_location, 3));
        mMap.setOnCameraChangeListener(new OnCameraChangeListener(){

			@Override
			public void onCameraChange(CameraPosition arg0) {
				// TODO Auto-generated method stub
				if(mMap.getCameraPosition().zoom<5){
					for(Marker m : marker_array){
						if(marker_array.indexOf(m)!=marker_array.size()-1)
							m.setVisible(false);
					}
				}
				else{
					for(Marker m : marker_array){
						m.setVisible(true);
					}
				}
			}
        	
        });

	}
	
	public void setTrack(ArrayList<Double> a){
		for(int i = 0; i<a.size()/2; i++){
			mMap.addMarker(new MarkerOptions().position(new LatLng(a.get(i*2+1), a.get(i*2))));
		}
	}
	
	public void setTrack(JSONArray arr){
		PolylineOptions line = new PolylineOptions();
		
		for(int ctr = 0; ctr<arr.length();ctr++){
			try {
			JSONArray coordinates = arr.getJSONObject(ctr).getJSONArray("Coordinates");
			int limit = coordinates.length()/2;
			for(int i = 0; i<limit; i++){
				Marker newMarker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(coordinates.getDouble(i*2+1), coordinates.getDouble(i*2)))
				.snippet(arr.getJSONObject(ctr).getString("Description"))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_typhoon_small))
				.anchor((float) 0.5, (float)0.5));
				if(arr.getJSONObject(ctr).getString("Title")!= null){
					newMarker.setTitle(storm_name+"  "+arr.getJSONObject(ctr).getString("Title"));
				}
				marker_array.add(newMarker);
				line.add(new LatLng(coordinates.getDouble(i*2+1), coordinates.getDouble(i*2)))
					.color(0x66111199);
			}
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.d("OUT","Storm exception: "+e);
				e.printStackTrace();
			}
		}
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMap.getCameraPosition()));
		
		mMap.addPolyline(line);
	}
	
	public void setShape(JSONArray arr){
		// Instantiates a new Polygon object and adds points to define a rectangle
		PolygonOptions poly = new PolygonOptions().fillColor(0x70FFFF00).strokeColor(0x7FFFFF00);
		for(int i = 0; i<arr.length()/2; i++){
			try {
				double lat = arr.getDouble(i*2+1);
				double lon = arr.getDouble(i*2);
				if(i!=0 && (Math.abs(lat-(arr.getDouble((i-1)*2+1)))<25 && (Math.abs(lon-(arr.getDouble((i-1)*2)))<20)))
						poly.add(new LatLng(lat, lon));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Get back the mutable Polygon
		Polygon polygon = mMap.addPolygon(poly);
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
                PARcoor = stormtrack_handler.getPARcoor();
                actual_track = stormtrack_handler.getActualTrack();
                forecast_track = stormtrack_handler.getForecastTrack();
                track = stormtrack_handler.getActualTrackJSON();
                forecast_error = stormtrack_handler.getForecastErrorJSON();
                storm_name = stormtrack_handler.getStormName();
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    	Log.d("OUT", "stormparse exception: "+e);
    	    }
			return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    		setPAR();

    		if(track.length()>0){
    			setTrack(track);
    		}
    		for(int i = 0;i<forecast_error.length()-1;i++){
    			try {
    				// map forecast error
					setShape(forecast_error.getJSONArray(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
        }
    }
	
}
