package com.kelsos.mbrc.configuration;

import com.google.inject.Inject;
import com.kelsos.mbrc.commands.CancelNotificationCommand;
import com.kelsos.mbrc.commands.ConnectionStatusChangedCommand;
import com.kelsos.mbrc.commands.HandleHanshake;
import com.kelsos.mbrc.commands.SocketDataAvailableCommand;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.events.SocketEvent;
import com.kelsos.mbrc.events.UserInputEvent;

public class SocketServiceCommandRegistration
{
	@Inject
	public static void register(Controller controller)
	{
		controller.register(SocketEvent.SocketDataAvailable, SocketDataAvailableCommand.class);
		controller.register(SocketEvent.SocketStatusChanged, ConnectionStatusChangedCommand.class);
		controller.register(SocketEvent.SocketHandshakeUpdate, HandleHanshake.class);

		//temp
		controller.register(UserInputEvent.CancelNotification, CancelNotificationCommand.class);
	}

	@Inject
	public static void unregister(Controller controller)
	{

	}
}
