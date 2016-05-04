package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.Shuffle.State
import com.kelsos.mbrc.dto.requests.ShuffleRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class ShuffleInteractorImpl : ShuffleInteractor {
  @Inject private lateinit var api: PlayerService

  override fun getShuffle(): Observable<String> {
    return api.getShuffleState()
        .task()
        .flatMap { it.state.toSingletonObservable() }
  }

  override fun updateShuffle(@State state: String): Observable<String> {
    val request = ShuffleRequest()
    request.status = state
    return api.updateShuffleState(request)
        .task()
        .flatMap { it.state.toSingletonObservable() }
  }
}
