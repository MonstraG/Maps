package com.company.maps.data.county

import com.company.maps.data.city.City

class Country internal constructor(private var name: String, private var cities: MutableList<City>) : Comparable<Country> {
    fun getName(): String {
        return name
    }

    fun getCityList(): MutableList<City> {
        return cities
    }

    fun getCityNameList(): List<String> {
        return cities.map { it.getName() }.sorted()
    }

    fun addCity(city: City) {
        this.cities.add(city)
    }

    fun removeCity(cityName: String) {
        this.cities.removeIf { it.getName() == cityName }
    }

    override fun compareTo(other: Country): Int = this.name.compareTo(other.name)
}
