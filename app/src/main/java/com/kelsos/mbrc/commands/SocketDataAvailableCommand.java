package com.kelsos.mbrc.commands;

import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;
import timber.log.Timber;

public class SocketDataAvailableCommand implements ICommand {
  private ProtocolHandler handler;

  @Inject public SocketDataAvailableCommand(ProtocolHandler handler) {
    this.handler = handler;
  }

  public void execute(final IEvent e) {
    try {
      handler.preProcessIncoming(e.getDataString());
    } catch (Exception ex) {
      Timber.d(ex, "message processing");
    }
  }
}
