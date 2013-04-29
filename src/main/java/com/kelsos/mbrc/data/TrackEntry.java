package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class TrackEntry {
    private String artist;
    private String title;

    public TrackEntry(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public TrackEntry(JsonNode jNode) {
        this.artist = jNode.path("artist").getTextValue();
        this.title = jNode.path("title").getTextValue();
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
