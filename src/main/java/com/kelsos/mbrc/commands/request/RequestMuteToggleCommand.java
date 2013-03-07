package com.kelsos.mbrc.commands.request;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.others.Const;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;

public class RequestMuteToggleCommand implements ICommand
{
    @Inject
    SocketService socket;
    @Override
    public void execute(IEvent e) {
        socket.sendData(new SocketMessage(Protocol.PlayerMute, Protocol.Request, e.getData()));
    }
}
