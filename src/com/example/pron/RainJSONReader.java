package com.example.pron;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RainJSONReader {
	JSONArray places;
	
	public RainJSONReader(String s){
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
			if(place.getString("name").indexOf(placename) == 0){
				return place;
			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public String getRainData(String placename, int index){
		JSONObject o = new JSONObject();
		String s = "";
		try {
			if(getPlaceObject(placename)!=null)
				o = getPlaceObject(placename).getJSONArray("data").getJSONObject(index);
			return o.getString("Rain");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public String getRainTimes(String placename, int index){
		JSONObject o = new JSONObject();
		String s = "";
		try {
			if(getPlaceObject(placename)!=null)
				o = getPlaceObject(placename).getJSONArray("data").getJSONObject(index);
			return o.getString("Time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
}
