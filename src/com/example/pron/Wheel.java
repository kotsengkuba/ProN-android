package com.example.pron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Wheel extends View{
	ShapeDrawable wheel;
	WindowManager wm;
	String DEBUG_TAG = "touch event";
	
	//private GestureDetectorCompat mDetector; 
	public VelocityTracker mVelocityTracker = null;
	
	float width,height,cx,cy,r,delta,lasty,ex,ey,rad,circle_rad, rad1,rad2,rad3,rad4;
	float nex, ney, angle;
	boolean snap = true, onWheelArea = false;
	int [] icon_array = new int[]{R.drawable.clear, R.drawable.cloudy, R.drawable.cloudy, R.drawable.rainy, R.drawable.rainy, R.drawable.rainy, R.drawable.clear, R.drawable.cloudy};
	Paint p = new Paint();
	Paint textp = new Paint();
	int [] pie_colors_array = new int [] {Color.rgb(50,55,140),Color.rgb(50,55,120),Color.rgb(255,241,151),Color.rgb(255,236,95),Color.rgb(255,221,0),Color.rgb(242,201,0),Color.rgb(50,55,180),Color.rgb(50,55,160)};
	int [] circle_colors_array = new int [] {Color.BLUE,Color.BLUE,Color.rgb(255,147,30),Color.rgb(255,147,30),Color.rgb(255,147,30),Color.rgb(255,147,30),Color.BLUE,Color.BLUE};
	RectF rect;
	Bitmap [] bitmap_array;
	String [] time_array;
	int offset;
	
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
		Log.d("OUT", "width: "+width+" height: "+height);
		circle_rad = width/5;
		bitmap_array = new Bitmap [8]; 		
		time_array = new String [8];
		cx = 0;
		cy = (float) ((height*0.8)/2);
		r = width/2;
		ex = cx+r;
		ey = cy;
		rad = 0;
		//offset = 0;
		
		textp.setTextSize(getResources().getDimension(R.dimen.smalltext));
		textp.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "REGULAR.TTF"));
		textp.setColor(Color.argb(70, 0, 0, 0));
		
		rad1 = (float) (circle_rad*0.52);
		rad2 = (float) (circle_rad*0.41);
		rad3 = (float) (circle_rad*0.62);
		rad4 = (float) (circle_rad*0.52);
		
		rect = new RectF(cx-r, cy-r, cx+r, cy+r);
			
    } 
    
    public void setIcons(int[] arr){
    	icon_array = arr;
    	
    	for(int i=0; i<8; i++){
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), icon_array[i]);
			bmp = Bitmap.createScaledBitmap(bmp, (int)circle_rad, (int)circle_rad, true);
			bitmap_array[i] = bmp;
		}
    }
    
    public void setTimeStrings(String [] arr){
    	for(int i=0; i<arr.length; i++){
			if(arr[i]!=null)
				time_array[i] = arr[i].substring(0, arr[i].length()-3);
			else
				time_array[i] = "";
			
			Log.d("OUT", "time array "+i+": "+arr[i]);
		}
    	
    }
    
    public void setOffset(int o){
    	offset = o;
    }
    
    public void reset(){
    	rad = 0;
    }

    protected void onDraw(Canvas canvas) { 
		ex = cx + (float) (r*Math.cos(rad));
		ey = cy + (float) (r*Math.sin(rad));
		
		p.setAntiAlias(true);
		p.setStyle(Paint.Style.FILL_AND_STROKE); 
		p.setStrokeWidth(4.5f);
		
//		for(int i=0; i<8; i++){
//			angle = (float) (i*(Math.PI/4));
//			nex = cx + (float) (r*Math.cos(rad+angle));
//			ney = cy + (float) (r*Math.sin(rad+angle));
//			//canvas.drawLine(cx, cy, nex, ney, p);
//
//			p.setColor(pie_colors_array[i]);
//			p.setStyle(Paint.Style.FILL);
//			canvas.drawArc(rect, (((rad+angle)*180)/(float)Math.PI)%360, 45, true, p);
//			
//			//p.setColor(Color.WHITE);
//			//p.setStyle(Paint.Style.STROKE); 
//			//canvas.drawArc(rect, (((rad+angle)*180)/(float)Math.PI)%360, 45, true, p);
//		
//			//blue = blue + 10;
//		}
		
		
		// draw wheel colors
		int i = offset;
		int ctr = 0;
		while(i<8){
			angle = (float) (ctr*(Math.PI/4));
			
			nex = cx + (float) (r*Math.cos(rad+angle));
			ney = cy + (float) (r*Math.sin(rad+angle));

			p.setColor(pie_colors_array[i]);
			p.setStyle(Paint.Style.FILL);
			canvas.drawArc(rect, (((rad+angle)*180)/(float)Math.PI)%360, 45, true, p);
			
			if(!snap && (ney<cy-30 || ney>cy+30))
				canvas.drawText(time_array[i], cx + (float) ((r+150)*Math.cos(rad+angle)), cy + (float) ((r+150)*Math.sin(rad+angle)), textp);
			
			i++;
			ctr++;
		}  
		
		i = 0;
		ctr = ctr%8;
		while(i<offset){
			angle = (float) (ctr*(Math.PI/4));
			
			nex = cx + (float) (r*Math.cos(rad+angle));
			ney = cy + (float) (r*Math.sin(rad+angle));

			p.setColor(pie_colors_array[i]);
			p.setStyle(Paint.Style.FILL);
			canvas.drawArc(rect, (((rad+angle)*180)/(float)Math.PI)%360, 45, true, p);
			
			if(!snap && (ney<cy-30 || ney>cy+30))
				canvas.drawText(time_array[i], cx + (float) ((r+150)*Math.cos(rad+angle)), cy + (float) ((r+150)*Math.sin(rad+angle)), textp);
			
//			nex = cx + (float) ((r+10)*Math.cos(rad+angle));
//			ney = cy + (float) ((r+10)*Math.sin(rad+angle));
			
			i++;
			ctr = (ctr+1)%8;
		}
		
		// draw icons
		i = offset;
		ctr = 0;
		while(i<8){
			angle = (float) (ctr*(Math.PI/4));
			
			nex = cx + (float) ((r+10)*Math.cos(rad+angle));
			ney = cy + (float) ((r+10)*Math.sin(rad+angle));
			
			if(!(ney-1>=cy-r*Math.sin(Math.PI/4) && ney+1<=cy+r*Math.sin(Math.PI/4))){
				Bitmap bmp = Bitmap.createScaledBitmap(bitmap_array[i], (int)(bitmap_array[i].getWidth()*0.8), (int)(bitmap_array[i].getHeight()*0.8), true);
				p.setColor(circle_colors_array[i]);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad1), p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad2), p);
				
				canvas.drawBitmap(bmp, nex-bmp.getWidth()/2, ney-bmp.getHeight()/2, null);
	
			}
			else{
				p.setColor(circle_colors_array[i]);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad3), p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad4), p);
				
		        canvas.drawBitmap(bitmap_array[i], nex-bitmap_array[i].getWidth()/2, ney-bitmap_array[i].getHeight()/2, null);
	
			}
			
			i++;
			ctr++;
		}  
		
		i = 0;
		ctr = ctr%8;
		while(i<offset){
			angle = (float) (ctr*(Math.PI/4));
			
			nex = cx + (float) ((r+10)*Math.cos(rad+angle));
			ney = cy + (float) ((r+10)*Math.sin(rad+angle));

			if(!(ney-1>=cy-r*Math.sin(Math.PI/4) && ney+1<=cy+r*Math.sin(Math.PI/4))){
				Bitmap bmp = Bitmap.createScaledBitmap(bitmap_array[i], (int)(bitmap_array[i].getWidth()*0.8), (int)(bitmap_array[i].getHeight()*0.8), true);
				p.setColor(circle_colors_array[i]);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad1), p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad2), p);
				
				canvas.drawBitmap(bmp, nex-bmp.getWidth()/2, ney-bmp.getHeight()/2, null);
				
			}
			else{
				p.setColor(circle_colors_array[i]);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad3), p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, (int)(rad4), p);
				
		        canvas.drawBitmap(bitmap_array[i], nex-bitmap_array[i].getWidth()/2, ney-bitmap_array[i].getHeight()/2, null);

			}
			
			i++;
			ctr = (ctr+1)%8;
		}
		
