package com.company.maps.data.item

import com.company.maps.data.county.Country

class Item(val name: String, val viewType: Int, val country: Country?) {
    init {
        if (viewType != CITY && viewType != COUNTRY) {
            throwUnknownTypeException(viewType)
        }
    }
}