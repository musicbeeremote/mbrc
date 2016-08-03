package com.kelsos.mbrc.commands;

import javax.inject.Inject;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;

public class RestartConnectionCommand implements ICommand {
  private SocketService socket;

  @Inject public RestartConnectionCommand(SocketService socket) {
    this.socket = socket;
  }

  @Override public void execute(IEvent e) {
    socket.socketManager(SocketAction.RESET);
  }
}
