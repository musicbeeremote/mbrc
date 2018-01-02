package com.kelsos.mbrc.content.library.artists

import android.arch.lifecycle.LiveData
import com.kelsos.mbrc.utilities.epoch
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

  override fun getArtistByGenre(genre: String): Single<LiveData<List<ArtistEntity>>> {
    return Single.just(dao.getArtistByGenre(genre))
  }

  override fun getAll(): Single<LiveData<List<ArtistEntity>>> {
    return Single.just(dao.getAll())
  }

  override fun getAndSaveRemote(): Single<LiveData<List<ArtistEntity>>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.fetch().doOnNext {
      dao.insertAll(it.map { mapper.map(it).apply { dateAdded = added } })
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.ignoreElements()
  }

  override fun search(term: String): Single<LiveData<List<ArtistEntity>>> {
    return Single.just(dao.search(term))
  }

  override fun getAlbumArtistsOnly(): Single<LiveData<List<ArtistEntity>>> {
    return Single.just(dao.getAlbumArtists())
  }

  override fun getAllRemoteAndShowAlbumArtist(): Single<LiveData<List<ArtistEntity>>> {
    return getRemote().andThen(getAlbumArtistsOnly())
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.just(dao.count() == 0L)
  }
}
