package com.kelsos.mbrc.commands.visual;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.fragments.NowPlayingFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class VisualUpdateTrackRemoved implements ICommand
{
    @Inject
    ActiveFragmentProvider afProvider;

	@Override
	public void execute(final IEvent e)
	{
//        if(afProvider.getActiveFragment(NowPlayingFragment.class)!=null)
//        {    final int index = Integer.parseInt(e.getData());
//            Activity cActivity = afProvider.getActiveFragment(NowPlayingFragment.class).getActivity();
//            cActivity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    ((NowPlayingFragment)afProvider.getActiveFragment(NowPlayingFragment.class)).removeSelectedTrack(index);
//                }
//            });
//        }
	}
}
