package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class Artist extends DataItem implements ArtistColumns {
    private String artistName;
    private String imageUrl;
    private long id;
    public static final String TABLE_NAME = "artists";
    public static final String[] FIELDS = {_ID, ARTIST_NAME };

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
            + ARTIST_NAME + " text unique," + IMAGE_URL + " text" + ")";
    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final int BASE_URI_CODE = 0xb450ddf;
    public static final int BASE_ITEM_CODE =  0x4213467;
    public static final int BASE_GENRE_FILTER = 0x099198;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/genre/*", BASE_GENRE_FILTER);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

    public Artist(String artistName, String imageUrl) {
        this.artistName = artistName;
        this.id = -1;
        this.imageUrl = imageUrl;
    }

    public Artist(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.artistName = cursor.getString(cursor.getColumnIndex(ARTIST_NAME));
        this.imageUrl = cursor.getString(cursor.getColumnIndex(IMAGE_URL));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME, artistName);
        return values;
    }

    @Override public String getTableName() {
        return TABLE_NAME;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
