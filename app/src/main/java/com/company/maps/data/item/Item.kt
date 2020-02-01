package com.company.maps.data.item

import com.company.maps.data.county.Country
import com.company.maps.logger.Logger

class Item(val name: String, viewType: Int, val country: Country?) {
    init {
        if (viewType != CITY && viewType != COUNTRY) {
            Logger.log("Attempt to create Item with unknown view type: $viewType")
            throwUnknownTypeException(viewType)
        }
    }
}