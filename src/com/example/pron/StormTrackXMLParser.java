package com.example.pron;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class StormTrackXMLParser extends DefaultHandler{
	
	String html = "";
	JSONObject json_final = new JSONObject();
	JSONArray json_array = new JSONArray();
	JSONObject json_obj = new JSONObject();
	boolean is_name, is_body, is_PAR, is_linestring, is_coordinates;
	ArrayList PARcoor = new ArrayList();
	int counter = 0;
	
	
	
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
    	Log.i("OUT","start element: "+qName);
    	if(qName.equals("name")){
    		is_name = true;
    		
    	}
    	else if(qName.equals("LineString")){
    		is_linestring = true;
    	}
    	else if(qName.equals("coordinates")){
    		is_coordinates = true;
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
    }


    public void characters (char ch[], int start, int length)
    {
    	String s = new String(ch, start, length);
    	if(is_name && s.equals("PAR")){
    		is_PAR = true;
    	}
    	if(is_PAR && is_coordinates){    		
    		String [] toks = s.split(",");
    		for(int i = 0; i<toks.length;i++){
    			if(toks[i].contains("0 "))
    				toks[i] = (String) toks[i].subSequence(2, toks[i].length());
    			Log.d("OUT", "toks int "+Double.parseDouble(toks[i]));
    			if(!toks[i].equals("0"))
    				PARcoor.add(Double.parseDouble(toks[i]));
    		}
    	}

	}
    
    public ArrayList getPARcoor(){
    	return PARcoor;
    }
	
	//private class HTMLParser{}
}
