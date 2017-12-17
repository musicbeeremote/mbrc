package com.kelsos.mbrc.content.library.artists

import android.arch.paging.DataSource
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ArtistRepositoryImpl
@Inject
constructor(
    private val dao: ArtistDao,
    private val remoteDataSource: RemoteArtistDataSource
) : ArtistRepository {

  private val mapper = ArtistDtoMapper()

  override fun getArtistByGenre(genre: String): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.just(dao.getArtistByGenre(genre))
  }

  override fun getAll(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.just(dao.getAll())
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    dao.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      dao.insertAll(it.map { mapper.map(it) })
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.just(dao.search(term))
  }

  override fun getAlbumArtistsOnly(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.just(dao.getAlbumArtists())
  }

  override fun getAllRemoteAndShowAlbumArtist(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return getRemote().andThen(getAlbumArtistsOnly())
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.just(dao.count() == 0L)
  }
}
