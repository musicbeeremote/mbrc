package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.node.ObjectNode;

import java.util.LinkedHashMap;

public class UpdatePlaybackPositionCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;

	public void execute(IEvent e)
	{

        ObjectNode oNode = (ObjectNode)e.getData();

		final int current = oNode.path("current").asInt();
		final int total = oNode.path("total").asInt();
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
