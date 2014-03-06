package com.example.pron;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class SearchViewActivity extends Activity {
	// List view
    private ListView lv;
    private ListView ol_lv;
     
    // Listview Adapter
    public CustomAdapter adapter;
    public CustomAdapter ol_adapter;
     
    // Search EditText
    EditText inputSearch;
    
    // Listview Data hardcode
    
    Integer[] imageId = {
            R.drawable.rainy,
            R.drawable.cloudy,
            R.drawable.clear     
    };
    
    List<Integer> search_results;
    List<String> product_results = new ArrayList<String>();
    List<String> temperature_results = new ArrayList<String>();
	List<Integer> imageId_results = new ArrayList<Integer>();
	List<String> other_results = new ArrayList<String>();
	
	List<String> ol_product_results = new ArrayList<String>();
    List<String> ol_temperature_results = new ArrayList<String>();
	List<Integer> ol_imageId_results = new ArrayList<Integer>();
	
	List<String> saved_places = new ArrayList<String>();;
	List<String> all_places = new ArrayList<String>();
	
	WeatherJSONReader weatherReader; 
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        weatherReader = new WeatherJSONReader(new Filer().fileToString("fourdaylive.json"));
		Log.d("OUT", "weatherReader getLength: "+weatherReader.getLength());

		// load all places list
       	try{
       		all_places = weatherReader.getAllPlaces();
       		java.util.Collections.sort(all_places);
       	} catch(Exception e){}
       	
    	String fileString = new Filer().fileToString("savedLocations.csv");
    	if(fileString.length()>0){
    		saved_places = new ArrayList<String>(Arrays.asList(new Filer().fileToString("savedLocations.csv").split("[,]")));
    	}
    	Log.d("OUT","saved_places: "+saved_places.size());
        
        reset();
        
        Log.d("OUT", "products: "+product_results);
        adapter = new CustomAdapter(SearchViewActivity.this, product_results, imageId_results, temperature_results);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
           	
            	// open activity for that location
        		saveLocationsToFile();
            	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra("key", product_results.get(position).toString());
		        //startActivity(intent);
				setResult(RESULT_OK, intent); 
				finish();
            }
        });
        
        //ol_adapter = new CustomAdapter(SearchViewActivity.this, ol_product_results, ol_imageId_results, ol_temperature_results);
//        ol_lv = new ListView(this);
//        ol_lv.addView(new TextView(this));
//        LinearLayout ll = (LinearLayout)findViewById(R.id.list_view).getParent();
//        ll.addView(ol_lv);
        
        inputSearch.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            	//get the text in the EditText
            	
        	   String searchString=inputSearch.getText().toString();
        	   int textLength=searchString.length();
        	 
        	   //clear the initial data set
        	   product_results.clear();
        	   imageId_results.clear();
        	   temperature_results.clear();
        	   
        	   if(textLength == 0){
        		   reset();
//        		   for(int i=0;i<saved_places.size();i++){
//	        		   if(textLength<=saved_places.get(i).length()){
//	     	        	    if(searchString.equalsIgnoreCase(saved_places.get(i).substring(0,textLength))){
//	     	        	    	product_results.add(saved_places.get(i));
//	     	        	    	imageId_results.add(weatherReader.getWeatherIcon(weatherReader.getDetailString(saved_places.get(i), "Weather Outlook", getCurrentDayIndex(), getCurrentTimeIndex())));
//	     	        	    	temperature_results.add(weatherReader.getDetailString(saved_places.get(i), "Temperature", getCurrentDayIndex(), getCurrentTimeIndex())+"°");
//	     	        	    }
//	     	        	}
//        	   		}
        	   }
        	   else{
	        	   for(int i=0;i<all_places.size();i++){
		        	  if(textLength<=all_places.get(i).length()){
		        	  //compare the String in EditText with Names in the list
		        	    if(searchString.equalsIgnoreCase(all_places.get(i).substring(0,textLength))){
		        	    	product_results.add(all_places.get(i));
		        	    	boolean saved = false;
		        	    	for(int j=0;j<saved_places.size();j++){
		 	        		   if(all_places.get(i).equalsIgnoreCase(saved_places.get(j))){
		 	        			  imageId_results.add(weatherReader.getWeatherIcon(weatherReader.getDetailString(saved_places.get(j), "Weather Outlook", getCurrentDayIndex(), getCurrentTimeIndex())));
		 	        			  temperature_results.add(weatherReader.getDetailString(saved_places.get(j), "Temperature", getCurrentDayIndex(), getCurrentTimeIndex())+"°");
		 	        			  saved = true;
		 	        			  break;
		 	     	        	}
		         	   		}
		        	    	if(!saved){
			        	    	imageId_results.add(null);	
		        	    		temperature_results.add("asdf");
		        	    	}
		        	    }
		        	  }
	        	   }
        	   }
        	   
        	   // search online
