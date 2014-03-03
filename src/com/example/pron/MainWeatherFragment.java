package com.example.pron;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainWeatherFragment extends Fragment implements GestureDetector.OnGestureListener{

	private static final int RESULT_OK = 0;
	TextView tempTextView, timeTextView, dayTextView, rainLabelTextView, owmTextView;
	Wheel wheelView;
	RainWheel rainWheelView;
	WebView webview;
	LinearLayout rainLayout;
	TableLayout rainTableLayout;
	boolean center = false;
	
	String DEBUG_TAG = "touch event";
	
	String [] time_array = new String[8];
	String [] temp_array = new String[8];
	String [] rain_array = new String[4];
	String [] raintime_array = new String[4];
	
	// default values
	String currentCity;
	String day = "Today";
	int dayIndex = 0;
	int timeIndex = 0;

	List<String> dates;
	
	JSONObject cityData = new JSONObject();
	JSONObject rainData = new JSONObject();
	
	WeatherJSONReader weatherReader;
	RainJSONReader rainReader;
	
	String fourdaydata;
	
	GestureDetectorCompat dayGDetector;
	
	View view;
	float downY = 0;
	Typeface font;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_weather_detail,
		        container, false);
		
		tempTextView = (TextView) view.findViewById(R.id.tempTextView);	
//		rainTextView1 = (TextView) view.findViewById(R.id.rainTextView1);
//		rainTextView2 = (TextView) view.findViewById(R.id.rainTextView2);
//		rainTextView3 = (TextView) view.findViewById(R.id.rainTextView3);
		timeTextView = (TextView) view.findViewById(R.id.timeTextView);
		dayTextView = (TextView) view.findViewById(R.id.dayTextView);
		rainLabelTextView = (TextView) view.findViewById(R.id.rainLabelTextView);		
		wheelView = (Wheel) view.findViewById(R.id.wheelView);
		rainLayout = (LinearLayout) view.findViewById(R.id.LinearLayout1);
		rainTableLayout = new TableLayout(this.getActivity());
		rainLayout.addView(rainTableLayout);
		//rainWheelView = (RainWheel) view.findViewById(R.id.rainWheelView);
		
		owmTextView = new TextView(this.getActivity());
		ViewGroup v = (ViewGroup) tempTextView.getParent();
		v.addView(owmTextView, 0);
		
		//Get the typeface from assets
		font = Typeface.createFromAsset(this.getActivity().getAssets(), "TRACK.OTF");
		//Set the TextView's typeface (font)
		tempTextView.setTypeface(font);
