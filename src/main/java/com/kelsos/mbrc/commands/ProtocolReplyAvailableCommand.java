package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;

public class ProtocolReplyAvailableCommand implements ICommand
{
	@Inject
	SocketService socket;
	public void execute(IEvent e)
	{
		socket.sendData(e.getData());
	}
}
