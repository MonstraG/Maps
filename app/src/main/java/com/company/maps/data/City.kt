package com.company.maps.data

import com.google.android.gms.maps.model.LatLng
import java.util.Calendar
import java.util.Date

class City internal constructor(private var name: String, private var latLng: LatLng,
                                private var country: String) {
    private var dateTimeOfCreation: Date = Calendar.getInstance().time

    fun getLatLng(): LatLng {
        return latLng
    }
}
