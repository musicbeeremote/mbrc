package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Services.ProtocolHandler;

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
