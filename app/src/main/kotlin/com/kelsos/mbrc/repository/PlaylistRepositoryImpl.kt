package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.repository.data.LocalPlaylistDataSource
import com.kelsos.mbrc.repository.data.RemotePlaylistDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor(private val localDataSource: LocalPlaylistDataSource,
                    private val remoteDataSource: RemotePlaylistDataSource) : PlaylistRepository {
  override fun getAllCursor(): Single<FlowCursorList<Playlist>> {
    return localDataSource.loadAllCursor().toSingle()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Playlist>> {
    return getRemote().andThen(localDataSource.loadAllCursor().toSingle())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.toCompletable()
  }
}
