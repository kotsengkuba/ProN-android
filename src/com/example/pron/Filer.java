package com.example.pron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.os.Environment;
import android.util.Log;

public class Filer {

	public void saveFile(String s, String filename){
    	Log.d("jsoup", "Saving file..." + "TO: " + filename);
    	String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/pron/saved_files");    
        myDir.mkdirs();
        File file = new File (myDir, filename);
        if (file.exists ()) file.delete (); 
        try {
               FileOutputStream out = new FileOutputStream(file);
               out.write(s.getBytes());
               out.flush();
               out.close();

        } catch (Exception e) {
               e.printStackTrace();
        }
    }
    
    public String fileToString(String filename){
    	String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/pron/saved_files");    
        //myDir.mkdirs();
        File file = new File (myDir, filename);
        String s = "";
        StringBuffer stringBuffer = new StringBuffer();
        try {
        	FileInputStream in = new FileInputStream(file);
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        	String content = "";
        	while((content = reader.readLine()) != null){
	        	//s = s+content;
        		stringBuffer.append(content);
        	}
            in.close();

        } catch (Exception e) {
               e.printStackTrace();
        }
        Log.d("jsoup", "FILE TO STRING: "+filename+" ("+stringBuffer.length() +")");
    	return stringBuffer.toString();
    }
}
