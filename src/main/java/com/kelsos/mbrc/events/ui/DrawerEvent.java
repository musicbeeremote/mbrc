package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.DisplayFragment;

public class DrawerEvent {
    private boolean closeDrawer;
    private DisplayFragment navigate;

    public DrawerEvent () {
        closeDrawer = true;
    }

    public DrawerEvent (DisplayFragment navigate) {
        closeDrawer = false;
        this.navigate = navigate;
    }

    public boolean isCloseDrawer() {
        return closeDrawer;
    }

    public DisplayFragment getNavigate() {
        return navigate;
    }
}
