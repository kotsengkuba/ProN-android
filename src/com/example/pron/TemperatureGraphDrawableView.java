package com.example.pron;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

public class TemperatureGraphDrawableView extends View{
	int[] bar_values;
	ShapeDrawable[] bars;
	
	public TemperatureGraphDrawableView(Context context) {
	    super(context);
	}
    public TemperatureGraphDrawableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        bar_values = new int[]{30,32,33,34,35,30,29,28};
        bars = new ShapeDrawable[bar_values.length];
        for(int i=0; i<bar_values.length; i++){
        	bars[i] = new ShapeDrawable(new RectShape());
        }
        
    } 

    protected void onDraw(Canvas canvas) { 
    	float mul = this.getHeight()/40;
    	int padding = 5;
    	int width = this.getWidth()/bars.length;
    	int x = 0;
    	int y = this.getHeight();
    	
    	
    	for(int i = 0; i<bars.length; i++){
    		bars[i].getPaint().setColor(0xff66A1D2);
    		bars[i].setBounds(x+padding, y-(int)(bar_values[i]*mul), x + width - padding, y);
    		bars[i].draw(canvas);
    		x+=width;
    	}
    }

}


