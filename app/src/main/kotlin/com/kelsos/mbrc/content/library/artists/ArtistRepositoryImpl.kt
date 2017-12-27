package com.kelsos.mbrc.content.library.artists

import androidx.paging.PagingData
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
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

  override suspend fun getArtistByGenre(genre: String): Flow<PagingData<Artist>> {
    return dao.getArtistByGenre(genre).paged()
  }

  override suspend fun getAll(): Flow<PagingData<Artist>> = dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<Artist>> {
    getRemote()
    return dao.getAll().paged()
  }

  override suspend fun getRemote() {
    withContext(dispatchers.io) {
      val added = epoch()
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

  override suspend fun search(term: String): Flow<PagingData<Artist>> =
    dao.search(term).paged()

  override suspend fun getAlbumArtistsOnly(): Flow<PagingData<Artist>> =
    dao.getAlbumArtists().paged()

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
