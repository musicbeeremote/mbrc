package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class Playlist {
    private String name;
    private String src;
    private int count;

    public Playlist(String name, String src, int count) {
        this.name = name;
        this.src = src;
        this.count = count;
    }

    public Playlist(JsonNode node) {
        this.name = node.path("name").getTextValue();
        this.count = node.path("count").getIntValue();
        this.src = node.path("src").getTextValue();
    }

    public int getCount() {
        return count;
    }

    public String getSrc() {
        return src;
    }

    public String getName() {
        return name;
    }
}
