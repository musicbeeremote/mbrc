package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.CancelNotificationCommand;
import com.kelsos.mbrc.commands.ConnectionStatusChangedCommand;
import com.kelsos.mbrc.commands.ResetHandshakeStatusCommand;
import com.kelsos.mbrc.commands.SocketDataAvailableCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.enums.SocketServiceEventType;
import com.kelsos.mbrc.enums.UserInputEventType;

public class SocketServiceCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.registerCommand(SocketServiceEventType.SOCKET_EVENT_PACKET_AVAILABLE, SocketDataAvailableCommand.class);
		controller.registerCommand(SocketServiceEventType.SOCKET_EVENT_STATUS_CHANGE, ConnectionStatusChangedCommand.class);
		controller.registerCommand(SocketServiceEventType.SOCKET_EVENT_HANDSHAKE_UPDATE, ResetHandshakeStatusCommand.class);

		//temp
		controller.registerCommand(UserInputEventType.USERINPUT_EVENT_CANCEL_NOTIFICATION, CancelNotificationCommand.class);
	}

	@Inject
	public static void unregister(Controller controller)
	{

	}
}
