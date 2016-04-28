package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.dto.requests.ShuffleRequest
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class ShuffleInteractorImpl : ShuffleInteractor {
    @Inject private lateinit var api: PlayerService

    override fun getShuffle(): Observable<String> {
        return api.getShuffleState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap<String>(Func1{ Observable.just<String>(it.state) })
    }

    override fun updateShuffle(@Shuffle.State state: String): Observable<String> {
        val request = ShuffleRequest()
        request.status = state
        return api.updateShuffleState(request)
                .subscribeOn(Schedulers.io()
                ).observeOn(AndroidSchedulers.mainThread())
                .flatMap<String>(Func1{ Observable.just<String>(it.state) })
    }
}
