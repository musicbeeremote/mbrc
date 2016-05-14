package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.DisplayFragment;

public class DrawerEvent {
  private boolean external;
  private boolean closeDrawer;
  private DisplayFragment navigate;

  public DrawerEvent() {
    closeDrawer = true;
  }

  public DrawerEvent(DisplayFragment navigate) {
    closeDrawer = false;
    this.navigate = navigate;
  }

  public DrawerEvent(DisplayFragment navigate, boolean external) {
    this(navigate);
    this.external = external;
  }

  public boolean isCloseDrawer() {
    return closeDrawer;
  }

  public DisplayFragment getNavigate() {
    return navigate;
  }

  public boolean isExternal() {
    return external;
  }
}
