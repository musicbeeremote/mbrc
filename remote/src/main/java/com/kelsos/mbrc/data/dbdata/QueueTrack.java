package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName = QueueTrack.TABLE_NAME)
public class QueueTrack {
    public static final String TABLE_NAME = "queue";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String artist;

    @DatabaseField
    private String title;

    @DatabaseField
    private int index;

    @DatabaseField
    private String path;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
