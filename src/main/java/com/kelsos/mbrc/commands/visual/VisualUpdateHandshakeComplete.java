package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.views.MainView;

public class VisualUpdateHandshakeComplete implements ICommand
{
	@Inject
	RunningActivityAccessor accessor;

	public void execute(IEvent e)
	{
		if(!Boolean.parseBoolean(e.getData())) return;
		if(MainView.class != accessor.getRunningActivity().getClass()) return;
		accessor.getRunningActivity().runOnUiThread(new Runnable() {
			public void run() {
				MainView view = (MainView) accessor.getRunningActivity();
				view.updateConnectivityStatus(ConnectionStatus.CONNECTION_ACTIVE);
			}
		});
	}
}


