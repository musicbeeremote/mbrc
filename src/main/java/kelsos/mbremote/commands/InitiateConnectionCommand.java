package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.enums.Input;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Services.SocketService;

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
