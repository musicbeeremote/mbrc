package com.kelsos.mbrc.commands.request;

import com.google.inject.Inject;
import com.kelsos.mbrc.others.XmlEncoder;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.services.ProtocolHandler;

public class PlaySpecifiedTrackCommand implements ICommand
{
	@Inject
	private ProtocolHandler pHandler;

	@Override
	public void execute(final IEvent e)
	{
		pHandler.requestAction(ProtocolHandler.PlayerAction.PlayNow, XmlEncoder.encode(e.getData()));
	}
}
