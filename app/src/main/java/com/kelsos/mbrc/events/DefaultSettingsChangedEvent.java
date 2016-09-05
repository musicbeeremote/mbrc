package com.kelsos.mbrc.events;

import android.support.annotation.NonNull;

public class DefaultSettingsChangedEvent {
  private DefaultSettingsChangedEvent() {
    //no instance
  }

  @NonNull
  public static DefaultSettingsChangedEvent create() {
    return new DefaultSettingsChangedEvent();
  }
}
