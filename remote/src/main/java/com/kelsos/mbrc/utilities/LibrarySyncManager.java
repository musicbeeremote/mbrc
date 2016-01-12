package com.kelsos.mbrc.utilities;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.mappers.CoverMapper;
import com.kelsos.mbrc.mappers.GenreMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import com.kelsos.mbrc.services.api.LibraryService;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibrarySyncManager {
  public static final int LIMIT = 400;
  public static final String LAST_SYNC = "last_sync";
  @Inject private LibraryService service;
  @Inject private LibraryRepository repository;
  @Inject private CoverDownloader downloader;
  @Inject private SharedPreferences preferences;

  public void sync() {
    final long after = preferences.getLong(LAST_SYNC, 0);
    Observable.create(subscriber -> {
      syncGenres(after);
      syncArtists(after);
      syncCovers(after);
      syncAlbums(after);
      syncTracks(after);

      subscriber.onCompleted();

      repository.getCovers()
          .subscribeOn(Schedulers.immediate())
          .subscribe(covers -> downloader.download(covers), Ln::v);
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(o -> {
    }, throwable -> {
    }, () -> preferences.edit().putLong(LAST_SYNC, System.currentTimeMillis() / 1000).apply());
  }

  private void syncTracks(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryTracks(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(tracks -> {
          repository.saveRemoteTracks(tracks.getData());
        }, throwable -> {
        }, () -> {
        });
  }

  private void syncGenres(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryGenres(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(genres -> {
          repository.saveGenres(GenreMapper.map(genres.getData()));
        }, throwable -> {
        }, () -> {
        });
  }

  private void syncArtists(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryArtists(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(artists -> repository.saveArtists(ArtistMapper.map(artists.getData())), throwable -> {
        }, () -> {

        });
  }

  private void syncAlbums(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryAlbums(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(albums -> {
          repository.saveRemoteAlbums(albums.getData());
        }, throwable -> {
        }, () -> {
        });
  }

  private void syncCovers(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryCovers(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(covers -> {
          repository.saveCovers(CoverMapper.map(covers.getData()));
        }, throwable -> {
        }, () -> {
        });
  }
}
