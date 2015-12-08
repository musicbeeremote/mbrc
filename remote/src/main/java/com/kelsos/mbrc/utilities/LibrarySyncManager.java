package com.kelsos.mbrc.utilities;

import com.google.inject.Inject;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.mappers.GenreMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import com.kelsos.mbrc.services.api.LibraryService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibrarySyncManager {
  public static final int LIMIT = 400;
  @Inject private LibraryService service;
  @Inject private LibraryRepository repository;

  public void sync() {
    Observable.create(subscriber -> {
      syncGenres();
      syncArtists();
      syncAlbums();
      syncTracks();
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(o -> {

    }, throwable -> {

    });

  }

  private void syncTracks() {
    Observable.range(0, Integer.MAX_VALUE -1)
        .concatMap(integer -> service.getLibraryTracks(LIMIT * integer, LIMIT))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(tracks -> {
          repository.saveRemoteTracks(tracks.getData());
        }, throwable -> {}, () -> {});
  }

  private void syncGenres() {
    Observable.range(0, Integer.MAX_VALUE -1)
        .concatMap(integer -> service.getLibraryGenres(LIMIT * integer, LIMIT))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(genres -> {
          repository.saveGenres(GenreMapper.map(genres.getData()));
        }, throwable -> {}, () -> {});
  }

  private void syncArtists() {
    Observable.range(0, Integer.MAX_VALUE -1)
        .concatMap(integer -> service.getLibraryArtists(LIMIT * integer, LIMIT))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(artists -> {
          repository.saveArtists(ArtistMapper.map(artists.getData()));
        }, throwable -> {}, () -> {

        });
  }

  private void syncAlbums() {
    Observable.range(0, Integer.MAX_VALUE -1)
        .concatMap(integer -> service.getLibraryAlbums(LIMIT * integer, LIMIT))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(page -> (page.getOffset() + page.getData().size()) <= page.getTotal())
        .subscribe(albums -> {
          repository.saveRemoteAlbums(albums.getData());
        }, throwable -> {}, () -> {});
  }

  private void syncCovers() {

  }

  //Observable.range(0, Integer.MAX_VALUE - 1)
  //    .concatMap(integer -> service.getNowPlayingList(LIMIT * integer, LIMIT).subscribeOn(Schedulers.io()))
  //    .takeWhile(page -> page.getOffset() < page.getTotal())
  //    .flatMap(page -> Observable.just(QueueTrackMapper.map(page.getData())));
}
