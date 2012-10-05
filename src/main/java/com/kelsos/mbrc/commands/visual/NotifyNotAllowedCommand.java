package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.SocketService;

public class NotifyNotAllowedCommand implements ICommand
{
	@Inject
	NotificationService notificationService;
	@Inject
	SocketService socketService;
	@Inject
	MainDataModel model;

	@Override
	public void execute(IEvent e)
	{
		notificationService.showToastMessage(R.string.notification_not_allowed);
		socketService.SocketManager(SocketAction.SOCKET_STOP);
		model.setConnectionState("false");
	}
}