//        	   product_results.add("Searching online...");
// 	    	   imageId_results.add(null);
// 	      	   temperature_results.add("");
//        	   new OnlineSearch().execute(searchString);
        	   
        	   Log.d("OUT", "products: "+product_results);
        	   adapter.notifyDataSetChanged();    	
               
            }
             
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                 
            }
             
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub                          
            }
        });   
        
        // swipe listener
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                            	if(saved_places.contains(lv.getItemAtPosition(position))){
                            		return true;
                            	}
                            	else
                            		return false;
                                
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    //adapter.remove(adapter.getItem(position));
                                	removeLocation(lv.getItemAtPosition(position).toString());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        lv.setOnScrollListener(touchListener.makeScrollListener());
    }
    
    protected void addLocation(String loc) {
    	if(!saved_places.contains(loc))
			saved_places.add(loc);
		reset();
		//adapter.notifyDataSetChanged();
		inputSearch.setText("");
	}
    
    protected void removeLocation(String loc) {
    	if(saved_places.contains(loc))
			saved_places.remove(loc);
		reset();
		//adapter.notifyDataSetChanged();
		inputSearch.setText("");
	}

	protected void reset() {
		// TODO Auto-generated method stub
		product_results.clear();
 	   	imageId_results.clear();
 	   	temperature_results.clear();
 	   	other_results.clear();
 	   	if(saved_places.size()>0){
			for(int i = 0; i<saved_places.size(); i++){
	        	product_results.add(saved_places.get(i));
	        	//imageId_results.add(imageId[0]);
	        	imageId_results.add(weatherReader.getWeatherIcon(weatherReader.getDetailString(saved_places.get(i), "Weather Outlook", getCurrentDayIndex(), getCurrentTimeIndex())));
	        	temperature_results.add(weatherReader.getDetailString(saved_places.get(i), "Temperature", getCurrentDayIndex(), getCurrentTimeIndex())+"°");
			}
 	   	}
 	   	else{
	 	   	for(int i = 0; i<all_places.size(); i++){
	 	   		Log.d("OUT", "add to products"+all_places.get(i));
	        	product_results.add(all_places.get(i));
	        	imageId_results.add(null);
	        	temperature_results.add("");
			}
 	   	}
	}
	
	protected void saveLocationsToFile(){
		String s = "";
		for(int i = 0; i<saved_places.size(); i++){
			s += saved_places.get(i).toString();
			if(i<saved_places.size()-1)
				s += ",";
        }
		new Filer().saveFile(s, "savedLocations.csv");
	}
	
	public int getCurrentDayIndex(){
		return 0;
	}
	
	public int getCurrentTimeIndex(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"), Locale.US);
		int hour = Integer.parseInt((new SimpleDateFormat("HH")).format(new Date()));
		return (int) Math.floor(hour/3);
	}
	
	private class OnlineSearch extends AsyncTask<String,Void,String[]>{
		@Override
    	protected void onPreExecute (){
			Log.d("OUT", "loading owm search...");
			  
    	}
		
		@Override
		protected String[] doInBackground(String... params) {
			// TODO Auto-generated method stub
			String temp = "";
			String [] res = {params[0], ""};
			other_results.clear();
			OpenWeatherMapHandler owmh = new OpenWeatherMapHandler();
			if(owmh.load(params[0])){
				res[1] = owmh.getCurrentTemp();
				other_results.add(params[0]);
				//temperature_results.add(temp+"°");
			}
			
			return res;
		}
		
		@Override
        protected void onPostExecute(String [] s) {
				Log.d("OUT", "Search text: "+inputSearch.getText()+", own seaerch: "+s);
			if(other_results.size()>0 && s[0].equalsIgnoreCase(inputSearch.getText().toString())){
//				  product_results.remove(product_results.size()-1);
//		    	  imageId_results.remove(imageId_results.size()-1);
//		      	  temperature_results.remove(temperature_results.size()-1);
		      	  
		      	  product_results.addAll(other_results);
		      	  imageId_results.add(null);
		      	  temperature_results.add(s[1]);
		      	  adapter.notifyDataSetChanged();
			}
			else{
//				product_results.remove(product_results.size()-1);
//				imageId_results.remove(imageId_results.size()-1);
//      	    	temperature_results.remove(temperature_results.size()-1);
//      	    	adapter.notifyDataSetChanged();
			}
		}
		
	}
}