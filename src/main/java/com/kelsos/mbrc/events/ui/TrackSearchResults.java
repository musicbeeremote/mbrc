package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.model.TrackEntry;

import java.util.ArrayList;

public class TrackSearchResults {
    private ArrayList<TrackEntry> list;
    private boolean stored;

    public TrackSearchResults(ArrayList<TrackEntry> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public ArrayList<TrackEntry> getList() {
        return this.list;
    }

    public boolean isStored() {
        return stored;
    }
}
