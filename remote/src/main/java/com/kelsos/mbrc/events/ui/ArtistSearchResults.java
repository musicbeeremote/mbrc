package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ArtistEntry;

import java.util.List;

public class ArtistSearchResults {
    private List<ArtistEntry> list;
    private boolean stored;

    public ArtistSearchResults(List<ArtistEntry> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public List<ArtistEntry> getList() {
        return this.list;
    }

    public boolean isStored() {
        return stored;
    }
}
