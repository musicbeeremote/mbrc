package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.di.modules.AppDispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor(
  private val localDataSource: LocalPlaylistDataSource,
  private val remoteDataSource: RemotePlaylistDataSource,
  private val dispatchers: AppDispatchers
) : PlaylistRepository {
  override suspend fun getAllCursor(): List<Playlist> = localDataSource.loadAllCursor()

  override suspend fun getAndSaveRemote(): List<Playlist> {
    getRemote()
    return localDataSource.loadAllCursor()
  }

  override suspend fun getRemote() {
    val epoch = Instant.now().epochSecond
    withContext(dispatchers.io) {
      remoteDataSource.fetch()
        .onCompletion {
          localDataSource.removePreviousEntries(epoch)
        }
        .collect { list ->
          val data = list.map { it.apply { dateAdded = epoch } }
          localDataSource.saveAll(data)
        }
    }
  }

  override suspend fun search(term: String): List<Playlist> = localDataSource.search(term)

  override suspend fun cacheIsEmpty(): Boolean = localDataSource.isEmpty()

  override suspend fun count(): Long = localDataSource.count()
}
