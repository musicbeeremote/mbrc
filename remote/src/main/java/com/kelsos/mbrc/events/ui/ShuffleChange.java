package com.kelsos.mbrc.events.ui;

public class ShuffleChange {
    private boolean isActive;

    public ShuffleChange(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return this.isActive;
    }
}
