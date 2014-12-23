package com.kelsos.mbrc.data;

import android.content.Context;
import android.os.Environment;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.dao.*;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.PaginatedDataResponse;
import com.kelsos.mbrc.util.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class SyncManager {

    public static final int BUFFER_SIZE = 1024;
    private final DaoSession daoSession;
    private RemoteApi api;
    private ObjectMapper mapper;
    private static final int STARTING_OFFSET = 0;
    private static final int LIMIT = 800;

	@Inject
	private Context mContext;

    @Inject
    public SyncManager(RemoteApi api, ObjectMapper mapper, DaoSession daoSession) {
        this.api = api;
        this.mapper = mapper;
        this.daoSession = daoSession;
    }

    public void startLibrarySyncing() {
        getGenres(STARTING_OFFSET, LIMIT).subscribe(this::processGenres, Logger::LogThrowable);
        getArtists(STARTING_OFFSET, LIMIT).subscribe(this::processArtists, Logger::LogThrowable);
        getAlbums(STARTING_OFFSET, LIMIT).subscribe(this::processAlbums, Logger::LogThrowable);
        getTracks(STARTING_OFFSET, LIMIT).subscribe(this::processTracks, Logger::LogThrowable);
        getCovers(STARTING_OFFSET, LIMIT).subscribe(this::processCovers, Logger::LogThrowable);
    }

    public void startCurrentQueueSyncing() {
        api.getNowPlayingList(STARTING_OFFSET, LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processCurrentQueue, Logger::LogThrowable);
    }

    public void clearCurrentQueue() {
        QueueTrackDao queueTrackDao = daoSession.getQueueTrackDao();
        queueTrackDao.deleteAll();
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
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::processCurrentQueue, Logger::LogThrowable);
        } else {
            Ln.d("no more data");
			mContext.getContentResolver().notifyChange(QueueTrackHelper.CONTENT_URI, null);
        }
    }

    private void processCovers(PaginatedDataResponse paginatedData) {
        CoverDao coverDao = daoSession.getCoverDao();
        daoSession.runInTx(() -> {
            try {
                for (JsonNode node : paginatedData.getData())     {
                    final Cover cover = mapper.readValue(node, Cover.class);
                    coverDao.insert(cover);
                }
            }   catch (IOException e){
                Ln.d(e);
            }
        });

        int total = paginatedData.getTotal();
        int offset = paginatedData.getOffset();
        int limit = paginatedData.getLimit();

        if (offset + limit < total) {
            getCovers(offset + limit, limit).subscribe(this::processCovers, Logger::LogThrowable);
        } else {
            Ln.d("no more data");
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
        }
    }

    private void fetchCovers() {
        CoverDao coverDao = daoSession.getCoverDao();
        List<Cover> covers = coverDao.loadAll();
        for (Cover cover : covers) {
            api.getCoverById(cover.getId())
                    .subscribeOn(Schedulers.io())
                    .subscribe(resp -> storeCover(resp, cover.getHash()),
                            Logger::LogThrowable);
        }
    }

    private void storeCover(Response response, String hash) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(String.format("%s/Android/data/%s/cache", sdCard.getAbsolutePath(), BuildConfig.APPLICATION_ID));
        final boolean mkdirs = dir.mkdirs();
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

    private Observable<PaginatedDataResponse> getCovers(int offset, int limit) {
        return api.getLibraryCovers(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
