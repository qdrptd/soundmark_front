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
     * @param clusterThresholdDegrees 뭉칠 거리 기준 (경위도 도 단위)
     */
    fun clusterPins(
        pins: List<MapPin>,
        clusterThresholdDegrees: Double = 0.002 // 약 200m 정도의 오차 범위
    ): List<ClusterMark> {
        val clusters = mutableListOf<ClusterMark>()
        val visited = mutableSetOf<String>()

        for (pin in pins) {
            if (pin.soundmarkId in visited) continue

            // 현재 핀을 기준으로 근처에 있는 핀들을 찾음
            val nearbyPins = pins.filter { otherPin ->
                otherPin.soundmarkId !in visited &&
                        calculateDistance(pin.latitude, pin.longitude, otherPin.latitude, otherPin.longitude) < clusterThresholdDegrees
            }

            if (nearbyPins.isNotEmpty()) {
                // 근처 핀들의 중심점 계산
                val avgLat = nearbyPins.map { it.latitude }.average()
                val avgLng = nearbyPins.map { it.longitude }.average()

                clusters.add(ClusterMark(LatLng(avgLat, avgLng), nearbyPins))
                visited.addAll(nearbyPins.map { it.soundmarkId })
            }
        }
        return clusters
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return sqrt((lat1 - lat2).pow(2.0) + (lon1 - lon2).pow(2.0))
    }
}