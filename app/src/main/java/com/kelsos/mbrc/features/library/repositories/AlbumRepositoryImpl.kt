package com.kelsos.mbrc.features.library.repositories

import androidx.paging.PagingData
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.data.AlbumCover
import com.kelsos.mbrc.features.library.data.AlbumDao
import com.kelsos.mbrc.features.library.data.CachedAlbumCover
import com.kelsos.mbrc.features.library.data.toAlbum
import com.kelsos.mbrc.features.library.dto.AlbumDto
import com.kelsos.mbrc.features.library.dto.toEntity
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class AlbumRepositoryImpl(
  private val dao: AlbumDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : AlbumRepository {
  override suspend fun count(): Long = withContext(dispatchers.database) { dao.count() }

  override fun getAlbumsByArtist(artist: String): Flow<PagingData<Album>> =
    paged({ dao.getAlbumsByArtist(artist) }) { it.toAlbum() }

  override fun getAll(): Flow<PagingData<Album>> = paged({ dao.getAll() }) { it.toAlbum() }

  override fun all(): List<Album> = dao.all().map { it.toAlbum() }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    return@catch withContext(dispatchers.network) {
      val added = epoch()
      val default = CachedAlbumCover(0, null)
      val cached = dao.all().associate { entry ->
        entry.album + entry.artist to CachedAlbumCover(entry.id, entry.cover)
      }
      val allPages = api.getAllPages(
        Protocol.LibraryBrowseAlbums,
        AlbumDto::class,
        progress
      )

      allPages.onCompletion {
        withContext(dispatchers.database) {
          dao.removePreviousEntries(added)
        }
      }
        .collect { albums ->
          val list = albums.map { dto ->
            dto.toEntity().apply {
              dateAdded = added
              val key = dto.album + dto.artist

              if (cached.containsKey(key)) {
                val cachedAlbum = cached.getOrDefault(key, default)
                id = cachedAlbum.id
                cover = cachedAlbum.cover
              }
            }
          }
          withContext(dispatchers.database) {
            dao.insert(list)
          }
        }
    }
  }

  override fun search(term: String): Flow<PagingData<Album>> =
    paged({ dao.search(term) }) { it.toAlbum() }

  override fun simpleSearch(term: String): List<Album> = error("unavailable method")

  override suspend fun cacheIsEmpty(): Boolean =
    withContext(dispatchers.database) { dao.count() == 0L }

  override suspend fun updateCovers(updated: List<AlbumCover>) {
    dao.updateCovers(updated)
  }

  override suspend fun getCovers(): List<AlbumCover> {
    return dao.getCovers()
  }

  override suspend fun getById(id: Long): Album? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext entity.toAlbum()
    }
  }
}
