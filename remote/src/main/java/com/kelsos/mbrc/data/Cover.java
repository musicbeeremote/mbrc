package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class Cover extends DataItem implements CoverColumns {

    private long id;
    private String coverHash;
    public static final String TABLE_NAME = "covers";
    public static final String[] FIELDS = {_ID, COVER_HASH};

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
                    + COVER_HASH + " text unique " + ")";
    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(LibraryProvider.AUTHORITY_URI, TABLE_NAME);
    public static final Uri CONTENT_IMAGE_URI = Uri.withAppendedPath(CONTENT_URI, "image");

    public static final int BASE_IMAGE_CODE = 0x92358;
    public static final int BASE_URI_CODE = 0xa9872c3;
    public static final int BASE_ITEM_CODE =  0xf62e95d;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/image/*", BASE_IMAGE_CODE);
    }

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

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

    @Override public String getTableName() {
        return TABLE_NAME;
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
