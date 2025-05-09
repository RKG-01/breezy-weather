package com.google.maps.android.model

import com.google.maps.android.SphericalUtil
import java.text.ParseException
import kotlin.math.max
import kotlin.math.min

class LatLng(
    latitude: Double,
    longitude: Double,
) {
    val latitude: Double
    val longitude: Double

    init {
        this.latitude = max(-90.0, min(90.0, latitude))
        this.longitude = max(-180.0, min(180.0, longitude))
    }

    /**
     * Returns the key of the nearest location from a predefined map of locations
     *
     * @param locationMap map of locations { key (unique identifier) => LatLng }
     * @param limit furthest allowed match in meters, null if no limit
     */
    fun getNearestLocation(
        locationMap: Map<String, LatLng>?,
        limit: Double? = null,
    ): String? {
        var distance: Double
        var nearestDistance = Double.POSITIVE_INFINITY
        var nearestLocation: String? = null
        locationMap?.keys?.forEach { key ->
            locationMap[key]?.let {
                distance = SphericalUtil.computeDistanceBetween(this, locationMap[key]!!)
                if (distance < nearestDistance) {
                    if (limit == null || distance <= limit) {
                        nearestDistance = distance
                        nearestLocation = key
                    }
                }
            }
        }
        return nearestLocation
    }

    override fun equals(other: Any?): Boolean {
        return if (other is LatLng) {
            latitude == other.latitude && longitude == other.longitude
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "$latitude,$longitude"
    }

    companion object {
        @Throws(ParseException::class)
        fun parse(value: String): LatLng {
            val coordArr = value.split(",")

            if (coordArr.size != 2) {
                throw ParseException("Failed parsing '$value' as LatLng", 0)
            }

            val lon = coordArr[0].trim().toDoubleOrNull()
            val lat = coordArr[1].trim().toDoubleOrNull()

            if (lon == null || lat == null || (lon == 0.0 && lat == 0.0)) {
                throw ParseException("Failed parsing '$value' as LatLng", 0)
            }
            return LatLng(lon, lat)
        }
    }
}
