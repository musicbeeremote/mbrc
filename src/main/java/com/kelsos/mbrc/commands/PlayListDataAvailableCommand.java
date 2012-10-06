package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.PlaylistView;
import com.kelsos.mbrc.controller.RunningActivityAccessor;

import java.util.ArrayList;

public class PlayListDataAvailableCommand implements ICommand
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private MainDataModel model;
	@Override
	public void execute(final IEvent e)
	{
		if(accessor.getRunningActivity()==null||accessor.getRunningActivity().getClass()!= PlaylistView.class) return;
		int index=0;
		final ArrayList<MusicTrack> playList = ((ProtocolDataEvent)e).getTrackList();
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


		accessor.getRunningActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				PlaylistView view = (PlaylistView) accessor.getRunningActivity();
				view.updateListData(playList, trackIndex);
			}
		});
	}
}
