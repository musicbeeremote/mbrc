package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.dto.requests.PositionRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.TrackService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class TrackPositionInteractorImpl : TrackPositionInteractor {
  @Inject private lateinit var service: TrackService

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