//		rainTextView1.setTypeface(font);
//		rainTextView2.setTypeface(font);
//		rainTextView3.setTypeface(font);
		timeTextView.setTypeface(font);
		dayTextView.setTypeface(font);
		rainLabelTextView.setTypeface(font);
		
		reset();
		
		initGestureDetector();	
		view.setOnTouchListener(new OnTouchListener(){
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int index = event.getActionIndex();
		        int action = event.getActionMasked();
		        int pointerId = event.getPointerId(index);
		        float i, dist;

		        switch(action) {
		            case MotionEvent.ACTION_DOWN:
		            	Log.d(DEBUG_TAG,"onDown: " + event.getX() + event.getY());
		            	wheelView.lasty = event.getY();
		            	Log.d("OUT", "set downY = "+event.getY());
		            	downY = event.getY();
		            	if(event.getX() > wheelView.width*0.8)
		            		wheelView.onWheelArea = false;
		            	else
		            		wheelView.onWheelArea = true;
		            	
		            	dist = (float) Math.sqrt(Math.pow((event.getX()-wheelView.r), 2) + Math.pow((event.getY()-wheelView.cy), 2));
		            	if(dist < 100){
		            		center = true;
		            	}
		            	else{
		            		center = false;
		            	}
		            	
		                if(wheelView.mVelocityTracker == null) {
		                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
		                	wheelView.mVelocityTracker = VelocityTracker.obtain();
		                }
		                else {
		                    // Reset the velocity tracker back to its initial state.
		                	wheelView.mVelocityTracker.clear();
		                }
		                // Add a user's movement to the tracker.
		                wheelView.mVelocityTracker.addMovement(event);
		                break;
		            case MotionEvent.ACTION_MOVE:
		            	wheelView.delta = event.getY() - wheelView.lasty;
		            	wheelView.lasty = event.getY();
		            	wheelView.snap = false;
		            	
		            	dist = (float) Math.sqrt(Math.pow((event.getX()-wheelView.r), 2) + Math.pow((event.getY()-wheelView.cy), 2));
		            	if(dist > 100){
		            		center = false;
		            	}
		            	
		            	if(wheelView.onWheelArea){
		            		wheelView.rad = wheelView.rad + (float) Math.PI*(wheelView.delta/(2*wheelView.r));
		            		wheelView.invalidate();
		            	}
		            	
		            	wheelView.mVelocityTracker.addMovement(event);
		                // When you want to determine the velocity, call 
		                // computeCurrentVelocity(). Then call getXVelocity() 
		                // and getYVelocity() to retrieve the velocity for each pointer ID. 
		            	wheelView.mVelocityTracker.computeCurrentVelocity(1000);
		                // Log velocity of pixels per second
		                // Best practice to use VelocityTrackerCompat where possible.
		                Log.d(DEBUG_TAG, "X velocity: " + 
		                        VelocityTrackerCompat.getXVelocity(wheelView.mVelocityTracker, 
		                        pointerId));
		                Log.d(DEBUG_TAG, "Y velocity: " + 
		                        VelocityTrackerCompat.getYVelocity(wheelView.mVelocityTracker,
		                        pointerId));
		                break;
		            case MotionEvent.ACTION_UP:
		            	
		            	Log.d("OUT", "center: "+center);
		            	float deg;
		            	if(center){
		            		center = false;
		            		loadDetailsFrag();
		            	}
		            	else{
			            	wheelView.snap = true;
			            	deg = (wheelView.rad*180/(float)Math.PI)%360;
			        		if(wheelView.snap){	
			        			if(deg<0)
			        				deg = deg + 360;
			        			if((deg>0 && deg<45/2)|| deg>360-(45/2))
			        				wheelView.rad = 0;
			        			else if(deg>45/2 && deg<45+(45/2))
			        				wheelView.rad = (float)Math.PI/4;
			        			else if(deg>45+(45/2) && deg<90+(45/2))
			        				wheelView.rad = (float)Math.PI/2;
			        			else if(deg>90+(45/2) && deg<135+(45/2))
			        				wheelView.rad = 3*(float)Math.PI/4;
			        			else if(deg>135+(45/2) && deg<180+(45/2))
			        				wheelView.rad = (float)Math.PI;
			        			else if(deg>180+(45/2) && deg<225+(45/2))
			        				wheelView.rad = 5*(float)Math.PI/4;
			        			else if(deg>225+(45/2) && deg<270+(45/2))
			        				wheelView.rad = 6*(float)Math.PI/4;
			        			else if(deg>270+(45/2) && deg<315+(45/2))
			        				wheelView.rad = 7*(float)Math.PI/4;
			        		}
			            				            	
			            	wheelView.invalidate();
			            	i = -wheelView.rad;
			            	while(0.0f>i || i>(float)(2*Math.PI)){
			            		if(i<0)
			            			i+=(float) 2*Math.PI;
			            		else
			            			i-=(float) 2*Math.PI;
			            	}
			            	Log.d("snap", "i="+i);
			            	int temp = timeIndex;
			            	timeIndex = (int) (Math.round(4*i/Math.PI)%8);
			            	timeIndex = (timeIndex+wheelView.offset) %8;
			            	timeIndex = timeIndex%8;
			            	
			            	// change day from wheel
			            	float deltaY = downY-event.getY();
			            	Log.d("OUT", "downY: "+ downY);
			            	Log.d("OUT", "upY: "+ event.getY());
			            	Log.d("OUT", "downIndex: "+ temp);
			            	Log.d("OUT", "upIndex: "+ timeIndex);
			            	if(timeIndex-temp<0 && deltaY>0){
			            		plusDay();
			            	}
			            	else if(temp-timeIndex<-3 && deltaY<0)
			            	{
			            		minusDay();
			            	}
			            	
			            	setTimeText(time_array[timeIndex]);
			            	setTempText(temp_array[timeIndex]);
			            	
			            	Log.d("snap", "rad="+wheelView.rad+" timeIndex="+timeIndex+"offset="+wheelView.offset);
		            	}
		            case MotionEvent.ACTION_CANCEL:
		                // Return a VelocityTracker object back to be re-used by others.
		            	wheelView.mVelocityTracker.recycle();
		                break;
		        }
		    	Log.d(DEBUG_TAG,"onTouch: " + event.toString());
		        // Be sure to call the superclass implementation
		        return true;
			}
		});
    
	    return view;
	  }	  
	  
		public void reset(){
			currentCity = ((MainActivity) this.getActivity()).getCurrentCity();
			
			weatherReader = new WeatherJSONReader(new Filer().fileToString("fourdaylive.json"));
			Log.d("OUT", "weatherReader getLength: "+weatherReader.getLength());
			
			rainReader = new RainJSONReader(new Filer().fileToString("rainchancelive.json"));
			Log.d("OUT", "rainReader getLength: "+rainReader.getLength());
			
			setToCurrentTime();
			setDataFromLocation();			
			updateData();
		}
		
		public void loadDetailsFrag(){
			DialogFragment newFragment = new WeatherDetailDialogFragment();
			Bundle b = new Bundle();
			b.putString("s", weatherReader.getAllDetailsString(currentCity, dayIndex, timeIndex));
			newFragment.setArguments(b);
		    newFragment.show(this.getActivity().getFragmentManager(), "String");		    
		}
	    
	    public void viewMap() {
	    	final Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML"));
	    	startActivity(myIntent);
	    }
	    
	    public void setToCurrentTime(){
	    	//Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"), Locale.US);
			int hour = Integer.parseInt((new SimpleDateFormat("HH")).format(new Date()));
			timeIndex = (int) Math.floor(hour/3);
			wheelView.setOffset(timeIndex);
			wheelView.reset();
			Log.d("OUT", "setToDurrentTime() TimeIndex: "+timeIndex);
			wheelView.invalidate();
	    }
	    		
		public void setDataFromLocation(){
			// read and search files for location data
			cityData = weatherReader.getPlaceObject(currentCity);
			dates = weatherReader.getDates(currentCity);
			Log.d("OUT","dates: "+dates);
			Log.d("OUT", "Current Date: "+getCurrentDate("MMMM dd, yyyy"));
			
			dayIndex = 0;
			for(int i=0; i<dates.size(); i++){
				if(getCurrentDate("MMMM dd, yyyy").equals(dates.get(i))){
					dayIndex = i;
				}
			}
			
			rainData = rainReader.getPlaceObject(currentCity);
			if(rainData == null)
				Log.d("jsoup", "Rain data from file: NULL");
			setDataStrings(dayIndex);
			setWeatherIcons(dayIndex);
			reset_textviews();
		}
		
		public void initGestureDetector(){
			// Gesture Detector for dayTextView
	        dayGDetector = new GestureDetectorCompat(this.getActivity(), new GestureListener(){

				@Override
				public void flinged() {
					//Toast.makeText(this, "FLINGED",Toast.LENGTH_LONG).show();
					Log.d("OUT", "flinged");
				}

				@Override
				public void onRightToLeft() {
					plusDay();
//					dayIndex = (dayIndex+1)%4;
//					setDataStrings(dayIndex);
//					setWeatherIcons(dayIndex);
//					reset_textviews();
				}

				@Override
				public void onLeftToRight() {
					minusDay();
//					dayIndex = (dayIndex+3)%4;
//					setDataStrings(dayIndex);
//					setWeatherIcons(dayIndex);
//					reset_textviews();
				}

				@Override
				public void onTopToBottom() {
					viewMap();
				}

				@Override
				public void onBottomToTop() {
					
				}});
	        
	        dayTextView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					dayGDetector.onTouchEvent(event);
					return true;
				}});
		}
		
		public void updateData(){
			try{
				JSONObject j = cityData.getJSONArray("dates").getJSONObject(0);
				String date = j.getString("date");
				String[] tokens = date.split(" ");
				String month  = tokens[0];
				int day = Integer.parseInt(tokens[1].replaceAll(",", ""));
				int year = Integer.parseInt(tokens[2]);
				Log.d("jsoup", "Current Date: "+Calendar.getInstance().get(Calendar.DATE));
				Log.d("jsoup", "Current Month: "+Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
				Log.d("jsoup", "Current Year: "+Calendar.getInstance().get(Calendar.YEAR));
				
				File file = new File (new File(Environment.getExternalStorageDirectory().toString() + "/pron/saved_files"), "fourdaylive.json");
		        //Log.d("OUT", "FILE date modified: "+file.lastModified());
				
				Log.d("OUT", "rain data: downloading... ");
				new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML", "storm");
				
				new OWMHTask().execute(currentCity);
				
		        if(file.lastModified()-System.currentTimeMillis()>3600000 || !(dates.get(0)).equals(getCurrentDate("MMMM dd, yyyy"))){
		        	Log.d("OUT", "weather data: downloading... ");
					new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML", "fourday");
					Log.d("OUT", "rain data: downloading... ");
					new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/rain-forecast.KML", "rainchance");
					Toast.makeText(getActivity(), "Updating...", Toast.LENGTH_SHORT).show();
		        }
		        else{
		        	Log.d("OUT", "weather data is updated.");
		        }

			} catch (Exception e){
				Log.d("OUT", "weather data: exception "+e);
			}
		}
		
		public void reset_textviews(){
			setDayText();
			setTimeText(time_array[timeIndex]);
			setTempText(temp_array[timeIndex]);
			setRainText();
		}
		
		public void setTimeText(String s){
			timeTextView.setText(s);
		}
		
		public void setTempText(String s){
			tempTextView.setText(s);
		}
		
		public void setRainText(){
			if(rainTableLayout==null){
				rainTableLayout = new TableLayout(this.getActivity());
				rainLayout.addView(rainTableLayout);
			}
			if(dayIndex == 0 && rain_array[0].length()>0){
				rainTableLayout.removeAllViews();
				for(int i=0; i<rain_array.length; i++){
					TableRow rowItem = new TableRow(this.getActivity());
					
					TextView RainTextViewItem = new TextView(this.getActivity());
					RainTextViewItem.setTypeface(font);
					RainTextViewItem.setPadding(10, 10, 10, 10);
					RainTextViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.xstext));
					RainTextViewItem.setTextColor(Color.parseColor("#3F8FD2"));
					RainTextViewItem.setText(rain_array[i]);
					rowItem.addView(RainTextViewItem);
					
					TextView RainTimeTextViewItem = new TextView(this.getActivity());
					RainTimeTextViewItem.setTypeface(font);
					RainTimeTextViewItem.setPadding(10, 10, 10, 10);
					RainTimeTextViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.xstext));
					RainTimeTextViewItem.setTextColor(Color.parseColor("#3F8FD2"));
					RainTimeTextViewItem.setText(raintime_array[i]);
					rowItem.addView(RainTimeTextViewItem);
					
					rainTableLayout.addView(rowItem);
				}
				setRainVisibility(View.VISIBLE);
				
				// static pa yung width ng rainLayout
			}
			else
				setRainVisibility(View.INVISIBLE);
		}
		
		public void setDayText(){
			
			if(dates.get(dayIndex).equals(getCurrentDate("MMMM dd, yyyy"))){
				dayTextView.setText("Today");
			}
			else if(dayIndex>0 && dates.get(dayIndex-1).equals(getCurrentDate("MMMM dd, yyyy"))){
				dayTextView.setText("Tomorrow");
			}
			else{
				dayTextView.setText(dates.get(dayIndex).split(",")[0]);
			}
			
//			if(dayIndex == 0)
//				dayTextView.setText("Today");
//			else if(dayIndex == 1)
//				dayTextView.setText("Tomorrow");
//			else{
//				try {
//					j = cityData.getJSONArray("dates");
//					String date = j.getJSONObject(dayIndex).getString("date");
//					date = date.split(",")[0];
//					dayTextView.setText(date);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
		
		/* set string arrays depende sa day */
		public void setDataStrings(int day){
	    	try{
				for(int j = 0; j < 8; j++){
					time_array[j] = weatherReader.getDetailString(currentCity, "Time", day, j);
					String s = weatherReader.getDetailString(currentCity, "Temperature", day, j)+"°";
					if(s.equals(null))
						temp_array[j] = "--";
					else
						temp_array[j] = s;					
				}
				for(int j = 0; j < 4; j++){
					rain_array[j] = rainReader.getRainData(currentCity, j);
					raintime_array[j] = rainReader.getRainTimes(currentCity, j);
				}
	    	}catch(Exception e){}
	    }
		
		public void setWeatherIcons(int day){
			int [] arr = new int[8];
			try {
				for(int j = 0; j < 8; j++){
					String img_src = weatherReader.getDetailString(currentCity, "Weather Outlook", day, j);
					if(img_src == null)
						arr[j] = R.drawable.null_gray;
					else
						arr[j] = weatherReader.getWeatherIcon(img_src);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wheelView.setIcons(arr);
			wheelView.invalidate();
		}
		
		public void plusDay(){
			dayIndex = (dayIndex+1)%dates.size();
			setDataStrings(dayIndex);
			setWeatherIcons(dayIndex);
			reset_textviews();
		}
		
		public void minusDay(){
			if(dayIndex == 0)
				dayIndex = dates.size()-1;
			else
				dayIndex--;
			setDataStrings(dayIndex);
			setWeatherIcons(dayIndex);
			reset_textviews();
		}
		
		public void addTyphoonButton(){
			Button b = new Button(view.getContext());
			b.setText("asd");
			LinearLayout l = (LinearLayout)view.findViewById(R.id.linearLayout2);
			//l.addView(b, 2);
		}
		
		public void setRainVisibility(int v){
			rainLayout.setVisibility(v);
		}
		
		/* Check network connection */
		public boolean isNetworkAvailable() {
		    ConnectivityManager cm = (ConnectivityManager) 
		      this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		    // if no network is available networkInfo will be null
		    // otherwise check if we are connected
		    if (networkInfo != null && networkInfo.isConnected()) {
		        return true;
		    }
		    return false;
		}
		
		/* Checks if external storage is available for read and write */
		public boolean isExternalStorageWritable() {
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		        return true;
		    }
		    return false;
		}

		/* Checks if external storage is available to at least read */
		public boolean isExternalStorageReadable() {
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state) ||
		        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		        return true;
		    }
		    return false;
		}
		
		public String getCurrentDate(String format){
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat(format);
			String formattedDate = df.format(c.getTime());
			return formattedDate;
		}
	    
	    private class XMLparser extends AsyncTask<String,Void,String>{
	    	SAXParserFactory factory;
	    	SAXParser saxParser;
	    	FourDayXMLParser fourday_handler;
	    	RainChanceXMLParser rainchance_handler;
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
	    			
	    			Log.d("jsoup", "writing RAW..."+params[0]);
	    			URL url = new URL(params[0]);
	                URLConnection connection = url.openConnection();
	                connection.connect();
	                InputStream input = new BufferedInputStream(url.openStream());
	                
	                String path = Environment.getExternalStorageDirectory()
	                        + "/pron/saved_files";
	                File file = new File(path);
		            file.mkdirs();
		            File outputFile = new File(file, params[1]+"RAW.txt");
		            OutputStream output = new FileOutputStream(outputFile);
		
		            byte data[] = new byte[1024];
		            int count;
		            
		            while ((count = input.read(data)) != -1) {
		                    output.write(data, 0, count);
		            }
		
		            output.flush();
		            output.close();
		            Log.d("jsoup", "RAW written.");
		            
		            
	    			if(params[1].equals("fourday")){
	    				Log.d("jsoup", "writing PARSED...");
	    				fourday_handler = new FourDayXMLParser();
		                saxParser.parse(outputFile, fourday_handler);
		                s = fourday_handler.get_json_string();
		                new Filer().saveFile(s,"fourdaylive.json");
		                Log.d("jsoup", "PARSED written.");
	    			}
	    			else if(params[1].equals("rainchance")){
	    				rainchance_handler = new RainChanceXMLParser();
	    				Log.i("kml","Starting (rainchance) parse... "+params[0]);
		                saxParser.parse(outputFile, rainchance_handler);
		                s = rainchance_handler.get_json_string();
		                Log.i("kml","s = "+s);
		                new Filer().saveFile(s,"rainchancelive.json");
	    			}
	    			else if(params[1].equals("storm")){
	    				stormtrack_handler = new StormTrackXMLParser();
	    	            saxParser.parse(params[0], stormtrack_handler);
	                    //Log.d("OUT", "storm exists: "+stormtrack_handler.stormExists());
	    	            if(stormtrack_handler.stormExists()){
	    	            	addTyphoonButton();
	    	            }
	    			}
	    			else if(params[1].equals("openweathermmap")){
	    				Log.d("OUT", "OWM"+(new Filer().fileToString(params[1]+"RAW.txt")));
	    			}

	    	    } catch(Exception e){
	    	    	e.printStackTrace();
	    	    }
	    	    
	    	    return s;
			}
	    	
	    	@Override
	        protected void onPostExecute(String s) {
	    	  Log.i("kml","End parse...");
	    	  
	    	  // reload displayed data
	    	  setDataFromLocation();
	    	  //Toast.makeText(null, "New data downloaded.", dayIndex).show();
	        }
	    }
	    
	    private class OWMHTask extends AsyncTask<String,Void,String>{
	    	String temp = "";
	    	
			@Override
			protected String doInBackground(String... params) {
				OpenWeatherMapHandler owmh = new OpenWeatherMapHandler();
				if(owmh.load(params[0])){
					temp = owmh.getTemp(dates.get(dayIndex),time_array[timeIndex]);
				}
				return "";
			}
			
			@Override
	        protected void onPostExecute(String s) {
	    	  Log.i("OUT","OpenWeatherMAp temp: "+temp);
	    	  owmTextView.setText("OWM: "+Math.round(Double.parseDouble(temp)*100)/100);
	        }
		}

	    /* Gestures */
	    
	    @Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
}
