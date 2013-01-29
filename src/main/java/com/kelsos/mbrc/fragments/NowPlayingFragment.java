package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.PlaylistArrayAdapter;
import com.kelsos.mbrc.enums.UserInputEventType;
import com.kelsos.mbrc.events.DragDropEvent;
import com.kelsos.mbrc.events.UserActionEvent;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class NowPlayingFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener
{
	@Inject
	ActiveFragmentProvider afProvider;
	@Inject
	private Bus bus;
    @Inject
    Injector injector;

	private PlaylistArrayAdapter adapter;
    private SearchView mSearchView;
    private MenuItem mSearchItem;

	public void updateListData(ArrayList<MusicTrack> nowPlayingList, int playingTrackIndex)
	{
		adapter = new PlaylistArrayAdapter(getActivity(), R.layout.playlistview_item, nowPlayingList);
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
        return false;
    }

    public boolean onQueryTextChange(String newText)
    {
        return true;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){

        mSearchView = new SearchView(((SlidingFragmentActivity)getActivity()).getSupportActionBar().getThemedContext());
        mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
        mSearchView.setIconifiedByDefault(true);

        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchItem = menu.findItem(R.id.now_playing_search_item);
        mSearchItem.setActionView(mSearchView);
        mSearchView.setOnQueryTextListener(this);
    }

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        bus.register(this);
        setHasOptionsMenu(true);
		afProvider.addActiveFragment(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(this.getListView());
        injector.injectMembers(this.getListView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.nowplayinglist, container, false);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_LIST));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		afProvider.addActiveFragment(this);
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_LIST));
	}

	@Override
	public void onPause()
	{
		afProvider.removeActiveFragment(this);
		super.onPause();
	}

	@Override
	public void onStop()
	{
		afProvider.removeActiveFragment(this);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		afProvider.removeActiveFragment(this);
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		adapter.setPlayingTrackIndex(position);
		((PlaylistArrayAdapter) l.getAdapter()).notifyDataSetChanged();
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_PLAY_NOW, Integer.toString(position + 1)));
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		menu.add(0, 11, 0, "Remove track");
		AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;

		menu.setHeaderTitle(adapter.getItem(mi.position).getTitle());
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
	public boolean onContextItemSelected(android.view.MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_NOWPLAYING_REMOVE, Integer.toString(mi.position)));
		return super.onContextItemSelected(item);
	}

    @Subscribe
    public void handleDragAndDrop(DragDropEvent event){

        int defaultBackgroundColor;
        int backgroundColor = 0xe0103010;

        switch (event.getType()) {

            case DRAG_START:
                event.getItem().setVisibility(View.INVISIBLE);
                defaultBackgroundColor = event.getItem().getDrawingCacheBackgroundColor();
                event.getItem().setBackgroundColor(backgroundColor);
                break;
            case DRAG_STOP:
                event.getItem().setVisibility(View.VISIBLE);
                //event.getItem().setBackgroundColor(defaultBackgroundColor);
                break;
        }
    }
}
