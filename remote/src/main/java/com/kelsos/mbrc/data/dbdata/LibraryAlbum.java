package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.AlbumColumns;

@DatabaseTable(tableName = LibraryAlbum.TABLE_NAME)
public class LibraryAlbum extends DataItem implements AlbumColumns {
    public static final String TABLE_NAME = "albums";
    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final Uri CONTENT_ARTIST_URI = Uri.withAppendedPath(getContentUri(), "artist");
    public static final int BASE_URI_CODE = 0x33872c3;
    public static final int BASE_ITEM_CODE = 0x462395d;
    public static final int BASE_ARTIST_FILTER = 0x92810d;
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String albumName;
    @DatabaseField(canBeNull = false, foreign = true)
    private LibraryArtist artist;
    private String coverHash;

    public LibraryAlbum() {
        // Required no-arg constructor

    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/artist/*", BASE_ARTIST_FILTER);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        return values;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public LibraryArtist getArtist() {
        return artist;
    }

    public void setArtist(LibraryArtist artist) {
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

    public String getCoverHash() {
        return coverHash;
    }

    public void setCoverHash(String coverHash) {
        this.coverHash = coverHash;
    }
}
