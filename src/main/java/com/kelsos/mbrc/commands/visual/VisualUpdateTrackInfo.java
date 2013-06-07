package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;

public class VisualUpdateTrackInfo implements ICommand
{
	@Inject
    MainThreadBusWrapper bus;
	@Inject
	MainDataModel model;

	public void execute(IEvent e)
	{
        bus.post(new TrackInfoChange(model.getArtist(),model.getTitle(),model.getAlbum(),model.getYear()));
	}
}
