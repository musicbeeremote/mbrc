package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.Mute
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.dto.requests.ChangeStateRequest
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class MuteInteractorImpl : MuteInteractor {
    @Inject private lateinit var cache: PlayerStateCache
    @Inject private lateinit var service: PlayerService

    override fun getMuteState(): Observable<Boolean> {
        val networkRequest = service.getMuteState()
                .flatMap<Int>(Func1{ Observable.just(if (it.enabled) Mute.ON else Mute.OFF) })
                .doOnNext({ cache.muteState = it })

        val cached = Observable.just(cache.muteState)

        return Observable.concat(networkRequest, cached)
                .filter { it !== Mute.UNDEF }
                .map<Boolean>(Func1{ it === Mute.ON })
                .doOnError { Observable.just(false) }
                .first()
    }

    override fun toggle(): Observable<Boolean> {
        return Observable.just(cache.muteState)
                .map<Boolean>(Func1 {
                    it === Mute.ON
                }).flatMap<Boolean>(Func1 {
            val stateRequest = ChangeStateRequest()
            stateRequest.enabled = !it
            service.updateMuteState(stateRequest)
                    .subscribeOn(Schedulers.io())
                    .flatMap<Boolean>(Func1 {
                        cache.muteState = if (it.enabled) Mute.ON else Mute.OFF
                        Observable.just<Boolean>(it.enabled)
                    })
        }).subscribeOn(Schedulers.io())
    }
}
