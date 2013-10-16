package com.example.pron;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CityItemView extends LinearLayout{
	TextView tv;
	ImageView iv;
	
	public CityItemView(Context context) {
		super(context);
		new LinearLayout(context);
		tv = new TextView(context);
		iv = new ImageView(context);
		this.addView(tv);
		this.addView(iv);
		this.setBackgroundColor(0x5500FF00 );
		
		
    }
	
	public void setText(String s){
		tv.setText(s);
	}
	
	public void setImage(Drawable d){
		iv.setImageDrawable(d);
	}
	
	public void setBG(int c){
		this.setBackgroundColor(c);
	}
}
