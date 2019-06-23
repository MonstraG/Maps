package com.arseniy.maps.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.arseniy.maps.R
import com.arseniy.maps.data.MapData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

import java.io.IOException

class AddCityActivity : AppCompatActivity() {

    private var cityNameField: EditText? = null
    private var cityLatField: EditText? = null
    private var cityLngField: EditText? = null
    private var coder: Geocoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_city)

        cityNameField = findViewById(R.id.cityNameField)
        cityLatField = findViewById(R.id.cityLatField)
        cityLngField = findViewById(R.id.cityLngField)

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
            val cityLat = java.lang.Double.parseDouble(cityLatField!!.text.toString())
            val cityLng = java.lang.Double.parseDouble(cityLngField!!.text.toString())
            intent.putExtra("startingLat", cityLat)
            intent.putExtra("startingLng", cityLng)
        } catch (ignored: Exception) { }

        startActivityForResult(intent, 0)
    }

    @SuppressLint("SetTextI18n") //that will add commas instead of dots.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == resultCode) {
            val lat = data!!.getFloatExtra("lat", 0f)
            val lng = data.getFloatExtra("lng", 0f)
            setLatLntData(LatLng(lat.toDouble(), lng.toDouble()))
        }
    }

    private fun addCityBtnInit() {
        val addCityBtn = findViewById<Button>(R.id.addCityBtn)
        addCityBtn.setOnClickListener {
            try {
                val cityName = cityNameField!!.text.toString().trim { it <= ' ' }
                val cityLat = java.lang.Float.parseFloat(cityLatField!!.text.toString())
                val cityLng = java.lang.Float.parseFloat(cityLngField!!.text.toString())
                storeCity(cityName, cityLat, cityLng)
                this.finish()
            } catch (e: Exception) {
                setResult(-1)
                Toast.makeText(this, "Ошибка создания записи, проверьте данные.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun storeCity(name: String, lat: Float, lng: Float) {
        MapData.addCityToStorage(name, lat.toDouble(), lng.toDouble()) //sharedPrefs
        setResult(0, Intent().putExtra("cityName", name)) //list
    }

    @SuppressLint("SetTextI18n") //that will add commas instead of dots.
    private fun getLatLngFromApiBtnInit() {
        val getLatLngFromApiBtn = findViewById<Button>(R.id.getLatLngFromApiBtn)
        getLatLngFromApiBtn.setOnClickListener {
            val pos = getLocationFromAddress(cityNameField!!.text.toString().trim { it <= ' ' })
            if (pos != null) {
                setLatLntData(pos)
                callOnMapPick()
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

        Toast.makeText(this, "Нет интернет-соединения или такой город не найден.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Неудалось получить локацию - разрешение не выдано.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLatLntData(pos: LatLng?) {
        if (pos != null) {
            cityLatField!!.setText(String.format("%.4f", pos.latitude))
            cityLngField!!.setText(String.format("%.4f", pos.longitude))
        }
    }
}
