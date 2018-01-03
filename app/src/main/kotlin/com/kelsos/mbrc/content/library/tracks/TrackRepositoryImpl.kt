package com.kelsos.mbrc.content.library.tracks

import android.arch.lifecycle.LiveData
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class TrackRepositoryImpl
@Inject constructor(
    private val dao: TrackDao,
    private val remoteDataSource: RemoteTrackDataSource,
    private val schedulerProvider: SchedulerProvider
) : TrackRepository {

  private val mapper = TrackDtoMapper()

  override fun getAll(): Single<LiveData<List<TrackEntity>>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getAlbumTracks(album: String, artist: String): Single<LiveData<List<TrackEntity>>> {
    return Single.fromCallable { dao.getAlbumTracks(album, artist) }
  }

  override fun getNonAlbumTracks(artist: String): Single<LiveData<List<TrackEntity>>> {
    return Single.fromCallable { dao.getNonAlbumTracks(artist) }
  }

  override fun getAndSaveRemote(): Single<LiveData<List<TrackEntity>>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.fetch().doOnNext {
      val tracks = it.map { mapper.map(it).apply { dateAdded = added } }
      dao.insertAll(tracks)
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.db())
        .ignoreElements()
  }

  override fun search(term: String): Single<LiveData<List<TrackEntity>>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun getGenreTrackPaths(genre: String): Single<List<String>> {
    return Single.fromCallable { dao.getGenreTrackPaths(genre) }
  }

  override fun getArtistTrackPaths(artist: String): Single<List<String>> {
    return Single.fromCallable { dao.getArtistTrackPaths(artist) }
  }

  override fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    return Single.fromCallable { dao.getAlbumTrackPaths(album, artist) }
  }

  override fun getAllTrackPaths(): Single<List<String>> {
    return Single.fromCallable { dao.getAllTrackPaths() }
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.fromCallable { dao.count() == 0L }
  }
}
