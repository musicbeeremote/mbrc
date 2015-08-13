package com.kelsos.mbrc.events.ui;

public class SearchScrollChanged {
  private final boolean scrollingUpwards;

  public SearchScrollChanged(boolean scrollingUpwards) {
    this.scrollingUpwards = scrollingUpwards;
  }

  public boolean isScrollingUpwards() {
    return scrollingUpwards;
  }
}
