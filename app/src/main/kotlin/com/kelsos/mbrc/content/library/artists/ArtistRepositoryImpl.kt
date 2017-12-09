package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.di.modules.AppDispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import javax.inject.Inject

class ArtistRepositoryImpl
@Inject
constructor(
  private val localDataSource: LocalArtistDataSource,
  private val remoteDataSource: RemoteArtistDataSource,
  private val dispatchers: AppDispatchers
) : ArtistRepository {

  override suspend fun getArtistByGenre(genre: String): List<Artist> =
    localDataSource.getArtistByGenre(genre)

  override suspend fun getAllCursor(): List<Artist> = localDataSource.loadAllCursor()

  override suspend fun getAndSaveRemote(): List<Artist> {
    getRemote()
    return localDataSource.loadAllCursor()
  }

  override suspend fun getRemote() {
    val epoch = Instant.now().epochSecond
    withContext(dispatchers.io) {
      remoteDataSource.fetch()
        .onCompletion {
          localDataSource.removePreviousEntries(epoch)
        }
        .collect { artists ->
          val data = artists.map { it.apply { dateAdded = epoch } }
          localDataSource.saveAll(data)
        }
    }
  }

  override suspend fun search(term: String): List<Artist> = localDataSource.search(term)

  override suspend fun getAlbumArtistsOnly(): List<Artist> =
    localDataSource.getAlbumArtists()

  override suspend fun getAllRemoteAndShowAlbumArtist(): List<Artist> {
    getRemote()
    return localDataSource.getAlbumArtists()
  }

  override suspend fun cacheIsEmpty(): Boolean = localDataSource.isEmpty()

  override suspend fun count(): Long = localDataSource.count()
}
