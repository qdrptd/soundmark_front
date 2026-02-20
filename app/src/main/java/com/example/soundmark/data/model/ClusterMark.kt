package com.example.soundmark.data.model

import com.google.android.gms.maps.model.LatLng

data class ClusterMark(
    val position: LatLng,
    val pins: List<MapPin>,
    val count: Int = pins.size
)