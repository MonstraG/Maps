package com.arseniy.maps.activities

import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.FragmentActivity

import com.arseniy.maps.R
import com.arseniy.maps.data.MapData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.util.Objects

class PickLocationOnMapActivity : FragmentActivity(), OnMapReadyCallback {

    private var selectedLocation: LatLng = LatLng(0.0, 0.0)
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_pick_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        Objects.requireNonNull<SupportMapFragment>(mapFragment).getMapAsync(this)


        fabInit()
    }

    private fun fabInit() {
        val fab = findViewById<FloatingActionButton>(R.id.okFAB)
        fab.setOnClickListener {
            setResult(0, Intent().putExtra("pickedLat", selectedLocation.latitude)
                    .putExtra("lng", selectedLocation.longitude))
            this.finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        MapData(this)

        val callingIntent = intent
        if (callingIntent.hasExtra("startingLat") && callingIntent.hasExtra("startingLng")) {
            addMarker(googleMap, LatLng(callingIntent.getDoubleExtra("startingLat", 0.0), callingIntent.getDoubleExtra("startingLng", 0.0)), true)
        }

        googleMap.setOnMapClickListener { latLng -> addMarker(googleMap, latLng, false) }
    }

    private fun addMarker(map: GoogleMap, latLng: LatLng, zoom: Boolean) {
        marker?.remove()
        marker = map.addMarker(MarkerOptions().draggable(true).position(latLng))
        selectedLocation = latLng

        if (zoom) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0F))
        } else
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}
