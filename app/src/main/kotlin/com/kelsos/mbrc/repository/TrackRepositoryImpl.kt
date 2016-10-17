package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.repository.data.LocalTrackDataSource
import com.kelsos.mbrc.repository.data.RemoteTrackDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single
import javax.inject.Inject

class TrackRepositoryImpl
@Inject constructor(private val localDataSource: LocalTrackDataSource,
                    private val remoteDataSource: RemoteTrackDataSource) : TrackRepository {


  override fun getAllCursor(): Single<FlowCursorList<Track>> {
    return localDataSource.loadAllCursor().toSingle()
  }

  override fun getAlbumTracks(album: String): Single<FlowCursorList<Track>> {
    return localDataSource.getAlbumTracks(album)
  }

  override fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>> {
    return localDataSource.getNonAlbumTracks(artist)
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Track>> {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.toCompletable().andThen(localDataSource.loadAllCursor().toSingle())
  }
}
