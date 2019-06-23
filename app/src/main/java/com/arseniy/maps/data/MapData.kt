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
        loadCityList().forEach { city ->
            if (mapData!!.contains(city + "lat") || mapData!!.contains(city + "lng")) {
                val pos = getPosOldWay(city)
                removeCityFromStorageOld(city)
                addCityToStorage(city, pos, "")
            }
        }
    }

    private fun debugPrintAllData() {
        Log.d("data", "ALL AVAILABLE DATA")
        mapData!!.all.entries.forEach { entry -> Log.d("data", entry.key + " " + entry.value) }
        Log.d("data", "END OF AVAILABLE DATA")

    }

    private fun loadAndDraw() {
        val map = mapData!!.getStringSet("cityNames", HashSet())
        map?.forEach { name -> MapsActivity.drawCity(name, getPos(name)!!, false) }
    }

    companion object {
        private var mapDataEditor: SharedPreferences.Editor? = null
        private var mapData: SharedPreferences? = null
        private var gson: Gson? = null

        private fun getPosOldWay(name: String): LatLng {
            return LatLng(mapData!!.getFloat(name + "lat", 0f).toDouble(),
                          mapData!!.getFloat(name + "lng", 0f).toDouble())
        }

        private fun removeCityFromStorageOld(name: String) {
            try {
                mapDataEditor = mapData!!.edit()

                //remove old storage types
                mapDataEditor!!.remove(name + "lat")
                mapDataEditor!!.remove(name + "lng")

                //remove city from list
                val cityNames = loadCityList()
                cityNames.remove(name)
                mapDataEditor!!.putStringSet("cityNames", HashSet(cityNames))

                //apply and log
                mapDataEditor!!.apply()
                Log.d("MapData-Old", "Removed city$name")
            } catch (ignored: Exception) { }
        }

        fun addCityToStorage(name: String, lat: Double, lng: Double, country: String) {
            addCityToStorage(name, LatLng(lat, lng), country)
        }

        private fun addCityToStorage(name: String, pos: LatLng, country: String) {
            mapDataEditor = mapData!!.edit()

            //add city data
            val city = City(name, pos, country)
            mapDataEditor!!.putString(name, gson!!.toJson(city))

            //add city to list
            val cityNames = loadCityList()
            cityNames.add(name)
            mapDataEditor!!.putStringSet("cityNames", HashSet(cityNames))

            mapDataEditor!!.apply()
            Log.d("MapData", "Added city $name, pos $pos")
        }

        fun removeCityFromStorage(name: String) {
            try {
                mapDataEditor = mapData!!.edit()

                //remove city from prefs
                mapDataEditor!!.remove(name)

                //remove city from list
                val cityNames = loadCityList()
                cityNames.remove(name)
                mapDataEditor!!.putStringSet("cityNames", HashSet(cityNames))

                mapDataEditor!!.apply()
                Log.d("MapData", "Removed city $name")
            } catch (ignored: Exception) { }
        }

        fun getPos(name: String): LatLng? {
            return gson!!.fromJson(mapData!!.getString(name, ""), City::class.java).latLng
        }

        fun loadCityList(): ArrayList<String> { //arrayList instead of MutableSet, because CityAdapter has sort.
            return ArrayList(mapData!!.getStringSet("cityNames", HashSet()))
        }
    }
}
