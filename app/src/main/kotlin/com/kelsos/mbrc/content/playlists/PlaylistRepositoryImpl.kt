package com.kelsos.mbrc.content.playlists

import android.arch.lifecycle.LiveData
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject
constructor(
    private val dao: PlaylistDao,
    private val remoteDataSource: RemotePlaylistDataSource
) : PlaylistRepository {
  private val mapper = PlaylistDtoMapper()

  override fun getAll(): Single<LiveData<List<PlaylistEntity>>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getAndSaveRemote(): Single<LiveData<List<PlaylistEntity>>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.fetch().doOnNext {
      val playlists = it.map {
        mapper.map(it).apply {
          this.dateAdded = added
        }
      }
      dao.insertAll(playlists)
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.ignoreElements()
  }

  override fun search(term: String): Single<LiveData<List<PlaylistEntity>>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.fromCallable { dao.count() == 0L }
}
