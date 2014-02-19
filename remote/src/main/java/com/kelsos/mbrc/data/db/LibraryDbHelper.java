package com.kelsos.mbrc.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.kelsos.mbrc.data.dbdata.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackLibrary.db";
    public static final String IS = " IS ?";
    public static final String ASC = " ASC";
    private final Context mContext;

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(Album.CREATE_TABLE);
        db.execSQL(Artist.CREATE_TABLE);
        db.execSQL(Genre.CREATE_TABLE);
        db.execSQL(Track.CREATE_TABLE);
        db.execSQL(NowPlayingTrack.CREATE_TABLE);
        db.execSQL(Playlist.CREATE_TABLE);
        db.execSQL(PlaylistTrack.CREATE_TABLE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Album.DROP_TABLE);
        db.execSQL(Artist.DROP_TABLE);
        db.execSQL(Genre.DROP_TABLE);
        db.execSQL(Track.DROP_TABLE);
        db.execSQL(NowPlayingTrack.DROP_TABLE);
        db.execSQL(Playlist.DROP_TABLE);
        db.execSQL(PlaylistTrack.DROP_TABLE);
        onCreate(db);
    }

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    /**
     * Given an id it returns a @{link Cursor} containing the album information.
     *
     * @param id The id of the album in the database.
     * @return A cursor containing the album.
     */
    public synchronized Cursor getAlbumCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null
                ? db.query(Album.TABLE_NAME,
                new String[]{
                        Album._ID,
                        Album.ALBUM_NAME,
                        Album.ARTIST_ID
                },
                Album._ID + IS,
                new String[]{
                        Long.toString(id)
                },
                null, null, null, null)
                : null;
    }

    /**
     * Given an id it returns an @{link Artist} object with the related information;
     * @param id The id of the artist in the database.
     * @return The Artist object.
     */
    public synchronized Artist getArtist(final long id) {
        final Cursor cursor = getArtistCursor(id);
        final Artist artist;
        if (cursor.moveToFirst()) {
            artist = new Artist(cursor);
        } else {
            artist = null;
        }
        cursor.close();
        return artist;
    }

    /**
     * Given an id it returns the @{link Cursor} associated with the artist that matches
     * the specified id.
     * @param id The id of the artist in the database.
     * @return The cursor containing the artist information.
     */
    public synchronized Cursor getArtistCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null
                ? db.query(Artist.TABLE_NAME,
                    Artist.FIELDS,
                    Artist._ID + IS,
                    new String[]{
                            Long.toString(id)
                    },
                    null, null,
                    Artist.ARTIST_NAME + ASC,
                    null)
                : null;
    }

    public synchronized Cursor getAllArtistsCursor(final String selection,
                                                   final String[] args) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Artist.TABLE_NAME, Artist.FIELDS, selection,
                args, null, null, Artist.ARTIST_NAME + ASC) : null;
    }

    public synchronized Cursor getGenreCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Genre.TABLE_NAME, Genre.FIELDS,
                Genre._ID + IS, new String[]{Long.toString(id)},
                null, null, Genre.GENRE_NAME + ASC, null) : null;
    }

    public synchronized Cursor getAllGenresCursor(final String selection,
                                                  final String[] args) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Genre.TABLE_NAME, Genre.FIELDS,
                selection, args, null, null, Genre.GENRE_NAME + ASC) : null;
    }

    /**
     * Used to batch insert now playlist data in the library
     * @param list A list of now playing tracks
     */
    public synchronized void batchNowPlayingInsert(final List<NowPlayingTrack> list) {
        SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(NowPlayingTrack.INSERT);
            for (NowPlayingTrack track : list) {
                if (stm != null) {
                    stm.bindString(1, track.getArtist());
                    stm.bindString(2, track.getTitle());
                    stm.bindString(3, track.getSrc());
                    stm.bindLong(4, track.getPosition());
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
    }

    /**
     * Used to batch insert playlists in the sqlite database
     * @param list The list of playlists
     */
    public synchronized void batchInsertPlaylists(final List<Playlist> list) {
        final SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(Playlist.INSERT);
            for (Playlist playlist : list) {
                if (stm != null) {
                    stm.bindString(1, playlist.getName());
                    stm.bindString(2, playlist.getHash());
                    stm.bindLong(3, playlist.getTracks());
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
        mContext.getContentResolver().notifyChange(Playlist.getContentUri(), null, false);
    }

    /**
     * Used to batch insert the playlist tracks in the database
     * @param list The list of playlist tracks
     */
    public synchronized void batchInsertPlaylistTracks(final List<PlaylistTrack> list) {
        final SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(PlaylistTrack.INSERT);
            for (PlaylistTrack track: list) {
                if (stm != null) {
                    stm.bindString(1, track.getArtist());
                    stm.bindString(2, track.getTitle());
                    stm.bindLong(3, track.getIndex());
                    stm.bindString(4, track.getHash());
                    stm.bindString(5, track.getPlaylistHash());
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
        mContext.getContentResolver().notifyChange(PlaylistTrack.getContentUri(), null, false);
    }

    /**
     * Receives a batch list of track meta data in json format and inserts them
     * in the database using compiled statements
     * @param list A list containing track items
     */
    public synchronized void processBatch(final List<Track> list) {
        SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement artistStatement = mDb.compileStatement(Artist.INSERT);
            SQLiteStatement genreStatement = mDb.compileStatement(Genre.INSERT);
            SQLiteStatement albumStatement = mDb.compileStatement(Album.INSERT);
            SQLiteStatement trackStatement = mDb.compileStatement(Track.INSERT);

            for(Track track : list) {
                if (artistStatement != null) {
                    artistStatement.bindString(1, track.getArtist());
                    artistStatement.execute();
                    artistStatement.clearBindings();

                    artistStatement.bindString(1, track.getAlbumArtist());
                    artistStatement.execute();
                    artistStatement.clearBindings();
                }

                if (genreStatement != null) {
                    genreStatement.bindString(1, track.getGenre());
                    genreStatement.execute();
                    genreStatement.clearBindings();
                }


                if (albumStatement != null) {
                    albumStatement.bindString(1, track.getAlbum());
                    albumStatement.bindString(2, track.getAlbumArtist());
                    albumStatement.execute();
                    albumStatement.clearBindings();
                }

                if (trackStatement != null) {
                    trackStatement.bindString(1, track.getHash());
                    trackStatement.bindString(2, track.getTitle());
                    trackStatement.bindString(3, track.getGenre());
                    trackStatement.bindString(4, track.getArtist());
                    trackStatement.bindString(5, track.getAlbum());
                    trackStatement.bindString(6, track.getYear());
                    trackStatement.bindLong(7, track.getTrackNo());
                    trackStatement.execute();
                    trackStatement.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
    }

    public synchronized Cursor getAllPlaylistsCursor(final String sortOrder) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null
                ? db.query(Playlist.TABLE_NAME,
                new String[]{
                        Playlist._ID,
                        Playlist.PLAYLIST_NAME,
                        Playlist.PLAYLIST_HASH,
                        Playlist.PLAYLIST_TRACKS
                },
                null, null,
                null, null,
                sortOrder, null)
                : null;
    }

    public synchronized Cursor getTrackCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(Track.SELECT_TRACK, new String[]{Long.toString(id)}) : null;
    }

    public synchronized Cursor getAllTracksCursor(final String[] args) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(Track.SELECT_TRACKS, args) : null;
    }

    /**
     * Returns a map of the album_id field (combination of Album Artist and
     * Album name, to the database ids.
     * @return The map of album_ids to row ids in the database
     */
    public synchronized Map<String, Long> getAlbumIdMapping() {
        Map<String, Long> ids = new HashMap<>();
        final SQLiteDatabase db = this.getReadableDatabase();
        if (db != null){
            final Cursor cursor = db.rawQuery(Album.SELECT_ALBUM_ID, null);
            while(cursor.moveToNext()) {
                String albumId = cursor.getString(cursor.getColumnIndex("album_id"));
                long column_id = cursor.getLong(cursor.getColumnIndex("_id"));
                ids.put(albumId, column_id);
            }
        }
        return ids;
    }

    /**
     * Gets a list of covers and updates the cover hashes in the database
     * @param list The list of covers.
     */
    public synchronized void updateCoverHashes(List<Cover> list) {
        final Map<String, Long> ids = getAlbumIdMapping();
        final SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(Album.UPDATE_COVER);
            for (Cover cover : list) {
                if (stm != null) {
                    stm.bindString(1, cover.getCoverHash());
                    stm.bindLong(2, ids.get(cover.getAlbumId()));
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
    }
}
