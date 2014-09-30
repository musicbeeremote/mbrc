package com.kelsos.mbrc.events.ui;

public class CoverAvailable {
    private String coverUrl;

    public CoverAvailable(String url) {
        coverUrl = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
