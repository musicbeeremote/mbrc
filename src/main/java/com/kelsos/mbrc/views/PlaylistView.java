package com.kelsos.mbrc.views;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
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
import java.util.regex.Pattern;

import static android.widget.AdapterView.AdapterContextMenuInfo;

public class PlaylistView extends RoboSherlockListActivity implements SearchView.OnQueryTextListener
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private Bus bus;

	private PlaylistArrayAdapter adapter;
	private SearchView mSearchView;
	private MenuItem mSearchItem;

	public void updateListData(ArrayList<MusicTrack> nowPlayingList, int playingTrackIndex)
	{
		adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, nowPlayingList);
		setListAdapter(adapter);
		adapter.setPlayingTrackIndex(playingTrackIndex);
		this.getListView().setSelection(playingTrackIndex);
	}

	public void updatePlayingTrack(String artist, String title)
	{
		adapter.setPlayingTrackIndex(adapter.getPosition(new MusicTrack(artist, title)));
		adapter.notifyDataSetChanged();
	}

	public void removeSelectedTrack(int index)
	{
		if (index < 0) return;
		adapter.remove(adapter.getItem(index));
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nowplayinglist);
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_LIST));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.string_value_now_playing);
		registerForContextMenu(this.getListView());


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		mSearchView = new SearchView(getSupportActionBar().getThemedContext());
		mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
		mSearchView.setIconifiedByDefault(true);


		menu.add(R.string.now_playing_search)
				.setIcon(R.drawable.abs__ic_search)
				.setActionView(mSearchView)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		mSearchView.setOnQueryTextListener(this);
		mSearchItem = menu.getItem(0);
		return true;

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
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		//String track = ((MusicTrack) getListView().getItemAtPosition(position)).getTitle();
		adapter.setPlayingTrackIndex(position);
		((PlaylistArrayAdapter) l.getAdapter()).notifyDataSetChanged();

		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_PLAY_NOW, Integer.toString(position + 1)));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.add(0, 11, 0, "Remove track");
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfo;


		menu.setHeaderTitle(adapter.getItem(mi.position).getTitle());
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
	public boolean onContextItemSelected(android.view.MenuItem item)
	{

		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_REMOVE, Integer.toString(mi.position)));
		return super.onContextItemSelected(item);
	}

	public boolean onQueryTextSubmit(String query)
	{

		int elements = adapter.getCount();
		for (int i = 0; i < elements; i++)
		{
			MusicTrack track = adapter.getItem(i);
			String trackString = track.getArtist() + " " + track.getTitle();
			boolean isContained = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(trackString).find();
			if (isContained)
			{
				adapter.setPlayingTrackIndex(i);
				adapter.notifyDataSetChanged();

				bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_PLAY_NOW, Integer.toString(i + 1)));
				this.getListView().setSelection(i);
				break;
			}
		}

		mSearchView.setIconified(true);
		mSearchItem.collapseActionView();
		return false; // pass on to other listeners.
	}


	public boolean onQueryTextChange(String newText)
	{
		return false;  ||
	}

}



