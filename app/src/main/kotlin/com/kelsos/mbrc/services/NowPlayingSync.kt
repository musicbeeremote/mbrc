package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.db.NowPlayingDatabase
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Completable
import rx.CompletableSubscriber
import rx.Observable
import rx.Scheduler
import timber.log.Timber
import javax.inject.Inject

class NowPlayingSync {
  @Inject lateinit var service: NowPlayingService

  fun syncNowPlaying(scheduler: Scheduler): Completable {
    return Completable.create { subscriber: CompletableSubscriber ->
      val count = SQLite.delete().from(NowPlaying::class.java).count()
      Timber.v("Deleted %d previous cached now playing tracks", count)
      Observable.range(0, Integer.MAX_VALUE)
          .flatMap { service.getNowPlaying(it!! * LIMIT, LIMIT) }
          .subscribeOn(scheduler)
          .takeWhile { it.offset < it.total }
          .map { it.data }
          .subscribe({ this.saveTracks(it) }, { subscriber.onError(it) }, { subscriber.onCompleted() })
    }
  }

  private fun saveTracks(nowPlayings: List<NowPlaying>) {
    val modelAdapter = FlowManager.getModelAdapter(NowPlaying::class.java)
    val transaction = FastStoreModelTransaction.insertBuilder(modelAdapter).addAll(nowPlayings).build()
    FlowManager.getDatabase(NowPlayingDatabase::class.java).executeTransaction(transaction)
  }

  companion object {
    private val LIMIT = 1500
  }
}
