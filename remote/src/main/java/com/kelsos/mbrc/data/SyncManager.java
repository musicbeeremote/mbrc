package com.kelsos.mbrc.data;

import android.content.Context;
import android.os.Environment;
import com.annimon.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Cover;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.Playlist;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dto.LibraryAlbum;
import com.kelsos.mbrc.dto.NowPlayingTrack;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.PaginatedResponse;
import com.kelsos.mbrc.utilities.Logger;
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

  private void processPlaylists(PaginatedResponse<com.kelsos.mbrc.dto.Playlist> data) {
    final TransactionManager manager = TransactionManager.getInstance();

    Stream.of(data.getData())
        .forEach(value -> {

          Playlist playlist = new Playlist().setId(value.getId())
              .setName(value.getName())
              .setPath(value.getPath())
              .setReadOnly(value.getReadOnly())
              .setTracks(value.getTracks());

          manager.saveOnSaveQueue(playlist);
        });

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

  private Observable<PaginatedResponse<com.kelsos.mbrc.dto.Playlist>> getPlaylists(int offset,
      int limit) {
    return api.getPlaylists(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
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

  private void processCurrentQueue(PaginatedResponse<NowPlayingTrack> page) {
    final TransactionManager manager = TransactionManager.getInstance();

    Stream.of(page.getData())
        .forEach(value -> {

          QueueTrack track = new QueueTrack().setTitle(value.getTitle())
              .setArtist(value.getArtist())
              .setPath(value.getPath())
              .setId(value.getId())
              .setPosition(value.getPosition());

          manager.saveOnSaveQueue(track);
        });

    int total = page.getTotal();
    int offset = page.getOffset();
    int limit = page.getLimit();

    if (offset + limit < total) {
      api.getNowPlayingList(offset + limit, limit)
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(this::processCurrentQueue, Logger::logThrowable);
    } else {
      Ln.d("Queue track sync complete.");
    }
  }

  private void processCovers(PaginatedResponse<com.kelsos.mbrc.dto.Cover> page) {
    final TransactionManager manager = TransactionManager.getInstance();

    Stream.of(page.getData())
        .forEach(value -> {
          Cover cover = new Cover().setHash(value.getHash())
              .setId(value.getId());
          manager.saveOnSaveQueue(cover);
        });

    int total = page.getTotal();
    int offset = page.getOffset();
    int limit = page.getLimit();

    if (offset + limit < total) {
      getCovers(offset + limit, limit).subscribe(this::processCovers, Logger::logThrowable);
    } else {
      Ln.d("Cover sync complete.");
      fetchCovers();
    }
  }

  private void processGenres(PaginatedResponse<com.kelsos.mbrc.dto.Genre> page) {
    final TransactionManager manager = TransactionManager.getInstance();

    Stream.of(page.getData())
        .forEach(value -> {
          Genre genre = new Genre().setId(value.getId())
              .setName(value.getName());
          manager.saveOnSaveQueue(genre);
        });

    int total = page.getTotal();
    int offset = page.getOffset();
    int limit = page.getLimit();

    if (offset + limit < total) {
      getGenres(offset + limit, limit).subscribe(this::processGenres, Logger::logThrowable);
    } else {

      Ln.d("Genre sync complete.");
    }
  }

  private void processArtists(PaginatedResponse<com.kelsos.mbrc.dto.Artist> paginatedData) {
    final TransactionManager manager = TransactionManager.getInstance();

    Stream.of(paginatedData.getData()).forEach(value -> {
      Artist artist = new Artist().setId(value.getId())
          .setName(value.getName());

      manager.saveOnSaveQueue(artist);
    });

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

      final List<Playlist> playlists = new Select().from(Playlist.class)
          .queryList();
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

  private void processPlaylistTracks(PaginatedResponse<com.kelsos.mbrc.dto.PlaylistTrack> data) {

    TransactionManager manager = TransactionManager.getInstance();

    Stream.of(data.getData()).forEach(value -> {
      // TODO: 9/2/15 Update the model in the database
    });


    int total = data.getTotal();
    int offset = data.getOffset();
    int limit = data.getLimit();

    if (offset + limit < total) {
      //getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::logThrowable);
    } else {

      Ln.d("Playlist sync complete.");
    }
  }

  private void processAlbums(PaginatedResponse<LibraryAlbum> paginatedData) {

    Stream.of(paginatedData.getData()).forEach(value -> {
      final Album album = new Album();
      // TODO: 9/2/15 Fix the proper mapping
    });


    int total = paginatedData.getTotal();
    int offset = paginatedData.getOffset();
    int limit = paginatedData.getLimit();

    if (offset + limit < total) {
      getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::logThrowable);
    } else {

      Ln.d("Album sync complete.");
    }
  }

  private void processTracks(PaginatedResponse<com.kelsos.mbrc.dto.Track> paginatedData) {

    // TODO: 9/2/15 Fix track processing

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

    List<Cover> covers = new Select().from(Cover.class)
        .queryList();
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
      final InputStream input = response.getBody()
          .in();
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

  private Observable<PaginatedResponse<com.kelsos.mbrc.dto.Genre>> getGenres(int offset,
      int limit) {
    return api.getLibraryGenres(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedResponse<com.kelsos.mbrc.dto.Artist>> getArtists(int offset,
      int limit) {
    return api.getLibraryArtists(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedResponse<LibraryAlbum>> getAlbums(int offset, int limit) {
    return api.getLibraryAlbums(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedResponse<com.kelsos.mbrc.dto.Track>> getTracks(int offset,
      int limit) {
    return api.getLibraryTracks(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedResponse<com.kelsos.mbrc.dto.Cover>> getCovers(int offset,
      int limit) {
    return api.getLibraryCovers(offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaginatedResponse<com.kelsos.mbrc.dto.PlaylistTrack>> getPlaylistTracks(
      Long playlistId, int offset, int limit) {
    return api.getPlaylistTracks(playlistId, offset, limit)
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
  }
}
