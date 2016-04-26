package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.dto.requests.PositionRequest
import com.kelsos.mbrc.services.api.TrackService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class TrackPositionInteractorImpl : TrackPositionInteractor {
    @Inject private lateinit var service: TrackService

    override fun getPosition(): Observable<TrackPosition> {
        return service.getCurrentPosition()
                .subscribeOn(Schedulers.io())
                .flatMap<TrackPosition>(Func1 {
                    Observable.just(TrackPosition(it.position, it.duration))
                }).observeOn(AndroidSchedulers.mainThread())
    }

    override fun setPosition(position: Int): Observable<TrackPosition> {
        return service.updatePosition(PositionRequest().setPosition(position))
                .subscribeOn(Schedulers.io())
                .flatMap<TrackPosition>(Func1{
                    Observable.just(TrackPosition(it.position, it.duration))
                }).observeOn(AndroidSchedulers.mainThread())
    }
}
