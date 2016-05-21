package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;
import com.kelsos.mbrc.utilities.SocketActivityChecker;

public class ProtocolPingHandle implements ICommand {
  private final SocketService service;
  @Inject private SocketActivityChecker activityChecker;

  @Inject public ProtocolPingHandle(SocketService service) {
    this.service = service;
  }

  @Override public void execute(IEvent e) {
    activityChecker.ping();
    service.sendData(SocketMessage.create(Protocol.PONG, ""));
  }
}
