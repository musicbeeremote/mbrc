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
import java.util.LinkedHashMap;

public class NowPlayingListData implements ICommand
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

        ArrayList<LinkedHashMap<String, Object>> nowPlaying = (ArrayList<LinkedHashMap<String, Object>>)e.getData();

		ArrayList<MusicTrack> playList = new ArrayList<MusicTrack>();
        //TODO:fix
		String artist = model.getArtist();
		String title = model.getTitle();
		for(int i=0;i<nowPlaying.size();i++)
		{
            LinkedHashMap<String, Object> map = nowPlaying.get(i);
            MusicTrack track = new MusicTrack((String)map.get("Artist"), (String)map.get("Title"), (Integer)map.get("Position"));
            playList.add(track);
			if(track.getArtist().contains(artist)&&track.getTitle().contains(title))
			{
				index = i;
			}
		}

		final int trackIndex = index;
        final ArrayList<MusicTrack> pl = playList;

		Activity cActivity = afProvider.getActiveFragment(NowPlayingFragment.class).getActivity();
		cActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				((NowPlayingFragment) afProvider.getActiveFragment(NowPlayingFragment.class)).updateListData(pl, trackIndex);;
			}
		});
	}
}
