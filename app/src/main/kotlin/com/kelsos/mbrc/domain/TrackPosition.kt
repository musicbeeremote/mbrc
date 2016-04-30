package com.kelsos.mbrc.domain

class TrackPosition(position: Int = 0, duration: Int = 0) {

    val currentSeconds: Int
    val currentMinutes: Int
    val current: Int
    val totalSeconds: Int
    val totalMinutes: Int
    val total: Int


    init {
        var current = position
        var total = duration
        this.current = current
        current /= 1000
        currentMinutes = current / 60
        currentSeconds = current % 60
        this.total = total
        total /= 1000
        totalMinutes = total / 60
        totalSeconds = total % 60
    }
}
