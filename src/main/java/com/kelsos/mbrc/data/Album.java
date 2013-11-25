package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Album implements BaseColumns, AlbumColumns {
    public static final String TABLE_NAME = "albums";
    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," +
                    ALBUM_NAME + " text unique " + ARTIST_ID + "integer, " +
                    "foreign key (" + ARTIST_ID + ") references " +
                    Artist.TABLE_NAME + "(" + _ID + ") on delete cascade" + ")";
    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    private long id;
    private String albumName;
    private long artistId;
    private String artist;

    public Album(String albumName, String artist) {
        this.id = -1;
        this.albumName = albumName;
        this.artist = artist;
    }

    public Album(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.albumName = cursor.getString(cursor.getColumnIndex(ALBUM_NAME));
        this.artistId = cursor.getLong(cursor.getColumnIndex(ARTIST_ID));
        this.artist = cursor.getString(cursor.getColumnIndex(Artist.ARTIST_NAME));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ALBUM_NAME, albumName);
        values.put(ARTIST_ID, artistId);
        return values;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
