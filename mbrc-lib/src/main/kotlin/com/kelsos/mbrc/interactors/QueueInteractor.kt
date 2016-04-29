package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.NowPlayingQueueRequest
import com.kelsos.mbrc.services.api.NowPlayingService
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class QueueInteractor {
    @Inject private lateinit var service: NowPlayingService

    fun execute(@MetaDataType.Type type: String, @Queue.Action action: String, id: Long): Observable<Boolean> {
        val body = NowPlayingQueueRequest()
        body.action = action
        body.id = id
        body.type = type
        return service.nowplayingQueue(body)
                .flatMap<Boolean>(Func1{ Observable.just(it.code == Code.SUCCESS) })
                .subscribeOn(Schedulers.io())
    }

    fun execute(@Queue.Action action: String, path: String): Observable<Boolean> {
        val body = NowPlayingQueueRequest()
        body.action = action
        body.path = path
        return service.nowplayingQueue(body)
                .flatMap<Boolean>(Func1{ Observable.just(it.code == Code.SUCCESS) })
                .subscribeOn(Schedulers.io())
    }
}
