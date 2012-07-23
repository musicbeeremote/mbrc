package kelsos.mbremote.Command;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Services.SocketService;

public class ProtocolReplyAvailableCommand implements ICommand
{
	@Inject
	SocketService socket;
	public void execute(IEvent e)
	{
		socket.sendData(e.getData());
	}
}
