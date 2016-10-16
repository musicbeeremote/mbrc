package com.kelsos.mbrc.services

import rx.Completable
import rx.Scheduler
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
    return Completable.complete()
  }

  fun syncAlbums(scheduler: Scheduler): Completable {
    return Completable.complete()
  }

  fun syncArtists(scheduler: Scheduler): Completable {
    return Completable.complete()
  }

  fun syncGenres(scheduler: Scheduler): Completable {
    return Completable.complete()
  }


}
