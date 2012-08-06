package kelsos.mbremote.commands;

import com.google.inject.Inject;
import kelsos.mbremote.Events.ProtocolDataEvent;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Views.PlaylistView;
import kelsos.mbremote.controller.RunningActivityAccessor;

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
