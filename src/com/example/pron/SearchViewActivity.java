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
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SearchViewActivity extends Activity {
	// List view
    private ListView lv;
    private ListView ol_lv;
    
    //Layout
    LinearLayout ll;
     
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
    
    OpenWeatherMapHandler owmh;
    TextView tv;
    
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
	
	OnlineSearch online_search_thread;
	
	WeatherJSONReader weatherReader; 
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        ll = (LinearLayout) findViewById(R.id.searchViewLayout);
        lv = (ListView) findViewById(R.id.list_view);
        ol_lv = new ListView(this);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        
        owmh = new OpenWeatherMapHandler();
        online_search_thread = new OnlineSearch();
        tv = new TextView(this);
        tv.setTypeface(Typeface.createFromAsset(getAssets(), "REGULAR.TTF"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.smalltext));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.addView(tv,1);

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
        
//        Log.d("OUT", "products: "+product_results);
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
				if(!owmh.IsNull())
					intent.putExtra("owmJSON", owmh.getRawString());
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
	        	   
	        	   if(product_results.size()==0){
            	   // search online
            		   Log.d("OUT", "no results");
            		   
            		   doOWMSearch(searchString); // hirap nito D:
            	   }
            	   else{
            		   tv.setVisibility(View.GONE);
            	   }
        	   }
        	   
        	   
//        	   Log.d("OUT", "products: "+product_results);
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
    	if(!saved_places.contains(loc)){
			saved_places.add(loc);
	    	
			// make toast
			Context context = getApplicationContext();
	    	CharSequence text = loc+" added.";
	    	int duration = Toast.LENGTH_SHORT;
	
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.setGravity(Gravity.TOP, 0, 0);
	    	toast.show();
    	}
		reset();
		//adapter.notifyDataSetChanged();
		inputSearch.setText("");
	}
    
    protected void removeLocation(String loc) {
    	if(saved_places.contains(loc)){
			saved_places.remove(loc);
			
			// make toast
			Context context = getApplicationContext();
	    	CharSequence text = loc+" removed.";
	    	int duration = Toast.LENGTH_SHORT;
	
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.setGravity(Gravity.TOP, 0, 0);
	    	toast.show();
    	}
		reset();
		//adapter.notifyDataSetChanged();
		inputSearch.setText("");
	}

	protected void reset() {
		// TODO Auto-generated method stub
		
		if(!online_search_thread.isCancelled())
			online_search_thread.cancel(true);
		
		product_results.clear();
 	   	imageId_results.clear();
 	   	temperature_results.clear();
 	   	other_results.clear();
 	   	tv.setVisibility(View.GONE);
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
	
	public void doOWMSearch(String s){
		if(online_search_thread.getStatus()==AsyncTask.Status.FINISHED || online_search_thread.getStatus()==AsyncTask.Status.PENDING)
			online_search_thread = (OnlineSearch) new OnlineSearch().execute(s);
		else if (online_search_thread.getStatus()==AsyncTask.Status.RUNNING){
			online_search_thread.cancel(true);
			if(online_search_thread.isCancelled()){
				online_search_thread = (OnlineSearch) new OnlineSearch();
				online_search_thread.execute(s);
			}
		}
		tv.setText("Searching online...");
		tv.setVisibility(View.VISIBLE);
	}
	
	public void addOwmToList(){
		tv.setText("Result(s) from Open Weather Map");
		tv.setVisibility(View.VISIBLE);
		if(!owmh.IsNull()){
			product_results.add(owmh.getLocation());
	    	imageId_results.add(owmh.getCurrentWeatherIcon());
	    	temperature_results.add(owmh.getCurrentTemp());
	    	adapter.notifyDataSetChanged();
		}
	}
	
	public void makeToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
	private class OnlineSearch extends AsyncTask<String,Void,Boolean>{
		@Override
    	protected void onPreExecute (){
			Log.d("OUT", "loading owm search...");
			  
    	}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			String temp = "";
			String [] res = {params[0], ""};
			other_results.clear();
			
			if(owmh.load(params[0])){
				return true;
			}
			
			return false;
		}
		
		@Override
        protected void onPostExecute(Boolean result) {
			makeToast("OWM Async result:"+result);
			if(result)
				addOwmToList();
			else{
				tv.setText("Location not found");
			}
		}
		
	}
}