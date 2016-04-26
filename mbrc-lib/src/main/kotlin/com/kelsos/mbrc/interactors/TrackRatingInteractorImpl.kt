package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.dto.requests.RatingRequest
import com.kelsos.mbrc.dto.track.Rating
import com.kelsos.mbrc.services.api.TrackService
import rx.Observable
import rx.functions.Func1

class TrackRatingInteractorImpl : TrackRatingInteractor {
    @Inject private lateinit var service: TrackService

    override fun getRating(): Observable<Rating> {
        return service.getTrackRating()
    }

    override fun updateRating(rating: Float): Observable<Float> {
        return service.updateRating(RatingRequest().setRating(rating))
                .flatMap<Float>(Func1 {
                    Observable.just(rating)
                })
    }
}
