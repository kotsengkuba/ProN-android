package com.example.pron;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class SearchViewActivity extends Activity {
	// List view
    private ListView lv;
     
    // Listview Adapter
    ArrayAdapter<String> adapter;
     
    // Search EditText
    EditText inputSearch;
    
    // Listview Data hardcode
    String products[] = {"Manila", "Quezon City", "Cebu City"};
    Integer[] imageId = {
            R.drawable.rainy,
            R.drawable.cloudy,
            R.drawable.clear     
    };
    
    List<Integer> search_results;
    List<String> product_results = new ArrayList<String>();
	List<Integer> imageId_results = new ArrayList<Integer>();
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        
        for(int i = 0; i<products.length; i++){
        	product_results.add(products[i]);
        	imageId_results.add(imageId[i]);
        }
        
        final CustomAdapter adapter = new CustomAdapter(SearchViewActivity.this, product_results, imageId_results);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				intent.putExtra("key", product_results.get(position).toString());
		        startActivity(intent);

            }
        });
        
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
        	           	   
        	   for(int i=0;i<products.length;i++)
        	   {
	        	  if(textLength<=products[i].length()){
	        	  //compare the String in EditText with Names in the list
	        	    if(searchString.equalsIgnoreCase(products[i].substring(0,textLength))){
	        	    	product_results.add(products[i]);
	        	    	imageId_results.add(imageId[i]);
	        	    }
	        	  }
        	   }
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
    }
}