package com.kelsos.mbrc.model;

public class LibraryTrack {
    private int id;
    private String artist;
    private String albumArtist;
    private String album;
    private String title;
    private String genre;
    private String cover;
    private String updated;
    private String file;
    private int trackNo;

    public LibraryTrack() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
