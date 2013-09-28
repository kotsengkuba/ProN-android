package com.example.pron;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class WeekViewActivity extends ListActivity {
    private ArrayList<HashMap> list;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        updateListView();
    }

    public void updateListView() {
        try {
            ArrayList<HashMap> logSet = new ArrayList<HashMap>();
            ArrayList<String[]> split = new ArrayList<String[]>();

            int id = R.drawable.sunny;

            split.add(new String[]{"Tomorrow", "33/28", String.valueOf(id)});
            split.add(new String[]{"Sunday", "34/29", String.valueOf(id)});

            logSet = getLogSet(split, logSet);

            ListView lview = (ListView) findViewById(android.R.id.list);
            entryAdapter adapter = new entryAdapter(this, logSet);
            lview.setAdapter(adapter);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Error", "blah");
        }
    }

    private ArrayList<HashMap> getLogSet(ArrayList<String[]> split, ArrayList<HashMap> logSet){
        for (int i=0; i<split.size(); i++) {
            HashMap map = new HashMap();
            map.put("day", split.get(i)[0]);
            map.put("temp", split.get(i)[1]);
            map.put("image", split.get(i)[2]);
            logSet.add(map);
        }
        return logSet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void goBack(View view) {
        finish();
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), SearchViewActivity.class);
        startActivity(intent);
        finish();
    }
}