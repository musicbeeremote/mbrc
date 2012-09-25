package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;

public class ConnectionStatusChangedCommand implements ICommand
{
	@Inject
	MainDataModel model;
	@Inject
	ProtocolHandler handler;

	public void execute(IEvent e)
	{
		model.setConnectionState(e.getData());
		if(model.getIsConnectionActive()) handler.requestPlayerData();
	}
}
