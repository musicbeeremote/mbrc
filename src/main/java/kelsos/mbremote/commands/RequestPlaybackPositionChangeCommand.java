package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Services.ProtocolHandler;

public class RequestPlaybackPositionChangeCommand implements ICommand
{
	@Inject private ProtocolHandler protocolHandler;
	public void execute(IEvent e)
	{
		protocolHandler.requestAction(ProtocolHandler.PlayerAction.PlaybackPosition, e.getData());
	}
}
