package com.kelsos.mbrc.content.library.albums

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.covers.AlbumCover
import com.kelsos.mbrc.content.library.covers.CachedAlbumCover
import com.kelsos.mbrc.di.modules.AppDispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject
constructor(
  private val dao: AlbumDao,
  private val remoteDataSource: RemoteAlbumDataSource,
  private val dispatchers: AppDispatchers
) : AlbumRepository {
  private val mapper = AlbumDtoMapper()

  override suspend fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, Album> =
    dao.getAlbumsByArtist(artist).map { it }

  override suspend fun getAll(): DataSource.Factory<Int, Album> = dao.getAll().map { it }

  override suspend fun getAndSaveRemote(): DataSource.Factory<Int, Album> {
    getRemote()
    return dao.getAll().map { it }
  }

  override suspend fun getRemote() {
    val epoch = Instant.now().epochSecond
    val default = CachedAlbumCover(0, null)
    val cached = dao.all().associate { entry ->
      entry.album + entry.artist to CachedAlbumCover(entry.id, entry.cover)
    }
    withContext(dispatchers.io) {
      remoteDataSource.fetch()
        .onCompletion {
          dao.removePreviousEntries(epoch)
        }
        .collect { albums ->
          val list = albums.map { dto ->
            mapper.map(dto).apply {
              dateAdded = epoch
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

  override suspend fun search(term: String): DataSource.Factory<Int, Album> =
    dao.search(term).map { it }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()

  override suspend fun updateCovers(updated: List<AlbumCover>) {
    dao.updateCovers(updated)
  }

  override suspend fun getCovers(): List<AlbumCover> {
    return dao.getCovers()
  }
}
