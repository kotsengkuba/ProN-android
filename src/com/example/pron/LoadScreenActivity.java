package com.example.pron;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class LoadScreenActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_screen);
		if((new Filer().fileExists("fourdaylive.json"))){
			File file = new File (new File(Environment.getExternalStorageDirectory().toString() + "/pron/saved_files"), "fourdaylive.json");
			if(file.lastModified()-System.currentTimeMillis()<240000000)
				gotoMain();
			else
				new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML", "fourday");

		} 
		else{
			new XMLparser().execute("http://mahar.pscigrid.gov.ph/static/kmz/four_day-forecast.KML", "fourday");
		}
	}
	
	public void gotoMain(){
		Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
	}
	
    private class XMLparser extends AsyncTask<String,Void,String>{
    	SAXParserFactory factory;
    	SAXParser saxParser;
    	FourDayXMLParser fourday_handler;
    	
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
    	    } catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    
    	    return s;
		}
    	
    	@Override
        protected void onPostExecute(String s) {
    	  Log.i("kml","End parse...");
    	  
    	  // reload displayed data
    	  //setDataFromLocation();
    	  //Toast.makeText(null, "New data downloaded.", dayIndex).show();
    	  
    	  gotoMain();
        }
    }
}
