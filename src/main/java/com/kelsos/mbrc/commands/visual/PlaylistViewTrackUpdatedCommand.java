package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.NowPlayingFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class PlaylistViewTrackUpdatedCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	private MainDataModel model;

	@Override
    public void execute(IEvent e)
    {
        if(afProvider.getActiveFragment(NowPlayingFragment.class)!=null)
        {
            Activity cActivity = afProvider.getActiveFragment(NowPlayingFragment.class).getActivity();
            cActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ((NowPlayingFragment)afProvider.getActiveFragment(NowPlayingFragment.class)).updatePlayingTrack(model.getArtist(),model.getTitle());;

                }
            });
        }
    }
}
