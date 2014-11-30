package com.kelsos.mbrc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.kelsos.mbrc.dao.*;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.PaginatedDataResponse;
import com.kelsos.mbrc.util.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;

public class Sync {
    private final DaoMaster daoMaster;
    private final DaoSession daoSession;
    private RemoteApi api;
    private ObjectMapper mapper;
    private static final int STARTING_OFFSET = 0;
    private static final int LIMIT = 800;
    private SQLiteDatabase db;
    private long startMillis;
    private long endMillis;

    @Inject
    public Sync(RemoteApi api, ObjectMapper mapper, Context mContext) {
        this.api = api;
        this.mapper = mapper;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "lib-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

    }

    public void startSyncing() {
        startMillis = System.currentTimeMillis();
        getGenres(STARTING_OFFSET, LIMIT).subscribe(this::processGenres, Logger::LogThrowable);
        getArtists(STARTING_OFFSET, LIMIT).subscribe(this::processArtists, Logger::LogThrowable);
        getAlbums(STARTING_OFFSET, LIMIT).subscribe(this::processAlbums, Logger::LogThrowable);
        getTracks(STARTING_OFFSET, LIMIT).subscribe(this::processTracks, Logger::LogThrowable);
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
            getGenres(offset + limit, limit).subscribe(this::processGenres, Logger::LogThrowable);
        } else {
            Ln.d("no more data");
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
            getArtists(offset + limit, limit).subscribe(this::processArtists, Logger::LogThrowable);
        } else {
            Ln.d("no more data");
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
            getAlbums(offset + limit, limit).subscribe(this::processAlbums, Logger::LogThrowable);
        } else {
            Ln.d("no more data");
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
            getTracks(offset + limit, limit).subscribe(this::processTracks, Logger::LogThrowable);
        } else {
            Ln.d("no more data");
            endMillis = System.currentTimeMillis();
            Ln.d("Time Elapsed %d ms", endMillis - startMillis);
        }
    }

    private Observable<PaginatedDataResponse> getGenres(int offset, int limit) {
        return api.getLibraryGenres(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private Observable<PaginatedDataResponse> getArtists(int offset, int limit) {
        return api.getLibraryArtists(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private Observable<PaginatedDataResponse> getAlbums(int offset, int limit) {
        return api.getLibraryAlbums(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private Observable<PaginatedDataResponse> getTracks(int offset, int limit) {
        return api.getLibraryTracks(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
