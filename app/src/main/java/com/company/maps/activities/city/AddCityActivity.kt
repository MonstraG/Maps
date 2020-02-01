package com.company.maps.activities.city

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.company.maps.R
import com.company.maps.activities.ListActivity.EditExtraStrings.CITY_NAME
import com.company.maps.data.MapData

open class AddCityActivity : BaseCityActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.add_city)
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.addCityBtn).setOnClickListener { addCityAndFinish() }
    }

    private fun addCityAndFinish() {
        val city = buildCityFromFields()
        val mapData = MapData(this)
        if (mapData.findCountryContainingCityName(city.getName()) != null) {
            Toast.makeText(this, R.string.toastCityAlreadyExists, Toast.LENGTH_LONG).show()
            return
        }
        MapData(this).addCity(city).save()
        setResult(0, Intent().putExtra(CITY_NAME, city.getName()))
        this.finish()
    }
}
