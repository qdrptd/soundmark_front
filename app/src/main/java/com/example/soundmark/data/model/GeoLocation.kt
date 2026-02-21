package com.example.soundmark.data.model

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val placeName: String?,
    val address: String? = null,
    val placeId: String? = null
) {
    companion object {
        val Default = GeoLocation(
            latitude = 37.5451,
            longitude = 127.0572,
            placeName = "엘리스랩 성수",
            address = "서울특별시 성동구 성수동2가 성수이로20길 16",
            placeId = "ChIJ_3jP7B-efDURR3j9_P7B-EU"
        )
    }
}