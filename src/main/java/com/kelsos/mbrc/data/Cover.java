package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class Cover implements BaseColumns, CoverColumns {
    private long id;
    private String coverHash;
    public static final String TABLE_NAME = "covers";

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," +
                    COVER_HASH + " text unique " + ")";
    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public static Uri URI() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME +
                LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final int BASE_URI_CODE = 0xa9872c3;
    public static final int BASE_ITEM_CODE =  0xf62e95d;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

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
