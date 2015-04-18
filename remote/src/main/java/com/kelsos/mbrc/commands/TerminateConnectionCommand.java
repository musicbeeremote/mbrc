package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;

public class TerminateConnectionCommand implements ICommand {
  private SocketService service;

  @Inject public TerminateConnectionCommand(SocketService service) {
    this.service = service;
  }

  @Override public void execute(IEvent e) {
    service.socketManager(SocketAction.TERMINATE);
  }
}
