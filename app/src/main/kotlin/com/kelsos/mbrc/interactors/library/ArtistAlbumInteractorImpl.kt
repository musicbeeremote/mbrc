package com.kelsos.mbrc.interactors.library

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class ArtistAlbumInteractorImpl
@Inject constructor(private val repository: AlbumRepository): ArtistAlbumInteractor {

  override fun getArtistAlbums(artistId: Long): Observable<List<Album>> {
    return repository.getAlbumsByArtist(artistId)
        .flatMap {
          Observable.defer {
            AlbumMapper.mapArtistAlbums(it).toSingletonObservable()
          }
        }
  }
}
