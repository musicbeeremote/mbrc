package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.views.PlaylistView;
import com.kelsos.mbrc.controller.RunningActivityAccessor;

public class PlayListDataAvailableCommand implements ICommand
{
	@Inject
	private RunningActivityAccessor accessor;
	@Override
	public void execute(final IEvent e)
	{
		if(accessor.getRunningActivity().getClass()!= PlaylistView.class) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				PlaylistView view = (PlaylistView) accessor.getRunningActivity();
				view.updateListData(((ProtocolDataEvent)e).getTrackList());
			}
		});
	}
}
