package com.kelsos.mbrc.events.ui;

public class UpdatePosition {
    private int current;
    private int total;

    public UpdatePosition(int current, int total) {
        this.current = current;
        this.total = total;
    }

    public int getCurrent() {
        return this.current;
    }

    public int getTotal() {
        return this.total;
    }
}
