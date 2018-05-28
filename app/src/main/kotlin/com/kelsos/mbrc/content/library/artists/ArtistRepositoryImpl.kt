package com.kelsos.mbrc.content.library.artists

import android.arch.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
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

class ArtistRepositoryImpl
@Inject
constructor(
  private val dao: ArtistDao,
  private val remoteDataSource: ApiBase,
  private val coroutineDispatchers: AppCoroutineDispatchers
) : ArtistRepository {

  private val mapper = ArtistDtoMapper()

  override suspend fun count(): Long {
    return withContext(coroutineDispatchers.database) { dao.count() }
  }

  override fun getArtistByGenre(genre: String): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.fromCallable { dao.getArtistByGenre(genre) }
  }

  override fun getAll(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun allArtists(): Single<DataModel<ArtistEntity>> {
    return Single.fromCallable { DataModel(dao.getAll(), dao.getAllIndexes()) }
  }

  override fun albumArtists(): Single<DataModel<ArtistEntity>> {
    return Single.fromCallable { DataModel(dao.getAlbumArtists(), dao.getAlbumArtistIndexes()) }
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.getAllPages(Protocol.LibraryBrowseArtists, ArtistDto::class).doOnNext {
      async(CommonPool) {
        val items = it.map { mapper.map(it).apply { dateAdded = added } }
        withContext(coroutineDispatchers.database) {
          dao.insertAll(items)
        }
      }

    }.doOnComplete {
      async(coroutineDispatchers.database) {
        dao.removePreviousEntries(added)
      }
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun getAlbumArtistsOnly(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return Single.fromCallable { dao.getAlbumArtists() }
  }

  override fun getAllRemoteAndShowAlbumArtist(): Single<DataSource.Factory<Int, ArtistEntity>> {
    return getRemote().andThen(getAlbumArtistsOnly())
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.fromCallable { dao.count() == 0L }
  }
}