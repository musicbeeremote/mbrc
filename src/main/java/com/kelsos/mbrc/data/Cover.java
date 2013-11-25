package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Cover implements BaseColumns, CoverColumns {
    private long id;
    private String coverHash;
    public static final String TABLE_NAME = "covers";

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," +
                    COVER_HASH + " text unique " + ")";

    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public Cover(String coverHash) {
        this.id = -1;
        this.coverHash = coverHash;
    }

    public Cover(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.coverHash = cursor.getString(cursor.getColumnIndex(COVER_HASH));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(COVER_HASH, coverHash);
        return values;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public void setCoverHash(String coverHash) {
        this.coverHash = coverHash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
