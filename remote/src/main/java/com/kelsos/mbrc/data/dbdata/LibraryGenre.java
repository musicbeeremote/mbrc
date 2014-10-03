package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.GenreColumns;

@DatabaseTable(tableName = LibraryGenre.TABLE_NAME)
public class LibraryGenre extends DataItem implements GenreColumns {

    public static final String TABLE_NAME = "genres";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(LibraryProvider.AUTHORITY_URI, TABLE_NAME);
    public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final int BASE_URI_CODE = 0x31847c3;
    public static final int BASE_ITEM_CODE = 0x1e2395d;
    public static final int BASE_FILTER_CODE = 0x214569;
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String genreName;

    public LibraryGenre(String genreName) {
        this.id = -1;
        this.genreName = genreName.length() > 0 ? genreName : "Unknown Genre";
    }

    public LibraryGenre(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.genreName = cursor.getString(cursor.getColumnIndex(GENRE_NAME));
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/filter/*", BASE_FILTER_CODE);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(GENRE_NAME, genreName);
        return values;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
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
