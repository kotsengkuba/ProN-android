package com.example.pron;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class StormTrackXMLParser extends DefaultHandler{
	
	String html = "";
	JSONArray track = new JSONArray();
	JSONArray forecast_error = new JSONArray();
	//JSONArray error_array;
	JSONObject obj;
	boolean is_name, is_placemark, is_body, is_PAR, is_linestring, is_coordinates, is_actualtrack, is_forecasttrack, is_desc, is_p, is_error;
	ArrayList PARcoor = new ArrayList();
	ArrayList actualTrackCoor = new ArrayList();
	ArrayList forecastTrackCoor = new ArrayList();
	String storm_name = "";
	int counter = 0;
	boolean storm = false;
	
	
	public void startDocument ()
    {
		is_name = false;
		is_body = false;
		
		Log.i("OUT","stormtrack kml start");
    }

    public void endDocument ()
    {
    	Log.i("OUT","stormtrack kml end");
    }

    public void startElement (String uri, String name, String qName, Attributes atts)
    {

    	if(qName.equals("name")){
    		is_name = true;
    		
    	}
    	else if(qName.equals("Placemark")){
    		is_placemark = true;
    	}
    	else if(qName.equals("LineString")){
    		is_linestring = true;
    	}
    	else if(qName.equals("coordinates")){
    		is_coordinates = true;
    	}
    	else if(qName.equals("description")){
    		is_desc = true;
    		try {
    			obj.put("Title", "");
				obj.put("Description", "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else if(qName.equals("p")){
    		is_p = true;
    	}
    }


    public void endElement (String uri, String name, String qName)
    {	

    	if(qName.equals("name")){
    		is_name = false;
    	}
    	else if(qName.equals("LineString")){
    		is_linestring = false;
    	}
    	else if(qName.equals("coordinates")){
    		is_coordinates = false;
    	}
    	else if(qName.equals("Folder") && is_PAR){
    		is_PAR = false;
    	}
    	else if(qName.equals("Folder") && is_actualtrack){
    		is_actualtrack = false;
    	}
    	else if(qName.equals("Folder") && is_forecasttrack){
    		is_forecasttrack = false;
    	}
    	else if(qName.equals("Folder") && is_error){
    		is_error = false;
    	}
    	else if(qName.equals("Placemark") && (is_actualtrack || is_forecasttrack)){
    		try {
				track.put(new JSONObject(obj.toString()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		is_placemark = false;
    	}
    	else if(qName.equals("Folder") && is_forecasttrack){
    		is_forecasttrack = false;
    	}
    	else if(qName.equals("desc")){
    		is_desc = false;
    	}
    	else if(qName.equals("p")){
    		is_p = false;
    	}
    }


    public void characters (char ch[], int start, int length)
    {
    	String s = new String(ch, start, length);
    	if(is_name && s.equals("PAR")){
    		is_PAR = true;
    	}
    	else if(is_PAR && is_coordinates){    		
    		String [] toks = s.split(",");
    		for(int i = 0; i<toks.length;i++){
    			if(toks[i].contains("0 "))
    				toks[i] = (String) toks[i].subSequence(2, toks[i].length());

    			if(!toks[i].equals("0"))
    				PARcoor.add(Double.parseDouble(toks[i]));
    		}
    	}
    	else if(is_name && is_placemark && storm_name.length()==0){
    		storm_name = s;

    	}
    	else if(is_name && s.equals("Actual Position")){

    		is_actualtrack = true;
    		obj = new JSONObject();
    	}
    	else if(is_name && s.equals("Forecast Track")){
    		is_forecasttrack = true;
    		obj = new JSONObject();
    	}
    	else if(is_name && s.equals("Forecast Error")){
    		is_error = true;
    	}
    	else if(is_actualtrack && is_coordinates){

    		if(s.length()>0){
    			storm = true;
    			String [] toks = s.split(",");
    			JSONArray coor = new JSONArray();
        		for(int i = 0; i<toks.length;i++){
        			if(toks[i].contains("0 ")){
        				toks[i] = (String) toks[i].subSequence(2, toks[i].length());
        			}

        			if(!toks[i].equals("0")){
        				actualTrackCoor.add(Double.parseDouble(toks[i]));
        				try {
							coor.put(Double.parseDouble(toks[i]));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
        		}
        		try {
					obj.put("Coordinates", coor);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	else if(is_forecasttrack && is_coordinates){
    		if(s.length()>0){
    			storm = true;
    			JSONArray coor = new JSONArray();
    			String [] toks = s.split(",| ");
        		for(int i = 0; i<toks.length;i++){
        			if(toks[i].contains("0 "))
        				toks[i] = (String) toks[i].subSequence(2, toks[i].length());
//        			Log.d("OUT", "toks int "+Double.parseDouble(toks[i]));
        			if(!toks[i].equals("0"))
        				forecastTrackCoor.add(Double.parseDouble(toks[i]));
        			try {
						coor.put(Double.parseDouble(toks[i]));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		try {
					obj.put("Coordinates", coor);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	else if((is_actualtrack || is_forecasttrack) && is_desc && is_p){
    		try {
    			if(s.equals("Actual Position") || s.equals("Forecast Track")){
    				obj.put("Title", obj.get("Title")+s);
    			}
    			else if(!s.contains("Source")){
    				obj.put("Description", obj.get("Description")+s);
    			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else if(is_error && is_coordinates){
    		if(s.length()>0){
    			JSONArray coor = new JSONArray();
    			String [] toks = s.split(",| ");
        		for(int i = 0; i<toks.length;i++){
        			if(toks[i].contains("0 "))
        				toks[i] = (String) toks[i].subSequence(2, toks[i].length());

        			if(!toks[i].equals("0"))
        				//forecastTrackCoor.add(Double.parseDouble(toks[i]));
        			try {
						coor.put(Double.parseDouble(toks[i]));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
    		
				forecast_error.put(coor);
			
    		}
    	}

	}
    
    public ArrayList getPARcoor(){
    	return PARcoor;
    }
    
    public ArrayList getActualTrack(){
    	return actualTrackCoor;
    }
    
    public ArrayList getForecastTrack(){
    	return forecastTrackCoor;
    }
    
    public JSONArray getActualTrackJSON(){
    	return track;
    }
    
    public JSONArray getForecastErrorJSON(){
    	return forecast_error;
    }
    
    public boolean stormExists(){
    	return storm;
    }
    
    public String getStormName(){
    	return storm_name;
    }
	
	//private class HTMLParser{}
}
