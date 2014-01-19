package com.example.pron;

import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class GestureListener extends GestureDetector.SimpleOnGestureListener {

     private static final int SWIPE_MIN_DISTANCE = 60;
     private static final int SWIPE_THRESHOLD_VELOCITY = 100;

	 @Override
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
           onRightToLeft();
           return true;
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
           onLeftToRight();
           return true;
        }
        if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
           onBottomToTop();
           return true;
        } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
           onTopToBottom();
           return true;
        }
		 flinged();
    	 return true;
     }

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public abstract void flinged();
	public abstract void onRightToLeft();
	public abstract void onLeftToRight();
	public abstract void onTopToBottom();
	public abstract void onBottomToTop();
}