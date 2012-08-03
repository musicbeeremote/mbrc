package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Views.MainView;
import kelsos.mbremote.controller.RunningActivityAccessor;

public class NoCoverFoundCommand implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;

	@Override
	public void execute(IEvent e)
	{
		if(MainView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				MainView view = (MainView) accessor.getRunningActivity();
				view.resetAlbumCover();
			}
		});
	}
}
