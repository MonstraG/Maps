package com.company.maps.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.company.maps.R

import com.company.maps.data.MapData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

import java.io.IOException

class AddCityActivity : AppCompatActivity() {

    private var cityNameField: EditText? = null
    private var cityLatField: EditText? = null
    private var cityLngField: EditText? = null
    private var countryField: EditText? = null
    private var coder: Geocoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_city)

        cityNameField = findViewById(R.id.cityNameField)
        cityLatField = findViewById(R.id.cityLatField)
        cityLngField = findViewById(R.id.cityLngField)
        countryField = findViewById(R.id.countryNameField)

        coder = Geocoder(this)

        onMapBtnInit()
        addCityBtnInit()
        getLatLngFromApiBtnInit()
        getFromCurLocBtnInit()
    }

    override fun onResume() {
        super.onResume()
        cityNameField!!.requestFocus()
    }

    private fun onMapBtnInit() {
        val onMapBtn = findViewById<Button>(R.id.onMapBtn)
        onMapBtn.setOnClickListener { callOnMapPick() }
    }

    private fun callOnMapPick() {
        val intent = Intent(this, PickLocationOnMapActivity::class.java)
        try {
            intent.putExtra("startingLat", cityLatField!!.text.toString().toDouble())
            intent.putExtra("startingLng", cityLngField!!.text.toString().toDouble())
        } catch (ignored: Exception) { }

        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == resultCode && intent != null) {
            setLatLntData(LatLng(
                    intent.getDoubleExtra("lat", 0.0),
                    intent.getDoubleExtra("lng", 0.0)
            ))
        }
    }

    private fun addCityBtnInit() {
        val addCityBtn = findViewById<Button>(R.id.addCityBtn)
        addCityBtn.setOnClickListener {
            try {
                val cityName = cityNameField!!.text.toString().trim { it <= ' ' }
                val cityLat = cityLatField!!.text.toString().toDouble()
                val cityLng = cityLngField!!.text.toString().toDouble()
                val country = countryField!!.text.toString()
                storeCity(cityName, cityLat, cityLng, country)
                this.finish()
            } catch (e: Exception) {
                setResult(-1)
                Toast.makeText(this, R.string.toastCannotCreate, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun storeCity(name: String, lat: Double, lng: Double, country: String) {
        MapData.addCityToStorage(name, lat, lng, country) //sharedPrefs
        setResult(0, Intent().putExtra("cityName", name)) //list
    }

    private fun getLatLngFromApiBtnInit() {
        val getLatLngFromApiBtn = findViewById<Button>(R.id.getLatLngFromApiBtn)
        getLatLngFromApiBtn.setOnClickListener {
            val pos = getLocationFromAddress(cityNameField!!.text.toString().trim { it <= ' ' })
            if (pos != null) {
                setLatLntData(pos)
                callOnMapPick()
            } else {
                Toast.makeText(this, R.string.toastApiError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocationFromAddress(strAddress: String): LatLng? {
        try {
            val address = coder!!.getFromLocationName(strAddress, 1)
            if (address != null && address.size > 0) {
                val location = address[0]
                return LatLng(location.latitude, location.longitude)
            }
        } catch (ignored: IOException) { }

        Toast.makeText(this, R.string.toastNoNetworkOrNotFound, Toast.LENGTH_SHORT).show()
        return null
    }

    private fun getFromCurLocBtnInit() {
        val fromCurLocBtn = findViewById<Button>(R.id.fromCurLocBtn)
        fromCurLocBtn.setOnClickListener {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }
            mFusedLocationClient.lastLocation.addOnSuccessListener { result -> setLatLntData(LatLng(result.latitude, result.longitude)) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { result -> setLatLntData(LatLng(result.latitude, result.longitude)) }
            }
        } else {
            Toast.makeText(this,  R.string.toastGeoNoPermission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLatLntData(pos: LatLng) {
        cityLatField!!.setText(String.format("%.4f", pos.latitude))
        cityLngField!!.setText(String.format("%.4f", pos.longitude))
    }
}
