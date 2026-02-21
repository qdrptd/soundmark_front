package com.example.soundmark.util

import com.example.soundmark.data.model.ClusterMark
import com.example.soundmark.data.model.MapPin
import com.google.android.gms.maps.model.LatLng
import kotlin.math.pow
import kotlin.math.sqrt

object MarkerClusteringUtil {

    /**
     * 특정 거리(radius) 내의 마크들을 하나로 뭉칩니다.
     * @param pins 원본 마크 리스트
     * @param zoom 지도의 현재 줌 레벨
     */
    fun clusterPins(
        pins: List<MapPin>,
        zoom: Float
    ): List<ClusterMark> {
        val clusters = mutableListOf<ClusterMark>()
        val visited = mutableSetOf<String>()

        val clusterThresholdDegrees = 0.002 * 2.0.pow((15 - zoom).toDouble().coerceAtLeast(0.0))

        for (pin in pins) {
            if (pin.soundmarkId in visited) continue

            // 현재 핀을 기준으로 근처에 있는 핀들을 찾음
            val nearbyPins = pins.filter { other ->
                other.soundmarkId !in visited &&
                        other.isActive == pin.isActive && // 💡 상태가 다르면 합치지 않음
                        calculateDistance(pin.latitude, pin.longitude, other.latitude, other.longitude) < clusterThresholdDegrees
            }

            if (nearbyPins.isNotEmpty()) {
                // 근처 핀들의 중심점 계산
                val avgLat = nearbyPins.map { it.latitude }.average()
                val avgLng = nearbyPins.map { it.longitude }.average()

                android.util.Log.d(
                    "ClusteringDebug",
                    "상태(${pin.isActive}) 그룹 생성: 핀 ${nearbyPins.size}개 합쳐짐 " +
                            "| 대상 IDs: ${nearbyPins.map { it.soundmarkId }} " +
                            "| 결과 위치: ($avgLat, $avgLng)"
                )

                clusters.add(ClusterMark(
                    id = nearbyPins.first().soundmarkId,
                    position = LatLng(avgLat, avgLng),
                    pins = nearbyPins,
                    isActive = pin.isActive)
                )
                visited.addAll(nearbyPins.map { it.soundmarkId })
            }
        }
        return clusters
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return sqrt((lat1 - lat2).pow(2.0) + (lon1 - lon2).pow(2.0))
    }
}