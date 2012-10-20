package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.views.PlaylistView;

public class VisualUpdateTrackRemoved implements ICommand
{
	@Inject
	private RunningActivityAccessor accessor;

	@Override
	public void execute(final IEvent e)
	{
		if(accessor.getRunningActivity()==null||PlaylistView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				PlaylistView view = (PlaylistView) accessor.getRunningActivity();
				final int index = Integer.parseInt(e.getData());
				view.removeSelectedTrack(index);
			}
		});
	}
}
