package com.kolbytn.where2park;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] lats;
    private String[] longs;
    private boolean results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        results = intent.getBooleanExtra("results", false);
        lats = intent.getStringArrayExtra("lats");
        longs = intent.getStringArrayExtra("longs");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.2518435,-111.6493156), 15));

        if (results) {
            for (int i = 0; i < 5; i++) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(lats[i]),
                                Double.parseDouble(longs[i])))
                        .title(Integer.toString(i + 1)));
            }
        }
    }
}
