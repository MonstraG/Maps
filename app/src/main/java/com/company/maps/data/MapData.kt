package com.company.maps.data

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import com.company.maps.activities.MapsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.util.*

class MapData(context: Context) {

    init {
        val appFlags = context.applicationInfo.flags
        val isDebug = appFlags and ApplicationInfo.FLAG_DEBUGGABLE != 0

        mapData = getDefaultSharedPreferences(context)

        if (isDebug)
            debugPrintAllData()

        transferStorageToJson() //todo: remove when done.

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
        map?.forEach { name -> MapsActivity.drawCity(name, getCity(name)!!.getLatLng(), false) }
    }

    companion object {
        private var mapData: SharedPreferences? = null
        private var gson: Gson = Gson()

        private fun getPosOldWay(name: String): LatLng {
            return LatLng(mapData!!.getFloat(name + "lat", 0f).toDouble(),
                    mapData!!.getFloat(name + "lng", 0f).toDouble())
        }

        private fun removeCityFromStorageOld(name: String) {
            try {
                val cityNames = loadCityList()
                cityNames.remove(name)

                mapData!!.edit()
                        .remove(name + "lat")
                        .remove(name + "lng")
                        .putStringSet("cityNames", HashSet(cityNames))
                        .apply()

                Log.d("MapData-Old", "Removed city$name")
            } catch (ignored: Exception) {
            }
        }

        fun addCityToStorage(name: String, lat: Double, lng: Double, country: String) {
            addCityToStorage(name, LatLng(lat, lng), country)
        }

        private fun addCityToStorage(name: String, pos: LatLng, country: String) {
            val city = City(name, pos, country)
            val cityNames = loadCityList()
            cityNames.add(name)

            mapData!!.edit()
                    .putString(name, gson.toJson(city)) //data
                    .putStringSet("cityNames", HashSet(cityNames)) //list
                    .apply()

            Log.d("MapData", "Added city $name, pos $pos")
        }

        fun removeCityFromStorage(name: String) {
            val cityNames = loadCityList()
            cityNames.remove(name)

            mapData!!.edit()
                    .remove(name) //remove city data
                    .putStringSet("cityNames", HashSet(cityNames)) //remove from list
                    .apply()

            Log.d("MapData", "Removed city $name")
        }

        fun getCity(name: String): City? {
            return gson.fromJson(mapData!!.getString(name, ""), City::class.java)
        }

        fun loadCityList(): MutableList<String> {
            return mapData!!.getStringSet("cityNames", HashSet())!!.toMutableList()
        }
    }
}
