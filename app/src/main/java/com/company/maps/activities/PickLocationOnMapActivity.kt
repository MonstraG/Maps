package com.company.maps.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.company.maps.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

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

        findViewById<FloatingActionButton>(R.id.okFAB).setOnClickListener { done() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        done()
    }

    private fun done() {
        setResult(0, Intent()
                .putExtra("pickedLat", selectedLocation.latitude)
                .putExtra("pickedLng", selectedLocation.longitude))
        this.finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (intent.hasExtra("startingLat") && intent.hasExtra("startingLng")) {
            moveMarker(googleMap, LatLng(
                    intent.getDoubleExtra("startingLat", 0.0),
                    intent.getDoubleExtra("startingLng", 0.0)
            ), true)
        }

        googleMap.setOnMapClickListener { latLng -> moveMarker(googleMap, latLng, false) }
    }

    private fun moveMarker(map: GoogleMap, latLng: LatLng, zoom: Boolean) {
        marker?.remove()
        marker = map.addMarker(MarkerOptions().draggable(true).position(latLng))
        selectedLocation = latLng

        if (zoom) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0F))
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }
}
