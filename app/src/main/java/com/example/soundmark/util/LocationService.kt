package com.example.soundmark.util

import android.annotation.SuppressLint
import android.content.Context
import com.example.soundmark.data.model.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoLocation? {
        return try {
            // Try last known location first for immediate response
            val lastLocation = fusedLocationClient.lastLocation.await()
            if (lastLocation != null) {
                return GeoLocation(
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude,
                    placeName = null
                )
            }

            // Fallback to high accuracy location if last location is unavailable
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
            
            location?.let {
                GeoLocation(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    placeName = null
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
