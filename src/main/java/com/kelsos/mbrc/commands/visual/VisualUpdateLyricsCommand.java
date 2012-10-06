package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.LyricsView;
import com.kelsos.mbrc.controller.RunningActivityAccessor;

public class VisualUpdateLyricsCommand implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;
	@Inject
	MainDataModel model;

	@Override
	public void execute(IEvent e)
	{
		if(accessor.getRunningActivity()==null||LyricsView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				LyricsView view = (LyricsView) accessor.getRunningActivity();
				view.updateLyricsData(model.getLyrics(), model.getArtist(), model.getTitle());
			}
		});
	}
}
