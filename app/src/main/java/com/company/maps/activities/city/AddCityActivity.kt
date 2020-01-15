package com.company.maps.activities.city

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.company.maps.R
import com.company.maps.activities.ListActivity.EditExtraStrings.CITY_NAME
import com.company.maps.data.MapData
import com.google.android.gms.maps.model.LatLng

open class AddCityActivity : BaseCityActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.add_city)
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.addCityBtn).setOnClickListener { addCityAndFinish() }
    }

    private fun addCityAndFinish() {
        try {
            val cityName = getString(cityNameField)
            val cityLat = getDouble(cityLatField)
            val cityLng = getDouble(cityLngField)
            val country = getString(countryField)
            val latLng = LatLng(cityLat, cityLng) //validation
            MapData.addCityToStorage(cityName, latLng.latitude, latLng.longitude, country) //sharedPrefs
            setResult(0, Intent().putExtra(CITY_NAME, cityName)) //list
            this.finish()
        } catch (e: Exception) {
            setResult(-1)
            Toast.makeText(this, R.string.toastCannotCreate, Toast.LENGTH_SHORT).show()
        }
    }
}
