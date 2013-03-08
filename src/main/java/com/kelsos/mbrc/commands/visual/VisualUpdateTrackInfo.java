package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class VisualUpdateTrackInfo implements ICommand
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
					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updateTitleText(model.getTitle());
					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updateAlbumText(model.getAlbum());
					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updateArtistText(model.getArtist());
					((MainFragment) afProvider.getActiveFragment(MainFragment.class)).updateYearText(model.getYear());
				}
			});
		}
	}
}
