package com.kelsos.mbrc.content.library.artists

import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ArtistRepositoryImpl
@Inject constructor(private val localDataSource: LocalArtistDataSource,
                    private val remoteDataSource: RemoteArtistDataSource) : ArtistRepository {

  override fun getArtistByGenre(genre: String): Single<List<Artist>> {
    return localDataSource.getArtistByGenre(genre)
  }

  override fun getAllCursor(): Single<List<Artist>> {
    return localDataSource.loadAllCursor().firstOrError()
  }

  override fun getAndSaveRemote(): Single<List<Artist>> {
    return getRemote().andThen(localDataSource.loadAllCursor().firstOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<List<Artist>> {
    return localDataSource.search(term)
  }

  override fun getAlbumArtistsOnly(): Single<List<Artist>> {
    return localDataSource.getAlbumArtists()
  }

  override fun getAllRemoteAndShowAlbumArtist(): Single<List<Artist>> {
    return getRemote().andThen(localDataSource.getAlbumArtists())
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
