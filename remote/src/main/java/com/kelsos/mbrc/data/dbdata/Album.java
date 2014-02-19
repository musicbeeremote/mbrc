package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.AlbumColumns;

public class Album extends DataItem implements AlbumColumns {
    public static final String TABLE_NAME = "albums";
    public static final String CREATE_TABLE = String.format("create table %s (%s integer primary key, "
            + "%s text, %s integer, %s cover_hash, foreign key (%s) references "
            + "%s (%s) on delete cascade, unique(%s) on conflict ignore)",
            TABLE_NAME, _ID, ALBUM_NAME, ARTIST_ID, COVER_HASH, ARTIST_ID, "artists",_ID, ALBUM_NAME);

    public static final String DROP_TABLE = String.format("drop table if exists %s", TABLE_NAME);
    public static final String INSERT = String.format("insert into %s (%s , %s) values "
            + "(?, (select _id from artists where artist_name = ?))", TABLE_NAME, ALBUM_NAME, ARTIST_ID);
    public static final String SELECT_ALBUM_ID =
            "select (artist_name || album_name) as album_id, al._id as _id "
                    + "from artists ar, albums al "
                    + "where ar._id = al.artist_id";

    public static final String UPDATE_COVER =
            "update albums " +
            "set cover_hash = ? " +
            "where _id = ?";


    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final Uri CONTENT_ARTIST_URI = Uri.withAppendedPath(getContentUri(), "artist");

    public static final int BASE_URI_CODE = 0x33872c3;
    public static final int BASE_ITEM_CODE =  0x462395d;
    public static final int BASE_ARTIST_FILTER = 0x92810d;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/artist/*", BASE_ARTIST_FILTER);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

    private long id;
    private String albumName;
    private long artistId;
    private String artist;
    private String coverHash;
    private int totalTracks;

    public Album(String albumName, String artist) {
        this.id = -1;
        this.albumName = albumName.length() > 0 ? albumName : "Unknown Album";
        this.artist = artist;
    }

    public Album(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.albumName = cursor.getString(cursor.getColumnIndex(ALBUM_NAME));
        this.artistId = cursor.getLong(cursor.getColumnIndex(ARTIST_ID));
        this.artist = cursor.getString(cursor.getColumnIndex(Artist.ARTIST_NAME));
       // this.coverHash = cursor.getString(cursor.getColumnIndex(COVER_HASH));
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ALBUM_NAME, albumName);
        values.put(ARTIST_ID, artistId);
        return values;
    }

    @Override public String getTableName() {
        return TABLE_NAME;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
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

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

}
