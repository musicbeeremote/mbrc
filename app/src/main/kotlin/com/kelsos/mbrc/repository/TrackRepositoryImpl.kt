package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.repository.data.LocalTrackDataSource
import com.kelsos.mbrc.repository.data.RemoteTrackDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrackRepositoryImpl
@Inject constructor(
  private val localDataSource: LocalTrackDataSource,
  private val remoteDataSource: RemoteTrackDataSource,
  private val dispatchers: AppDispatchers
) : TrackRepository {

  override suspend fun getAllCursor(): FlowCursorList<Track> = localDataSource.loadAllCursor()

  override suspend fun getAlbumTracks(album: String, artist: String): FlowCursorList<Track> =
    localDataSource.getAlbumTracks(album, artist)

  override suspend fun getNonAlbumTracks(artist: String): FlowCursorList<Track> =
    localDataSource.getNonAlbumTracks(artist)

  override suspend fun getAndSaveRemote(): FlowCursorList<Track> {
    getRemote()
    return localDataSource.loadAllCursor()
  }

  override suspend fun getRemote() {
    localDataSource.deleteAll()
    withContext(dispatchers.io) {
      remoteDataSource.fetch().collect {
        localDataSource.saveAll(it)
      }
    }
  }

  override suspend fun search(term: String): FlowCursorList<Track> {
    return localDataSource.search(term)
  }

  override suspend fun getGenreTrackPaths(genre: String): List<String> {
    return localDataSource.getGenreTrackPaths(genre)
  }

  override suspend fun getArtistTrackPaths(artist: String): List<String> =
    localDataSource.getArtistTrackPaths(artist)

  override suspend fun getAlbumTrackPaths(album: String, artist: String): List<String> =
    localDataSource.getAlbumTrackPaths(album, artist)

  override suspend fun getAllTrackPaths(): List<String> = localDataSource.getAllTrackPaths()

  override suspend fun cacheIsEmpty(): Boolean = localDataSource.isEmpty()
}
