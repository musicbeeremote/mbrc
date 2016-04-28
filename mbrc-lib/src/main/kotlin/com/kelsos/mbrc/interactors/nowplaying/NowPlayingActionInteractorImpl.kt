package com.kelsos.mbrc.interactors.nowplaying

import com.google.inject.Inject
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.MoveRequest
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import com.kelsos.mbrc.services.api.NowPlayingService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class NowPlayingActionInteractorImpl : NowPlayingActionInteractor {

    @Inject
    private lateinit var service: NowPlayingService

    override fun play(path: String): Observable<Boolean> {
        val request = PlayPathRequest()
        request.path = path
        return service.nowPlayingPlayTrack(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap<Boolean>(Func1{
                    Observable.just(it.code == Code.SUCCESS)
                })
    }

    override fun remove(position: Long): Observable<Boolean> {
        return service.nowPlayingRemoveTrack(position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap<Boolean>(Func1{ Observable.just(it.code == Code.SUCCESS) })
    }

    override fun move(from: Int, to: Int): Observable<Boolean> {
        val request = MoveRequest()
        request.from = from
        request.to = to

        return service.nowPlayingMoveTrack(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap<Boolean>(Func1{ Observable.just(it.code == Code.SUCCESS) })
    }
}
