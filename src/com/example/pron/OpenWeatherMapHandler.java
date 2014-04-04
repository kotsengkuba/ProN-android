package com.example.pron;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.util.Log;

public class OpenWeatherMapHandler {
	String location = "";
	String rawString = "";
	JSONObject response;
	JSONArray list;
	boolean is_null = true;
	long date_time;
	
	public OpenWeatherMapHandler(){
		
		
	}
	
	public boolean load(String location){
		is_null = true;
		try{
			String query = URLEncoder.encode(location, "utf-8");
			Log.d("OUT", "query: "+query);
			URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q="+query+"&mode=json");
	        URLConnection connection = url.openConnection();
	        connection.connect();
	        InputStream input = new BufferedInputStream(url.openStream());
	        OutputStream output = new ByteArrayOutputStream();
	        byte data[] = new byte[1024];
	        int count;
	        
	        while ((count = input.read(data)) != -1) {
	            output.write(data, 0, count);    
	        }
	        rawString = output.toString();
	        return setHandlerFromResponse();
//	        response = new JSONObject(rawString);
//	        if(response.getString("cod").equals("200")){
//	        	list = response.getJSONArray("list");
//	        	is_null = false;
//	        	return true;
//	        }
		}catch(Exception e){}
		return false;
	}
	
	public boolean loadFromJSONString(String s){
		rawString = s;
		return(setHandlerFromResponse());
	}
	
	public boolean setHandlerFromResponse(){
		date_time = System.currentTimeMillis();
		try {
			response = new JSONObject(rawString);
	        if(response.getString("cod").equals("200")){
	        	list = response.getJSONArray("list");
	        	is_null = false;
	        	return true;
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String getLocation(){
		try {
			return response.getJSONObject("city").getString("name")+", "+response.getJSONObject("city").getString("country");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getRawString(){
		return rawString;
	}
	
	public boolean IsNull(){
		return is_null;
	}
	
	public long getDateTime(){
		return date_time;
	}
	
	public String getCurrentTemp(){
		for(int i=0; i<list.length(); i++){
			try{
				JSONObject listItem = list.getJSONObject(i);
				if(listItem.getDouble("dt")*1000>new Date().getTime()){
					if(i>0){
						JSONObject prevItem = list.getJSONObject(i-1);
//						double d = Double.parseDouble(prevItem.getJSONObject("main").getString("temp")) - 273.15;
						int n = (int)Math.round(Double.parseDouble(prevItem.getJSONObject("main").getString("temp")) - 273.15);
						return String.valueOf(n)+"°";
					}
				}
			}catch(Exception e){}
		}
		return null;
	}
	
	public String getTemp(String Sdate, String Stime){
		for(int i=0; i<list.length(); i++){
			try{
				JSONObject listItem = list.getJSONObject(i);
				SimpleDateFormat form = new SimpleDateFormat("MMMM dd, yyyy.hh:mma");
				java.util.Date date = null;
				try 
				{
				    date = form.parse(Sdate+"."+Stime);
				}
				catch (ParseException e) 
				{

				    e.printStackTrace();
				}
				SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String newDateStr = postFormater.format(date);
//				Log.d("OUT", "main: "+newDateStr+", owm: "+listItem.getString("dt_txt"));
				if(listItem.getString("dt_txt").equals(newDateStr)){
					if(i>0){
						double d = Double.parseDouble(listItem.getJSONObject("main").getString("temp")) - 273.15;
						return String.valueOf(d);
					}
				}
			}catch(Exception e){}
		}
		return null;
	}
	
	public int getCurrentWeatherIcon(){
		for(int i=0; i<list.length(); i++){
			try{
				JSONObject listItem = list.getJSONObject(i);
				if(listItem.getDouble("dt")*1000>new Date().getTime()){
					if(i>0){
						JSONObject prevItem = list.getJSONObject(i-1);
						String s = prevItem.getJSONObject("weather").getString("main");
						if(s.equalsIgnoreCase("cleat"))
							return R.drawable.clear;
					}
				}
			}catch(Exception e){}
		}
//		return 0;
		return R.drawable.clear;
	}

	
}
