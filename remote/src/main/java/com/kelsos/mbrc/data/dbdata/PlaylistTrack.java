package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = PlaylistTrack.TABLE_NAME)
public class PlaylistTrack {

    public static final String TABLE_NAME = "playlist_tracks";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String artist;

    @DatabaseField
    private String title;

    @DatabaseField
    private String path;

    @DatabaseField
    private int index;

    @DatabaseField(canBeNull = false, foreign = true)
    private Playlist playlist;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
