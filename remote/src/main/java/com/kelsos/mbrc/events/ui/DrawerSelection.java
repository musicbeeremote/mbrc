package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.DisplayFragment;

public class DrawerSelection {
  private boolean closeDrawer;
  private DisplayFragment navigate;

  public DrawerSelection() {
    closeDrawer = true;
  }

  public DrawerSelection(DisplayFragment navigate) {
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
