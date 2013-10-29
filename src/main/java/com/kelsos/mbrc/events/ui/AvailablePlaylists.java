package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.model.Playlist;

import java.util.ArrayList;

public class AvailablePlaylists {
    private ArrayList<Playlist> list;
    private boolean stored;

    public AvailablePlaylists(ArrayList<Playlist> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public boolean isStored() {
        return stored;
    }

    public ArrayList<Playlist> getList() {
        return list;
    }
}
