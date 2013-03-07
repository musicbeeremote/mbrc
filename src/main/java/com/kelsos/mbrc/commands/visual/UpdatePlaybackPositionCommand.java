package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdatePlaybackPositionCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;

	public void execute(IEvent e)
	{
//		String duration[] = e.getData().split("##");
//		final int current = Integer.parseInt(duration[0]);
//		final int total = Integer.parseInt(duration[1]);
//		if (afProvider.getActiveFragment(MainFragment.class) != null)
//		{
//			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
//			cActivity.runOnUiThread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updateDurationDisplay(current, total);;
//				}
//			});
//		}
	}
}
