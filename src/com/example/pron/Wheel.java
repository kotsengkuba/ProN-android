package com.example.pron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;

public class Wheel extends View{
	ShapeDrawable wheel;
	WindowManager wm;
	String DEBUG_TAG = "touch event";
	
	//private GestureDetectorCompat mDetector; 
	public VelocityTracker mVelocityTracker = null;
	
	float width,height,cx,cy,r,delta,lasty,ex,ey,rad;
	boolean snap = true, onWheelArea = false;
	int [] icon_array = new int[]{R.drawable.clear, R.drawable.cloudy, R.drawable.cloudy, R.drawable.rainy, R.drawable.rainy, R.drawable.rainy, R.drawable.clear, R.drawable.cloudy};
	Paint p = new Paint();
	int [] pie_colors_array = new int [] {Color.rgb(50,55,180),Color.rgb(50,55,160),Color.rgb(50,55,140),Color.rgb(50,55,120),Color.rgb(255,241,151),Color.rgb(255,236,95),Color.rgb(255,221,0),Color.rgb(242,201,0)};
	int [] circle_colors_array = new int [] {Color.BLUE,Color.BLUE,Color.BLUE,Color.BLUE,Color.rgb(255,147,30),Color.rgb(255,147,30),Color.rgb(255,147,30),Color.rgb(255,147,30)};

	public Wheel(Context context) {
	    super(context);
	}
    public Wheel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        
        //mDetector = new GestureDetectorCompat(context,this);
        
        Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		
		cx = 0;
		cy = height/2;
		r = width/2;
		ex = cx+r;
		ey = cy;
		rad = 0;
    } 
    
    public void setIcons(int[] arr){
    	icon_array = arr;
    }

    protected void onDraw(Canvas canvas) { 
		Log.d("paint","delta: "+delta);
		Log.d("paint","diameter: "+(2*r));
				
		
		ex = cx + (float) (r*Math.cos(rad));
		ey = cy + (float) (r*Math.sin(rad));
		
		//canvas.drawCircle(cx, cy, r, p);
		//canvas.drawLine(cx, cy, ex, ey, p);
		int blue = 120;
		p.setAntiAlias(true);
		p.setStyle(Paint.Style.FILL_AND_STROKE); 
		p.setStrokeWidth(4.5f);
		RectF rect = new RectF(cx-r, cy-r, cx+r, cy+r);
		
						
		float nex, ney, angle;
		
		for(int i=0; i<8; i++){
			angle = (float) (i*(Math.PI/4));
			nex = cx + (float) (r*Math.cos(rad+angle));
			ney = cy + (float) (r*Math.sin(rad+angle));
			//canvas.drawLine(cx, cy, nex, ney, p);

			p.setColor(pie_colors_array[i]);
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawArc(rect, (((rad+angle)*180)/(float)Math.PI)%360, 45, true, p);
			
			p.setColor(Color.WHITE);
			p.setStyle(Paint.Style.STROKE); 
			canvas.drawArc(rect, (((rad+angle)*180)/(float)Math.PI)%360, 45, true, p);
		
			blue = blue + 10;
		}
		
		
		for(int i=0; i<8; i++){
			angle = (float) (i*(Math.PI/4));
			nex = cx + (float) ((r+10)*Math.cos(rad+angle));
			ney = cy + (float) ((r+10)*Math.sin(rad+angle));
			
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), icon_array[i]);
			bmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
			
			if(!(ney-1>=cy-r*Math.sin(Math.PI/4) && ney+1<=cy+r*Math.sin(Math.PI/4))){
				bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*0.8), (int)(bmp.getHeight()*0.8), true);
				p.setColor(circle_colors_array[i]);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 90, p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 70, p);
			}
			else{
				p.setColor(circle_colors_array[i]);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 120, p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 100, p);
			}
	        canvas.drawBitmap(bmp, nex-bmp.getWidth()/2, ney-bmp.getHeight()/2, null);
		}
		
    }
}
