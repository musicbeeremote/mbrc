package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.MainView;

public class VisualUpdateRepeatCommand implements ICommand
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
				view.updateRepeatButtonState(model.getIsRepeatButtonActive());
			}
		});
	}
}
