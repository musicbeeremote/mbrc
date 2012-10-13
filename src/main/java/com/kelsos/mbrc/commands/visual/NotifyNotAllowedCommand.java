package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;

public class NotifyNotAllowedCommand implements ICommand
{
	private SocketService socketService;

	private MainDataModel model;

	private ProtocolHandler handler;

	private NotificationService notificationService;

	@Inject
	public NotifyNotAllowedCommand(SocketService socketService, MainDataModel model, ProtocolHandler handler, NotificationService notificationService)
	{
		this.socketService = socketService;
		this.model = model;
		this.handler = handler;
		this.notificationService = notificationService;
	}

	@Override
	public void execute(IEvent e)
	{
		notificationService.showToastMessage(R.string.notification_not_allowed);
		socketService.SocketManager(SocketAction.STOP);
		model.setConnectionState("false");
		handler.setHandshakeComplete(false);
	}
}
