package com.kelsos.mbrc.data;

public class AlbumEntry {
    private String artist;
    private String album;
    private int count;

    public AlbumEntry(String artist, String album, int count) {
        this.artist = artist;
        this.album = album;
        this.count = count;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbum(){
        return album;
    }

    public int getCount(){
        return count;
    }
}
