package com.company.maps.activities.city

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
import com.company.maps.activities.PickLocationOnMapActivity
import com.company.maps.data.city.City

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

open class BaseCityActivity : AppCompatActivity() {
    var cityNameField: EditText? = null
    var cityLatField: EditText? = null
    var cityLngField: EditText? = null
    var countryField: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cityNameField = findViewById(R.id.cityNameField)
        cityLatField = findViewById(R.id.cityLatField)
        cityLngField = findViewById(R.id.cityLngField)
        countryField = findViewById(R.id.countryNameField)

        //init ways to get latLng
        getLatLngFromApiBtnInit()
        findViewById<Button>(R.id.onMapBtn).setOnClickListener { onMapPick() }
        getFromCurLocBtnInit()
    }

    override fun onResume() {
        super.onResume()
        cityNameField!!.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == resultCode && intent != null) {
            setLatLntData(LatLng(
                    intent.getDoubleExtra("pickedLat", 0.0),
                    intent.getDoubleExtra("pickedLng", 0.0)
            ))
        }
    }

    private fun onMapPick() {
        val intent = Intent(this, PickLocationOnMapActivity::class.java)
        val lat = getDouble(cityLatField)
        val lng =  getDouble(cityLngField)

        if (lat != null && lng != null) {
            intent.putExtra(STARTING_LAT, getDouble(cityLatField))
            intent.putExtra(STARTING_LNG, getDouble(cityLngField))
        }

        startActivityForResult(intent, 0)
    }

    private fun getLatLngFromApiBtnInit() {
        findViewById<Button>(R.id.getLatLngFromApiBtn).setOnClickListener {
            val pos = getLocationFromAddress(getString(cityNameField))
            if (pos != null) {
                setLatLntData(pos)
                onMapPick()
            }
        }
    }

    private fun getLocationFromAddress(strAddress: String): LatLng? {
        val address = Geocoder(this).getFromLocationName(strAddress, 1)
        if (address == null || address.size == 0) {
            Toast.makeText(this, R.string.toastNoNetworkOrNotFound, Toast.LENGTH_SHORT).show()
            return null
        }

        val location = address[0]
        return LatLng(location.latitude, location.longitude)
    }

    private fun getFromCurLocBtnInit() {
        findViewById<Button>(R.id.fromCurLocBtn).setOnClickListener {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }
            mFusedLocationClient.lastLocation.addOnSuccessListener { result -> setLatLntData(LatLng(result.latitude, result.longitude)) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { result -> setLatLntData(LatLng(result.latitude, result.longitude)) }
            }
        } else {
            Toast.makeText(this, R.string.toastGeoNoPermission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLatLntData(pos: LatLng) {
        cityLatField!!.setText(pos.latitude.format())
        cityLngField!!.setText(pos.longitude.format())
    }

    fun buildCityFromFields(): City? {
        val cityName = getString(cityNameField)
        val cityLat = getDouble(cityLatField)
        val cityLng = getDouble(cityLngField)
        val country = getString(countryField)

        if (cityName == "" || cityLat == null || cityLng == null) {
            return null
        }

        return City(cityName, LatLng(cityLat, cityLng), country)
    }

    companion object {
        const val STARTING_LAT = "startingLat"
        const val STARTING_LNG = "startingLng"
        const val PICKED_LAT = "pickedLat"
        const val PICKED_LNG = "pickedLng"

        fun getDouble(field: EditText?): Double? {
            return field!!.text.toString().toDoubleOrNull()
        }

        fun getString(field: EditText?): String {
            return field!!.text.toString().trim { it <= ' ' }
        }

        fun Double.format(): String {
            return String.format("%.4f", this)
        }
    }
}
