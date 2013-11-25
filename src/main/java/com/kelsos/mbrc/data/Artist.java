package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Artist implements BaseColumns, ArtistColumns{
    private String artistName;
    private long id;
    public static final String TABLE_NAME = "artists";
    public static final String[] FIELDS = { _ID, ARTIST_NAME };

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
            + ARTIST_NAME + " text unique" + ")";

    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public Artist(String artistName) {
        this.artistName = artistName;
        this.id = -1;
    }

    public Artist(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.artistName = cursor.getString(cursor.getColumnIndex(ARTIST_NAME));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME, artistName);
        return values;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
