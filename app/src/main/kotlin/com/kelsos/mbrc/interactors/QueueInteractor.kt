package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.MetaDataType.Type
import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.NowPlayingQueueRequest
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.services.api.NowPlayingService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class QueueInteractor
@Inject constructor(private val service: NowPlayingService) {

  fun execute(@Type type: String, @Action action: String, id: Long): Observable<Boolean> {
    val body = NowPlayingQueueRequest()
    body.action = action
    body.id = id
    body.type = type
    return service.nowplayingQueue(body)
        .io()
        .flatMap { (it.code == Code.SUCCESS).toSingletonObservable() }
  }

  fun execute(@Action action: String, path: String): Observable<Boolean> {
    val body = NowPlayingQueueRequest()
    body.action = action
    body.path = path
    return service.nowplayingQueue(body)
        .io()
        .flatMap { (it.code == Code.SUCCESS).toSingletonObservable() }
  }
}
