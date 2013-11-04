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
    private int trackNo;

    public LibraryTrack(int id, String artist, String albumArtist, String album, String title, String genre, String cover, String updated, int trackNo) {
        this.id = id;
        this.artist = artist;
        this.albumArtist = albumArtist;
        this.album = album;
        this.title = title;
        this.genre = genre;
        this.cover = cover;
        this.updated = updated;
        this.trackNo = trackNo;
    }

    public int getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getAlbum() {
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getCover() {
        return cover;
    }

    public String getUpdated() {
        return updated;
    }

    public int getTrackNo() {
        return trackNo;
    }
}
