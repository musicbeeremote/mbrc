package com.kelsos.mbrc.interactors.library

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class ArtistAlbumInteractorImpl : ArtistAlbumInteractor {
  @Inject private lateinit var repository: AlbumRepository

  override fun getArtistAlbums(artistId: Long): Observable<List<Album>> {
    return repository.getAlbumsByArtist(artistId)
        .flatMap {
          Observable.defer {
            AlbumMapper.mapArtistAlbums(it).toSingletonObservable()
          }
        }
  }
}
