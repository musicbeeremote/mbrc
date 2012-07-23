package kelsos.mbremote.Command;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Services.ProtocolHandler;

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
