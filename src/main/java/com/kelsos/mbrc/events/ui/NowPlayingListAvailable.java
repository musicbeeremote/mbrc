package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.MusicTrack;

import java.util.ArrayList;

public class NowPlayingListAvailable {
    private ArrayList<MusicTrack> list;
    private int index;

    public NowPlayingListAvailable(ArrayList<MusicTrack> list, int index) {
        this.list = list;
    }

    public ArrayList<MusicTrack> getList() {
        return this.list;
    }

    public int getIndex() {
        return this.index;
    }
}
