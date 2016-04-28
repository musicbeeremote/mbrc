package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.annotations.Connection.Status;

public class ConnectionStatusChangeEvent {
  @Status private long status;

  private ConnectionStatusChangeEvent(@Status long status) {
    this.status = status;
  }

  @NonNull public static ConnectionStatusChangeEvent create(@Status long status) {
    return new ConnectionStatusChangeEvent(status);
  }

  @Status public long getStatus() {
    return this.status;
  }
}
