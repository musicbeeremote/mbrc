package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.annotations.Connection.Status;

public class ConnectionStatusChangeEvent {
  @Status private int status;

  private ConnectionStatusChangeEvent(@Status int status) {
    this.status = status;
  }

  @NonNull public static ConnectionStatusChangeEvent create(@Status int status) {
    return new ConnectionStatusChangeEvent(status);
  }

  @Status public int getStatus() {
    return this.status;
  }
}
