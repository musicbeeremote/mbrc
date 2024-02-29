package com.kelsos.mbrc.features.playlists

import androidx.paging.PagingData
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.common.data.TestApi
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

interface PlaylistRepository : Repository<Playlist>

class PlaylistRepositoryImpl(
  private val dao: PlaylistDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : PlaylistRepository {
  override val test: TestApi<Playlist> = object : TestApi<Playlist> {
    override fun getAll(): List<Playlist> = dao.all().map { it.toPlaylist() }
    override fun search(term: String): List<Playlist> =
      dao.simpleSearch(term).map { it.toPlaylist() }
  }

  override suspend fun count(): Long = withContext(dispatchers.database) { dao.count() }

  override fun getAll(): Flow<PagingData<Playlist>> =
    paged({ dao.getAll() }) { it.toPlaylist() }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    withContext(dispatchers.network) {
      val added = epoch()
      val allPages = api.getAllPages(
        Protocol.PlaylistList,
        PlaylistDto::class,
        progress
      )
      allPages.onCompletion {
        withContext(dispatchers.database) {
          dao.removePreviousEntries(added)
        }
      }.collect { items ->
        val playlists = items.map {
          PlaylistDtoMapper.map(it).apply {
            this.dateAdded = added
          }
        }
        withContext(dispatchers.database) {
          dao.insertAll(playlists)
        }
      }
    }
  }

  override fun search(term: String): Flow<PagingData<Playlist>> =
    paged({ dao.search(term) }) { it.toPlaylist() }

  override suspend fun getById(id: Long): Playlist? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext entity.toPlaylist()
    }
  }
}
