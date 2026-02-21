package com.example.soundmark.data.model

import com.google.android.gms.maps.model.LatLng

data class ClusterMark(
    val id: String,
    val position: LatLng,
    val pins: List<MapPin>,
    val isActive: Boolean,
    val count: Int = pins.size
)