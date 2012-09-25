package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.enums.Input;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.SocketService;

public class InitiateConnectionCommand implements ICommand
{
	@Inject
	SocketService socketService;

	@Override
	public void execute(IEvent e)
	{
		socketService.initSocketThread(Input.INIT);
	}
}
