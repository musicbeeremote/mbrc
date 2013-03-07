package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;
import com.squareup.otto.Bus;

public class ConnectionStatusChangedCommand implements ICommand
{
	@Inject
	MainDataModel model;
    @Inject
    SocketService service;
	@Inject
	NotificationService notificationService;

	public void execute(IEvent e)
	{
		model.setConnectionState(e.getDataString());
		if(model.getIsConnectionActive()){
            service.sendData(new SocketMessage(Protocol.Player, Protocol.Request, "Android"));
		} else {
			notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
		}
	}
}
