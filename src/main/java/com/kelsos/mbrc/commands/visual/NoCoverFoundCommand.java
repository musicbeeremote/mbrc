package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;

public class NoCoverFoundCommand implements ICommand
{
    @Inject MainThreadBusWrapper bus;

	@Override
	public void execute(IEvent e)
	{
        bus.post(new CoverAvailable());
	}
}
