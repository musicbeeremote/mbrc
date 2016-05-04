package com.kelsos.mbrc.interactors

import android.text.TextUtils
import com.google.inject.Inject
import com.kelsos.mbrc.annotations.Repeat.Mode
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.dto.player.Repeat
import com.kelsos.mbrc.dto.requests.RepeatRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.functions.Func1

class RepeatInteractorImpl : RepeatInteractor {
  @Inject private lateinit var cache: PlayerStateCache
  @Inject private lateinit var service: PlayerService

  override fun getRepeat(): Observable<String> {
    val networkRequest = service.getRepeatMode()
        .map { it.value }
        .doOnNext { cache.repeat = it }
    val cached = Observable.just(cache.repeat)

    return Observable.concat(cached, networkRequest)
        .filter { !TextUtils.isEmpty(it) && Shuffle.UNDEF != it }
        .doOnError { Observable.just(Shuffle.OFF) }
        .first()
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
