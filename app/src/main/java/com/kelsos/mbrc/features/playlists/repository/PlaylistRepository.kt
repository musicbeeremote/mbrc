package com.kelsos.mbrc.features.playlists.repository

import androidx.paging.DataSource
import arrow.core.Try
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.features.playlists.PlaylistDto
import com.kelsos.mbrc.features.playlists.PlaylistDtoMapper
import com.kelsos.mbrc.features.playlists.PlaylistEntityMapper
import com.kelsos.mbrc.features.playlists.data.PlaylistDao
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

interface PlaylistRepository : Repository<Playlist>

class PlaylistRepositoryImpl(
  private val dao: PlaylistDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : PlaylistRepository {

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, Playlist> {
    return dao.getAll().map { PlaylistEntityMapper.map(it) }
  }

  override suspend fun getRemote(): Try<Unit> = Try {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(Protocol.PlaylistList, PlaylistDto::class)
    pages.blockingForEach { page ->
      runBlocking(dispatchers.disk) {
        val playlists = page.map {
          PlaylistDtoMapper.map(it).apply {
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
    return dao.search(term).map { PlaylistEntityMapper.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L
}