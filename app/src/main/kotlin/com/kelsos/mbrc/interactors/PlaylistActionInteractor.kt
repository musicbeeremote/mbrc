package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.services.api.PlaylistService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class PlaylistActionInteractor
@Inject constructor(private val service: PlaylistService){

  fun play(path: String): Observable<Boolean> {
    val request = PlayPathRequest()
    request.path = path
    return service.playPlaylist(request)
        .task()
        .flatMap { (it.code == Code.SUCCESS).toSingletonObservable() }
  }
}
