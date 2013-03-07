package com.kelsos.mbrc.commands;

import android.app.Activity;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.fragments.NowPlayingFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

import java.util.ArrayList;

public class PlayListDataAvailableCommand implements ICommand
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	private MainDataModel model;
	@Override
	public void execute(final IEvent e)
	{
		if(afProvider.getActiveFragment(NowPlayingFragment.class) == null) return;
		int index=0;
		final ArrayList<MusicTrack> playList = new ArrayList<MusicTrack>();
        //TODO:fix
		String artist = model.getArtist();
		String title = model.getTitle();
		for(int i=0;i<playList.size();i++)
		{
			if(playList.get(i).getArtist().contains(artist)&&playList.get(i).getTitle().contains(title))
			{
				index = i;
				break;
			}
		}

		final int trackIndex = index;

		Activity cActivity = afProvider.getActiveFragment(NowPlayingFragment.class).getActivity();
		cActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				((NowPlayingFragment) afProvider.getActiveFragment(NowPlayingFragment.class)).updateListData(playList, trackIndex);;
			}
		});
	}
}
