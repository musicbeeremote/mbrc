package com.kelsos.mbrc.interactors

import javax.inject.Inject
import com.kelsos.mbrc.annotations.Shuffle.State
import com.kelsos.mbrc.dto.requests.ShuffleRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable

class ShuffleInteractorImpl : ShuffleInteractor {
  @Inject private lateinit var api: PlayerService

  override fun getShuffle(): Observable<String> {
    return api.getShuffleState()
        .map { it.state }
        .task()
  }

  override fun updateShuffle(@State state: String): Observable<String> {
    val request = ShuffleRequest()
    request.status = state
    return api.updateShuffleState(request)
        .map { it.state }
        .task()
  }
}
