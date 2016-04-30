package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import com.kelsos.mbrc.services.api.PlaylistService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class PlaylistActionInteractor {
    @Inject private lateinit var service: PlaylistService

    fun play(path: String): Observable<Boolean> {
        val request = PlayPathRequest()
        request.path = path
        return service.playPlaylist(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap<Boolean>(Func1{ Observable.just(it.code == Code.SUCCESS) })
    }
}
