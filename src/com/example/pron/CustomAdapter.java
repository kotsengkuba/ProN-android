package com.example.pron;

import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends ArrayAdapter{

	private final Activity context;
	private final List<String> names;
	private final List<Integer> images;
	public CustomAdapter(Activity context,	List<String> names, List<Integer> images) {
		super(context, R.layout.list_item, names);
		this.context = context;
		this.names = names;
		this.images = images;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {		
		CityItemView civ = new CityItemView(context);
		civ.setText(names.get(position).toString());
		civ.setImage(context.getResources().getDrawable(images.get(position)));
		civ.setFont(Typeface.createFromAsset(context.getAssets(), "TRACK.OTF"));
		
		return civ;		
	}
	
	public void refreshView(){
		
	}
	
	public String getWeb(){
		return names.toString();
	}
	
}
