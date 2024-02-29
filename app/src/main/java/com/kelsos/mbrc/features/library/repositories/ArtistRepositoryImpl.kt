package com.kelsos.mbrc.features.library.repositories

import androidx.paging.PagingData
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.data.TestApi
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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class ArtistRepositoryImpl(
  private val dao: ArtistDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : ArtistRepository {
  override val test: TestApi<Artist> = object : TestApi<Artist> {
    override fun search(term: String): List<Artist> = error("unavailable method")
    override fun getAll(): List<Artist> = dao.all().map { it.toArtist() }
  }

  override suspend fun count(): Long = withContext(dispatchers.database) {
    dao.count()
  }

  override fun getArtistByGenre(genreId: Long): Flow<PagingData<Artist>> =
    paged({ dao.getArtistByGenre(genreId) }) { it.toArtist() }

  override fun getAll(): Flow<PagingData<Artist>> = paged({ dao.getAll() }) {
    it.toArtist()
  }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    withContext(dispatchers.network) {
      val added = epoch()
      val allPages = api.getAllPages(
        Protocol.LibraryBrowseArtists,
        ArtistDto::class,
        progress
      )

      allPages.onCompletion {
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
    paged({ dao.search(term) }) { it.toArtist() }

  override fun getAlbumArtistsOnly(): Flow<PagingData<Artist>> =
    paged({ dao.getAlbumArtists() }) { it.toArtist() }

  override suspend fun getById(id: Long): Artist? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext entity.toArtist()
    }
  }
}
