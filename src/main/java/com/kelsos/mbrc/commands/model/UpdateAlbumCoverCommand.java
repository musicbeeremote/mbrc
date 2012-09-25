package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateAlbumCoverCommand implements ICommand
{
	@Inject private MainDataModel model;
	public void execute(IEvent e)
	{
		model.setAlbumCover(e.getData());
	}
}
