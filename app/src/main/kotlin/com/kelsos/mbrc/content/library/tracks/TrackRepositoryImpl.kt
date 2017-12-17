package com.kelsos.mbrc.content.library.tracks

import android.arch.paging.DataSource
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

  override fun getAll(): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.just(dao.getAll())
  }

  override fun getAlbumTracks(album: String, artist: String): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.just(dao.getAlbumTracks(album, artist))
  }

  override fun getNonAlbumTracks(artist: String): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.just(dao.getNonAlbumTracks(artist))
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, TrackEntity>> {
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

  override fun search(term: String): Single<DataSource.Factory<Int, TrackEntity>> {
    return Single.just(dao.search(term))
  }

  override fun getGenreTrackPaths(genre: String): Single<List<String>> {
    return Single.just(dao.getGenreTrackPaths(genre))
  }

  override fun getArtistTrackPaths(artist: String): Single<List<String>> {
    return Single.just(dao.getArtistTrackPaths(artist))
  }

  override fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    return Single.just(dao.getAlbumTrackPaths(album, artist))
  }

  override fun getAllTrackPaths(): Single<List<String>> {
    return Single.just(dao.getAllTrackPaths())
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.just(dao.count() == 0L)
  }
}
