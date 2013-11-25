package com.kelsos.mbrc.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LibraryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackLibrary.db";

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(Album.CREATE_TABLE);
        db.execSQL(Artist.CREATE_TABLE);
        db.execSQL(Cover.CREATE_TABLE);
        db.execSQL(Genre.CREATE_TABLE);
        db.execSQL(Track.CREATE_TABLE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Album.DROP_TABLE);
        db.execSQL(Artist.DROP_TABLE);
        db.execSQL(Cover.DROP_TABLE);
        db.execSQL(Genre.DROP_TABLE);
        db.execSQL(Track.DROP_TABLE);

        onCreate(db);
    }

    public synchronized Album getAlbum(final long id) {
        return null;
    }

    public synchronized long insertAlbum(final Album album) {
        album.setId(getAlbumId(album.getAlbumName()));
        if (album.getId() > 0) {
            return album.getId();
        }
        album.setArtistId(insertArtist(new Artist(album.getArtist())));
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(Album.TABLE_NAME, null, album.getContentValues());
    }

    public synchronized int deleteAlbum(final Album album) {
        return -1;
    }

    public synchronized long getAlbumId(final String albumName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, Album.TABLE_NAME, new String[] {Album._ID},
                Album.ALBUM_NAME + " = ?", new String[] {albumName},
                null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(Album._ID));
            } while (c.moveToNext());
        }
        c.close();
        return id;
    }

    public synchronized Artist getArtist(final long id) {
        return null;
    }

    public synchronized long getArtistId(final String artistName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, Artist.TABLE_NAME, new String[] {Artist._ID},
                Artist.ARTIST_NAME + " = ?", new String[] {artistName},
                null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(Artist._ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public synchronized long insertArtist(final Artist artist) {
        long id = -1;
        id = getArtistId(artist.getArtistName());
        if (id > 0) {
            return id;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(Artist.TABLE_NAME, null, artist.getContentValues());
    }

    public synchronized long insertGenre(final Genre genre) {
        genre.setId(getGenreId(genre.getGenreName()));
        if (genre.getId() > 0) {
            return genre.getId();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(Genre.TABLE_NAME, null, genre.getContentValues());
    }

    public synchronized long getGenreId (final String genreName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, Genre.TABLE_NAME, new String[] {Genre._ID},
                Genre.GENRE_NAME + " = ?", new String[] {genreName},
                null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(Genre._ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public synchronized long getCoverId (final String hash) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, Cover.COVER_HASH, new String[] {Cover._ID},
                Cover.COVER_HASH + " = ?", new String[] {hash},
                null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(Cover._ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public synchronized long insertCover (final Cover cover) {
        cover.setId(getCoverId(cover.getCoverHash()));
        if (cover.getId() > 0) {
            return cover.getId();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(Cover.TABLE_NAME, null, cover.getContentValues());
    }

    public synchronized long getTrackId(String hash) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, Track.TABLE_NAME, new String[] {Track._ID},
                Track.HASH + " = ?", new String[] {hash},
                null, null, null, null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(Track._ID));
            } while (c.moveToNext());
        }
        c.close();

        return id;
    }

    public synchronized long insertTrack (final Track track) {
        track.setAlbumId(insertAlbum(new Album(track.getAlbum(),track.getAlbumArtist())));
        track.setArtistId(insertArtist(new Artist(track.getArtist())));
        track.setGenreId(insertGenre(new Genre(track.getGenre())));
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(Track.TABLE_NAME, null, track.getContentValues());
    }
}
