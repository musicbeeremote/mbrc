package com.kelsos.mbrc.features.playlists.repository

import androidx.paging.DataSource
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.features.playlists.PlaylistDto
import com.kelsos.mbrc.features.playlists.PlaylistDtoMapper
import com.kelsos.mbrc.features.playlists.PlaylistEntityMapper
import com.kelsos.mbrc.features.playlists.data.PlaylistDao
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

interface PlaylistRepository : Repository<Playlist>

class PlaylistRepositoryImpl(
  private val dao: PlaylistDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppDispatchers
) : PlaylistRepository {

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, Playlist> {
    return dao.getAll().map { PlaylistEntityMapper.map(it) }
  }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(Protocol.PlaylistList, PlaylistDto::class, progress)
    pages.collect { page ->
      withContext(dispatchers.io) {
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

  override suspend fun getById(id: Long): Playlist? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext PlaylistEntityMapper.map(entity)
    }
  }
}
