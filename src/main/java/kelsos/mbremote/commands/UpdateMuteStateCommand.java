package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;

public class UpdateMuteStateCommand implements ICommand
{
	@Inject private MainDataModel model;
	public void execute(IEvent e)
	{
		model.setMuteState(e.getData());
	}
}
