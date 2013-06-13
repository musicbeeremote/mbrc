package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.AlbumEntry;

import java.util.ArrayList;

public class AlbumSearchResults {
    private ArrayList<AlbumEntry> list;

    public AlbumSearchResults(ArrayList<AlbumEntry> list) {
        this.list = list;
    }

    public ArrayList<AlbumEntry> getList() {
        return this.list;
    }
}
