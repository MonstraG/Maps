package com.company.maps.data.city

import com.google.android.gms.maps.model.LatLng
import java.util.*

class City internal constructor(private var name: String, private var latLng: LatLng,
                                private var country: String) : Comparable<City> {
    private var dateTimeOfCreation: Date = Calendar.getInstance().time

    fun getName(): String {
        return name
    }

    fun getLatLng(): LatLng {
        return latLng
    }

    fun getCountry(): String {
        return country
    }

    override fun compareTo(other: City): Int = this.name.compareTo(other.name)

}
