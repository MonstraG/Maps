package com.arseniy.maps.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.arseniy.maps.data.MapData;
import com.arseniy.maps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class PickLocationOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;

    private float lat;
    private float lng;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_pick_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);


        fabInit();
    }

    private void fabInit() {
        FloatingActionButton fab = findViewById(R.id.okFAB);
        fab.setOnClickListener(view -> {
            setResult(0, new Intent().putExtra("lat", lat).putExtra("lng", lng));
            this.finish();
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new MapData(this);

        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra("startingLat") && callingIntent.hasExtra("startingLng")) {
            addMarker(new LatLng(callingIntent.getDoubleExtra("startingLat", 0.0), callingIntent.getDoubleExtra("startingLng", 0.0)), true);
        }

        mMap.setOnMapClickListener(latLng -> addMarker(latLng, false));
    }

    private void addMarker(LatLng latLng, Boolean zoom) {
        if (marker != null)
            marker.remove();
        marker = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
        lat = (float) latLng.latitude;
        lng = (float) latLng.longitude;

        if (zoom) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
        } else
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
