package com.example.soundmark.data.repository.profile

import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.MapPin
import com.example.soundmark.data.model.Profile
import com.example.soundmark.data.model.SoundMark

interface MarkRepository {
    suspend fun getNearbyMarks(geoLocation: GeoLocation): Result<List<MapPin>>

}
