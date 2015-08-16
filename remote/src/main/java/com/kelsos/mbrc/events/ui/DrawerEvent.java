package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.DisplaySelection;

public class DrawerEvent {
  private boolean closeDrawer;
  private DisplaySelection navigate;

  public DrawerEvent() {
    closeDrawer = true;
  }

  public DrawerEvent(DisplaySelection navigate) {
    closeDrawer = false;
    this.navigate = navigate;
  }

  public boolean isCloseDrawer() {
    return closeDrawer;
  }

  public DisplaySelection getNavigate() {
    return navigate;
  }
}
