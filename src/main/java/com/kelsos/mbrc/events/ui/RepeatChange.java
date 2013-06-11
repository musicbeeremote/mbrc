package com.kelsos.mbrc.events.ui;

public class RepeatChange {
    private boolean isActive;
    public RepeatChange(boolean isActive){
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return this.isActive;
    }
}
