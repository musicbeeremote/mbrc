package com.kelsos.mbrc.commands.visual;

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
		if (accessor.getRunningActivity()==null||MainView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable()
		{
			public void run()
			{
				try
				{
					MainView view = (MainView) accessor.getRunningActivity();
					view.updateDurationDisplay(current, total);
				} catch (Exception ignore)
				{

				}
			}
		});
	}
}
