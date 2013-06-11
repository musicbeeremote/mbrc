package com.kelsos.mbrc.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistArrayAdapter;
import com.kelsos.mbrc.controller.ActiveFragmentProvider;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.events.DragDropEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


public class NowPlayingFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener {
    @Inject
    ActiveFragmentProvider afProvider;
    @Inject
    Injector injector;
    @Inject
    private Bus bus;
    private PlaylistArrayAdapter adapter;
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private int defaultBackgroundColor;

    public void updateListData(ArrayList<MusicTrack> nowPlayingList, int playingTrackIndex) {
        adapter = new PlaylistArrayAdapter(getActivity(), R.layout.ui_list_track_item, nowPlayingList);
        setListAdapter(adapter);
        adapter.setPlayingTrackIndex(playingTrackIndex);
        this.getListView().setSelection(playingTrackIndex);
    }

    @Subscribe public void handlePlayingTrackChange(TrackInfoChange event) {
        adapter.setPlayingTrackIndex(adapter.getPosition(new MusicTrack(event.getArtist(), event.getTitle())));
        adapter.notifyDataSetChanged();
    }

    public void removeSelectedTrack(int index) {
        if (index < 0) return;
        adapter.remove(adapter.getItem(index));
        adapter.notifyDataSetChanged();
    }

    public boolean onQueryTextSubmit(String query) {

        bus.post(new MessageEvent(UserInputEvent.RequestNowPlayingSearch, query.trim()));

        mSearchView.setIconified(true);
        mSearchItem.collapseActionView();
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        mSearchView = new SearchView(((RoboSherlockFragmentActivity) getActivity()).getSupportActionBar().getThemedContext());
        mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
        mSearchView.setIconifiedByDefault(true);

        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchItem = menu.findItem(R.id.now_playing_search_item);
        mSearchItem.setActionView(mSearchView);
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public void onStart() {
        super.onStart();
        bus.post(new MessageEvent(UserInputEvent.RequestNowPlayingList));
    }

    @Override
    public void onResume() {
        super.onResume();
        afProvider.addActiveFragment(this);
        bus.post(new MessageEvent(UserInputEvent.RequestNowPlayingList));
    }

    @Override
    public void onPause() {
        afProvider.removeActiveFragment(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        afProvider.removeActiveFragment(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        afProvider.removeActiveFragment(this);
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 11, 0, "Remove track");
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(adapter.getItem(mi.position).getTitle());
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        bus.post(new MessageEvent(UserInputEvent.RequestNowPlayingRemoveTrack, Integer.toString(mi.position)));
        return super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_nowplaying, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        adapter.setPlayingTrackIndex(position);
        ((PlaylistArrayAdapter) l.getAdapter()).notifyDataSetChanged();
        bus.post(new MessageEvent(UserInputEvent.RequestNowPlayingPlayTrack, Integer.toString(position + 1)));
    }

    @Subscribe
    public void handleDragAndDrop(DragDropEvent event) {

        int backgroundColor = 0xe0103010;

        switch (event.getType()) {

            case DRAG_START:
                event.getItem().setVisibility(View.INVISIBLE);
                defaultBackgroundColor = event.getItem().getDrawingCacheBackgroundColor();
                event.getItem().setBackgroundColor(backgroundColor);
                break;
            case DRAG_STOP:
                event.getItem().setVisibility(View.VISIBLE);
                event.getItem().setBackgroundColor(defaultBackgroundColor);
                break;
        }
    }
}
