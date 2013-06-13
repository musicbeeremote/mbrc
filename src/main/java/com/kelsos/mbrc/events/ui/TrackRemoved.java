package com.kelsos.mbrc.events.ui;

public class TrackRemoved {
    private int index;

    public TrackRemoved(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
