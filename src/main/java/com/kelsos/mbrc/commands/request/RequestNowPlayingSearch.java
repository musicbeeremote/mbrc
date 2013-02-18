package com.kelsos.mbrc.commands.request;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class RequestNowPlayingSearch implements ICommand {

    @Inject private ProtocolHandler pHandler;
    @Override
    public void execute(IEvent e) {
        pHandler.requestAction(ProtocolHandler.PlayerAction.NowPlayingSearch, e.getData());
    }
}
