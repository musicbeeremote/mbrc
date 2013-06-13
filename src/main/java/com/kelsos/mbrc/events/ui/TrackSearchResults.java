package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.TrackEntry;

import java.util.ArrayList;

public class TrackSearchResults {
    private ArrayList<TrackEntry> list;

    public TrackSearchResults(ArrayList<TrackEntry> list) {
        this.list = list;
    }

    public ArrayList<TrackEntry> getList() {
        return this.list;
    }
}
