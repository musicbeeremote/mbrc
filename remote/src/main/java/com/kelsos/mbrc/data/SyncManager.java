package com.kelsos.mbrc.data;

import android.content.Context;
import android.os.Environment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Cover;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.Playlist;
import com.kelsos.mbrc.dao.PlaylistTrack;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.Track;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.PaginatedDataResponse;
import com.kelsos.mbrc.util.Logger;
import com.raizlabs.android.dbflow.runtime.DBTransactionInfo;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.DeleteTransaction;
import com.raizlabs.android.dbflow.sql.language.Select;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class SyncManager {

  public static final int BUFFER_SIZE = 1024;
  private static final int STARTING_OFFSET = 0;
  private static final int LIMIT = 800;
  private RemoteApi api;
  private ObjectMapper mapper;
  @Inject private Context mContext;

  @Inject public SyncManager(RemoteApi api, ObjectMapper mapper) {
    this.api = api;
    this.mapper = mapper;
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
    final TransactionManager manager = TransactionManager.getInstance();

    for (JsonNode node : data.getData()) {
      final Playlist playlist;
      try {
        playlist = mapper.treeToValue(node, Playlist.class);
        manager.saveOnSaveQueue(playlist);
      } catch (JsonProcessingException e) {
        Ln.v(e);
      }
    }

    int total = data.getTotal();
    int offset = data.getOffset();
    int limit = data.getLimit();

    if (offset + limit < total) {
      getPlaylists(offset + limit, limit).subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(this::processPlaylists, Logger::logThrowable);
    } else {

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
    TransactionManager.getInstance()
        .addTransaction(new DeleteTransaction<>(DBTransactionInfo.create(), QueueTrack.class));
  }

  private void processCurrentQueue(PaginatedDataResponse paginatedData) {
    final TransactionManager manager = TransactionManager.getInstance();
    for (JsonNode node : paginatedData.getData()) {
      final QueueTrack track;
      try {
        track = mapper.treeToValue(node, QueueTrack.class);
        manager.saveOnSaveQueue(track);
      } catch (JsonProcessingException e) {
        Ln.e(e);
      }
    }

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
    }
  }

  private void processCovers(PaginatedDataResponse paginatedData) {

    try {
      for (JsonNode node : paginatedData.getData()) {
        final Cover cover = mapper.treeToValue(node, Cover.class);

      }
    } catch (IOException e) {
      Ln.d(e);
    }

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

    try {
      for (JsonNode node : paginatedData.getData()) {
        final Genre genre = mapper.treeToValue(node, Genre.class);
      }
    } catch (IOException e) {
      Ln.d(e);
    }

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getGenres(offset + limit, limit).subscribe(this::processGenres, Logger::logThrowable);
    } else {

      Ln.d("Genre sync complete.");
    }
  }

  private void processArtists(PaginatedDataResponse paginatedData) {
    try {
      for (JsonNode node : paginatedData.getData()) {
        final Artist artist = mapper.treeToValue(node, Artist.class);

      }
    } catch (IOException e) {
      Ln.d(e);
    }


    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getArtists(offset + limit, limit).subscribe(this::processArtists, Logger::logThrowable);
    } else {

      Ln.d("Artist sync complete.");
    }
  }

  private void startPlaylistTrackSyncing() {
    Observable.create((Subscriber<? super List<Playlist>> subscriber) -> {

      final List<Playlist> playlists = new Select().from(Playlist.class).queryList();
      subscriber.onNext(playlists);
      subscriber.onCompleted();
    })
        .subscribeOn(Schedulers.io())
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

    try {
      for (JsonNode node : data.getData()) {
        final PlaylistTrack album = mapper.treeToValue(node, PlaylistTrack.class);

      }
    } catch (IOException e) {
      Ln.d(e);
    }

    int total = data.getTotal();
    int offset = data.getOffset();
    int limit = data.getLimit();

    if (offset + limit < total) {
      //getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::logThrowable);
    } else {

      Ln.d("Playlist sync complete.");
    }
  }

  private void processAlbums(PaginatedDataResponse paginatedData) {

    try {
      for (JsonNode node : paginatedData.getData()) {
        final Album album = mapper.treeToValue(node, Album.class);

      }
    } catch (IOException e) {
      Ln.d(e);
    }

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::logThrowable);
    } else {

      Ln.d("Album sync complete.");
    }
  }

  private void processTracks(PaginatedDataResponse paginatedData) {

    try {

      for (JsonNode node : paginatedData.getData()) {
        final Track track = mapper.treeToValue(node, Track.class);

      }
    } catch (IOException e) {
      Ln.d(e);
    }

    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getTracks(offset + limit, limit).subscribe(this::processTracks, Logger::logThrowable);
    } else {

      Ln.d("Track sync complete.");
    }
  }

  private void fetchCovers() {

    List<Cover> covers = new Select().from(Cover.class).queryList();
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
