package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;

import java.util.List;

public class NowPlayingListAvailable {
    private List<NowPlayingTrack> list;
    private int index;

    public NowPlayingListAvailable(List<NowPlayingTrack> list, int index) {
        this.list = list;
        this.index = index;
    }

    public List<NowPlayingTrack> getList() {
        return this.list;
    }

    public int getIndex() {
        return this.index;
    }
}
