package com.kelsos.mbrc.data.dbdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = LibraryTrack.TABLE_NAME)
public class LibraryTrack {

    public static final String TABLE_NAME = "tracks";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String title;

    @DatabaseField
    private int index;

    @DatabaseField(foreign = true)
    private LibraryGenre genre;

    @DatabaseField(foreign = true)
    private LibraryArtist artist;

    @DatabaseField(foreign = true)
    private LibraryAlbum album;

    @DatabaseField
    private String year;

    @DatabaseField
    private String path;


    public LibraryTrack() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public LibraryGenre getGenre() {
        return genre;
    }

    public void setGenre(LibraryGenre genre) {
        this.genre = genre;
    }

    public LibraryArtist getArtist() {
        return artist;
    }

    public void setArtist(LibraryArtist artist) {
        this.artist = artist;
    }

    public LibraryAlbum getAlbum() {
        return album;
    }

    public void setAlbum(LibraryAlbum album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
