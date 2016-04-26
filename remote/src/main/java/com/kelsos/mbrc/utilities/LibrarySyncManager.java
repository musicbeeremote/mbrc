package com.kelsos.mbrc.utilities;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Code;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumDao_Table;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao_Table;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.GenreDao_Table;
import com.kelsos.mbrc.dao.PlaylistDao_Table;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.TrackDao_Table;
import com.kelsos.mbrc.dto.PaginatedResponse;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.mappers.CoverMapper;
import com.kelsos.mbrc.mappers.GenreMapper;
import com.kelsos.mbrc.mappers.PlaylistMapper;
import com.kelsos.mbrc.mappers.PlaylistTrackInfoMapper;
import com.kelsos.mbrc.mappers.PlaylistTrackMapper;
import com.kelsos.mbrc.mappers.TrackMapper;
import com.kelsos.mbrc.repository.PlaylistRepository;
import com.kelsos.mbrc.repository.library.AlbumRepository;
import com.kelsos.mbrc.repository.library.ArtistRepository;
import com.kelsos.mbrc.repository.library.CoverRepository;
import com.kelsos.mbrc.repository.library.GenreRepository;
import com.kelsos.mbrc.repository.library.TrackRepository;
import com.kelsos.mbrc.services.api.LibraryService;
import com.kelsos.mbrc.services.api.PlaylistService;
import java.util.List;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LibrarySyncManager {
  public static final int LIMIT = 400;
  public static final String LAST_SYNC = "last_sync";
  @Inject private LibraryService service;
  @Inject private AlbumRepository albumRepository;
  @Inject private ArtistRepository artistRepository;
  @Inject private GenreRepository genreRepository;
  @Inject private TrackRepository trackRepository;
  @Inject private CoverRepository coverRepository;
  @Inject private CoverDownloader downloader;
  @Inject private SharedPreferences preferences;
  @Inject private PlaylistService playlistService;
  @Inject private PlaylistRepository playlistRepository;

  public void sync() {
    GenreDao_Table.index_genre_name_index.createIfNotExists();
    AlbumDao_Table.index_album_name_index.createIfNotExists();
    ArtistDao_Table.index_artist_name_index.createIfNotExists();
    TrackDao_Table.index_track_title_index.createIfNotExists();
    PlaylistDao_Table.index_playlist_name_index.createIfNotExists();



    final long after = preferences.getLong(LAST_SYNC, 0);
    Observable.create(subscriber -> {
      syncGenres(after);
      syncArtists(after);
      syncCovers(after);
      syncAlbums(after);
      syncTracks(after);

      coverRepository.getAllObservable()
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
    }, this::handlerError, () -> preferences.edit().putLong(LAST_SYNC, System.currentTimeMillis() / 1000).apply());
  }

  private void handlerError(Throwable throwable) {
    Timber.e(throwable, "Error ");
  }

  private void syncTracks(long after) {
    List<ArtistDao> artists = artistRepository.getAll();
    List<GenreDao> genres = genreRepository.getAll();
    List<AlbumDao> albums = albumRepository.getAll();
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryTracks(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(tracks -> {
          List<TrackDao> daos = TrackMapper.mapDtos(tracks.getData(), artists, genres, albums);
          trackRepository.save(daos);
        }, this::handlerError, () -> {
        });
  }

  private void syncGenres(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryGenres(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(genres -> {
          genreRepository.save(GenreMapper.map(genres.getData()));
        },this::handlerError);
  }

  private void syncArtists(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryArtists(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(artists -> artistRepository.save(ArtistMapper.map(artists.getData())), this::handlerError, () -> {

        });
  }

  private boolean canGetNext(PaginatedResponse<?> page) {
    boolean isSuccessful = page.getCode() == Code.SUCCESS;
    int data = page.getData().size();
    int retrieved = page.getOffset() + data;
    return data > 0 && (retrieved <= page.getTotal()) && isSuccessful;
  }

  private void syncAlbums(long after) {
    List<CoverDao> cachedCovers = coverRepository.getAll();
    List<ArtistDao> cachedArtists = artistRepository.getAll();
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryAlbums(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(albums -> {
          List<AlbumDao> daos = AlbumMapper.mapDtos(albums.getData(), cachedCovers, cachedArtists);
          albumRepository.save(daos);
        }, this::handlerError, () -> {
        });
  }

  private void syncCovers(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getLibraryCovers(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(covers -> {
          coverRepository.save(CoverMapper.map(covers.getData()));
        }, this::handlerError, () -> {
        });
  }

  private void syncPlaylists(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> playlistService.getPlaylists(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(tracks -> {
          playlistRepository.savePlaylists(PlaylistMapper.INSTANCE.mapDto(tracks.getData()));
        }, this::handlerError);
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
        }, this::handlerError);
  }

  private void syncPlaylistTrackInfo(long after) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> playlistService.getPlaylistTrackInfo(LIMIT * integer, LIMIT, after))
        .subscribeOn(Schedulers.immediate())
        .takeWhile(this::canGetNext)
        .subscribe(info -> {
          playlistRepository.savePlaylistTrackInfo(PlaylistTrackInfoMapper.map(info.getData()));
        }, this::handlerError);
  }
}
