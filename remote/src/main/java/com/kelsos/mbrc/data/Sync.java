package com.kelsos.mbrc.data;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.kelsos.mbrc.data.db.CacheHelper;
import com.kelsos.mbrc.data.dbdata.LibraryAlbum;
import com.kelsos.mbrc.data.dbdata.LibraryArtist;
import com.kelsos.mbrc.data.dbdata.LibraryGenre;
import com.kelsos.mbrc.data.dbdata.LibraryTrack;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.PaginatedDataResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;

public class Sync {
    private RemoteApi api;
    private ObjectMapper mapper;
    private static final int STARTING_OFFSET = 0;
    private static final int LIMIT = 50;

    @Inject
    private CacheHelper mHelper;

    @Inject
    public Sync(RemoteApi api, ObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    public void startSyncing() {
        getGenres(STARTING_OFFSET, LIMIT).subscribe(this::processGenres);
        getArtists(STARTING_OFFSET, LIMIT).subscribe(this::processArtists);
        getAlbums(STARTING_OFFSET, LIMIT).subscribe(this::processAlbums);
        getTracks(STARTING_OFFSET, LIMIT).subscribe(this::processTracks);
    }

    private void processGenres(PaginatedDataResponse paginatedData){

        try {
            final Dao<LibraryGenre, Integer> genreDao = mHelper.getGenreDao();
            genreDao.callBatchTasks(() -> {

                for (JsonNode node : paginatedData.getData()) {
                    final LibraryGenre genre = mapper.readValue(node, LibraryGenre.class);
                    genreDao.createOrUpdate(genre);
                }

                return null;
            });
        } catch (Exception e) {
            Ln.d(e);
        }

        int total = paginatedData.getTotal();
        int offset = paginatedData.getOffset();
        int limit = paginatedData.getLimit();

        if (offset + limit < total) {
            getGenres(offset + limit, limit).subscribe(this::processGenres);
        } else {
            Ln.d("no more data");
        }
    }

    private void processArtists(PaginatedDataResponse paginatedData) {

        try {
            final Dao<LibraryArtist, Integer> artistDao = mHelper.getArtistDao();
            artistDao.callBatchTasks(() -> {

                for (JsonNode node : paginatedData.getData()) {
                    final LibraryArtist artist = mapper.readValue(node, LibraryArtist.class);
                    artistDao.createOrUpdate(artist);
                }

                return null;
            });
        } catch (Exception e) {
            Ln.d(e);
        }

        int total = paginatedData.getTotal();
        int offset = paginatedData.getOffset();
        int limit = paginatedData.getLimit();

        if (offset + limit < total) {
            getArtists(offset + limit, limit).subscribe(this::processArtists);
        } else {
            Ln.d("no more data");
        }
    }

    private void processAlbums(PaginatedDataResponse paginatedData) {

        try {
            final Dao<LibraryAlbum, Integer> albumDao = mHelper.getAlbumDao();
            final Dao<LibraryArtist, Integer> artistDao = mHelper.getArtistDao();
            albumDao.callBatchTasks(() -> {

                for (JsonNode node : paginatedData.getData()) {
                    final LibraryAlbum album = mapper.readValue(node, LibraryAlbum.class);
                    int artistId = node.path("artistId").asInt(1);
                    final LibraryArtist artist = artistDao.queryForId(artistId);
                    album.setArtist(artist);
                    albumDao.createOrUpdate(album);
                }
                return null;
            });
        } catch (Exception e) {
            Ln.d(e);
        }

        int total = paginatedData.getTotal();
        int offset = paginatedData.getOffset();
        int limit = paginatedData.getLimit();

        if (offset + limit < total) {
            getAlbums(offset + limit, limit).subscribe(this::processAlbums);
        } else {
            Ln.d("no more data");
        }
    }

    private void processTracks(PaginatedDataResponse paginatedData) {

        try {
            final Dao<LibraryTrack, Integer> trackDao = mHelper.getTrackDao();
            final Dao<LibraryArtist, Integer> artistDao = mHelper.getArtistDao();
            final Dao<LibraryGenre, Integer> genreDao = mHelper.getGenreDao();
            final Dao<LibraryAlbum, Integer> albumDao = mHelper.getAlbumDao();

            trackDao.callBatchTasks(() -> {

                for (JsonNode node : paginatedData.getData()) {
                    final LibraryTrack track = mapper.readValue(node, LibraryTrack.class);
                    int genreId = node.path("genreId").asInt();
                    int artistId = node.path("artistId").asInt();
                    int albumArtistId = node.path("albumArtistId").asInt();
                    int albumId = node.path("albumId").asInt();

                    track.setGenre(genreDao.queryForId(genreId));
                    track.setArtist(artistDao.queryForId(artistId));
                    track.setAlbumArtist(artistDao.queryForId(albumArtistId));
                    track.setAlbum(albumDao.queryForId(albumId));

                    trackDao.createOrUpdate(track);
                }
                return null;
            });
        } catch (Exception e) {
            Ln.d(e);
        }

        int total = paginatedData.getTotal();
        int offset = paginatedData.getOffset();
        int limit = paginatedData.getLimit();

        if (offset + limit < total) {
            getTracks(offset + limit, limit).subscribe(this::processTracks);
        } else {
            Ln.d("no more data");
        }
    }

    private Observable<PaginatedDataResponse> getGenres(int offset, int limit) {
        return api.getLibraryGenres(offset, limit)
                .subscribeOn(Schedulers.io());
    }

    private Observable<PaginatedDataResponse> getArtists(int offset, int limit) {
        return api.getLibraryArtists(offset, limit)
                .subscribeOn(Schedulers.io());
    }

    private Observable<PaginatedDataResponse> getAlbums(int offset, int limit) {
        return api.getLibraryAlbums(offset, limit)
                .subscribeOn(Schedulers.io());
    }

    private Observable<PaginatedDataResponse> getTracks(int offset, int limit) {
        return api.getLibraryTracks(offset, limit)
                .subscribeOn(Schedulers.io());
    }
}
