package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class ArtistEntry {
    private String artist;
    private int count;

    public ArtistEntry(JsonNode node) {
        this.artist = node.path("artist").getTextValue();
        this.count = node.path("count").getIntValue();
    }

    public ArtistEntry(String artist, int count) {
        this.artist = artist;
        this.count = count;
    }

    public String getArtist() {
        return artist;
    }

    public int getCount() {
        return count;
    }
}
