package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.PlaylistColumns;
import org.codehaus.jackson.JsonNode;

public class Playlist extends DataItem implements PlaylistColumns {
    private long id;
    private String name;
    private String hash;
    private int tracks;

    public static final String TABLE_NAME = "playlists";
    public static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;
    public static final String INSERT = "insert into " + TABLE_NAME + " ("
            + PLAYLIST_NAME + ","
            + PLAYLIST_HASH + ","
            + PLAYLIST_TRACKS + ") values (?, ?, ?)";
    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME
            + "(" + _ID + " integer primary key,"
            + PLAYLIST_NAME + " text,"
            + PLAYLIST_HASH + " text,"
            + PLAYLIST_TRACKS + " integer, "
            + "unique(" + PLAYLIST_HASH + ") on conflict ignore)";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;

    public static final int BASE_URI_CODE = 0x3ae47c3;
    public static final int BASE_ITEM_CODE =  0x2e21f5d;

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
    }

    public Playlist(String name, String hash, int tracks) {
        this.name = name;
        this.hash = hash;
        this.tracks = tracks;
    }

    public Playlist(JsonNode node) {
        this.name = node.path("name").getTextValue();
        this.tracks = node.path("tracks").getIntValue();
        this.hash = node.path("hash").getTextValue();
    }

    public Playlist(final Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(_ID));
        this.name = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME));
        this.hash = cursor.getString(cursor.getColumnIndex(PLAYLIST_HASH));
        this.tracks = cursor.getInt(cursor.getColumnIndex(PLAYLIST_TRACKS));
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME +
                LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public int getTracks() {
        return tracks;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    @Override
    public ContentValues getContentValues() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }
}
