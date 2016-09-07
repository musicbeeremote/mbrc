package com.kelsos.mbrc.services;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.db.CacheDatabase;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import javax.inject.Inject;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class BrowseSync {
  public static final int LIMIT = 400;

  @Inject LibraryService service;

  public void sync() {
    final Scheduler scheduler = Schedulers.immediate();
    Completable.concat(syncGenres(scheduler), syncArtists(scheduler), syncAlbums(scheduler), syncTracks(scheduler))
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .subscribe(() -> {
          Timber.v("Sync complete successfully");
        },t -> {
          Timber.v(t, "Sync failed due to reasons");
        });
  }

  @NonNull
  public Completable syncTracks(Scheduler scheduler) {
    return Completable.create((CompletableSubscriber subscriber) -> {
      long count = SQLite.delete().from(Track.class).count();
      Timber.v("Deleted %d previous cached Tracks", count);
      Observable.range(0, Integer.MAX_VALUE)
          .flatMap(page -> service.getTracks(page * LIMIT, LIMIT))
          .subscribeOn(scheduler)
          .takeWhile(page -> page.getOffset() < page.getTotal())
          .map(Page::getData)
          .subscribe(this::saveTracks, subscriber::onError, subscriber::onCompleted);
    });
  }

  private void saveTracks(List<Track> tracks) {
    FlowManager.getDatabase(CacheDatabase.class).executeTransaction(dbw -> {
      Observable.from(tracks).forEach(BaseModel::save);
    });
  }

  private void saveAlbums(List<Album> albums) {
    FlowManager.getDatabase(CacheDatabase.class).executeTransaction(dbw -> {
      Observable.from(albums).forEach(BaseModel::save);
    });
  }

  private void saveArtists(List<Artist> artists) {
    FlowManager.getDatabase(CacheDatabase.class).executeTransaction(dbw -> {
      Observable.from(artists).forEach(BaseModel::save);
    });
  }

  private void saveGenres(List<Genre> genres) {
    FlowManager.getDatabase(CacheDatabase.class).executeTransaction(dbw -> {
      Observable.from(genres).forEach(BaseModel::save);
    });

  }

  @NonNull
  public Completable syncAlbums(Scheduler scheduler) {
    return Completable.create((CompletableSubscriber subscriber) -> {
      long count = SQLite.delete().from(Album.class).count();
      Timber.v("Deleted %d previous cached Albums", count);
      Observable.range(0, Integer.MAX_VALUE)
          .flatMap(page -> service.getAlbums(page * LIMIT, LIMIT))
          .subscribeOn(scheduler)
          .takeWhile(albumPage -> albumPage.getOffset() < albumPage.getTotal())
          .map(Page::getData)
          .subscribe(this::saveAlbums, subscriber::onError, subscriber::onCompleted);
    });
  }

  @NonNull
  public Completable syncArtists(Scheduler scheduler) {

    return Completable.create((CompletableSubscriber subscriber) -> {
      long count = SQLite.delete().from(Artist.class).count();
      Timber.v("Deleted %d previous cached Artists", count);

      Observable.range(0, Integer.MAX_VALUE)
          .flatMap(page -> service.getArtists(page * LIMIT, LIMIT))
          .subscribeOn(scheduler)
          .takeWhile(artistPage -> artistPage.getOffset() < artistPage.getTotal())
          .map(Page::getData)
          .subscribe(this::saveArtists, subscriber::onError, subscriber::onCompleted);
    });
  }

  @NonNull
  public Completable syncGenres(Scheduler scheduler) {

    return Completable.create((CompletableSubscriber subscriber) -> {
      long count = SQLite.delete().from(Genre.class).count();
      Timber.v("Deleted %d previous cached genres", count);

      Observable.range(0, Integer.MAX_VALUE)
          .flatMap(page -> service.getGenres(page * LIMIT, LIMIT))
          .subscribeOn(scheduler)
          .takeWhile(genrePage -> genrePage.getOffset() < genrePage.getTotal())
          .map(Page::getData)
          .subscribe(this::saveGenres, subscriber::onError, subscriber::onCompleted);
    });
  }
}
