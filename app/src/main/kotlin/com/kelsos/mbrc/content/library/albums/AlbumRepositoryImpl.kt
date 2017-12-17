package com.kelsos.mbrc.content.library.albums

import android.arch.paging.DataSource
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject
constructor(
    private val dao: AlbumDao,
    private val remoteDataSource: RemoteAlbumDataSource
) : AlbumRepository {

  private val mapper = AlbumDtoMapper()

  override fun getAlbumsByArtist(artist: String): Single<DataSource.Factory<Int, AlbumEntity>> {
    return Single.just(dao.getAlbumsByArtist(artist))
  }

  override fun getAll(): Single<DataSource.Factory<Int, AlbumEntity>> {
    return Single.just(dao.getAll())
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, AlbumEntity>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    dao.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      dao.insert(it.map { mapper.map(it) })
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, AlbumEntity>> {
    return Single.just(dao.search(term))
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.just(dao.count() == 0L)

  override fun getAlbumsSorted(@Sorting.Fields order: Long, ascending: Boolean): Single<DataSource.Factory<Int, AlbumEntity>> {
    val factory = when (order) {
      Sorting.ALBUM -> {
        if (ascending) {
          dao.getSortedByAlbumAsc()
        } else {
          dao.getSortedByAlbumDesc()
        }
      }
      Sorting.ALBUM_ARTIST__ALBUM -> {
        if (ascending) {
          dao.getSortedByAlbumArtistAndAlbumAsc()
        } else {
          dao.getSortedByAlbumArtistAndAlbumDesc()
        }
      }
      Sorting.ALBUM_ARTIST__YEAR__ALBUM -> {
        if (ascending) {
          dao.getSortedByAlbumArtistAndYearAndAlbumAsc()
        } else {
          dao.getSortedByAlbumArtistAndYearAndAlbumDesc()
        }
      }
      Sorting.ARTIST__ALBUM -> {
        if (ascending) {
          dao.getSortedByArtistAndAlbumAsc()
        } else {
          dao.getSortedByArtistAndAlbumDesc()
        }
      }
      Sorting.GENRE__ALBUM_ARTIST__ALBUM -> {
        if (ascending) {
          dao.getSortedByGenreAndAlbumArtistAndAlbumAsc()
        } else {
          dao.getSortedByGenreAndAlbumArtistAndAlbumDesc()
        }
      }
      Sorting.YEAR__ALBUM -> {
        if (ascending) {
          dao.getSortedByYearAndAlbumAsc()
        } else {
          dao.getSortedByYearAndAlbumDesc()
        }
      }
      Sorting.YEAR__ALBUM_ARTIST__ALBUM -> {
        if (ascending) {
          dao.getSortedByYearAndAlbumArtistAndAlbumAsc()
        } else {
          dao.getSortedByYearAndAlbumArtistAndAlbumDesc()
        }
      }
      else -> throw IllegalArgumentException("Invalid option")
    }

    return Single.just(factory)
  }
}
