package com.example.pron;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FourDayView extends LinearLayout{
	List <DayView> dayItems = new ArrayList<DayView>();
	
	public FourDayView(Context context, AttributeSet attrs){
		super(context, attrs);
		new LinearLayout(context);

		for(int i=0; i<4; i++){
			DayView dv = new DayView(context, attrs);
			dayItems.add(dv);
		}
		
		for (int i = 0; i < dayItems.size(); i++) {
			this.addView(dayItems.get(i));
		}
		
		this.setPadding(10, 30, 10, 30);
		this.setBackgroundColor(Color.parseColor("#B6D0E5"));
		this.setVisibility(View.INVISIBLE);
	}
	
	public void setDay(int index, String s){
		dayItems.get(index).setDay(s);
	}
	
	public void setTemp(int index, float temp){
		dayItems.get(index).setTemp(temp+"°");
	}
	
	public void setImage(int index, Drawable d){
		dayItems.get(index).setImage(d);
	}
	
	
	
	public class DayView extends LinearLayout{
		TextView tv,dayv;
		ImageView iv;
		public DayView(Context context, AttributeSet attrs){
			super(context, attrs);
			new LinearLayout(context);
			this.setOrientation(HORIZONTAL);
			tv = new TextView(context);
			dayv = new TextView(context);
			iv = new ImageView(context);
			tv.setText("test");
			//iv.setImageDrawable(getResources().getDrawable(R.drawable.clear));
			
			//set format
			tv.setTextSize(30);
			tv.setPadding(10, 30, 10, 10);
			tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "TRACK.OTF"));
			dayv.setTextSize(30);
			dayv.setPadding(10, 30, 10, 10);
			dayv.setTypeface(Typeface.createFromAsset(context.getAssets(), "TRACK.OTF"));
			
			this.addView(iv);
			this.addView(tv);
			this.addView(dayv);
		}
		
		public void setTemp(String s){
			tv.setText(s);
		}
		
		public void setImage(Drawable d){
			iv.setImageDrawable(d);
		}
		public void setDay(String s){
			dayv.setText(s);
		}
	}
}