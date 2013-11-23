package com.example.pron;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityItemView extends LinearLayout{
	TextView tv, temptv;
	ImageView iv;
	
	public CityItemView(Context context) {
		super(context);
		new LinearLayout(context);
		tv = new TextView(context);
		iv = new ImageView(context);
		temptv = new TextView(context);
		
		//set format
		tv.setTextSize(30);
		tv.setPadding(10, 30, 10, 10);
		temptv.setTextSize(30);	
		temptv.setPadding(10, 30, 10, 10);
		
		this.addView(iv);
		this.addView(temptv);
		this.addView(tv);		
    }
	
	public void setText(String s){
		tv.setText(s);
	}
	
	public void setTemp(String s){
		temptv.setText(s);
	}
	
	public void setImage(Drawable d){
		iv.setImageDrawable(d);
	}
	
	public void setBG(int c){
		this.setBackgroundColor(c);
	}
	public void setFont(Typeface font){
		tv.setTypeface(font);
		temptv.setTypeface(font);
	}
	
	public String getText(){
		return (String) tv.getText();
	}
}
