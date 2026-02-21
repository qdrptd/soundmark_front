package com.example.soundmark.data.network

import com.example.soundmark.data.dto.GooglePlacesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String, // "lat,lng"
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): GooglePlacesResponse
}
