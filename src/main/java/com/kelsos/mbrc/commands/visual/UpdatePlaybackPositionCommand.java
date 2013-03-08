package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

import java.util.LinkedHashMap;

public class UpdatePlaybackPositionCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;

	public void execute(IEvent e)
	{

        LinkedHashMap<String, Integer> map = (LinkedHashMap<String, Integer>) e.getData();

		final int current = map.get("current");
		final int total = map.get("total");
		if (afProvider.getActiveFragment(MainFragment.class) != null)
		{
			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updateDurationDisplay(current, total);;
				}
			});
		}
	}
}
