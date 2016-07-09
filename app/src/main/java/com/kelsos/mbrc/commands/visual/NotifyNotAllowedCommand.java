package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.ConnectionModel;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;

public class NotifyNotAllowedCommand implements ICommand {
  private SocketService socketService;
  private ConnectionModel model;
  private ProtocolHandler handler;
  private RxBus bus;

  @Inject
  public NotifyNotAllowedCommand(SocketService socketService, ConnectionModel model,
                                 ProtocolHandler handler, RxBus bus) {
    this.socketService = socketService;
    this.model = model;
    this.handler = handler;
    this.bus = bus;
  }

  @Override
  public void execute(IEvent e) {
    bus.post(new NotifyUser(R.string.notification_not_allowed));
    socketService.socketManager(SocketAction.STOP);
    model.setConnectionState("false");
    handler.resetHandshake();
  }
}
