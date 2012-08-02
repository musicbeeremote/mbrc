package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Services.ProtocolHandler;

public class SocketDataAvailableCommand implements ICommand
{
	@Inject
	ProtocolHandler handler;

	public void execute(IEvent e)
	{
		handler.answerProcessor(e.getData());
	}
}
