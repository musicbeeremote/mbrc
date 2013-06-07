package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.services.SocketService;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;

public class VisualUpdateHandshakeComplete implements ICommand
{
	@Inject MainThreadBusWrapper bus;
    @Inject SocketService service;

	public void execute(IEvent e)
	{
		if(!(Boolean)e.getData()) return;

        service.sendData(new SocketMessage(Protocol.PlayerStatus,Protocol.Request, ""));
        service.sendData(new SocketMessage(Protocol.NowPlayingTrack, Protocol.Request, ""));
        service.sendData(new SocketMessage(Protocol.NowPlayingCover, Protocol.Request, ""));

		bus.post(new ConnectionStatusChange(ConnectionStatus.CONNECTION_ACTIVE));
	}
}


