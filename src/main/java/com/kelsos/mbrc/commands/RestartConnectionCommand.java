package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;

public class RestartConnectionCommand implements ICommand
{
	@Inject
	SocketService socket;
	@Override
	public void execute(IEvent e)
	{
		socket.SocketManager(SocketAction.RETRY_COUNTER_RESET);
		socket.SocketManager(SocketAction.SOCKET_RESET);
	}
}
