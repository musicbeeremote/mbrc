package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.MusicTrack;

import java.util.List;

public class NowPlayingListAvailable {
    private List<MusicTrack> list;
    private int index;

    public NowPlayingListAvailable(List<MusicTrack> list, int index) {
        this.list = list;
        this.index = index;
    }

    public List<MusicTrack> getList() {
        return this.list;
    }

    public int getIndex() {
        return this.index;
    }
}
