package kelsos.mbremote.Command;

import com.google.inject.Inject;
import kelsos.mbremote.Controller.RunningActivityAccessor;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Views.MainView;

public class VisualUpdateScrobbleCommand implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;
	@Inject
	MainDataModel model;

	public void execute(IEvent e)
	{
		if(MainView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				MainView view = (MainView) accessor.getRunningActivity();
				view.updateScrobblerButtonState(model.getIsScrobbleButtonActive());
			}
		});
	}
}
