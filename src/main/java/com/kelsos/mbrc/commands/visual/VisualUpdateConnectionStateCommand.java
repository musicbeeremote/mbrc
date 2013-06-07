package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;

public class VisualUpdateConnectionStateCommand implements ICommand
{
	@Inject MainDataModel model;
    @Inject MainThreadBusWrapper bus;

	public void execute(IEvent e)
	{
        bus.post(new ConnectionStatusChange(model.getIsConnectionActive() ?
                ConnectionStatus.CONNECTION_ON :
                ConnectionStatus.CONNECTION_OFF));
	}
}
