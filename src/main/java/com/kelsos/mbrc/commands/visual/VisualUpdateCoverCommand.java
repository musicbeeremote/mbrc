package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.MainFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;

public class VisualUpdateCoverCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	MainDataModel model;
    @Inject
    NotificationService service;

	public void execute(IEvent e)
	{
        service.notificationBuilder(model.getTitle(),model.getArtist(),model.getAlbumCover(),model.getPlayState());
		if(afProvider.getActiveFragment(MainFragment.class)!=null)
		{
			Activity cActivity = afProvider.getActiveFragment(MainFragment.class).getActivity();
			cActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					((MainFragment)afProvider.getActiveFragment(MainFragment.class)).updateAlbumCover(model.getAlbumCover());
				}
			});
		}
	}
}
