package kelsos.mbremote.Command;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Views.MainView;

public class UpdatePlaybackPositionCommand implements ICommand
{
	@Inject
	private MainView mainView;

	public void execute(IEvent e)
	{
		String duration[] = e.getData().split("##");
		final int current = Integer.parseInt(duration[0]);
		final int total = Integer.parseInt(duration[1]);
		mainView.runOnUiThread(new Runnable()
		{
			public void run()
			{
				mainView.updateDurationDisplay(current, total);
			}
		});
	}
}
