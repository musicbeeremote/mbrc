package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = LibraryAlbum.TABLE_NAME)
public class LibraryAlbum {
    public static final String TABLE_NAME = "albums";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String name;

    @DatabaseField(canBeNull = false, foreign = true)
    private LibraryArtist artist;

    @DatabaseField(foreign = true)
    private LibraryCover cover;

    @DatabaseField
    private String albumId;

    public LibraryAlbum() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LibraryArtist getArtist() {
        return artist;
    }

    public void setArtist(LibraryArtist artist) {
        this.artist = artist;
    }

    public LibraryCover getCover() {
        return cover;
    }

    public void setCover(LibraryCover cover) {
        this.cover = cover;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
