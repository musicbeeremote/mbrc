package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.db.CacheDatabase
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Completable
import rx.CompletableSubscriber
import rx.Observable
import rx.Scheduler
import rx.functions.Action0
import rx.functions.Action1
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class BrowseSync {

  @Inject lateinit var service: LibraryService

  fun sync() {
    val scheduler = Schedulers.immediate()
    Completable.concat(syncGenres(scheduler),
        syncArtists(scheduler),
        syncAlbums(scheduler),
        syncTracks(scheduler))
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .subscribe({ Timber.v("Sync complete successfully") }) { t ->
          Timber.v(t,
              "Sync failed due to reasons")
        }
  }

  fun syncTracks(scheduler: Scheduler): Completable {
    return Completable.create { subscriber: CompletableSubscriber ->
      val count = SQLite.delete().from(Track::class.java).count()
      Timber.v("Deleted %d previous cached Tracks", count)
      Observable.range(0,
          Integer.MAX_VALUE).flatMap {
        service.getTracks(it!! * LIMIT, LIMIT)
      }.subscribeOn(scheduler).takeWhile { it.offset < it.total }.map { it.data }.subscribe(Action1<List<Track>> {
        this.saveTracks(it)
      }, Action1<Throwable> { subscriber.onError(it) }, Action0 { subscriber.onCompleted() })
    }
  }

  private fun saveTracks(tracks: List<Track>) {
    val adapter = FlowManager.getModelAdapter(Track::class.java)
    val transactionModel = FastStoreModelTransaction.insertBuilder(adapter).addAll(tracks).build()
    FlowManager.getDatabase(CacheDatabase::class.java).executeTransaction(transactionModel)
  }

  private fun saveAlbums(albums: List<Album>) {
    val adapter = FlowManager.getModelAdapter(Album::class.java)
    val transactionModel = FastStoreModelTransaction.insertBuilder(adapter).addAll(albums).build()
    FlowManager.getDatabase(CacheDatabase::class.java).executeTransaction(transactionModel)
  }

  private fun saveArtists(artists: List<Artist>) {
    val adapter = FlowManager.getModelAdapter(Artist::class.java)
    val transactionModel = FastStoreModelTransaction.insertBuilder(adapter).addAll(artists).build()
    FlowManager.getDatabase(CacheDatabase::class.java).executeTransaction(transactionModel)
  }

  private fun saveGenres(genres: List<Genre>) {
    val adapter = FlowManager.getModelAdapter(Genre::class.java)
    val transactionModel = FastStoreModelTransaction.insertBuilder(adapter).addAll(genres).build()
    FlowManager.getDatabase(CacheDatabase::class.java).executeTransaction(transactionModel)
  }

  fun syncAlbums(scheduler: Scheduler): Completable {
    return Completable.create { subscriber: CompletableSubscriber ->
      val count = SQLite.delete().from(Album::class.java).count()
      Timber.v("Deleted %d previous cached Albums", count)
      Observable.range(0, Integer.MAX_VALUE).flatMap { page ->
        service.getAlbums(page!! * LIMIT, LIMIT)
      }.subscribeOn(scheduler).takeWhile { it.offset < it.total }.map { it.data }.subscribe(Action1<List<Album>> {
        this.saveAlbums(it)
      }, Action1<Throwable> { subscriber.onError(it) }, Action0 { subscriber.onCompleted() })
    }
  }

  fun syncArtists(scheduler: Scheduler): Completable {

    return Completable.create { subscriber: CompletableSubscriber ->
      val count = SQLite.delete().from(Artist::class.java).count()
      Timber.v("Deleted %d previous cached Artists", count)

      Observable.range(0, Integer.MAX_VALUE).flatMap {
        service.getArtists(it!! * LIMIT, LIMIT)
      }.subscribeOn(scheduler).takeWhile { it.offset < it.total }.map { it.data }.subscribe(Action1<List<Artist>> {
        this.saveArtists(it)
      }, Action1<Throwable> { subscriber.onError(it) }, Action0 { subscriber.onCompleted() })
    }
  }

  fun syncGenres(scheduler: Scheduler): Completable {

    return Completable.create { subscriber: CompletableSubscriber ->
      val count = SQLite.delete().from(Genre::class.java).count()
      Timber.v("Deleted %d previous cached genres", count)

      Observable.range(0, Integer.MAX_VALUE).flatMap {
        service.getGenres(it!! * LIMIT, LIMIT)
      }.subscribeOn(scheduler).takeWhile { it.offset < it.total }.map { it.data }.subscribe(Action1<List<Genre>> {
        this.saveGenres(it)
      }, Action1<Throwable> { subscriber.onError(it) }, Action0 { subscriber.onCompleted() })
    }
  }

  companion object {
    const val LIMIT = 400
  }
}
