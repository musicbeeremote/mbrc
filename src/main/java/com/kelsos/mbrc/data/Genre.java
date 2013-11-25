package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Genre implements BaseColumns, GenreColumns {
    private long id;
    private String genreName;
    public static final String TABLE_NAME = "genres";

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," +
                    GENRE_NAME + " text unique " + ")";

    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public Genre(String genreName) {
        this.id = -1;
        this.genreName = genreName;
    }

    public Genre(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.genreName = cursor.getString(cursor.getColumnIndex(GENRE_NAME));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(GENRE_NAME, genreName);
        return values;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
