package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class LibraryAlbumInteractorImpl
@Inject constructor(private val repository: AlbumRepository): LibraryAlbumInteractor {

  override fun getPage(offset: Int, limit: Int): Observable<List<Album>> {
    return repository.getAlbumViews(offset, limit)
        .flatMap { AlbumMapper.map(it).toSingletonObservable() }
        .io()
  }
}
