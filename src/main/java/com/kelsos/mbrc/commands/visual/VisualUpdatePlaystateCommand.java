package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class VisualUpdatePlaystateCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	MainDataModel model;

	public void execute(IEvent e)
	{
		if (afProvider.getActiveFragment(MainFragment.class) != null)
		{
			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updatePlayState(model.getPlayState());
				}
			});
		}
	}
}
