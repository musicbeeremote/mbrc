package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.views.MainView;

public class UpdatePlaybackPositionCommand implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;

	public void execute(IEvent e)
	{
		String duration[] = e.getData().split("##");
		final int current = Integer.parseInt(duration[0]);
		final int total = Integer.parseInt(duration[1]);
		if (MainView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable()
		{
			public void run()
			{
				MainView view = (MainView) accessor.getRunningActivity();
				view.updateDurationDisplay(current, total);
			}
		});
	}
}
