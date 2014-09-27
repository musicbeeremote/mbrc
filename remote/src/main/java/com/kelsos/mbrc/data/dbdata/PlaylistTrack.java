package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kelsos.mbrc.data.db.LibraryProvider;
import org.codehaus.jackson.JsonNode;

@DatabaseTable(tableName = PlaylistTrack.TABLE_NAME)
public class PlaylistTrack extends DataItem {

    public static final String TABLE_NAME = "playlist_tracks";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(LibraryProvider.AUTHORITY_URI, TABLE_NAME);
    public static final Uri CONTENT_HASH_URI = Uri.withAppendedPath(CONTENT_URI, "hash");
    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final int BASE_URI_CODE = 0x39399c3;
    public static final int BASE_ITEM_CODE = 0x1eb3b5d;
    public static final int BASE_HASH_CODE = 0xe1f569;
    @DatabaseField
    private String artist;
    @DatabaseField
    private String title;
    @DatabaseField
    private String hash;
    @DatabaseField
    private int index;
    @DatabaseField(canBeNull = false, foreign = true)
    private Playlist playlist;
    @DatabaseField(id = true)
    private long id;

    public PlaylistTrack() {
        // required no-arg constructor
    }

    public PlaylistTrack(final JsonNode node) {
        this.artist = node.path("artist").asText();
        this.title = node.path("title").asText();
        this.index = node.path("index").asInt(0);
        this.hash = node.path("hash").asText();
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME
                + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/hash/*", BASE_HASH_CODE);
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

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
