package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class ResetHandshakeStatusCommand implements ICommand
{
	@Inject
	ProtocolHandler handler;

	public void execute(IEvent e)
	{
		handler.setHandshakeComplete(Boolean.parseBoolean(e.getData()));
	}
}
