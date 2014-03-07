package com.example.pron;

import java.util.Iterator;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WeatherDetailDialogFragment extends DialogFragment{
	
	AlertDialog.Builder builder;
	TableLayout tl;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		String s = "";
		JSONObject o;
		
		// Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_details, null);
        builder.setView(view);
        builder.setTitle("Details");
        builder .setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }});
        
        tl = (TableLayout) view.findViewById(R.id.detailsTableLayout);
        tl.setPadding(20, 10, 10, 10);
        
		try {
			o = new JSONObject(getArguments().getString("s"));
//	        s = getString(o);
//	        builder.setMessage(s)
//	               .setNegativeButton("Back", new DialogInterface.OnClickListener() {
//	                   public void onClick(DialogInterface dialog, int id) {
//	                       // User cancelled the dialog
//	                   }
//	               })
//	        		.setTitle("Details");
	        setTable(o);
	        
	        
	        
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
	
	public String getWeatherIcon(String img){
		return "";
	}
	
	public void setTable(JSONObject o){
		try{
			for(int i=2; i<o.length();i++){
				TableRow rowItem = new TableRow(this.getActivity());
				
				TextView RainTextViewItem = new TextView(this.getActivity());
				//RainTextViewItem.setTypeface(font);
				RainTextViewItem.setPadding(10, 10, 10, 10);
				RainTextViewItem.setGravity(Gravity.CENTER);
				RainTextViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.xstext));
				RainTextViewItem.setTextColor(Color.parseColor("#3F8FD2"));
				RainTextViewItem.setText(o.getString(o.names().getString(i)));
				rowItem.addView(RainTextViewItem);
				
				TextView RainTimeTextViewItem = new TextView(this.getActivity());
				//RainTimeTextViewItem.setTypeface(font);
				RainTimeTextViewItem.setPadding(10, 10, 10, 10);
				RainTimeTextViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.xstext));
				RainTimeTextViewItem.setTextColor(Color.parseColor("#3F8FD2"));
				RainTimeTextViewItem.setText(o.names().getString(i));
				rowItem.addView(RainTimeTextViewItem);
			
				tl.addView(rowItem);
			}
		} catch(Exception e){}
	}
}
