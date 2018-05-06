package com.kelsos.mbrc.content.library.albums

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.covers.AlbumCover
import com.kelsos.mbrc.content.library.covers.CachedAlbumCover
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject
constructor(
  private val dao: AlbumDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : AlbumRepository {
  private val mapper = AlbumDtoMapper()

  override suspend fun getAlbumsByArtist(artist: String): Flow<PagingData<Album>> =
    dao.getAlbumsByArtist(artist).paged()

  override suspend fun getAll(): Flow<PagingData<Album>> = dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<Album>> {
    getRemote()
    return dao.getAll().paged()
  }

  override suspend fun getRemote() {
    val added = epoch()
    val default = CachedAlbumCover(0, null)
    val cached = dao.all().associate { entry ->
      entry.album + entry.artist to CachedAlbumCover(entry.id, entry.cover)
    }
    withContext(dispatchers.network) {
      api.getAllPages(Protocol.LibraryBrowseAlbums, AlbumDto::class)
        .onCompletion {
          dao.removePreviousEntries(added)
        }
        .collect { albums ->
          val list = albums.map { dto ->
            mapper.map(dto).apply {
              dateAdded = added
              val key = dto.album + dto.artist

              if (cached.containsKey(key)) {
                val cachedAlbum = cached.getOrDefault(key, default)
                id = cachedAlbum.id
                cover = cachedAlbum.cover
              }
            }
          }
          dao.insert(list)
        }
    }
  }

  override suspend fun search(term: String): Flow<PagingData<Album>> =
    dao.search(term).paged()

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()

  override suspend fun updateCovers(updated: List<AlbumCover>) {
    dao.updateCovers(updated)
  }

  override suspend fun getCovers(): List<AlbumCover> {
    return dao.getCovers()
  }
}
