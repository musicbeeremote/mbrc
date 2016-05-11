package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class LibraryAlbumInteractorImpl : LibraryAlbumInteractor {
  @Inject private lateinit var repository: AlbumRepository

  override fun getPage(offset: Int, limit: Int): Observable<List<Album>> {
    return repository.getAlbumViews(offset, limit)
        .flatMap { AlbumMapper.map(it).toSingletonObservable() }
        .io()
  }
}
