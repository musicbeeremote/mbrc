package com.kelsos.mbrc.content.playlists

import androidx.paging.DataSource
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor(
  private val dao: PlaylistDao,
  private val remoteDataSource: RemotePlaylistDataSource,
  private val dispatchers: AppDispatchers
) : PlaylistRepository {
  private val mapper = PlaylistDtoMapper()

  override suspend fun getAll(): DataSource.Factory<Int, Playlist> = dao.getAll().map { it }

  override suspend fun getAndSaveRemote(): DataSource.Factory<Int, Playlist> {
    getRemote()
    return dao.getAll().map { it }
  }

  override suspend fun getRemote() {
    val added = epoch()
    withContext(dispatchers.io) {
      remoteDataSource.fetch().onCompletion {
        dao.removePreviousEntries(added)
      }.collect { items ->
        val playlists = items.map {
          mapper.map(it).apply {
            this.dateAdded = added
          }
        }
        dao.insertAll(playlists)
      }
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, Playlist> =
    dao.search(term).map { it }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
