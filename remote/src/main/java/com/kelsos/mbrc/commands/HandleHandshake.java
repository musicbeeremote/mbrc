package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;

public class HandleHandshake implements ICommand {
  private ProtocolHandler handler;
  private MainDataModel model;

  @Inject public HandleHandshake(ProtocolHandler handler, MainDataModel model) {
    this.handler = handler;
    this.model = model;
  }

  public void execute(IEvent e) {
    if (!(Boolean) e.getData()) {
      handler.resetHandshake();
      model.setHandShakeDone(false);
    }
  }
}
