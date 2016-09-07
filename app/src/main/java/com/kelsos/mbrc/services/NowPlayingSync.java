package com.kelsos.mbrc.services;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.data.NowPlaying;
import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.db.NowPlayingDatabase;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import java.util.List;
import javax.inject.Inject;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Scheduler;
import timber.log.Timber;

public class NowPlayingSync {
  private static final int LIMIT = 1500;
  @Inject NowPlayingService service;

  @NonNull
  public Completable syncNowPlaying(Scheduler scheduler) {
    return Completable.create((CompletableSubscriber subscriber) -> {
      long count = SQLite.delete().from(NowPlaying.class).count();
      Timber.v("Deleted %d previous cached now playing tracks", count);
      Observable.range(0, Integer.MAX_VALUE)
          .flatMap(page -> service.getNowPlaying(page * LIMIT, LIMIT))
          .subscribeOn(scheduler)
          .takeWhile(albumPage -> albumPage.getOffset() < albumPage.getTotal())
          .map(Page::getData)
          .subscribe(this::saveTracks, subscriber::onError, subscriber::onCompleted);
    });
  }

  private void saveTracks(List<NowPlaying> nowPlayings) {
    FastStoreModelTransaction<NowPlaying> transaction =
        FastStoreModelTransaction.insertBuilder(FlowManager.getModelAdapter(NowPlaying.class))
            .addAll(nowPlayings)
            .build();

    FlowManager.getDatabase(NowPlayingDatabase.class).executeTransaction(transaction);
  }
}
