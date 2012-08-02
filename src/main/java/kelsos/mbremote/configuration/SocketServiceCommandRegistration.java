package kelsos.mbremote.configuration;

import com.google.inject.Inject;
import kelsos.mbremote.commands.ConnectionStatusChangedCommand;
import kelsos.mbremote.commands.HandshakeCompleteCommand;
import kelsos.mbremote.commands.SocketDataAvailableCommand;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.enums.SocketServiceEventType;

public class SocketServiceCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.registerCommand(SocketServiceEventType.PacketAvailable, SocketDataAvailableCommand.class);
		controller.registerCommand(SocketServiceEventType.StatusChange, ConnectionStatusChangedCommand.class);
		controller.registerCommand(SocketServiceEventType.HandshakeUpdate, HandshakeCompleteCommand.class);
	}

	@Inject
	public static void unregister(Controller controller)
	{

	}
}
