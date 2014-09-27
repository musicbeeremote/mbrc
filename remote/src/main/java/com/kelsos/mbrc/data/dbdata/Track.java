package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.TrackColumns;
import com.kelsos.mbrc.util.RemoteUtils;

import java.util.Date;

@DatabaseTable(tableName = Track.TABLE_NAME)
public class Track extends DataItem implements TrackColumns {
    public static final String TABLE_NAME = "tracks";
    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final Uri CONTENT_ALBUM_URI = Uri.withAppendedPath(getContentUri(), "album");
    public static final int BASE_URI_CODE = 0x8ee72c3;
    public static final int BASE_ITEM_CODE = 0x63b3c5d;
    public static final int BASE_ALBUM_FILTER_CODE = 0x3821dd2e;
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String hash;
    @DatabaseField
    private String title;
    @DatabaseField(foreign = true)
    private Album album;
    @DatabaseField(foreign = true)
    private Artist artist;
    @DatabaseField
    private String year;
    @DatabaseField
    private int trackNo;
    @DatabaseField
    private Date updated;


    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME +
                LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/album/*", BASE_ALBUM_FILTER_CODE);
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(HASH, hash);
        values.put(TITLE, title);
        values.put(YEAR, year);
        values.put(TRACK_NO, trackNo);
        values.put(UPDATED, RemoteUtils.currentTime());
        return values;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Album getAlbum() {
        return album;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }
}
