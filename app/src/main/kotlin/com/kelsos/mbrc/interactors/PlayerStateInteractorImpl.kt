package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class PlayerStateInteractorImpl : PlayerStateInteractor {
  @Inject private lateinit var cache: PlayerStateCache
  @Inject private lateinit var service: PlayerService

  override fun getState(): Observable<String> {
    val networkRequest = service.getPlayState()
        .map { it.value }
        .doOnNext { cache.playState = it }

    val cached = cache.playState.toSingletonObservable()

    return Observable.concat(networkRequest, cached)
        .filter { !PlayerState.UNDEFINED.equals(it) }
        .doOnError { PlayerState.STOPPED.toSingletonObservable() }
        .firstOrDefault(PlayerState.STOPPED)
        .task()
  }
}
