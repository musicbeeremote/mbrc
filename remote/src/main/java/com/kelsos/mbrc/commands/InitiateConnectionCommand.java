package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.SocketService;

public class InitiateConnectionCommand implements ICommand {
    private SocketService socketService;

    @Inject public InitiateConnectionCommand(SocketService socketService) {
        this.socketService = socketService;
    }

    @Override public void execute(IEvent e) {
        socketService.SocketManager(SocketAction.START);
    }
}
