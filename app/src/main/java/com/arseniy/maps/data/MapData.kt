package com.arseniy.maps.data

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.util.Log

import com.arseniy.maps.activities.MapsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

import java.util.ArrayList
import java.util.HashSet
import java.util.Objects

import android.preference.PreferenceManager.getDefaultSharedPreferences

class MapData(context: Context) {

    init {
        val appFlags = context.applicationInfo.flags
        val isDebug = appFlags and ApplicationInfo.FLAG_DEBUGGABLE != 0

        mapData = getDefaultSharedPreferences(context)
        gson = Gson()

        if (isDebug)
            debugPrintAllData()

        transferStorageToJson()

        if (isDebug)
            debugPrintAllData()

        loadAndDraw()
    }

    private fun transferStorageToJson() {
        val cityNames = loadCityList()
        if (cityNames.size > 0) {
            if (mapData.contains(cityNames[0] + "lat") || mapData.contains(cityNames[0] + "lng")) {
                cityNames.forEach { city ->
                    val pos = getPosOldWay(city)
                    removeCityFromStorageOld(city)
                    addCityToStorage(city, pos)
                }
            }
        }
    }

    private fun debugPrintAllData() {
        Log.d("data", "ALL AVAILABLE DATA")
        mapData.all.entries.forEach { entry -> Log.d("data", entry.key + " " + entry.value) }
        Log.d("data", "END OF AVAILABLE DATA")

    }

    private fun loadAndDraw() {
        val map = mapData.getStringSet("cityNames", HashSet())
        map?.forEach { name -> MapsActivity.drawCity(name, getPos(name)!!, false) }
    }

    companion object {
        private var mapDataEditor: SharedPreferences.Editor? = null
        private lateinit var mapData: SharedPreferences
        private lateinit var gson: Gson

        private fun getPosOldWay(name: String): LatLng {
            return LatLng(mapData.getFloat(name + "lat", 0f).toDouble(), mapData.getFloat(name + "lng", 0f).toDouble())
        }

        private fun removeCityFromStorageOld(name: String) {
            try {
                mapDataEditor = mapData.edit()
                mapDataEditor!!.remove(name + "lat")
                mapDataEditor!!.remove(name + "lng")
                val cityNames = loadCityList()
                cityNames.remove(name)
                mapDataEditor!!.putStringSet("cityNames", HashSet(cityNames))
                mapDataEditor!!.apply()
                Log.d("MapData-Old", "Removed city$name")
            } catch (ignored: Exception) {
            }

        }

        fun addCityToStorage(name: String, lat: Double, lng: Double) {
            addCityToStorage(name, LatLng(lat, lng))
        }

        private fun addCityToStorage(name: String, pos: LatLng) {
            mapDataEditor = mapData.edit()
            val city = City(name, pos)
            mapDataEditor!!.putString(name, gson.toJson(city))

            val cityNames = loadCityList()
            cityNames.add(name)
            mapDataEditor!!.putStringSet("cityNames", HashSet(cityNames))
            mapDataEditor!!.apply()
            Log.d("MapData", "Added city $name, pos $pos")
        }

        fun removeCityFromStorage(name: String) {
            try {
                mapDataEditor = mapData.edit()
                mapDataEditor!!.remove(name)
                val cityNames = loadCityList()
                cityNames.remove(name)
                mapDataEditor!!.putStringSet("cityNames", HashSet(cityNames))
                mapDataEditor!!.apply()
                Log.d("MapData", "Removed city $name")
            } catch (ignored: Exception) {
            }
        }

        fun getPos(name: String): LatLng? {
            val city = gson.fromJson(mapData.getString(name, ""), City::class.java)
            return city.latLng
        }

        fun loadCityList(): ArrayList<String> {
            return ArrayList(Objects.requireNonNull(mapData.getStringSet("cityNames", HashSet())))
        }
    }
}
