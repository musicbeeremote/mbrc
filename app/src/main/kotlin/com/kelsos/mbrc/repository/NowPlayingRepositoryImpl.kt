package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.repository.data.LocalNowPlayingDataSource
import com.kelsos.mbrc.repository.data.RemoteNowPlayingDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single
import javax.inject.Inject

class NowPlayingRepositoryImpl
@Inject constructor(private val remoteDataSource: RemoteNowPlayingDataSource,
                    private val localDataSource: LocalNowPlayingDataSource) : NowPlayingRepository {
  override fun getAllCursor(): Single<FlowCursorList<NowPlaying>> {
    return localDataSource.loadAllCursor().toSingle()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<NowPlaying>> {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.toCompletable().andThen(localDataSource.loadAllCursor().toSingle())
  }
}
