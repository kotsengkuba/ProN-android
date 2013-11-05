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
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;

public class Wheel extends View implements GestureDetector.OnGestureListener{
	ShapeDrawable wheel;
	WindowManager wm;
	String DEBUG_TAG = "touch event";
	
	private GestureDetectorCompat mDetector; 
	private VelocityTracker mVelocityTracker = null;
	
	float width,height,cx,cy,r,delta,lasty,ex,ey,rad;
	boolean snap = true, onWheelArea = false;

	public Wheel(Context context) {
	    super(context);
	}
    public Wheel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        
        mDetector = new GestureDetectorCompat(context,this);
        
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

    protected void onDraw(Canvas canvas) { 
		Log.d("paint","delta: "+delta);
		Log.d("paint","diameter: "+(2*r));
		rad = rad + (float) Math.PI*(delta/(2*r));
		if(snap){
			float deg = (rad*180/(float)Math.PI)%360;
			if(deg<0)
				deg = deg + 360;
			if((deg>0 && deg<45/2)|| deg>360-(45/2))
				rad = 0;
			else if(deg>45/2 && deg<45+(45/2))
				rad = (float)Math.PI/4;
			else if(deg>45+(45/2) && deg<90+(45/2))
				rad = (float)Math.PI/2;
			else if(deg>90+(45/2) && deg<135+(45/2))
				rad = 3*(float)Math.PI/4;
			else if(deg>135+(45/2) && deg<180+(45/2))
				rad = (float)Math.PI;
			else if(deg>180+(45/2) && deg<225+(45/2))
				rad = 5*(float)Math.PI/4;
			else if(deg>225+(45/2) && deg<270+(45/2))
				rad = 6*(float)Math.PI/4;
			else if(deg>270+(45/2) && deg<315+(45/2))
				rad = 7*(float)Math.PI/4;
		}		
		
		ex = cx + (float) (r*Math.cos(rad));
		ey = cy + (float) (r*Math.sin(rad));
		
		//canvas.drawCircle(cx, cy, r, p);
		//canvas.drawLine(cx, cy, ex, ey, p);
		int blue = 120;
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setStyle(Paint.Style.FILL_AND_STROKE); 
		p.setStrokeWidth(4.5f);
		RectF rect = new RectF(cx-r, cy-r, cx+r, cy+r);
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sunny);
		
		float nex, ney, angle;
		
		for(int i=0; i<8; i++){
			angle = (float) (i*(Math.PI/4));
			nex = cx + (float) (r*Math.cos(rad+angle));
			ney = cy + (float) (r*Math.sin(rad+angle));
			//canvas.drawLine(cx, cy, nex, ney, p);

			p.setColor(Color.rgb(50,55,blue));
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
			
			bmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
			if(ney-1>=cy-r*Math.sin(Math.PI/4) && ney+1<=cy+r*Math.sin(Math.PI/4)){
				bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*1.5), (int)(bmp.getHeight()*1.5), true);
				p.setColor(Color.BLUE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 120, p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 100, p);
			}
			else{
				p.setColor(Color.BLUE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 90, p);
				
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(nex, ney, 70, p);
			}
	        canvas.drawBitmap(bmp, nex-bmp.getWidth()/2, ney-bmp.getHeight()/2, null);
		}
		
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
            	Log.d(DEBUG_TAG,"onDown: " + event.getX() + event.getY());
            	lasty = event.getY();
            	if(event.getX() > width*0.8)
            		onWheelArea = false;
            	else
            		onWheelArea = true;
            	
                if(mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
            	delta = event.getY() - lasty;
            	lasty = event.getY();
            	snap = false;
            	if(onWheelArea)
            		invalidate();
            	
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call 
                // computeCurrentVelocity(). Then call getXVelocity() 
                // and getYVelocity() to retrieve the velocity for each pointer ID. 
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                Log.d(DEBUG_TAG, "X velocity: " + 
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker, 
                        pointerId));
                Log.d(DEBUG_TAG, "Y velocity: " + 
                        VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                        pointerId));
                break;
            case MotionEvent.ACTION_UP:
            	snap = true;
            	invalidate();
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                break;
        }
        return true;
    }
    
    @Override
    public boolean onDown(MotionEvent event) { 
        Log.d(DEBUG_TAG,"onDown: " + event.toString()); 
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, 
            float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString()); 
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }
}
