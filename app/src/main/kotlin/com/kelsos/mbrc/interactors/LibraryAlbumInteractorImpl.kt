package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class LibraryAlbumInteractorImpl : LibraryAlbumInteractor {
    @Inject private lateinit var repository: AlbumRepository

    override fun execute(offset: Int, limit: Int): Observable<List<Album>> {
        return repository.getAlbumViews(offset, limit)
                .flatMap<List<Album>>(Func1{ Observable.just(AlbumMapper.map(it)) })
                .subscribeOn(Schedulers.io())
    }
}
