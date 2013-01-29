package com.kelsos.mbrc.commands.request;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class RequestLfmBan implements ICommand {
    @Inject
    private ProtocolHandler protocolHandler;
    @Override
    public void execute(IEvent e) {
        protocolHandler.requestAction(ProtocolHandler.PlayerAction.LfmBan);
    }
}
