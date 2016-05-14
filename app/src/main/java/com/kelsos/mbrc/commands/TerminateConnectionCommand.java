package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.SocketService;

public class TerminateConnectionCommand implements ICommand {
  private SocketService service;
  private MainDataModel model;

  @Inject public TerminateConnectionCommand(SocketService service, MainDataModel model) {
    this.service = service;
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setHandShakeDone(false);
    model.setConnectionState("false");
    service.socketManager(SocketAction.TERMINATE);
  }
}
