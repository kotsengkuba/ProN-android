package com.example.pron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WeatherJSONReader {
	JSONArray places;
	JSONObject currentPlace;
	HashMap <String, Integer> weather_icon_hash = new HashMap<String, Integer>();
	
	public WeatherJSONReader(String s){
		try {
			//Initialize hashmap of icons
			initIconHash();
			
			places = new JSONObject(s).getJSONArray("places");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initIconHash(){
    	weather_icon_hash.put("12.png", R.drawable.cloudy);
    	weather_icon_hash.put("13.png", R.drawable.rainy);
    	weather_icon_hash.put("30.png", R.drawable.cloudy);
    	weather_icon_hash.put("31.png", R.drawable.clear);
    	weather_icon_hash.put("32.png", R.drawable.clear);
    	weather_icon_hash.put("33.png", R.drawable.cloudy);
    	weather_icon_hash.put("34.png", R.drawable.cloudy);
    	weather_icon_hash.put("40.png", R.drawable.rainy);
    	weather_icon_hash.put("28.png", R.drawable.cloudy);
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
	
	public HashMap<String, String> getDetailsHash(String placename, int dayIndex, int timeIndex){
		String s = "";
		JSONArray dates;
		HashMap<String, String> hm = new HashMap<String , String>();
		try {
			dates = getPlaceObject(placename).getJSONArray("dates");
			JSONObject first = dates.getJSONObject(dayIndex);
			JSONArray data = first.getJSONArray("data");
			JSONObject o = data.getJSONObject(timeIndex);
			JSONArray locnames = o.names();
			for(int i=0; i<locnames.length(); i++){
				//o.getString(locnames.getString(i));
				hm.put(locnames.getString(i), o.getString(locnames.getString(i)));
			}
//			s += o.toString();
//			return s;
			return hm;
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
	
	public int getWeatherIcon(String img){
		return weather_icon_hash.get(img);
	}
	
	public List<String> getAllPlaces(){
		List<String> all_places = new ArrayList<String>();
			try {
				for(int i = 0; i < getLength(); i++){
					all_places.add(places.getJSONObject(i).getString("name"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return all_places;
	}
	
	public List<String> getDates(String placename){
		List<String> l = new ArrayList<String>();
		JSONObject place = getPlaceObject(placename);
		if(place!=null){
			try {
				JSONArray arr = place.getJSONArray("dates");
				for(int i = 0; i<arr.length(); i++){
					l.add(arr.getJSONObject(i).getString("date"));
				}
				return l;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}	
}
