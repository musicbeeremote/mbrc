package com.kelsos.mbrc.utilities;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Code;
import com.kelsos.mbrc.dto.PaginatedResponse;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.mappers.CoverMapper;
import com.kelsos.mbrc.mappers.GenreMapper;
import com.kelsos.mbrc.mappers.PlaylistMapper;
import com.kelsos.mbrc.mappers.PlaylistTrackInfoMapper;
import com.kelsos.mbrc.mappers.PlaylistTrackMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import com.kelsos.mbrc.repository.PlaylistRepository;
import com.kelsos.mbrc.services.api.LibraryService;
import com.kelsos.mbrc.services.api.PlaylistService;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LibrarySyncManager {
  public static final int LIMIT = 400;
  public static final String LAST_SYNC = "last_sync";
  @Inject private LibraryService service;
  @Inject private LibraryRepository repository;
  @Inject private CoverDownloader downloader;
  @Inject private SharedPreferences preferences;
  @Inject private PlaylistService playlistService;
  @Inject private PlaylistRepository playlistRepository;

  public void sync() {
    final long after = preferences.getLong(LAST_SYNC, 0);
    Observable.create(subscriber -> {
      syncGenres(after);
      syncArtists(after);
      syncCovers(after);
      syncAlbums(after);
      syncTracks(after);

      repository.getCovers()
          .subscribeOn(Schedulers.immediate())
          .subscribe(covers -> downloader.download(covers), Ln::v);

      syncPlaylistTrackInfo(after);
      syncPlaylists(after);

      playlistRepository.getPlaylists()
          .subscribeOn(Schedulers.immediate())
          .observeOn(Schedulers.immediate())
          .concatMap(Observable::from)
          .subscribe(playlist -> {
            syncPlaylistTracks(playlist.getId(), after);
          }, Ln::v);

      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(o -> {
    }, throwable -> {
    }, () -> preferences.edit().putLong(LAST_SYNC, System.currentTimeMillis() / 1000).apply());
  }

  private void syncTracks(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryTracks(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
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
        .takeWhile(this::canGetNext)
        .subscribe(genres -> {
          repository.saveGenres(GenreMapper.map(genres.getData()));
        }, t -> {
          Timber.v(t, "Error while syncing");
        });
  }

  private void syncArtists(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryArtists(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(artists -> repository.saveArtists(ArtistMapper.map(artists.getData())), throwable -> {
        }, () -> {

        });
  }

  private boolean canGetNext(PaginatedResponse<?> page) {
    boolean isSuccessful = page.getCode() == Code.SUCCESS;
    return ((page.getOffset() + page.getData().size()) <= page.getTotal()) && isSuccessful;
  }

  private void syncAlbums(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryAlbums(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
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
        .takeWhile(this::canGetNext)
        .subscribe(covers -> {
          repository.saveCovers(CoverMapper.map(covers.getData()));
        }, throwable -> {
        }, () -> {
        });
  }

  private void syncPlaylists(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> playlistService.getPlaylists(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(tracks -> {
          playlistRepository.savePlaylists(PlaylistMapper.mapDto(tracks.getData()));
        });
  }

  private void syncPlaylistTracks(long playlistId, long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> playlistService.getPlaylistTracks(playlistId, LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(tracks -> {
          playlistRepository.savePlaylistTracks(PlaylistTrackMapper.map(tracks.getData(),
              playlistRepository::getPlaylistById,
              playlistRepository::getTrackInfoById));
        });
  }

  private void syncPlaylistTrackInfo(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> playlistService.getPlaylistTrackInfo(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(info -> {
          playlistRepository.savePlaylistTrackInfo(PlaylistTrackInfoMapper.map(info.getData()));
        });
  }
}
