package com.kelsos.mbrc.data;

public class ArtistEntry {
    private String artist;
    private int count;

    public ArtistEntry(String artist, int count) {
        this.artist = artist;
        this.count = count;
    }

    public String getArtist(){
        return artist;
    }

    public int getCount(){
        return count;
    }
}
