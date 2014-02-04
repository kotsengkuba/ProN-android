package com.example.pron;

import java.util.Iterator;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class WeatherDetailDialogFragment extends DialogFragment{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		String s = "";
		JSONObject o;
		
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
		try {
			o = new JSONObject(getArguments().getString("s"));
	        s = getString(o);
	        builder.setMessage(s)
	               .setNegativeButton("Back", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               })
	        		.setTitle("Details");
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create the AlertDialog object and return it
        return builder.create();
    }
	
	public String getString(JSONObject o){
		String s = "";
		@SuppressWarnings("unchecked")
		Iterator<String> iter = o.keys();
		while (iter.hasNext()) {
	        String key = iter.next();
	        try {
	            s += key + ": " + o.getString(key) + "\n";
	        } catch (Exception e) {
	            // Something went wrong!
	        }
	    }
		return s;
	}
}
