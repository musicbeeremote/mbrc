package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.DisplaySelection;

public class DrawerSelection {
  private boolean closeDrawer;
  private DisplaySelection navigate;

  public DrawerSelection() {
    closeDrawer = true;
  }

  public DrawerSelection(DisplaySelection navigate) {
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
