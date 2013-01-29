package com.kelsos.mbrc.commands.request;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;


public class RequestRatingChangeCommand implements ICommand {
    @Inject
    ProtocolHandler handler;
    @Override
    public void execute(IEvent e) {
        handler.requestAction(ProtocolHandler.PlayerAction.Rating,e.getData());
    }
}
