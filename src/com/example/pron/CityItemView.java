package com.example.pron;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityItemView extends LinearLayout{
	TextView tv, temptv;
	ImageView weather_iv, add_iv;
	boolean addme = false;
	
	public CityItemView(Context context) {
		super(context);
		new LinearLayout(context);
		tv = new TextView(context);
		weather_iv = new ImageView(context);
		add_iv = new ImageView(context);
		temptv = new TextView(context);
		
		//set format
		tv.setTextSize(30);
		tv.setPadding(10, 30, 10, 10);
		temptv.setTextSize(30);	
		temptv.setPadding(10, 30, 10, 10);	
    }
	
	public void setSavedLocation(){
		this.addView(weather_iv);
		this.addView(temptv);
		this.addView(tv);
	}
	
	public void setUnsavedLocation(){
		this.addView(add_iv);
		this.addView(tv);	
		
		add_iv.setImageDrawable(getResources().getDrawable(R.drawable.plus_icon2));
		add_iv.setPadding(20, 40, 10, 10);
		add_iv.setTag("add_me");
	}
	
	public void setText(String s){
		tv.setText(s);
	}
	
	public void setTemp(String s){
		temptv.setText(s);
	}
	
	public void setImage(Drawable d){
		weather_iv.setImageDrawable(d);
	}
	
	public void setAddMe(boolean b){
		addme = b;
	}
	
	public void setBG(int c){
		this.setBackgroundColor(c);
	}
	
	public void setFont(Typeface font){
		tv.setTypeface(font);
		temptv.setTypeface(font);
	}
	
	public void setFontSize(int dimen){
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(dimen));
		temptv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(dimen));
	}
	
	public String getText(){
		return (String) tv.getText();
	}
	
	public boolean getAddMe(){
		return addme;
	}
	
	public ImageView getAddMeView(){
		return add_iv;
	}
}
