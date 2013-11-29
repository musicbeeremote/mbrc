package com.kelsos.mbrc.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LibraryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackLibrary.db";
    private final Context mContext;

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
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

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    public synchronized Cursor getAlbumCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.rawQuery(Album.SELECT_ALBUM, new String[]{Long.toString(id)});
        return cursor;
    }

    public synchronized Album getAlbum(final long id) {
        final Cursor cursor = getAlbumCursor(id);
        final Album result;
        result = cursor.moveToFirst() ? new Album(cursor) : null;
        cursor.close();
        return result;
    }

    public synchronized Cursor getAllAlbumsCursor() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.rawQuery(Album.SELECT_ALBUMS, null);
        return cursor;
    }

    public synchronized List<Album> getAllAlbums() {
        final List<Album> result = new ArrayList<Album>();
        final Cursor cursor = getAllAlbumsCursor();

        if (cursor.moveToFirst()) {
            while(cursor.moveToNext()) {
                Album album = new Album(cursor);
                result.add(album);
            }
        }

        cursor.close();
        return result;
    }

    public synchronized long insertAlbum(final Album album) {
        album.setId(getAlbumId(album.getAlbumName()));
        if (album.getId() > 0) {
            return album.getId();
        }
        album.setArtistId(insertArtist(new Artist(album.getArtist())));
        SQLiteDatabase db = this.getWritableDatabase();
        final long id = db.insert(Album.TABLE_NAME, null, album.getContentValues());
        if (id > 0) {
            album.notifyProvider(mContext);
        }

        return id;
    }

    public synchronized int deleteItem(final DataItem item) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(item.getTableName(), Album._ID +
            " IS ?", new String[] {Long.toString(item.getId())});

        if (result > 0) {
            item.notifyProvider(mContext);
        }
        return result;
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
        final Cursor cursor = getArtistCursor(id);
        final Artist artist;
        if (cursor.moveToFirst()) {
            artist = new Artist(cursor);
        } else {
            artist = null;
        }
        return artist;
    }

    public synchronized Cursor getArtistCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(Artist.TABLE_NAME, Artist.FIELDS,
                Artist._ID + " IS ?", new String[] {Long.toString(id)},
                null,null,null,null);
        return cursor;
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

    public synchronized Cursor getAllArtistsCursor() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(Artist.TABLE_NAME, Artist.FIELDS, null,
                null,null,null,null);
        return cursor;
    }

    public synchronized List<Artist> getAllArtists() {
        final List<Artist> artistList = new ArrayList<Artist>();
        final Cursor cursor = getAllArtistsCursor();
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Artist artist = new Artist(cursor);
                artistList.add(artist);
            }
        }
        cursor.close();
        return artistList;
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
