package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.repository.data.LocalAlbumDataSource
import com.kelsos.mbrc.repository.data.RemoteAlbumDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject constructor(private val localDataSource: LocalAlbumDataSource,
                    private val remoteDataSource: RemoteAlbumDataSource) :
    AlbumRepository {
  override fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>> {
    return localDataSource.getAlbumsByArtist(artist)
  }

  override fun getAllCursor(): Single<FlowCursorList<Album>> {
    return localDataSource.loadAllCursor().toSingle()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Album>> {
    return getRemote().andThen(localDataSource.loadAllCursor().toSingle())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.toCompletable()
  }

  override fun search(term: String): Single<FlowCursorList<Album>> {
    return localDataSource.search(term)
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
