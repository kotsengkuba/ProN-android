package com.example.pron;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class RainChanceXMLParser extends DefaultHandler{
	
	String html = "";
	JSONObject json_final = new JSONObject();
	JSONArray json_array = new JSONArray();
	JSONObject json_obj = new JSONObject();
	boolean is_name, is_body;
	int counter = 0;
	public void startDocument ()
    {
		is_name = false;
		is_body = false;	
    }

    public void endDocument ()
    {
    }

    public void startElement (String uri, String name, String qName, Attributes atts)
    {
    	if(qName.equals("name")){
    		is_name = true;
    		
    	}
    	else if(qName.equals("description")){
    		is_body = true;
    		html = "";
    	}
    }


    public void endElement (String uri, String name, String qName)
    {	
    	if(qName.equals("name")){
    		is_name = false;
    	}
    	else if(qName.equals("description")){
    		is_body = false;
    		
			HtmlParser p = new HtmlParser();
    		String[] labels = {"Time", "Rain"};
    		
    		try{
    			json_obj.put("data", p.toRainChanceJSON(html, labels));
    			json_array.put(json_array.length(), new JSONObject(json_obj.toString()));
    		} catch(JSONException e){}
    	}
    	else if(qName.equals("Document")){
			try{
				json_final.put("places", json_array);
			} catch(JSONException e){}
    	}
    }


    public void characters (char ch[], int start, int length)
    {
    	String s = new String(ch, start, length);
    	if(is_name && !s.equalsIgnoreCase("rain-forecast.KML")){
    		counter ++;
    		
    		try{
    			json_obj.put("name", s);
    		} catch(JSONException e){}

    	}
    	else if(is_name && s.equalsIgnoreCase("rain-forecast.KML")){
    		is_name = false;
    	}
    	
    	if(is_body){
    		html += s;
    	}
	}
    
    public String get_json_string(){
    	return json_final.toString();
    }
}
