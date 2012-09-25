package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateLyricsCommand implements ICommand
{
	@Inject private MainDataModel _model;

	@Override
	public void execute(IEvent e)
	{
		_model.setLyrics(e.getData());
	}
}
