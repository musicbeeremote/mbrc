package com.kelsos.mbrc.interactors

import javax.inject.Inject
import com.kelsos.mbrc.dto.requests.RatingRequest
import com.kelsos.mbrc.dto.track.Rating
import com.kelsos.mbrc.services.api.TrackService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class TrackRatingInteractorImpl : TrackRatingInteractor {
  @Inject private lateinit var service: TrackService

  override fun getRating(): Observable<Rating> {
    return service.getTrackRating()
  }

  override fun updateRating(rating: Float): Observable<Float> {
    val request = RatingRequest()
    request.rating = rating
    return service.updateRating(request)
        .flatMap { rating.toSingletonObservable() }
  }
}
