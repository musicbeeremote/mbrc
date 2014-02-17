package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.PlaylistTrackColumns;
import org.codehaus.jackson.JsonNode;

public class PlaylistTrack extends DataItem implements PlaylistTrackColumns {

    public static final String TABLE_NAME = "playlist_tracks";
    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;
    public static final String INSERT = "insert into " + TABLE_NAME + " ("
            + ARTIST + ","
            + TITLE + ","
            + INDEX + ","
            + HASH + ","
            + PLAYLIST_ID + ") values (?, ?, ?, ?, (select _id from playlists where playlist_hash = ?))";
    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME
                    + "(" + _ID + " integer primary key, "
                    + ARTIST + " text, "
                    + TITLE + " text, "
                    + INDEX + " integer, "
                    + HASH + " text, "
                    + PLAYLIST_ID + " integer, "
                    + "foreign key (" + PLAYLIST_ID + ") references "
                    + "playlists (" + _ID + ") on delete cascade )";

    private String artist;
    private String title;
    private String playlistHash;
    private String hash;
    private int index;
    private int playlistId;
    private long id;

    public PlaylistTrack(final JsonNode node) {
        this.artist = node.path("artist").asText();
        this.title = node.path("title").asText();
        this.index = node.path("index").asInt(0);
        this.hash = node.path("hash").asText();
    }

    public PlaylistTrack(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.artist = cursor.getString(cursor.getColumnIndex(ARTIST));
        this.title = cursor.getString(cursor.getColumnIndex(TITLE));
        this.playlistId = cursor.getInt(cursor.getColumnIndex(PLAYLIST_ID));
        this.hash = cursor.getString(cursor.getColumnIndex(HASH));
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME
                + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(LibraryProvider.AUTHORITY_URI, TABLE_NAME);
    public static final Uri CONTENT_HASH_URI = Uri.withAppendedPath(CONTENT_URI, "hash");

    public static final int BASE_URI_CODE = 0x39399c3;
    public static final int BASE_ITEM_CODE =  0x1eb3b5d;
    public static final int BASE_HASH_CODE = 0xe1f569;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/hash/*", BASE_HASH_CODE);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public ContentValues getContentValues() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getPlaylistHash() {
        return playlistHash;
    }

    public void setPlaylistHash(String playlistHash) {
        this.playlistHash = playlistHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
