package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ArtistEntry;

import java.util.ArrayList;

public class ArtistSearchResults {
    private ArrayList<ArtistEntry> list;
    private boolean stored;

    public ArtistSearchResults(ArrayList<ArtistEntry> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public ArrayList<ArtistEntry> getList() {
        return this.list;
    }

    public boolean isStored() {
        return stored;
    }
}
