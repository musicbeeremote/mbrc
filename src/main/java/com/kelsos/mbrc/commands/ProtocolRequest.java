package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.model.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.net.SocketService;

public class ProtocolRequest implements ICommand {
    private SocketService socket;

    @Inject public ProtocolRequest(SocketService socket) {
        this.socket = socket;
    }

    public void execute(IEvent e) {
        socket.sendData(new SocketMessage(Protocol.Protocol, Protocol.Request, 2));
    }
}
