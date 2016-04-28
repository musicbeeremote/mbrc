package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.mappers.ArtistMapper
import com.kelsos.mbrc.repository.library.ArtistRepository
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class LibraryArtistInteractor {
    @Inject private lateinit var repository: ArtistRepository

    fun execute(offset: Int, limit: Int): Observable<List<Artist>> {
        return repository.getPageObservable(offset, limit)
                .flatMap<List<Artist>>(Func1{ Observable.just(ArtistMapper.mapData(it)) })
                .subscribeOn(Schedulers.io())
    }
}
