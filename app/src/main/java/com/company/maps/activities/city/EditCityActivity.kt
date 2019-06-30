package com.company.maps.activities.city

import android.os.Bundle
import android.widget.Button
import com.company.maps.R
import com.company.maps.data.MapData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class EditCityActivity : AddCityActivity () {
    var cityName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_city)

        try {
            cityName = intent.getStringExtra("cityName") ?: throw IllegalArgumentException("City name cannot be null")
        } catch (e: Exception) {
            this.finish()
        }



        fabInit()
        deleteButtonInit()
    }

    private fun fabInit() {
        findViewById<FloatingActionButton>(R.id.okFAB).setOnClickListener {
            MapData.removeCityFromStorage(cityName)
            addCityAndFinish()
        }
    }

    private fun deleteButtonInit() {
        findViewById<Button>(R.id.deleteCityBtn).setOnClickListener {
            MapData.removeCityFromStorage(cityName)
            this.finish()
        }
    }
}