package com.company.maps.activities.city

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.company.maps.R
import com.company.maps.activities.ListActivity.EditExtraStrings.NEW_CITY_NAME
import com.company.maps.activities.ListActivity.EditExtraStrings.OLD_CITY_NAME
import com.company.maps.data.MapData
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditCityActivity : BaseCityActivity() {
    private var initialCityName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.edit_city)
        super.onCreate(savedInstanceState)

        finishEditingFabInit()
        deleteButtonInit()

        loadCityData()
    }

    private fun loadCityData() {
        try {
            initialCityName = intent.getStringExtra("cityName") ?: throw IllegalArgumentException("City name cannot be null")
            val city = MapData.getCity(initialCityName) ?: throw IllegalArgumentException("City cannot be null")
            cityNameField!!.setText(city.getName())
            cityLatField!!.setText(city.getLatLng().latitude.toString())
            cityLngField!!.setText(city.getLatLng().longitude.toString())
            countryField!!.setText(city.getCountry())
        } catch (e: Exception) {
            Log.e("EditCityActivity", "Unable to loadCityData, err: ${e.message}, stackTrace: ${e.stackTrace}")
            this.finish()
        }
    }

    // removes city, adds new city and finishes the activity
    private fun finishEditingFabInit() {
        findViewById<FloatingActionButton>(R.id.okFAB).setOnClickListener {
            MapData.removeCityFromStorage(initialCityName)
            addsCityAndFinish()
        }
    }

    private fun deleteButtonInit() {
        findViewById<Button>(R.id.deleteCityBtn).setOnClickListener {
            MapData.removeCityFromStorage(initialCityName)
            this.finish()
        }
    }

    private fun addsCityAndFinish() {
        try {
            val cityName = getString(cityNameField)
            val cityLat = getDouble(cityLatField)
            val cityLng = getDouble(cityLngField)
            val country = getString(countryField)
            val latLng = LatLng(cityLat, cityLng) //validation
            MapData.addCityToStorage(cityName, latLng.latitude, latLng.longitude, country) //sharedPrefs
            setResult(0, Intent().putExtra(NEW_CITY_NAME, cityName).putExtra(OLD_CITY_NAME, initialCityName)) //list
            this.finish()
        } catch (e: Exception) {
            setResult(-1)
            Toast.makeText(this, R.string.toastCannotCreate, Toast.LENGTH_SHORT).show()
        }
    }
}