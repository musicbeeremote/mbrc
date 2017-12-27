package com.kelsos.mbrc.content.playlists

import androidx.paging.PagingData
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
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

  override suspend fun getAll(): Flow<PagingData<Playlist>> = dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<Playlist>> {
    getRemote()
    return dao.getAll().paged()
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

  override suspend fun search(term: String): Flow<PagingData<Playlist>> =
    dao.search(term).paged()

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
