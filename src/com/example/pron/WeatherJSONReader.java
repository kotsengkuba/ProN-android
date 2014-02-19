package com.example.pron;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WeatherJSONReader {
	JSONArray places;
	JSONObject currentPlace;
	
	public WeatherJSONReader(String s){
		try {
			places = new JSONObject(s).getJSONArray("places");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getLength(){
		return places.length();
	}
	
	public JSONArray getJSONArray(){
		return places;
	}
	
	public JSONObject getPlaceObject(String placename){
		JSONObject place;
		try {
			for(int i = 0; i < places.length(); i++){
				place = places.getJSONObject(i);
				//Log.d("OUT", "getPlaceObject: "+place.getString("name")+", "+placename);
				if(place.getString("name").indexOf(placename) == 0){
					currentPlace = place;
					return currentPlace;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getDetailString(String placename, String key, int dayIndex, int timeIndex){
		JSONArray dates;
		try {
			dates = getPlaceObject(placename).getJSONArray("dates");
			JSONObject o = dates.getJSONObject(dayIndex).getJSONArray("data").getJSONObject(timeIndex);
			return o.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getAllDetailsString(String placename, int dayIndex, int timeIndex){
		String s = "";
		JSONArray dates;
		try {
			dates = getPlaceObject(placename).getJSONArray("dates");
			JSONObject first = dates.getJSONObject(dayIndex);
			JSONArray data = first.getJSONArray("data");
			JSONObject o = data.getJSONObject(timeIndex);
			s += o.toString();
			return s;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
