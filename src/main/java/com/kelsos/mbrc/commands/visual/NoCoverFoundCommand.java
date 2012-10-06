package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.views.MainView;
import com.kelsos.mbrc.controller.RunningActivityAccessor;

public class NoCoverFoundCommand implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;

	@Override
	public void execute(IEvent e)
	{
		if(accessor.getRunningActivity()==null||MainView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				MainView view = (MainView) accessor.getRunningActivity();
				view.resetAlbumCover();
			}
		});
	}
}
