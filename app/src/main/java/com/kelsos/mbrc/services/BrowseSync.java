package com.kelsos.mbrc.services;

import android.support.annotation.NonNull;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Cache;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class BrowseSync {
  public static final int LIMIT = 400;

  @Inject
  private LibraryService service;

  public void sync() {

    Completable.concat(syncGenres(), syncArtists(), syncAlbums(), syncTracks())
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .subscribe(t -> {
          Timber.v(t, "Sync failed due to reasons");
        }, () -> {
          Timber.v("Sync complete successfully");
        });
  }

  @NonNull
  private Completable syncTracks() {
    return Completable.create(subscriber -> {
      FlowManager.getDatabase(Cache.class).executeTransaction(dbw -> {
        long count = SQLite.delete().from(Track.class).count();
        Timber.v("Deleted %d previous cached Tracks", count);

        Observable.range(0, Integer.MAX_VALUE)
            .flatMap(page -> service.getTracks(page * LIMIT, LIMIT))
            .subscribeOn(Schedulers.immediate())
            .takeWhile(TrackPage -> TrackPage.getOffset() < TrackPage.getTotal())
            .map(Page::getData)
            .flatMap(Observable::from)
            .subscribe(BaseModel::save, subscriber::onError, subscriber::onCompleted);
      });
    });
  }

  @NonNull
  private Completable syncAlbums() {
    return Completable.create(subscriber -> {
      FlowManager.getDatabase(Cache.class).executeTransaction(dbw -> {
        long count = SQLite.delete().from(Album.class).count();
        Timber.v("Deleted %d previous cached Albums", count);

        Observable.range(0, Integer.MAX_VALUE)
            .flatMap(page -> service.getAlbums(page * LIMIT, LIMIT))
            .subscribeOn(Schedulers.immediate())
            .takeWhile(albumPage -> albumPage.getOffset() < albumPage.getTotal())
            .map(Page::getData)
            .flatMap(Observable::from)
            .subscribe(BaseModel::save, subscriber::onError, subscriber::onCompleted);
      });
    });
  }

  @NonNull
  private Completable syncArtists() {
    return Completable.create(subscriber -> {
      FlowManager.getDatabase(Cache.class).executeTransaction(dbw -> {
        long count = SQLite.delete().from(Artist.class).count();
        Timber.v("Deleted %d previous cached Artists", count);

        Observable.range(0, Integer.MAX_VALUE)
            .flatMap(page -> service.getArtists(page * LIMIT, LIMIT))
            .subscribeOn(Schedulers.immediate())
            .takeWhile(artistPage -> artistPage.getOffset() < artistPage.getTotal())
            .map(Page::getData)
            .flatMap(Observable::from)
            .subscribe(BaseModel::save, subscriber::onError, subscriber::onCompleted);
      });
    });
  }

  @NonNull
  private Completable syncGenres() {

    return Completable.create(subscriber -> {
      FlowManager.getDatabase(Cache.class).executeTransaction(dbw -> {
        long count = SQLite.delete().from(Genre.class).count();
        Timber.v("Deleted %d previous cached genres", count);

        Observable.range(0, Integer.MAX_VALUE)
            .flatMap(page -> service.getGenres(page * LIMIT, LIMIT))
            .subscribeOn(Schedulers.immediate())
            .takeWhile(genrePage -> genrePage.getOffset() < genrePage.getTotal())
            .map(Page::getData)
            .flatMap(Observable::from)
            .subscribe(BaseModel::save, subscriber::onError, subscriber::onCompleted);
      });
    });
  }
}
