package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.dto.requests.PositionRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.TrackService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class TrackPositionInteractorImpl
@Inject constructor(private val service: TrackService) :
    TrackPositionInteractor {

  override fun getPosition(): Observable<TrackPosition> {
    return service.getCurrentPosition()
        .task()
        .flatMap { TrackPosition(it.position, it.duration).toSingletonObservable() }
  }

  override fun setPosition(position: Int): Observable<TrackPosition> {
    val request = PositionRequest()
    request.position = position
    return service.updatePosition(request)
        .task()
        .flatMap { TrackPosition(it.position, it.duration).toSingletonObservable() }
  }
}
