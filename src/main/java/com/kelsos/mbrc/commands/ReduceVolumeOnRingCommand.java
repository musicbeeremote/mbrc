package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;

public class ReduceVolumeOnRingCommand implements ICommand
{
	@Inject MainDataModel model;
	@Inject SocketService service;

	@Override
	public void execute(IEvent e)
	{
        service.sendData(new SocketMessage(Protocol.PlayerVolume, Protocol.Request,(int)(model.getVolume() * 0.2)));
	}
}
