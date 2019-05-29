package com.arseniy.maps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.arseniy.maps.data.MapData;
import com.arseniy.maps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.openCityListFAB);
        fab.setOnClickListener(view -> startActivity(new Intent(this, ListActivity.class)));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new MapData(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pickRandomCameraPos()));
    }

    public static void drawCity(String name, LatLng pos, Boolean moveCamera) {
        Log.d("drawCity", "Loaded city: " + name + " " + pos.latitude + " " + pos.longitude);
        mMap.addMarker(new MarkerOptions().position(pos).title(name));
        if (moveCamera)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    private LatLng pickRandomCameraPos() {
        ArrayList<String> cities = MapData.loadCityList();
        if (cities.size() > 0) {
            Random randomGenerator = new Random();
            String cityName = cities.get(randomGenerator.nextInt(cities.size()));
            return MapData.getPos(cityName);
        } else
            return new LatLng(0.0, 0.0);
    }

}
