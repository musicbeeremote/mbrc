package com.kelsos.mbrc.content.playlists

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class PlaylistRepositoryImpl(
  private val dao: PlaylistDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : PlaylistRepository {

  private val mapper = PlaylistDtoMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override suspend fun getAll(): DataSource.Factory<Int, PlaylistEntity> {
    return dao.getAll()
  }

  override suspend fun getRemote() {
    val added = epoch()
    remoteDataSource.getAllPages(Protocol.PlaylistList, PlaylistDto::class).blockingForEach {
      launch(dispatchers.disk) {
        val playlists = it.map {
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

  override suspend fun search(term: String): DataSource.Factory<Int, PlaylistEntity> {
    return dao.search(term)
  }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L
}