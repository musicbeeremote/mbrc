package com.kelsos.mbrc.commands;

import android.app.Activity;
import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.fragments.NowPlayingFragment;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
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

        ArrayNode node = (ArrayNode)e.getData();
        ArrayList<MusicTrack> playList = new ArrayList<MusicTrack>();
        String artist = model.getArtist();
        String title = model.getTitle();

        for(int i = 0; i < node.size(); i++) {
            JsonNode jNode = node.get(i);
            MusicTrack track = new MusicTrack(jNode);
            playList.add(track);
            if(track.getArtist().contains(artist)&&track.getTitle().contains(title))
            {
                index = i;
            }
            Log.d("MBRC", String.valueOf(i));
            Log.d("MBRC", track.getArtist() +'\t'+track.getTitle());
        }

		final int trackIndex = index;
        final ArrayList<MusicTrack> pl = playList;

		Activity cActivity = afProvider.getActiveFragment(NowPlayingFragment.class).getActivity();
		cActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				((NowPlayingFragment) afProvider.getActiveFragment(NowPlayingFragment.class)).updateListData(pl, trackIndex);
			}
		});
	}
}
