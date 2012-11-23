package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.squareup.otto.Bus;

public class ConnectionStatusChangedCommand implements ICommand
{
	@Inject
	MainDataModel model;
	@Inject
	ProtocolHandler handler;
	@Inject
	NotificationService notificationService;
	@Inject
	Bus bus;

	public void execute(IEvent e)
	{
		model.setConnectionState(e.getData());
		if(model.getIsConnectionActive()){
			handler.requestPlayerData();
		} else {
			notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
		}
	}
}
