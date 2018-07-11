package com.kelsos.mbrc.content.library.albums

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class AlbumRepositoryImpl(
  private val dao: AlbumDao,
  private val remoteDataSource: ApiBase,
  private val coroutineDispatchers: AppCoroutineDispatchers
) : AlbumRepository {

  private val mapper = AlbumDtoMapper()

  override suspend fun count(): Long {
    return withContext(coroutineDispatchers.database) { dao.count() }
  }

  override suspend fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, AlbumEntity> {
    return withContext(coroutineDispatchers.database) { dao.getAlbumsByArtist(artist) }
  }

  override suspend fun getAll(): DataSource.Factory<Int, AlbumEntity> {
    return withContext(coroutineDispatchers.database) { dao.getAll() }
  }

  override suspend fun getRemote() {
    val added = epoch()
    remoteDataSource.getAllPages(Protocol.LibraryBrowseAlbums, AlbumDto::class).blockingForEach {
      launch(coroutineDispatchers.disk) {
        val list = it.map { mapper.map(it).apply { dateAdded = added } }
        withContext(coroutineDispatchers.database) {
          dao.insert(list)
        }
      }
    }

    withContext(coroutineDispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, AlbumEntity> {
    return dao.search(term)
  }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override fun getAlbumsSorted(
    @Sorting.Fields order: Int,
    ascending: Boolean
  ): AlbumsModel {
    val model = when (order) {
      Sorting.ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByAlbumAscIndexes()
          } else {
            dao.getSortedByAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByAlbumAsc()
          } else {
            dao.getSortedByAlbumDesc()
          }
        )
      }
      Sorting.ALBUM_ARTIST__ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByAlbumArtistAndAlbumAscIndexes()
          } else {
            dao.getSortedByAlbumArtistAndAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByAlbumArtistAndAlbumAsc()
          } else {
            dao.getSortedByAlbumArtistAndAlbumDesc()
          }
        )

      }
      Sorting.ALBUM_ARTIST__YEAR__ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByAlbumArtistAndYearAndAlbumAscIndexes()
          } else {
            dao.getSortedByAlbumArtistAndYearAndAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByAlbumArtistAndYearAndAlbumAsc()
          } else {
            dao.getSortedByAlbumArtistAndYearAndAlbumDesc()
          }

        )

      }
      Sorting.ARTIST__ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByArtistAndAlbumAscIndexes()
          } else {
            dao.getSortedByArtistAndAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByArtistAndAlbumAsc()
          } else {
            dao.getSortedByArtistAndAlbumDesc()
          }
        )
      }
      Sorting.GENRE__ALBUM_ARTIST__ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByGenreAndAlbumArtistAndAlbumAscIndexes()
          } else {
            dao.getSortedByGenreAndAlbumArtistAndAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByGenreAndAlbumArtistAndAlbumAsc()
          } else {
            dao.getSortedByGenreAndAlbumArtistAndAlbumDesc()
          }
        )
      }
      Sorting.YEAR__ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByYearAndAlbumAscIndexes()
          } else {
            dao.getSortedByYearAndAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByYearAndAlbumAsc()
          } else {
            dao.getSortedByYearAndAlbumDesc()
          }
        )
      }
      Sorting.YEAR__ALBUM_ARTIST__ALBUM -> {
        AlbumsModel(
          order,
          if (ascending) {
            dao.getSortedByYearAndAlbumArtistAndAlbumAscIndexes()
          } else {
            dao.getSortedByYearAndAlbumArtistAndAlbumDescIndexes()
          },
          if (ascending) {
            dao.getSortedByYearAndAlbumArtistAndAlbumAsc()
          } else {
            dao.getSortedByYearAndAlbumArtistAndAlbumDesc()
          }
        )
      }
      else -> throw IllegalArgumentException("Invalid option")
    }

    return model
  }
}