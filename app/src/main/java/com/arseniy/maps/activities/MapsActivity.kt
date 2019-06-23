package com.arseniy.maps.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.FragmentActivity

import com.arseniy.maps.R
import com.arseniy.maps.data.MapData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.util.Objects
import java.util.Random


class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        Objects.requireNonNull<SupportMapFragment>(mapFragment).getMapAsync(this)

        val fab = findViewById<FloatingActionButton>(R.id.openCityListFAB)
        fab.setOnClickListener { startActivity(Intent(this, ListActivity::class.java)) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        MapData(this)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(pickRandomCameraPos()))
    }

    private fun pickRandomCameraPos(): LatLng? {
        val cities = MapData.loadCityList()
        return if (cities.size > 0) {
            val randomGenerator = Random()
            val cityName = cities[randomGenerator.nextInt(cities.size)]
            MapData.getPos(cityName)
        } else
            LatLng(0.0, 0.0)
    }

    companion object {

        private var mMap: GoogleMap? = null

        fun drawCity(name: String, pos: LatLng, moveCamera: Boolean) {
            Log.d("drawCity", "Loaded city: " + name + " " + pos.latitude + " " + pos.longitude)
            mMap!!.addMarker(MarkerOptions().position(pos).title(name))
            if (moveCamera)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(pos))
        }
    }

}
