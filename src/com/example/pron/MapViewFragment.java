package com.example.pron;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewFragment extends Fragment {
	private GoogleMap mMap;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "Map Activity",Toast.LENGTH_SHORT).show();
        View view = inflater.inflate(R.layout.fragment_map, null, false);
        //setContentView(R.layout.activity_map);
        
        Log.d("OUT", "MapViewFragment");
        
        mMap = getMap();
        setUpMapIfNeeded();
        mMap.addMarker(new MarkerOptions()
        .position(new LatLng(0, 0))
        .title("Hello world"));
        
        return view;
    }
    
    private GoogleMap getMap() {
		// TODO Auto-generated method stub
		return null;
	}

	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapViewFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
    }
}
