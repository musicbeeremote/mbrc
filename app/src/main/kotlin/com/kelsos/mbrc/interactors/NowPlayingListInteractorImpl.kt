package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.repository.NowPlayingRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NowPlayingListInteractorImpl : NowPlayingListInteractor {
    @Inject private lateinit var repository: NowPlayingRepository

    override fun execute(): Observable<List<QueueTrack>> {
        return repository.getNowPlayingList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}
