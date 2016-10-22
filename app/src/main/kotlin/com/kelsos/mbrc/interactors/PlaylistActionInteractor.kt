package com.kelsos.mbrc.interactors

import javax.inject.Inject
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlaylistService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class PlaylistActionInteractor {
  @Inject private lateinit var service: PlaylistService

  fun play(path: String): Observable<Boolean> {
    val request = PlayPathRequest()
    request.path = path
    return service.playPlaylist(request)
        .task()
        .flatMap { (it.code == Code.SUCCESS).toSingletonObservable() }
  }
}
