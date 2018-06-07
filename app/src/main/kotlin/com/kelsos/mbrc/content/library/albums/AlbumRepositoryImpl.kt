package com.kelsos.mbrc.content.library.albums

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject
constructor(
  private val dao: AlbumDao,
  private val remoteDataSource: ApiBase,
  private val coroutineDispatchers: AppCoroutineDispatchers
) : AlbumRepository {

  private val mapper = AlbumDtoMapper()

  override suspend fun count(): Long {
    return withContext(coroutineDispatchers.database) { dao.count() }
  }

  override fun getAlbumsByArtist(artist: String): Single<DataSource.Factory<Int, AlbumEntity>> {
    return Single.fromCallable { dao.getAlbumsByArtist(artist) }
  }

  override fun getAll(): Single<DataSource.Factory<Int, AlbumEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.getAllPages(Protocol.LibraryBrowseAlbums, AlbumDto::class).doOnNext {
      async(CommonPool) {
        val list = it.map { mapper.map(it).apply { dateAdded = added } }
        withContext(coroutineDispatchers.database) {
          dao.insert(list)
        }
      }
    }.doOnComplete {
      async(coroutineDispatchers.database) {
        dao.removePreviousEntries(added)
      }
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, AlbumEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.fromCallable { dao.count() == 0L }

  override fun getAlbumsSorted(
    @Sorting.Fields order: Int,
    ascending: Boolean
  ): Single<AlbumsModel> {
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

    return Single.fromCallable { model }
  }
}