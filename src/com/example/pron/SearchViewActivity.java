package com.example.pron;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class SearchViewActivity extends Activity {
	// List view
    private ListView lv;
     
    // Listview Adapter
    ArrayAdapter<String> adapter;
     
    // Search EditText
    EditText inputSearch;
    
    // Listview Data
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

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
            	e.printStackTrace();
            	return null;
            } catch (XmlPullParserException e) {
            	e.printStackTrace();
            	return null;
            }
        }
        
        @Override
        protected void onPreExecute() {
        	Toast.makeText(SearchViewActivity.this, "Downloading...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onPostExecute(String result) {  
            // Displays the HTML string in the UI via a WebView
            //WebView myWebView = (WebView) findViewById(R.id.webView);
            //myWebView.loadData(result, "text/html", null);
        }
    }
    
	 // Uploads XML from stackoverflow.com, parses it, and combines it with
	 // HTML markup. Returns HTML string.
	 private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
	     InputStream stream = null;
	     // Instantiate the parser
	     XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         factory.setNamespaceAware(true);
         XmlPullParser xpp = factory.newPullParser();
	     Calendar rightNow = Calendar.getInstance(); 
	     SimpleDateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");
	         
	         
	     StringBuilder htmlString = new StringBuilder();
	     htmlString.append("<h3>" + urlString + "</h3>");
	     htmlString.append("<em> " + 
	             formatter.format(rightNow.getTime()) + "</em>");
	     String s = "";
	     try {
	         stream = downloadUrl(urlString);        
	         // entries = stackOverflowXmlParser.parse(stream);
	         InputStreamReader isw = new InputStreamReader(stream);
	 	    	int data = isw.read();
	 	        while (data != -1) {
	 	            char current = (char) data;
	 	            s = s + current;
	 	            data = isw.read();
	 	            System.out.print(current);
	 	        }	 	       
	     // Makes sure that the InputStream is closed after the app is
	     // finished using it.
	     } finally {
	         if (stream != null) {
	             stream.close();
	         } 
	      }
	     
	     xpp.setInput( new StringReader (s) );
         int eventType = xpp.getEventType();
         while (eventType != XmlPullParser.END_DOCUMENT) {
          htmlString.append("<p>");
          if(eventType == XmlPullParser.START_DOCUMENT) {
        	  htmlString.append("Start document ");
              System.out.println("Start document");
          } else if(eventType == XmlPullParser.START_TAG) {
        	  htmlString.append("Start tag "+xpp.getName());
              System.out.println("Start tag "+xpp.getName());
          } else if(eventType == XmlPullParser.END_TAG) {
        	  htmlString.append("End tag "+xpp.getName());
              System.out.println("End tag "+xpp.getName());
          } else if(eventType == XmlPullParser.TEXT) {
        	  htmlString.append("Text "+xpp.getText());
              System.out.println("Text "+xpp.getText());
          }
          htmlString.append("</p>");
          eventType = xpp.next();
         }
         System.out.println("End document");
     
	     
	     htmlString.append("<h4>Stream: "+stream.toString()+"</h4>");
	     
	     return htmlString.toString();
	 }
	
	 // Given a string representation of a URL, sets up a connection and gets
	 // an input stream.
	 private InputStream downloadUrl(String urlString) throws IOException {
	     URL url = new URL(urlString);
	     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	     conn.setReadTimeout(10000 /* milliseconds */);
	     conn.setConnectTimeout(15000 /* milliseconds */);
	     conn.setRequestMethod("GET");
	     conn.setDoInput(true);
	     // Starts the query
	     conn.connect();
	     return conn.getInputStream();
	 }
	 
	 private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		 @Override
	        protected Bitmap doInBackground(String... urls) {
	            try {
	                return download_Image(urls[0]);
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	return null;
	            }
	        }
	        
	        @Override
	        protected void onPreExecute() {
	        	Toast.makeText(SearchViewActivity.this, "Downloading...",Toast.LENGTH_SHORT).show();
	        }
	        
	        @Override
	        protected void onPostExecute(Bitmap result) {  
	            //ImageView iv = (ImageView) findViewById(R.id.imageView1);
	            //iv.setImageBitmap(result);
	        }
	        private Bitmap download_Image(String url) {
	            //---------------------------------------------------
	            Bitmap bm = null;
	            try {
	                URL aURL = new URL(url);
	                URLConnection conn = aURL.openConnection();
	                conn.connect();
	                InputStream is = conn.getInputStream();
	                BufferedInputStream bis = new BufferedInputStream(is);
	                bm = BitmapFactory.decodeStream(bis);
	                bis.close();
	                is.close();
	            } catch (IOException e) {
	                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
	            } 
	            return bm;
	            //---------------------------------------------------
	        }
	 }
}