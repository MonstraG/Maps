package com.company.maps.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.company.maps.activities.MapsActivity
import com.company.maps.data.city.City
import com.company.maps.data.county.Country
import com.company.maps.logger.Logger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapData(context: Context) {
    var countries: MutableList<Country>
    private val serializer = Serializer(context)

    init {
        this.countries = serializer.load().toMutableList()
        Logger.log("Loaded countries. Country count: ${this.countries.size}")
    }

    fun addCity(city: City): MapData {
        Logger.log("Adding city: ${city.serialize()}")
        val country = countries.find { country -> country.getName() == city.getCountry() }
        if (country != null) {
            country.addCity(city)
        } else {
            countries.add(Country(city.getCountry(), mutableListOf(city)))
        }
        return this
    }

    fun removeCity(cityName: String): MapData {
        Logger.log("Removing city $cityName")
        val country = findCountryContainingCityName(cityName)
        if (country == null) {
            Logger.log("Tried to delete a city '${cityName}' that doesn't exist!")
            throw Exception("Cannot remove city that doesn't exist!")
        } else {
            country.removeCity(cityName)
            if (country.getCityList().isEmpty()) {
                countries.removeIf { it.getName() == country.getName() }
                Logger.log("Was last city in the country ${country.getName()}, removing country")
            }
        }
        return this
    }

    fun save() {
        Logger.log("Saving countries")
        serializer.save(countries)
        this.countries = serializer.load().toMutableList()
    }

    fun findCountryContainingCityName(cityName: String): Country? {
        return countries.find { country -> country.getCityNameList().contains(cityName) }
    }

    fun getCityList(): List<City> {
        val cities = ArrayList<City>()
        countries.forEach { cities.addAll(it.getCityList()) }
        return cities
    }

    fun drawAllCities() {
        Logger.log("Drawing all cities")
        countries.forEach { country -> country.getCityList().forEach { city -> drawCity(city) } }
    }

    private fun drawCity(city: City) {
        MapsActivity.drawCity(city.getName(), city.getLatLng(), false)
    }

    class Serializer(context: Context) {
        private var storage: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        private var gson: Gson = Gson()
        private val storageStringName = "data"

        fun load(): List<Country> {
            val listType = object : TypeToken<ArrayList<Country>>() {}.type
            return gson.fromJson(storage.getString(storageStringName, "[]"), listType)
        }

        fun save(mapData: List<Country>) {
            storage.edit().putString(storageStringName, gson.toJson(mapData)).apply()
        }
    }
}
