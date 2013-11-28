package com.kelsos.mbrc.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import com.kelsos.mbrc.util.RemoteUtils;

import java.util.Date;

public class Track implements BaseColumns, TrackColumns {
    private long id;
    private String hash;
    private String title;
    private long albumId;
    private String album;
    private String albumArtist;
    private long genreId;
    private String genre;
    private long artistId;
    private String artist;
    private String year;
    private int trackNo;
    private long coverId;
    private String coverHash;
    private Date updated;
    public static final String TABLE_NAME = "tracks";

    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "(" + _ID +
            " integer primary key autoincrement," + HASH + " text unique," +
            TITLE + " text," + ALBUM_ID + " integer, " + GENRE_ID + " integer," +
            ARTIST_ID + " integer," + YEAR + " text," + TRACK_NO + " integer," +
            COVER_ID + " integer, " + UPDATED + " datetime, " +
            "foreign key (" + ARTIST_ID + ") references " +
            Artist.TABLE_NAME + "("+ _ID + ") on delete cascade, " +
            "foreign key (" + ALBUM_ID + ") references " +
            Album.TABLE_NAME + "("+ _ID + ") on delete cascade, " +
            "foreign key (" + GENRE_ID + ") references " +
            Genre.TABLE_NAME + "("+ _ID + ") on delete cascade, " +
            "foreign key (" + COVER_ID + ") references " +
            Cover.TABLE_NAME + "("+ _ID + ") on delete cascade " + ")";

    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;


    public static Uri URI() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME +
                LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final int BASE_URI_CODE = 0x8ee72c3;
    public static final int BASE_ITEM_CODE =  0x63b3c5d;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

    public Track() {
        this.id = -1;
        this.hash = "";
        this.title = "";
        this.albumId = -1;
        this.album = "";
        this.genreId = -1;
        this.genre = "";
        this.artistId = -1;
        this.artist = "";
        this.year = "";
        this.trackNo = -1;
        this.coverId = -1;
    }

    public Track(String hash, String title, String album, String genre, String artist, String year, int trackNo, Date updated) {
        this.id = -1;
        this.albumId = -1;
        this.genreId = -1;
        this.artistId = -1;
        this.coverId = -1;
        this.hash = hash;
        this.title = title;
        this.album = album;
        this.genre = genre;
        this.artist = artist;
        this.year = year;
        this.trackNo = trackNo;
        this.updated = updated;
    }

    public Track(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.hash = cursor.getString(cursor.getColumnIndex(HASH));
        this.title = cursor.getString(cursor.getColumnIndex(TITLE));
        this.albumId = cursor.getLong(cursor.getColumnIndex(ALBUM_ID));
        this.album = cursor.getString(cursor.getColumnIndex(Album.ALBUM_NAME));
        this.genreId = cursor.getLong(cursor.getColumnIndex(GENRE_ID));
        this.genre = cursor.getString(cursor.getColumnIndex(Genre.GENRE_NAME));
        this.artistId = cursor.getLong(cursor.getColumnIndex(ARTIST_ID));
        this.artist = cursor.getString(cursor.getColumnIndex(Artist.ARTIST_NAME));
        this.year = cursor.getString(cursor.getColumnIndex(YEAR));
        this.trackNo = cursor.getInt(cursor.getColumnIndex(TRACK_NO));
        this.coverId = cursor.getLong(cursor.getColumnIndex(COVER_ID));
        //this.updated = updated;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(HASH, hash);
        values.put(TITLE, title);
        values.put(ALBUM_ID, albumId);
        values.put(GENRE_ID, genreId);
        values.put(ARTIST_ID, artistId);
        values.put(YEAR, year);
        values.put(TRACK_NO, trackNo);
        values.put(COVER_ID, coverId);
        values.put(UPDATED, RemoteUtils.Now());
        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public void setCoverHash(String coverHash) {
        this.coverHash = coverHash;
    }
}
