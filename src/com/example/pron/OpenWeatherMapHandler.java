package com.example.pron;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
	String [] time_array = {"02:00AM", "05:00AM","08:00AM","11:00AM","02:00PM","05:00PM","08:00PM","11:00PM"};
	HashMap <String, Integer> weather_icon_hash = new HashMap<String, Integer>();
	
	public OpenWeatherMapHandler(){
		initIconHash();	
	}
	
	public void initIconHash(){
    	weather_icon_hash.put("Rain", R.drawable.rainy);
    	weather_icon_hash.put("Clouds", R.drawable.cloudy);
    	weather_icon_hash.put("Snow", R.drawable.cloudy);
    	weather_icon_hash.put("Clear", R.drawable.clear);
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
			if(response.getJSONObject("city").getString("name").length()>0)
				return response.getJSONObject("city").getString("name")+", "+response.getJSONObject("city").getString("country");
			else
				return response.getJSONObject("city").getString("country");
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
	
	public int getWeatherIcon(String weather){
		return weather_icon_hash.get(weather);
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
	
	public List<String> getDates(){
		List<String> l = new ArrayList<String>();
		if(!IsNull()){
			try {
				long now = System.currentTimeMillis();
				String nowday = new SimpleDateFormat("MMMM dd, yyyy").format(now);
				for(int i = 0; i<list.length(); i++){
					long datetime = Long.parseLong(list.getJSONObject(i).getString("dt"))*1000;
					String day = new SimpleDateFormat("MMMM dd, yyyy").format(datetime);
					if(nowday.equals(day) || datetime>now){
						boolean b = true;
						for(int j=0; j<l.size(); j++){
							if(l.get(j).equals(day)){
								b = false;
								break;
							}
						}
						if(b){
							l.add(day);
						}
					}
				}
				Log.d("OUT","owmh get dates: "+l);
				return l;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String[] getTimeArray(){
		return time_array;
	}
	
	public String[] getTempArray(String day){
		String[] arr = new String[time_array.length];
		boolean found;
		for(int j=0; j<time_array.length; j++){
			found = false;
			for(int i=0; i<list.length();i++){
				try {
					long datetime = Long.parseLong(list.getJSONObject(i).getString("dt"))*1000;
					String listday = new SimpleDateFormat("MMMM dd, yyyy").format(datetime);
					if(listday.equals(day)){
						String time_string = new SimpleDateFormat("hh:mma").format(datetime);
						if(time_array[j].equals(time_string)){
							found = true;
							double temp = Double.parseDouble(list.getJSONObject(i).getJSONObject("main").getString("temp")) - 273.15;
							arr[j] = String.valueOf((int)Math.round(temp))+"°C";
//							Log.d("OUT", "owmh time_string="+time_string+" temp="+temp);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!found){
				arr[j] = null;
			}
		}

		Log.d("OUT", "owmh temp array: "+day+Arrays.toString(arr));
		return arr;
	}
	
	public int[] getWeatherIconsArray(String day){
		int[] arr = new int[time_array.length];
		boolean found;
		for(int j=0; j<time_array.length; j++){
			found = false;
			for(int i=0; i<list.length();i++){
				try {
					long datetime = Long.parseLong(list.getJSONObject(i).getString("dt"))*1000;
					String listday = new SimpleDateFormat("MMMM dd, yyyy").format(datetime);
					if(listday.equals(day)){
						String time_string = new SimpleDateFormat("hh:mma").format(datetime);
						if(time_array[j].equals(time_string)){
							found = true;
							String weather = list.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main");
							arr[j] = getWeatherIcon(weather);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!found){
				arr[j] = R.drawable.null_gray;
			}
		}
		return arr;
	}
	
	public String getDetails(String day, int timeIndex){
		JSONObject details = new JSONObject();
		boolean found=false;
		for(int i=0; i<list.length();i++){
			found = false;
			try {
				long datetime = Long.parseLong(list.getJSONObject(i).getString("dt"))*1000;
				String listday = new SimpleDateFormat("MMMM dd, yyyy").format(datetime);
				if(listday.equals(day)){
					String time_string = new SimpleDateFormat("hh:mma").format(datetime);
					if(time_array[timeIndex].equals(time_string)){
						found = true;
						details.put("fromOWM", true);
						if(list.getJSONObject(i).has("main")){
							JSONObject main = list.getJSONObject(i).getJSONObject("main");
							for(int j=0;j<main.length();j++){
								String name = main.names().getString(j);
								String value = main.getString(name);
								String label = name;
								if(label.equalsIgnoreCase("temp")){
									label = "Temperature";
									value = String.valueOf(Math.round(Float.parseFloat(value)-273.15));
									value += "°C";
									details.put(label, value);
								}
								else if(label.equalsIgnoreCase("pressure")){
									label = "Pressure";
									value += " hpa";
									details.put(label, value);
								}
								else if(label.equalsIgnoreCase("humidity")){
									label = "Humidity";
									value += "%";
									details.put(label, value);
								}	
							}
						}
						if(list.getJSONObject(i).has("wind")){
							JSONObject wind = list.getJSONObject(i).getJSONObject("wind");
							details.put("Wind Speed", wind.getString("speed")+"m/s");
							details.put("Wind Direction", wind.getString("deg")+"°");
						}
						return details.toString();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!found){
			return null;
		}
		return null;
	}
	
}
