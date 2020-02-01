package com.company.maps.data.item

const val COUNTRY = 0
const val CITY = 1

fun throwUnknownTypeException(viewType: Int) {
    throw Exception("Unknown viewType: '$viewType'!")
}