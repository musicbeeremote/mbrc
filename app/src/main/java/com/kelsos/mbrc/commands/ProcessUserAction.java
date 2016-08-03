package com.kelsos.mbrc.commands;

import javax.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;

public class ProcessUserAction implements ICommand {
  private SocketService socket;

  @Inject public ProcessUserAction(SocketService socket) {
    this.socket = socket;
  }

  @Override public void execute(IEvent e) {
    socket.sendData(SocketMessage.create(((UserAction) e.getData()).getContext(),
        ((UserAction) e.getData()).getData()));
  }
}
