package com.example.pron;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLES20 extends Activity{
	private GLSurfaceView myGLView;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		myGLView = new MyGLSurfaceView(this);
		setContentView(myGLView);

	}
}

class MyGLSurfaceView extends GLSurfaceView{
	public MyGLSurfaceView(Context context){
		super(context);
		
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		setRenderer(new MyRenderer());
	}
}
