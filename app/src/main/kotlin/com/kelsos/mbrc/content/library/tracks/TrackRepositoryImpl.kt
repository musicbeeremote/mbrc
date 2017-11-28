package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.content.library.UpdatedDataSource
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.Instant
import javax.inject.Inject

class TrackRepositoryImpl
@Inject constructor(
    private val localDataSource: LocalTrackDataSource,
    private val remoteDataSource: RemoteTrackDataSource,
    private val updatedDataSource: UpdatedDataSource,
    private val schedulerProvider: SchedulerProvider
) : TrackRepository {

  override fun getAllCursor(): Single<FlowCursorList<Track>> {
    return localDataSource.loadAllCursor().singleOrError()
  }

  override fun getAlbumTracks(album: String, artist: String): Single<FlowCursorList<Track>> {
    return localDataSource.getAlbumTracks(album, artist)
  }

  override fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>> {
    return localDataSource.getNonAlbumTracks(artist)
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Track>> {
    return getRemote().andThen(localDataSource.loadAllCursor().singleOrError())
  }

  override fun getRemote(): Completable {
    val epoch = Instant.now().epochSecond

    //localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
      updatedDataSource.addUpdated(it.mapNotNull { it.src }, epoch)
    }.doOnComplete {
      val paths = updatedDataSource.getPathInsertedAtEpoch(epoch)
      localDataSource.deletePaths(paths)
      updatedDataSource.deleteAll()
    }.subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.db())
        .ignoreElements()
  }

  override fun search(term: String): Single<FlowCursorList<Track>> {
    return localDataSource.search(term)
  }

  override fun getGenreTrackPaths(genre: String): Single<List<String>> {
    return localDataSource.getGenreTrackPaths(genre)
  }

  override fun getArtistTrackPaths(artist: String): Single<List<String>> {
    return localDataSource.getArtistTrackPaths(artist)
  }

  override fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    return localDataSource.getAlbumTrackPaths(album, artist)
  }

  override fun getAllTrackPaths(): Single<List<String>> {
    return localDataSource.getAllTrackPaths()
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
