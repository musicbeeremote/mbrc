package com.kelsos.mbrc.events.actions;

public class ChangeVolumeEvent {
    private int volume;

    public ChangeVolumeEvent(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }
}
