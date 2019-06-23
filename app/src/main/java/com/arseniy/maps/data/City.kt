package com.arseniy.maps.data

import com.google.android.gms.maps.model.LatLng
import java.util.Calendar
import java.util.Date

class City internal constructor(name: String, latLng: LatLng) {
    private var name: String? = name
    internal var latLng: LatLng? = latLng
    private var dateTimeOfCreation: Date? = null

    init {
        this.dateTimeOfCreation = Calendar.getInstance().time
    }
}