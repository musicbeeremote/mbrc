package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.controller.RunningActivityAccessor;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Views.MainView;

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
