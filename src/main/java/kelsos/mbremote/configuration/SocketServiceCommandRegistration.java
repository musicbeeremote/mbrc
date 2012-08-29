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
		controller.registerCommand(SocketServiceEventType.SOCKET_EVENT_PACKET_AVAILABLE, SocketDataAvailableCommand.class);
		controller.registerCommand(SocketServiceEventType.SOCKET_EVENT_STATUS_CHANGE, ConnectionStatusChangedCommand.class);
		controller.registerCommand(SocketServiceEventType.SOCKET_EVENT_HANDSHAKE_UPDATE, HandshakeCompleteCommand.class);
	}

	@Inject
	public static void unregister(Controller controller)
	{

	}
}
