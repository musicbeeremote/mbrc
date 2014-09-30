package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.util.MainThreadBusWrapper;

public class NotifyNotAllowedCommand implements ICommand {
    private SocketService socketService;
    private MainThreadBusWrapper bus;

    @Inject public NotifyNotAllowedCommand(SocketService socketService, MainThreadBusWrapper bus) {
        this.socketService = socketService;
        this.bus = bus;
    }

    @Override public void execute(IEvent e) {
        bus.post(new NotifyUser(R.string.notification_not_allowed));
        socketService.socketManager(SocketAction.STOP);
    }
}
