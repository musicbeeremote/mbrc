package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class VisualUpdateArtistCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvide;
	@Inject
	MainDataModel model;

	public void execute(IEvent e)
	{
		if(afProvide.getActiveFragment(MainFragment.class)!=null)
		{
			Activity cActivity = afProvide.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					((MainFragment)afProvide.getActiveFragment(MainFragment.class)).updateArtistText(model.getArtist());
				}
			});
		}
	}
}
