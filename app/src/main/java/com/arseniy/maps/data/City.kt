package com.arseniy.maps.data

import android.util.Log

import com.google.android.gms.maps.model.LatLng
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

import java.io.IOException
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class City {
    private var name: String? = null
    internal var latLng: LatLng? = null
    var dateTimeOfCreation: Date? = null
        private set

    private val latitude: Double
        get() = latLng!!.latitude

    private val longitude: Double
        get() = latLng!!.longitude

    internal constructor(name: String, latLng: LatLng) {
        this.name = name
        this.latLng = latLng
        this.dateTimeOfCreation = Calendar.getInstance().time
    }

    constructor(name: String, latitude: Float, longitude: Float) {
        this.name = name
        this.latLng = LatLng(latitude.toDouble(), longitude.toDouble())
        this.dateTimeOfCreation = Calendar.getInstance().time

    }

    constructor(name: String, latitude: Double, longitude: Double) {
        this.name = name
        this.latLng = LatLng(latitude, longitude)
        this.dateTimeOfCreation = Calendar.getInstance().time
    }

    //only for deserialization
    private constructor()

    private fun setLatLng(latitude: Double, longitude: Double) {
        this.latLng = LatLng(latitude, longitude)
    }

    private fun serializeDateTimeOfCreation(): String {
        return DateFormat.getDateTimeInstance().format(dateTimeOfCreation)
    }

    private fun deserializeDateTimeOfCreation(serializedDateTime: String) {
        dateTimeOfCreation = try {
            DateFormat.getDateTimeInstance().parse(serializedDateTime)
        } catch (e: Exception) {
            Calendar.getInstance().time
        }

    }

    private class CityAdapter : TypeAdapter<City>() {

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: City) {
            out.beginObject()
            out.name("name").value(value.name)
            out.name("latitude").value(value.latitude)
            out.name("longitude").value(value.longitude)
            out.name("dateTimeOfCreation").value(value.serializeDateTimeOfCreation())
            out.endObject()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): City {
            `in`.beginObject()
            val city = City()
            var latitude = 0.0
            var longitude = 0.0

            while (`in`.hasNext()) {
                val name = `in`.nextName()
                when (name) {
                    "name" -> city.name = `in`.nextString()
                    "latitude" -> latitude = `in`.nextDouble()
                    "longitude" -> longitude = `in`.nextDouble()
                    "dateTimeOfCreation" -> city.deserializeDateTimeOfCreation(`in`.nextString())
                    else -> Log.e("City deserialization", "couldn't map field name $name")
                }
            }
            `in`.endObject()

            city.setLatLng(latitude, longitude)

            return city
        }
    }

}
