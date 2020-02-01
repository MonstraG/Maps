package com.company.maps.activities.city

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.company.maps.R
import com.company.maps.activities.ListActivity.IntentExtraStrings.CITY_NAME
import com.company.maps.activities.ListActivity.IntentExtraStrings.NEW_CITY_NAME
import com.company.maps.activities.ListActivity.IntentExtraStrings.OLD_CITY_NAME
import com.company.maps.data.MapData
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditCityActivity : BaseCityActivity() {
    private var initialCityName: String = ""
    private var mapData: MapData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.edit_city)
        super.onCreate(savedInstanceState)

        mapData = MapData(this)

        finishEditingFabInit()
        deleteButtonInit()

        loadCityData()
    }

    private fun loadCityData() {
        initialCityName = intent.getStringExtra(CITY_NAME)
                ?: throw IllegalArgumentException("City name cannot be null")
        val city = mapData!!.getCityList().find { it.getName() == initialCityName }

        if (city == null) {
            Log.e("EditCityActivity", "Unable to loadCityData, city with this name not found")
            this.finish()
        } else {
            cityNameField!!.setText(city.getName())
            cityLatField!!.setText(city.getLatLng().latitude.toString())
            cityLngField!!.setText(city.getLatLng().longitude.toString())
            countryField!!.setText(city.getCountry())
        }
    }

    // removes city, adds new city and finishes the activity
    private fun finishEditingFabInit() {
        findViewById<FloatingActionButton>(R.id.okFAB).setOnClickListener {
            val newCity = buildCityFromFields()
            if (newCity == null) {
                Toast.makeText(this, R.string.toastCannotCreateCity, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mapData!!.removeCity(initialCityName)
            mapData!!.addCity(newCity)
            setResult(0, Intent().putExtra(NEW_CITY_NAME, newCity.getName()).putExtra(OLD_CITY_NAME, initialCityName)) //list
            mapData!!.save()
            this.finish()
        }
    }

    private fun deleteButtonInit() {
        findViewById<Button>(R.id.deleteCityBtn).setOnClickListener {
            mapData!!.removeCity(initialCityName)
            mapData!!.save()
            this.finish()
        }
    }
}