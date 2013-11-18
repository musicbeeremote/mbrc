package com.kelsos.mbrc.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kelsos.mbrc.util.RemoteUtils;

import java.util.ArrayList;
import java.util.List;

public class LibraryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackLibrary.db";

    private static final String TABLE_LIBRARY ="library";
    private static final String TABLE_GENRES = "genres";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_ALBUMS = "albums";
    private static final String TABLE_COVERS = "covers";

    private static final String ENTRY_ID = "id";
    private static final String FILE = "file";
    private static final String ARTIST = "artist";
    private static final String ALBUMARTIST = "albumartist";
    private static final String TITLE = "title";
    private static final String ALBUM = "album";
    private static final String YEAR = "year";
    private static final String TRACK_NO = "track_no";
    private static final String GENRE = "genre";
    private static final String COVER = "cover";
    private static final String UPDATED = "updated";
    private static final String NAME = "name";
    private static final String SHA1 = "sha1";
    private static final String LENGTH = "length";

    private static final String CREATE_TABLE_GENRES = "CREATE TABLE " +
            TABLE_GENRES + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            NAME + " TEXT UNIQUE" + ")";

    private static final String CREATE_TABLE_ARTISTS = "CREATE TABLE " +
            TABLE_ARTISTS + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            NAME + " TEXT UNIQUE" + ")";

    private static final String CREATE_TABLE_ALBUMS = "CREATE TABLE " +
            TABLE_ALBUMS + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            NAME + " TEXT UNIQUE" + ")";


    private static final String CREATE_TABLE_LIBRARY = "CREATE TABLE "
            + TABLE_LIBRARY + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            SHA1 + " TEXT UNIQUE," + ARTIST + " INTEGER," + ALBUMARTIST + " INTEGER," +
            TITLE + " TEXT," + ALBUM + " INTEGER," + YEAR + " TEXT," +
            GENRE + " INTEGER," + COVER + " INTEGER," + TRACK_NO + " INTEGER," + UPDATED
            + " DATETIME" + ")";

    private static final String CREATE_TABLE_COVERS = "CREATE TABLE " +
            TABLE_COVERS + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            FILE + " TEXT UNIQUE," + SHA1 + " TEXT UNIQUE," + LENGTH + " INTEGER," +
            UPDATED + " DATETIME" + ")";

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALBUMS);
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_COVERS);
        db.execSQL(CREATE_TABLE_LIBRARY);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COVERS);

        onCreate(db);
    }

    public long getLibraryEntryId(String sha1) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, TABLE_LIBRARY, new String[] {ENTRY_ID},
                SHA1 + " = ?", new String[] {sha1}, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(ENTRY_ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public long createLibraryEntry(LibraryTrack track) {
        long id = -1;

        if (getLibraryEntryId(track.getSha1()) >= 0) {
            return id;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SHA1, track.getSha1());

        long artist = getArtistId(track.getArtist());
        if (artist < 0) {
            artist = insertArtist(track.getArtist());
        }
        values.put(ARTIST, artist);
        long albumarist = getArtistId(track.getAlbumArtist());
        if (albumarist < 0) {
            albumarist = insertArtist(track.getAlbumArtist());
        }
        values.put(ALBUMARTIST, albumarist);
        long album = getAlbumId(track.getAlbum());
        if (album < 0) {
            album = insertAlbum(track.getAlbum());
        }
        values.put(ALBUM, album);
        long genre = getGenreId(track.getGenre());
        if (genre < 0) {
            genre = insertGenre(track.getGenre());
        }

        values.put(COVER,getCoverId(track.getCover()));
        values.put(GENRE, genre);
        values.put(YEAR, track.getYear());
        values.put(TRACK_NO, track.getTrackNo());
        values.put(UPDATED, RemoteUtils.Now());
        values.put(TITLE, track.getTitle());

        try {
            id = db.insert(TABLE_LIBRARY, null, values);
        } catch (SQLiteConstraintException ex) {}

        return id;
    }



    public List<LibraryTrack> getAllTracks() {
        List<LibraryTrack> tracks = new ArrayList<LibraryTrack>();
        String selectQuery = "SELECT * FROM " + TABLE_LIBRARY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                LibraryTrack track = new LibraryTrack();
                track.setId(c.getInt(c.getColumnIndex(ENTRY_ID)));
                track.setSha1(c.getString(c.getColumnIndex(FILE)));
                tracks.add(track);
            } while (c.moveToNext());
        }
        return tracks;
    }


    public long insertCover (String sha1, int length) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SHA1, sha1);
        values.put(LENGTH, length);
        values.put(UPDATED, RemoteUtils.Now());
        return db.insert(TABLE_COVERS, null, values);
    }

    public long getCoverId(String sha1) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, TABLE_COVERS, new String[] {ENTRY_ID},
                SHA1 + " = ?", new String[] {sha1}, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(ENTRY_ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;

        //SQLiteDatabase db = this.getReadableDatabase();
        //String selectQuery = "SELECT * FROM " + TABLE_COVERS + " WHERE " + SHA1 + " = '" + sha1 + "'";
        //Cursor c = db.rawQuery(selectQuery, null);

//        if (c.moveToFirst()) {
//            do {
//                LibraryTrack track = new LibraryTrack();
//                track.setId(c.getInt(c.getColumnIndex(ENTRY_ID)));
//                track.setSha1(c.getString(c.getColumnIndex(FILE)));
//                tracks.add(track);
//            } while (c.moveToNext());
//        }
//        return tracks;
    }

    public long insertArtist(String artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, artist);
        return db.insert(TABLE_ARTISTS, null, values);
    }

    public long insertAlbum(String album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, album);
        return db.insert(TABLE_ALBUMS, null, values);
    }

    public long insertGenre(String genre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, genre);
        return db.insert(TABLE_GENRES, null, values);
    }

    public long getArtistId(String artistName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, TABLE_ARTISTS, new String[] {ENTRY_ID},
                NAME + " = ?", new String[] {artistName}, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(ENTRY_ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public long getAlbumId (String albumName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, TABLE_ALBUMS, new String[] {ENTRY_ID},
                NAME + " = ?", new String[] {albumName}, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(ENTRY_ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public long getGenreId (String genreName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, TABLE_GENRES, new String[] {ENTRY_ID},
                NAME + " = ?", new String[] {genreName}, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(ENTRY_ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }
}
