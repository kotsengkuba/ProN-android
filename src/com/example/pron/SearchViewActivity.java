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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SearchViewActivity extends Activity {
	List citiesList = new ArrayList();
	List tempList = new ArrayList();
	LinearLayout ll;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        
        citiesList.add("Quezon City");
        citiesList.add("Manila");
        citiesList.add("Cebu City");
        
        tempList.add(30);
        tempList.add(31);
        tempList.add(32);
        
        ll = (LinearLayout)findViewById(R.id.citiesLinearLayout);
        init_list();
        
        //setContentView(R.layout.activity_search_view);
	    //new DownloadXmlTask().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML");
	    //new DownloadImageTask().execute("http://main.noahsark.webfactional.com/static/images/rain_value_contour.png");
    }
    
    public void init_list(){
    	for (int i = 0; i < citiesList.size(); i++) {
    		//TextView tv = new TextView(this);
    		//tv.setText(citiesList.get(i).toString());
    		//ll.addView(tv);
    		
    		CityItemView civ = new CityItemView(this);
    		civ.setText(citiesList.get(i).toString());
    		civ.setTemp(tempList.get(i).toString()+"°");
    		civ.setImage(getResources().getDrawable(R.drawable.clear));
    		civ.setFont(Typeface.createFromAsset(getAssets(), "TRACK.OTF"));
    		ll.addView(civ);
    		
    		civ.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			        startActivity(intent);
				}
			});
    	}
    	
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