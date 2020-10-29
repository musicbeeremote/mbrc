package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.repository.data.LocalAlbumDataSource
import com.kelsos.mbrc.repository.data.RemoteAlbumDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject
constructor(
  private val localDataSource: LocalAlbumDataSource,
  private val remoteDataSource: RemoteAlbumDataSource,
  private val dispatchers: AppDispatchers
) : AlbumRepository {
  override suspend fun getAlbumsByArtist(artist: String): FlowCursorList<Album> =
    localDataSource.getAlbumsByArtist(artist)

  override suspend fun getAllCursor(): FlowCursorList<Album> = localDataSource.loadAllCursor()

  override suspend fun getAndSaveRemote(): FlowCursorList<Album> {
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

  override suspend fun search(term: String): FlowCursorList<Album> = localDataSource.search(term)

  override suspend fun cacheIsEmpty(): Boolean = localDataSource.isEmpty()
}
