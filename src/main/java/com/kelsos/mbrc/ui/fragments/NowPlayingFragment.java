package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.events.ui.NowPlayingListAvailable;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.others.Protocol;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;


public class NowPlayingFragment extends RoboSherlockListFragment implements SearchView.OnQueryTextListener {
    @Inject Injector injector;
    @Inject private Bus bus;
    private PlaylistArrayAdapter adapter;
    private SearchView mSearchView;
    private MenuItem mSearchItem;

    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        // dragStartMode = onDown
        // removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }

    @Subscribe public void handleNowPlayingListAvailable(NowPlayingListAvailable event){
        adapter = new PlaylistArrayAdapter(getActivity(), R.layout.ui_list_track_item, event.getList());
        setListAdapter(adapter);
        adapter.setPlayingTrackIndex(event.getIndex());
        this.getListView().setSelection(event.getIndex());
    }

    @Subscribe public void handlePlayingTrackChange(TrackInfoChange event) {
        if (adapter == null || !adapter.getClass().equals(PlaylistArrayAdapter.class)) return;
        adapter.setPlayingTrackIndex(adapter.getPosition(new MusicTrack(event.getArtist(), event.getTitle())));
        adapter.notifyDataSetChanged();
    }

    public boolean onQueryTextSubmit(String query) {
        bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingListSearch, query.trim())));
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDslv = (DragSortListView) getListView();
        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);
        registerForContextMenu(getListView());
        injector.injectMembers(getListView());
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingList, true)));
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.ui_fragment_nowplaying, container, false);
        mDslv = (DragSortListView) mView.findViewById(android.R.id.list);
        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);
        return mView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        adapter.setPlayingTrackIndex(position);
        adapter.notifyDataSetChanged();
        bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingListPlay, position + 1)));
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override public void drop(int from, int to) {
            if (from != to) {

                Map<String, Integer> move = new HashMap<String, Integer>();
                move.put("from", from);
                move.put("to", to);
                bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingListMove,move)));

            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override public void remove(int which) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.NowPlayingListRemove, which)));
        }
    };

    @Subscribe public void handleTrackMoved(TrackMoved event) {
        if (event.isSuccess()) {
            MusicTrack track = adapter.getItem(event.getFrom());
            adapter.remove(track);
            adapter.insert(track, event.getTo());
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe public void handleTrackRemoval(TrackRemoval event) {
        if (event.isSuccess()) {
            adapter.remove(adapter.getItem(event.getIndex()));
        }
        adapter.notifyDataSetChanged();
    }
}
