package com.example.pron;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

public class CustomDrawableView extends View {
    private ShapeDrawable mDrawable;

    public CustomDrawableView(Context context) {
    super(context);
    
    }
    public CustomDrawableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mDrawable = new ShapeDrawable(new RectShape());
    } 

    protected void onDraw(Canvas canvas) {
    	int x = 10;
    	int y = 10;
        int width = this.getWidth()/2;
        int height = this.getHeight()/2;
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(x, y, x + width, y + height);
   
        mDrawable.draw(canvas);
    }
}
