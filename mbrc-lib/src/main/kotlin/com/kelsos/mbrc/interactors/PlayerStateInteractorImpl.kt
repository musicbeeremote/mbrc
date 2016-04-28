package com.kelsos.mbrc.interactors

import android.text.TextUtils
import com.google.inject.Inject
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.dto.player.PlayState
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class PlayerStateInteractorImpl : PlayerStateInteractor {
    @Inject private lateinit var cache: PlayerStateCache
    @Inject private lateinit var service: PlayerService

    override val state: Observable<String>
        get() {
            val networkRequest = service.getPlayState()
                    .map<String>(Func1<PlayState, String> { it.value }).doOnNext({ cache.playState = it })
            val cached = Observable.just(cache.playState)

            return Observable.concat(networkRequest, cached).filter {
                !TextUtils.isEmpty(it) && PlayerState.UNDEFINED != it
            }.doOnError {
                Observable.just(PlayerState.STOPPED)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
}
