package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.dto.track.Rating

import rx.Observable

interface TrackRatingInteractor {
    fun getRating(): Observable<Rating>
    fun updateRating(rating: Float): Observable<Float>
}
