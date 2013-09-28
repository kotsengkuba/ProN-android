package com.example.pron;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

/**
 * Created by Jasper on 9/22/13.
 */
public class SearchViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}