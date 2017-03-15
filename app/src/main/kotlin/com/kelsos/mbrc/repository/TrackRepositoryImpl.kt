package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.repository.data.LocalTrackDataSource
import com.kelsos.mbrc.repository.data.RemoteTrackDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class TrackRepositoryImpl
@Inject constructor(private val localDataSource: LocalTrackDataSource,
                    private val remoteDataSource: RemoteTrackDataSource) : TrackRepository {

  override fun getAllCursor(): Single<FlowCursorList<Track>> {
    return localDataSource.loadAllCursor().singleOrError()
  }

  override fun getAlbumTracks(album: String, artist: String): Single<FlowCursorList<Track>> {
    return localDataSource.getAlbumTracks(album, artist)
  }

  override fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>> {
    return localDataSource.getNonAlbumTracks(artist)
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Track>> {
    return getRemote().andThen(localDataSource.loadAllCursor().singleOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<FlowCursorList<Track>> {
    return localDataSource.search(term)
  }

  override fun getGenreTrackPaths(genre: String): Single<List<String>> {
    return localDataSource.getGenreTrackPaths(genre)
  }

  override fun getArtistTrackPaths(artist: String): Single<List<String>> {
    return localDataSource.getArtistTrackPaths(artist)
  }

  override fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    return localDataSource.getAlbumTrackPaths(album, artist)
  }

  override fun getAllTrackPaths(): Single<List<String>> {
    return localDataSource.getAllTrackPaths()
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
