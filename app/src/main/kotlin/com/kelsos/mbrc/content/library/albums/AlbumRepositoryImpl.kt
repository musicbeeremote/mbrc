package com.kelsos.mbrc.content.library.albums

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
  private val localDataSource: LocalAlbumDataSource,
  private val remoteDataSource: RemoteAlbumDataSource,
  private val dispatchers: AppDispatchers
) : AlbumRepository {

  override suspend fun getAlbumsByArtist(artist: String): List<Album> =
    localDataSource.getAlbumsByArtist(artist)

  override suspend fun getAllCursor(): List<Album> = localDataSource.loadAllCursor()

  override suspend fun getAndSaveRemote(): List<Album> {
    getRemote()
    return localDataSource.loadAllCursor()
  }

  override suspend fun getRemote() {
    val epoch = Instant.now().epochSecond
    val default = CachedAlbumCover(0, null)
    val cached = localDataSource.loadAllCursor().associate {
      it.album + it.artist to CachedAlbumCover(it.id, it?.cover)
    }
    withContext(dispatchers.io) {
      remoteDataSource.fetch()
        .onCompletion {
          localDataSource.removePreviousEntries(epoch)
        }
        .collect { albums ->
          val list = albums.map {
            it.apply {
              dateAdded = epoch
              val key = it.album + it.artist

              if (cached.containsKey(key)) {
                val cachedAlbum = cached.getOrDefault(key, default)
                id = cachedAlbum.id
                cover = cachedAlbum.cover
              }
            }
          }
          localDataSource.saveAll(list)
        }
    }
  }

  override suspend fun search(term: String): List<Album> = localDataSource.search(term)

  override suspend fun cacheIsEmpty(): Boolean = localDataSource.isEmpty()

  override suspend fun count(): Long = localDataSource.count()

  override suspend fun updateCovers(updated: List<AlbumCover>) {
    localDataSource.updateCovers(updated)
  }
}
