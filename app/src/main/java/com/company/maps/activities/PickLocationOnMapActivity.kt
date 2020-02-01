package com.company.maps.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.company.maps.R
import com.company.maps.activities.city.BaseCityActivity.Companion.PICKED_LAT
import com.company.maps.activities.city.BaseCityActivity.Companion.PICKED_LNG
import com.company.maps.activities.city.BaseCityActivity.Companion.STARTING_LAT
import com.company.maps.activities.city.BaseCityActivity.Companion.STARTING_LNG
import com.company.maps.logger.Logger
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
        Logger.log("Created")
        setContentView(R.layout.map_pick_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<FloatingActionButton>(R.id.okFAB).setOnClickListener { done() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        done()
    }

    private fun done() {
        setResult(0, Intent()
                .putExtra(PICKED_LAT, selectedLocation.latitude)
                .putExtra(PICKED_LNG, selectedLocation.longitude))
        this.finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        selectedLocation = LatLng(
                intent.getDoubleExtra(STARTING_LAT, 0.0),
                intent.getDoubleExtra(STARTING_LNG, 0.0)
        )

        if (selectedLocation.latitude != 0.0 && selectedLocation.longitude != 0.0) {
            moveMarker(googleMap, selectedLocation, true)
        }

        googleMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            moveMarker(googleMap, selectedLocation, false)
        }
    }

    private fun moveMarker(map: GoogleMap, latLng: LatLng, zoom: Boolean) {
        marker?.remove()
        marker = map.addMarker(MarkerOptions().draggable(true).position(latLng))

        if (zoom) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0F))
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }
}
