package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.AlbumEntry;

import java.util.List;

public class AlbumSearchResults {
    private List<AlbumEntry> list;
    private boolean stored;

    public AlbumSearchResults(List<AlbumEntry> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public List<AlbumEntry> getList() {
        return this.list;
    }

    public boolean isStored() {
        return stored;
    }
}
