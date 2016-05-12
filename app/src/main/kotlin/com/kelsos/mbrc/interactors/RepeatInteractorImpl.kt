package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.Repeat.Mode
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.dto.requests.RepeatRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class RepeatInteractorImpl : RepeatInteractor {
  @Inject private lateinit var cache: PlayerStateCache
  @Inject private lateinit var service: PlayerService

  override fun getRepeat(): Observable<String> {
    val networkRequest = service.getRepeatMode()
        .map { it.value }
        .doOnNext { cache.repeat = it }
    val cached = cache.repeat.toSingletonObservable()

    return Observable.concat(cached, networkRequest)
        .filter { !Shuffle.UNDEF.equals(it) }
        .doOnError { Shuffle.OFF.toSingletonObservable() }
        .firstOrDefault(Shuffle.OFF)
        .task()
  }

  override fun setRepeat(@Mode mode: String): Observable<String> {
    val request = RepeatRequest()
    request.mode = mode
    return service.updateRepeatState(request)
        .task()
        .map { it.value }

  }
}
