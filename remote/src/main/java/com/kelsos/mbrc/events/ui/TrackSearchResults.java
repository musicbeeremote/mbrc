package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.TrackEntry;

import java.util.List;

public class TrackSearchResults {
    private List<TrackEntry> list;
    private boolean stored;

    public TrackSearchResults(List<TrackEntry> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public List<TrackEntry> getList() {
        return this.list;
    }

    public boolean isStored() {
        return stored;
    }
}
