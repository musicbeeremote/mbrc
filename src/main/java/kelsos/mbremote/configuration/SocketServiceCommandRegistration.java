package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.Command.ConnectionStatusChangedCommand;
import kelsos.mbremote.Command.HandshakeCompleteCommand;
import kelsos.mbremote.Command.SocketDataAvailableCommand;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Enumerations.SocketServiceEventType;

public class SocketServiceCommandRegistration
{
	@Inject
	public void register(Controller controller)
	{
		controller.registerCommand(SocketServiceEventType.PacketAvailable, SocketDataAvailableCommand.class);
		controller.registerCommand(SocketServiceEventType.StatusChange, ConnectionStatusChangedCommand.class);
		controller.registerCommand(SocketServiceEventType.HandshakeUpdate, HandshakeCompleteCommand.class);
	}

	@Inject
	public void unregister(Controller controller)
	{

	}
}
