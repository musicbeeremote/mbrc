package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.PlaylistView;

public class PlaylistViewTrackUpdatedCommand implements ICommand
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private MainDataModel model;

	@Override
	public void execute(IEvent e)
	{
		if(accessor.getRunningActivity()==null||PlaylistView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				PlaylistView view = (PlaylistView) accessor.getRunningActivity();
				view.updatePlayingTrack(model.getArtist(),model.getTitle());

			}
		});
	}
}
