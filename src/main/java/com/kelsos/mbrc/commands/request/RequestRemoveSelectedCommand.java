package com.kelsos.mbrc.commands.request;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class RequestRemoveSelectedCommand implements ICommand
{
	@Inject
	ProtocolHandler protocolHandler;

	@Override
	public void execute(IEvent e)
	{
		protocolHandler.requestAction(ProtocolHandler.PlayerAction.NowPlayingRemoveSelected,e.getData());
	}
}
