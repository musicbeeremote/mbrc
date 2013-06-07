package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;

public class VisualUpdateCoverCommand implements ICommand
{
	@Inject MainDataModel model;
    @Inject NotificationService service;
    @Inject MainThreadBusWrapper bus;

	public void execute(IEvent e)
	{
        service.notificationBuilder(model.getTitle(),model.getArtist(),model.getAlbumCover(),model.getPlayState());
        bus.post(new CoverAvailable(model.getAlbumCover()));
	}
}
