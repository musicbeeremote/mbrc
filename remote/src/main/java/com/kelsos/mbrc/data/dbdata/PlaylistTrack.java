package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
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
            + PLAYLIST_ID + ") values (?, ?, ?, (select _id from playlists where playlist_hash = ?))";
    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME
                    + "(" + _ID + " integer primary key, "
                    + ARTIST + " text, "
                    + TITLE + " text, "
                    + INDEX + " integer, "
                    + PLAYLIST_ID + " integer, "
                    + "foreign key (" + PLAYLIST_ID + ") references "
                    + "playlists (" + _ID + ") on delete cascade )";

    private String artist;
    private String title;
    private String playlistHash;
    private int index;
    private int playlistId;
    private long id;

    public PlaylistTrack(final JsonNode node) {
        this.artist = node.path("artist").asText();
        this.title = node.path("title").asText();
        this.index = node.path("index").asInt(0);
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME
                + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

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
}
