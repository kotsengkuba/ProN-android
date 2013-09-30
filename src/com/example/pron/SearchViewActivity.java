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
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Jasper on 9/22/13.
 */
public class SearchViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupMap();
        //setContentView(R.layout.activity_search_view);
	    //new DownloadXmlTask().execute("http://mahar.pscigrid.gov.ph/static/kmz/storm-track.KML");
	    //new DownloadImageTask().execute("http://main.noahsark.webfactional.com/static/images/rain_value_contour.png");
    }
    
    private void setupMap(){}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
            WebView myWebView = (WebView) findViewById(R.id.webView);
            myWebView.loadData(result, "text/html", null);
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
	            ImageView iv = (ImageView) findViewById(R.id.imageView1);
	            iv.setImageBitmap(result);
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