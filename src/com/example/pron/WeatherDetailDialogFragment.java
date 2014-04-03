package com.example.pron;

import java.util.Iterator;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
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
	Typeface font;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		String s = "";
		JSONObject o;
		font = Typeface.createFromAsset(this.getActivity().getAssets(), "REGULAR.TTF");
		
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
			String units="", label, value;
			for(int i=2; i<o.length();i++){
				TableRow rowItem = new TableRow(this.getActivity());
				value = o.getString(o.names().getString(i));
				label = o.names().getString(i);
				
				if(label.equalsIgnoreCase("Real Feel"))
					units = "�C";
				else if(label.equalsIgnoreCase("Windspeed"))
					units = "m/s";
				else if(label.equalsIgnoreCase("Rainfall"))
					units = "mm/hr";
				else if(label.equalsIgnoreCase("Temperature"))
					units = "�C";
				else if(label.equalsIgnoreCase("Wind Direction"))
					units = "";
				else if(label.equalsIgnoreCase("Relative Humidity"))
					units = "%";
				
				TextView ValueTextViewItem = new TextView(this.getActivity());
//				ValueTextViewItem.setTypeface(font);
				ValueTextViewItem.setPadding(20, 10, 20, 10);
				ValueTextViewItem.setGravity(Gravity.CENTER);
				ValueTextViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.xstext));
				ValueTextViewItem.setTextColor(Color.parseColor("#3F8FD2"));
				
				TextView LabelTimeTextViewItem = new TextView(this.getActivity());
//				LabelTimeTextViewItem.setTypeface(font);
				LabelTimeTextViewItem.setPadding(10, 10, 10, 10);
				LabelTimeTextViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.xstext));
				LabelTimeTextViewItem.setTextColor(Color.parseColor("#3F8FD2"));
				
				ValueTextViewItem.setText(value+" "+units);
				rowItem.addView(ValueTextViewItem);
				
				LabelTimeTextViewItem.setText(label);
				rowItem.addView(LabelTimeTextViewItem);
			
				tl.addView(rowItem);
			}
		} catch(Exception e){}
	}
}
