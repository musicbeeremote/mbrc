package com.kelsos.mbrc.content.playlists

import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor(private val localDataSource: LocalPlaylistDataSource,
                    private val remoteDataSource: RemotePlaylistDataSource) : PlaylistRepository {
  override fun getAllCursor(): Single<List<Playlist>> {
    return localDataSource.loadAllCursor().firstOrError()
  }

  override fun getAndSaveRemote(): Single<List<Playlist>> {
    return getRemote().andThen(localDataSource.loadAllCursor().firstOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<List<Playlist>> {
    return localDataSource.search(term)
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
