package com.kelsos.mbrc.features.library.repositories

import androidx.paging.PagingData
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.data.ArtistDao
import com.kelsos.mbrc.features.library.data.toArtist
import com.kelsos.mbrc.features.library.dto.ArtistDto
import com.kelsos.mbrc.features.library.dto.toEntity
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class ArtistRepositoryImpl(
  private val dao: ArtistDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : ArtistRepository {

  override suspend fun count(): Long = withContext(dispatchers.database) {
    dao.count()
  }

  override fun getArtistByGenre(genre: String): Flow<PagingData<Artist>> =
    dao.getArtistByGenre(genre).paged { it.toArtist() }

  override fun getAll(): Flow<PagingData<Artist>> = dao.getAll().paged {
    it.toArtist()
  }

  override suspend fun getRemote(): Try<Unit> = Try {
    withContext(dispatchers.network) {
      val added = epoch()
      api.getAllPages(Protocol.LibraryBrowseArtists, ArtistDto::class)
        .onCompletion {
          withContext(dispatchers.database) {
            dao.removePreviousEntries(added)
          }
        }
        .collect { artists ->
          val data = artists.map { it.toEntity().apply { dateAdded = added } }
          withContext(dispatchers.database) {
            dao.insertAll(data)
          }
        }
    }
  }

  override fun search(term: String): Flow<PagingData<Artist>> =
    dao.search(term).paged { it.toArtist() }

  override fun getAlbumArtistsOnly(): Flow<PagingData<Artist>> =
    dao.getAlbumArtists().paged { it.toArtist() }

  override suspend fun cacheIsEmpty(): Boolean = withContext(dispatchers.database) {
    dao.count() == 0L
  }
}
