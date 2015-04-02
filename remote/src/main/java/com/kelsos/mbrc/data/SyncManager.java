package com.kelsos.mbrc.data;

import android.content.Context;
import android.os.Environment;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumHelper;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistHelper;
import com.kelsos.mbrc.dao.Cover;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.DaoSession;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.GenreHelper;
import com.kelsos.mbrc.dao.Playlist;
import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.dao.PlaylistHelper;
import com.kelsos.mbrc.dao.PlaylistTrack;
import com.kelsos.mbrc.dao.PlaylistTrackDao;
import com.kelsos.mbrc.dao.PlaylistTrackHelper;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.QueueTrackDao;
import com.kelsos.mbrc.dao.QueueTrackHelper;
import com.kelsos.mbrc.dao.Track;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.TrackHelper;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.PaginatedDataResponse;
import com.kelsos.mbrc.util.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class SyncManager {

  public static final int BUFFER_SIZE = 1024;
  private static final int STARTING_OFFSET = 0;
  private static final int LIMIT = 800;
  private final DaoSession daoSession;
  private RemoteApi api;
  private ObjectMapper mapper;
  @Inject private Context mContext;

  @Inject public SyncManager(RemoteApi api, ObjectMapper mapper, DaoSession daoSession) {
    this.api = api;
    this.mapper = mapper;
    this.daoSession = daoSession;
  }

  public void startLibrarySyncing() {
    getGenres(STARTING_OFFSET, LIMIT).subscribe(this::processGenres, Logger::logThrowable);
    getArtists(STARTING_OFFSET, LIMIT).subscribe(this::processArtists, Logger::logThrowable);
    getAlbums(STARTING_OFFSET, LIMIT).subscribe(this::processAlbums, Logger::logThrowable);
    getTracks(STARTING_OFFSET, LIMIT).subscribe(this::processTracks, Logger::logThrowable);
    getCovers(STARTING_OFFSET, LIMIT).subscribe(this::processCovers, Logger::logThrowable);
  }

  public void startPlaylistSync() {
    getPlaylists(STARTING_OFFSET, LIMIT).subscribe(this::processPlaylists, Logger::logThrowable);
  }

  private void processPlaylists(PaginatedDataResponse data) {
    PlaylistDao playlistDao = daoSession.getPlaylistDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : data.getData()) {
          final Playlist playlist = mapper.readValue(node, Playlist.class);
          playlistDao.insert(playlist);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = data.getTotal();
    int offset = data.getOffset();
    int limit = data.getLimit();

    if (offset + limit < total) {
      getPlaylists(offset + limit, limit).subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(this::processPlaylists, Logger::logThrowable);
    } else {
      mContext.getContentResolver().notifyChange(PlaylistHelper.CONTENT_URI, null);
      Ln.d("Playlist sync complete.");
      startPlaylistTrackSyncing();
    }
  }

  private Observable<PaginatedDataResponse> getPlaylists(int offset, int limit) {
    return api.getPlaylists(offset, limit).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
  }

  public void reloadQueue() {
    Observable.create(subscriber -> {
      clearCurrentQueue();
      subscriber.onNext(true);
      subscriber.onCompleted();
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(r -> startCurrentQueueSyncing());
  }

  public void startCurrentQueueSyncing() {
    api.getNowPlayingList(STARTING_OFFSET, LIMIT)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(this::processCurrentQueue, Logger::logThrowable);
  }

  private void clearCurrentQueue() {
    QueueTrackDao queueTrackDao = daoSession.getQueueTrackDao();
    daoSession.runInTx(queueTrackDao::deleteAll);
  }

  private void processCurrentQueue(PaginatedDataResponse paginatedData) {
    QueueTrackDao queueTrackDao = daoSession.getQueueTrackDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : paginatedData.getData()) {
          final QueueTrack track = mapper.readValue(node, QueueTrack.class);
          queueTrackDao.insert(track);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      api.getNowPlayingList(offset + limit, limit)
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(this::processCurrentQueue, Logger::logThrowable);
    } else {
      Ln.d("Queue track sync complete.");
      mContext.getContentResolver().notifyChange(QueueTrackHelper.CONTENT_URI, null);
    }
  }

  private void processCovers(PaginatedDataResponse paginatedData) {
    CoverDao coverDao = daoSession.getCoverDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : paginatedData.getData()) {
          final Cover cover = mapper.readValue(node, Cover.class);
          coverDao.insert(cover);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getCovers(offset + limit, limit).subscribe(this::processCovers, Logger::logThrowable);
    } else {
      Ln.d("Cover sync complete.");
      fetchCovers();
    }
  }

  private void processGenres(PaginatedDataResponse paginatedData) {

    GenreDao genreDao = daoSession.getGenreDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : paginatedData.getData()) {
          final Genre genre = mapper.readValue(node, Genre.class);
          genreDao.insert(genre);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getGenres(offset + limit, limit).subscribe(this::processGenres, Logger::logThrowable);
    } else {
      mContext.getContentResolver().notifyChange(GenreHelper.CONTENT_URI, null);
      Ln.d("Genre sync complete.");
    }
  }

  private void processArtists(PaginatedDataResponse paginatedData) {
    ArtistDao artistDao = daoSession.getArtistDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : paginatedData.getData()) {
          final Artist artist = mapper.readValue(node, Artist.class);
          artistDao.insert(artist);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getArtists(offset + limit, limit).subscribe(this::processArtists, Logger::logThrowable);
    } else {
      mContext.getContentResolver().notifyChange(ArtistHelper.CONTENT_URI, null);
      Ln.d("Artist sync complete.");
    }
  }

  private void startPlaylistTrackSyncing() {
    Observable.create((Subscriber<? super List<Playlist>> subscriber) -> {
      PlaylistDao playlistDao = daoSession.getPlaylistDao();
      final List<Playlist> playlists = playlistDao.loadAll();
      subscriber.onNext(playlists);
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(this::startGettingTracksForPlaylists, Logger::logThrowable);
  }

  private void startGettingTracksForPlaylists(List<Playlist> list) {
    Observable.from(list)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(i -> getPlaylistTracks(i.getId(), STARTING_OFFSET, LIMIT).subscribe(
            this::processPlaylistTracks, Logger::logThrowable), Logger::logThrowable);
  }

  private void processPlaylistTracks(PaginatedDataResponse data) {
    PlaylistTrackDao dao = daoSession.getPlaylistTrackDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : data.getData()) {
          final PlaylistTrack album = mapper.readValue(node, PlaylistTrack.class);
          dao.insert(album);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = data.getTotal();
    int offset = data.getOffset();
    int limit = data.getLimit();

    if (offset + limit < total) {
      //getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::logThrowable);
    } else {
      mContext.getContentResolver().notifyChange(PlaylistTrackHelper.CONTENT_URI, null);
      Ln.d("Playlist sync complete.");
    }
  }

  private void processAlbums(PaginatedDataResponse paginatedData) {

    AlbumDao albumDao = daoSession.getAlbumDao();
    daoSession.runInTx(() -> {
      try {
        for (JsonNode node : paginatedData.getData()) {
          final Album album = mapper.readValue(node, Album.class);
          albumDao.insert(album);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::logThrowable);
    } else {
      mContext.getContentResolver().notifyChange(AlbumHelper.CONTENT_URI, null);
      Ln.d("Album sync complete.");
    }
  }

  private void processTracks(PaginatedDataResponse paginatedData) {

    TrackDao trackDao = daoSession.getTrackDao();
    daoSession.runInTx(() -> {
      try {

        for (JsonNode node : paginatedData.getData()) {
          final Track track = mapper.readValue(node, Track.class);
          trackDao.insert(track);
        }
      } catch (IOException e) {
        Ln.d(e);
      }
    });

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getTracks(offset + limit, limit).subscribe(this::processTracks, Logger::logThrowable);
    } else {
      mContext.getContentResolver().notifyChange(TrackHelper.CONTENT_URI, null);
      Ln.d("Track sync complete.");
    }
  }

  private void fetchCovers() {
    CoverDao coverDao = daoSession.getCoverDao();
    List<Cover> covers = coverDao.loadAll();
    for (Cover cover : covers) {
      api.getCoverById(cover.getId())
          .subscribeOn(Schedulers.io())
          .subscribe(resp -> storeCover(resp, cover.getHash()), Logger::logThrowable);
    }
  }

  private void storeCover(Response response, String hash) {
    File sdCard = Environment.getExternalStorageDirectory();
    File dir = new File(String.format("%s/Android/data/%s/cache", sdCard.getAbsolutePath(),
        BuildConfig.APPLICATION_ID));
    //noinspection ResultOfMethodCallIgnored
    dir.mkdirs();
    File file = new File(dir, hash);
    try {
      final OutputStream output = new FileOutputStream(file);
      final byte[] buffer = new byte[BUFFER_SIZE];
      final InputStream input = response.getBody().in();
      int read;
      while ((read = input.read(buffer)) != -1) {
        output.write(buffer, 0, read);
      }
      output.flush();
      output.close();
      input.close();
    } catch (IOException e) {
      Ln.d(e);
    }
  }

  private Observable<PaginatedDataResponse> getGenres(int offset, int limit) {
    return api.getLibraryGenres(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedDataResponse> getArtists(int offset, int limit) {
    return api.getLibraryArtists(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedDataResponse> getAlbums(int offset, int limit) {
    return api.getLibraryAlbums(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedDataResponse> getTracks(int offset, int limit) {
    return api.getLibraryTracks(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedDataResponse> getCovers(int offset, int limit) {
    return api.getLibraryCovers(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedDataResponse> getPlaylistTracks(Long playlistId, int offset,
      int limit) {
    return api.getPlaylistTracks(playlistId, offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }
}
