package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ArtistEntry;

import java.util.ArrayList;

public class ArtistSearchResults {
    private ArrayList<ArtistEntry> list;

    public ArtistSearchResults (ArrayList<ArtistEntry> list) {
        this.list = list;
    }

    public ArrayList<ArtistEntry> getList() {
        return this.list;
    }
}
