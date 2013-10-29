package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.model.SocketMessage;
import com.kelsos.mbrc.model.UserAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.net.SocketService;

public class ProcessUserAction implements ICommand {
    private SocketService socket;

    @Inject public ProcessUserAction(SocketService socket) {
        this.socket = socket;
    }

    @Override public void execute(IEvent e) {
        socket.sendData(new SocketMessage(((UserAction) e.getData()).getContext(), Protocol.Request,
                ((UserAction) e.getData()).getData()));
    }
}
