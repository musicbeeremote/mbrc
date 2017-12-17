package com.kelsos.mbrc.content.library.artists

import androidx.paging.DataSource
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistRepositoryImpl
@Inject
constructor(
  private val dao: ArtistDao,
  private val remoteDataSource: RemoteArtistDataSource,
  private val dispatchers: AppDispatchers
) : ArtistRepository {

  private val mapper = ArtistDtoMapper()

  override suspend fun getArtistByGenre(genre: String): DataSource.Factory<Int, Artist> {
    return dao.getArtistByGenre(genre).map { it }
  }

  override suspend fun getAll(): DataSource.Factory<Int, Artist> = dao.getAll().map { it }

  override suspend fun getAndSaveRemote(): DataSource.Factory<Int, Artist> {
    getRemote()
    return dao.getAll().map { it }
  }

  override suspend fun getRemote() {
    val added = epoch()
    withContext(dispatchers.io) {
      remoteDataSource.fetch()
        .onCompletion {
          dao.removePreviousEntries(added)
        }
        .collect { artists ->
          val data = artists.map { mapper.map(it).apply { dateAdded = added } }
          dao.insertAll(data)
        }
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, Artist> =
    dao.search(term).map { it }

  override suspend fun getAlbumArtistsOnly(): DataSource.Factory<Int, Artist> =
    dao.getAlbumArtists().map { it }

  override suspend fun getAllRemoteAndShowAlbumArtist(): DataSource.Factory<Int, Artist> {
    getRemote()
    return dao.getAlbumArtists().map { it }
  }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
