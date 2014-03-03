package com.example.pron;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class RainWheel extends View{
	ShapeDrawable wheel;
	WindowManager wm;
	String DEBUG_TAG = "touch event";

	int [] icon_array = new int[]{R.drawable.clear, R.drawable.cloudy, R.drawable.cloudy, R.drawable.rainy, R.drawable.rainy, R.drawable.rainy, R.drawable.clear, R.drawable.cloudy};
	Paint p = new Paint();
	RectF rect;
	
	public RainWheel(Context context) {
	    super(context);
	}
    public RainWheel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        
        //mDetector = new GestureDetectorCompat(context,this);
        
        Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		rect = new RectF(100,100,100,100);
			
    } 

    protected void onDraw(Canvas canvas) { 
    	//drawCircle(30, 30, 30, p);
		//canvas.drawArc(rect, 0, 180, true, p);
    }
}
