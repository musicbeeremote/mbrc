package com.kelsos.mbrc.viewmodels;

import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.Connection;

@Singleton public class ConnectionStatusModel {
  @Connection.Status private int status;

  @Connection.Status public int getStatus() {
    return status;
  }

  public void setStatus(@Connection.Status int status) {
    this.status = status;
  }
}
