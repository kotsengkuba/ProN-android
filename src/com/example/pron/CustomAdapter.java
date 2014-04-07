package com.example.pron;

import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class CustomAdapter extends ArrayAdapter{

	private final Activity context;
	private final List<String> names;
	private final List<String> temperatures;
	private final List<Integer> images;
	public CustomAdapter(Activity context,	List<String> names, List<Integer> images, List<String> temperatures) {
		super(context, R.layout.list_item, names);
		this.context = context;
		this.names = names;
		this.images = images;
		this.temperatures = temperatures;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {		
		CityItemView civ = new CityItemView(context);
		civ.setText(names.get(position));
		civ.setFont(Typeface.createFromAsset(context.getAssets(), "REGULAR.TTF"));
		civ.setFontSize(R.dimen.medbigtext);
		
		if(images.get(position) == null){
			civ.setUnsavedLocation();
			final String loc = (String) civ.tv.getText();
			ImageView iv = (ImageView) civ.add_iv;
			iv.setClickable(true);
			iv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("jsoup", "iv clicked: "+loc);
					((SearchViewActivity) context).addLocation(loc);
				}
				
			});
		}
		else{
			civ.setSavedLocation();
			civ.setImage(context.getResources().getDrawable(images.get(position)));
			civ.setTemp(temperatures.get(position));
		}
				
		return civ;		
	}
	
	
}
