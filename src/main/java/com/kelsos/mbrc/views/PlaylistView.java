package com.kelsos.mbrc.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockListActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.RunningActivityAccessor;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.PlaylistArrayAdapter;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.UserActionEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;

public class PlaylistView extends RoboSherlockListActivity
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private Bus bus;

	private PlaylistArrayAdapter adapter;

    public void updateListData(ArrayList<MusicTrack> nowPlayingList, int playingTrackIndex) {

        adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, nowPlayingList);
        setListAdapter(adapter);
		adapter.setPlayingTrackIndex(playingTrackIndex);
    }

	public void updatePlayingTrack(String artist, String title)
	{
		adapter.setPlayingTrackIndex(adapter.getPosition(new MusicTrack(artist,title)));
		adapter.notifyDataSetChanged();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.nowplayinglist);
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_LIST));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.string_value_now_playing);
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		accessor.register(this);
	}

	@Override
	protected void onPause()
	{
		accessor.unRegister(this);
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		accessor.unRegister(this);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		accessor.unRegister(this);
		super.onDestroy();
	}

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String track = ((MusicTrack) getListView().getItemAtPosition(position)).getTitle();
		adapter.setPlayingTrackIndex(position);
		 ((PlaylistArrayAdapter)l.getAdapter()).notifyDataSetChanged();

		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_PLAY_NOW, track));

    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			    finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}


