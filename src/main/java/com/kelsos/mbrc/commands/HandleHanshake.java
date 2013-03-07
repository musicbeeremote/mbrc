package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;

public class HandleHanshake implements ICommand
{
	@Inject
	ProtocolHandler handler;
    @Inject
    SocketService service;

	public void execute(IEvent e)
	{
        if(!(Boolean)e.getData()) {
            handler.resetHandshake();
        }

	}
}