//		for(int i=0; i<8; i++){
//			angle = (float) (i*(Math.PI/4));
//			nex = cx + (float) ((r+10)*Math.cos(rad+angle));
//			ney = cy + (float) ((r+10)*Math.sin(rad+angle));
//			
//			//Bitmap bmp = BitmapFactory.decodeResource(getResources(), icon_array[i]);
//			///bmp = Bitmap.createScaledBitmap(bmp, 200, 200, true);
//			
//			if(!(ney-1>=cy-r*Math.sin(Math.PI/4) && ney+1<=cy+r*Math.sin(Math.PI/4))){
//				Bitmap bmp = Bitmap.createScaledBitmap(bitmap_array[i], (int)(bitmap_array[i].getWidth()*0.8), (int)(bitmap_array[i].getHeight()*0.8), true);
//				p.setColor(circle_colors_array[i]);
//				p.setStyle(Paint.Style.FILL_AND_STROKE);
//				canvas.drawCircle(nex, ney, 90, p);
//				
//				p.setColor(Color.WHITE);
//				p.setStyle(Paint.Style.FILL_AND_STROKE);
//				canvas.drawCircle(nex, ney, 70, p);
//				
//				canvas.drawBitmap(bmp, nex-bmp.getWidth()/2, ney-bmp.getHeight()/2, null);
//
//			}
//			else{
//				p.setColor(circle_colors_array[i]);
//				p.setStyle(Paint.Style.FILL_AND_STROKE);
//				canvas.drawCircle(nex, ney, 120, p);
//				
//				p.setColor(Color.WHITE);
//				p.setStyle(Paint.Style.FILL_AND_STROKE);
//				canvas.drawCircle(nex, ney, 100, p);
//				
//		        canvas.drawBitmap(bitmap_array[i], nex-bitmap_array[i].getWidth()/2, ney-bitmap_array[i].getHeight()/2, null);
//
//			}
//		}
		
    }
}
