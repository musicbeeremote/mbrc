package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;

public class ReduceVolumeOnRingCommand implements ICommand
{
	@Inject
	MainDataModel model;
	@Inject
	ProtocolHandler pHandler;

	@Override
	public void execute(IEvent e)
	{
		int reducedVolume = (int)(model.getVolume() * 0.2);
		pHandler.requestAction(ProtocolHandler.PlayerAction.Volume, Integer.toString(reducedVolume));
	}
}
