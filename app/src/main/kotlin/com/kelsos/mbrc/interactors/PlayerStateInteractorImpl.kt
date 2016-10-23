package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class PlayerStateInteractorImpl
@Inject constructor(private val cache: PlayerStateCache,
                    private val service: PlayerService) : PlayerStateInteractor {

  override fun getState(): Observable<String> {
    val networkRequest = service.getPlayState()
        .map { it.value }
        .doOnNext { cache.playState = it }

    val cached = cache.playState.toSingletonObservable()

    return Observable.concat(networkRequest, cached)
        .filter { PlayerState.UNDEFINED != it }
        .doOnError { PlayerState.STOPPED.toSingletonObservable() }
        .firstOrDefault(PlayerState.STOPPED)
        .task()
  }
}
