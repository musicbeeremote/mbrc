package com.kelsos.mbrc.viewmodels;

import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.Connection;

@Singleton public class ConnectionStatusModel {
  @Connection.Status private long status;

  @Connection.Status public long getStatus() {
    return status;
  }

  public void setStatus(@Connection.Status long status) {
    this.status = status;
  }
}
