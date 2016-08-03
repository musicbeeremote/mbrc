package com.kelsos.mbrc.model;

import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.annotations.Connection.Status;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.RequestConnectionStateEvent;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConnectionModel {
  private boolean connectionActive;
  private boolean isHandShakeDone;
  private final RxBus bus;

  @Inject
  public ConnectionModel(RxBus bus) {
    this.bus = bus;
    connectionActive = false;
    isHandShakeDone = false;
    this.bus.register(this, RequestConnectionStateEvent.class, this::onConnectionStateRequest);
  }

  @Status
  public int getConnection() {
    if (connectionActive && isHandShakeDone) {
      return Connection.ACTIVE;
    } else if (connectionActive) {
      return Connection.ON;
    }

    return Connection.OFF;
  }

  public void setConnectionState(String connectionActive) {
    this.connectionActive = Boolean.parseBoolean(connectionActive);
    notifyState();
  }

  private void notifyState() {
    bus.post(ConnectionStatusChangeEvent.create(getConnection()));
  }

  public void setHandShakeDone(boolean handShakeDone) {
    this.isHandShakeDone = handShakeDone;
    notifyState();
  }

  public boolean isConnectionActive() {
    return connectionActive;
  }

  private void onConnectionStateRequest(RequestConnectionStateEvent event) {
    notifyState();
  }
}
