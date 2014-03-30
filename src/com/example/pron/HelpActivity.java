package com.example.pron;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HelpActivity extends Activity{
	
	ImageView image;
	ArrayList<Integer> images;
	int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		images = new ArrayList<Integer>();
		images.add(R.drawable.shot1);
		images.add(R.drawable.shot2);
		images.add(R.drawable.shot3);
		images.add(R.drawable.shot4);
		images.add(R.drawable.shot5);
		images.add(R.drawable.shot6);
		images.add(R.drawable.shot7);
		images.add(R.drawable.shot8);
		images.add(R.drawable.shot9);
		images.add(R.drawable.shot10);
		images.add(R.drawable.shot11);
		
		index = 0;
		
		image = (ImageView) findViewById(R.id.helpImageView); 
		setImage();
		
		image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(index==images.size()-1){
					index = 0;
				}
				else
					index++;
				
				setImage();
			}});
	}
	
	public void setImage(){
//		image.setImageResource(images.get(index));
		image.setImageDrawable(getResources().getDrawable(images.get(index)));
	}
}
