package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.ArtistColumns;

@DatabaseTable(tableName = Artist.TABLE_NAME)
public class Artist extends DataItem implements ArtistColumns {
    public static final String TABLE_NAME = "artists";
    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final Uri CONTENT_GENRE_URI = Uri.withAppendedPath(getContentUri(), "genre");
    public static final int BASE_URI_CODE = 0xb450ddf;
    public static final int BASE_ITEM_CODE = 0x4213467;
    public static final int BASE_GENRE_FILTER = 0x099198;
    @DatabaseField
    private String artistName;
    @DatabaseField(generatedId = true)
    private long id;

    public Artist(String artistName) {
        this.artistName = artistName.length() > 0 ? artistName : "Unknown Artist";
        this.id = -1;
    }

    public Artist(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.artistName = cursor.getString(cursor.getColumnIndex(ARTIST_NAME));
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/genre/*", BASE_GENRE_FILTER);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ARTIST_NAME, artistName);
        return values;
    }

    @Override
    public String getTableName() {
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

}
