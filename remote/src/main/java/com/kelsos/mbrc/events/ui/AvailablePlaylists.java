package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.dbdata.Playlist;

import java.util.List;

public class AvailablePlaylists {
    private List<Playlist> list;
    private boolean stored;

    public AvailablePlaylists(List<Playlist> list, boolean stored) {
        this.list = list;
        this.stored = stored;
    }

    public boolean isStored() {
        return stored;
    }

    public List<Playlist> getList() {
        return list;
    }
}
