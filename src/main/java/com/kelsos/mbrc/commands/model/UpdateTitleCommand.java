package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateTitleCommand implements ICommand
{
	@Inject private MainDataModel model;
	@Inject private NotificationService service;
	public void execute(IEvent e)
	{
		model.setTitle(e.getData());
		service.notificationBuilder(model.getTitle(),model.getArtist(),model.getAlbumCover());
	}
}
