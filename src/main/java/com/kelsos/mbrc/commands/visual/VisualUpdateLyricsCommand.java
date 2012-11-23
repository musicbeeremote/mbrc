package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.LyricsFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class VisualUpdateLyricsCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	MainDataModel model;

	@Override
	public void execute(IEvent e)
	{
		if (afProvider.getActiveFragment(LyricsFragment.class) != null)
		{
			Activity cActivity = afProvider.getActiveFragment(LyricsFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					LyricsFragment lFragment = ((LyricsFragment) afProvider.getActiveFragment(LyricsFragment.class));
					lFragment.updateLyricsData(model.getLyrics(), model.getArtist(), model.getTitle());
				}
			});
		}
	}
}
