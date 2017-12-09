package com.kelsos.mbrc.content.library.albums

import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject constructor(private val localDataSource: LocalAlbumDataSource,
                    private val remoteDataSource: RemoteAlbumDataSource) :
    AlbumRepository {

  override fun getAlbumsByArtist(artist: String): Single<List<Album>> {
    return localDataSource.getAlbumsByArtist(artist)
  }

  override fun getAllCursor(): Single<List<Album>> {
    return localDataSource.loadAllCursor().firstOrError()
  }

  override fun getAndSaveRemote(): Single<List<Album>> {
    return getRemote().andThen(localDataSource.loadAllCursor().firstOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<List<Album>> {
    return localDataSource.search(term)
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }

  override fun getAlbumsSorted(@Sorting.Fields order: Long, ascending: Boolean): Single<List<Album>> {
    return localDataSource.getAlbumsSorted(order, ascending)
  }
}
