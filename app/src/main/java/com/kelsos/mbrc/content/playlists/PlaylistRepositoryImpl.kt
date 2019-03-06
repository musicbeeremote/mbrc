package com.kelsos.mbrc.content.playlists

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
  private val dao: PlaylistDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : PlaylistRepository {

  private val mapper = PlaylistDtoMapper()
  private val entity2model = PlaylistEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, Playlist> {
    return dao.getAll().map { entity2model.map(it) }
  }

  override suspend fun getRemote() {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(Protocol.PlaylistList, PlaylistDto::class)
    pages.blockingForEach { page ->
      runBlocking(dispatchers.disk) {
        val playlists = page.map {
          mapper.map(it).apply {
            this.dateAdded = added
          }
        }
        withContext(dispatchers.database) {
          dao.insertAll(playlists)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, Playlist> {
    return dao.search(term).map { entity2model.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L
}