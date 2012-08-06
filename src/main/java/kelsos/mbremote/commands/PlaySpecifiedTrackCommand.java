package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Services.ProtocolHandler;

public class PlaySpecifiedTrackCommand implements ICommand
{
	@Inject
	private ProtocolHandler pHandler;

	@Override
	public void execute(final IEvent e)
	{
		pHandler.requestAction(ProtocolHandler.PlayerAction.PlayNow, e.getData());
	}
}
