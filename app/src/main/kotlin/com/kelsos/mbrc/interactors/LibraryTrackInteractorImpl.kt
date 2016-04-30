package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.mappers.TrackMapper
import com.kelsos.mbrc.repository.library.TrackRepository
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class LibraryTrackInteractorImpl : LibraryTrackInteractor {
    @Inject private lateinit var repository: TrackRepository

    override fun execute(page: Int, items: Int): Observable<List<Track>> {
        return repository.getTracks(page * PAGE_SIZE, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .flatMap<List<Track>>(Func1{ Observable.just<List<Track>>(TrackMapper.map(it)) })
    }

    companion object {
        val PAGE_SIZE = 100
    }
}
