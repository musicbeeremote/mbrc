package com.kelsos.mbrc.ui.fragments;


import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreEntryAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.model.GenreEntry;
import com.kelsos.mbrc.model.Queue;
import com.kelsos.mbrc.model.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class BrowseGenreFragment extends RoboSherlockListFragment {
    private static final int GROUP_ID = 11;
    private String mDefault;
    private GenreEntryAdapter adapter;
    @Inject Bus bus;

    @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
        mDefault = action.getAction();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_library_simpl, container, false);
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_artists);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = adapter.getItem(mi.position);
            final String qContext = Protocol.LibraryQueueGenre;
            final String gSub = Protocol.LibraryGenreArtists;
            String query = ((GenreEntry) line).getName();

            UserAction ua = null;
            switch (item.getItemId()) {
                case BrowseMenuItems.QUEUE_NEXT:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_next), query));
                    break;
                case BrowseMenuItems.QUEUE_LAST:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_last), query));
                    break;
                case BrowseMenuItems.PLAY_NOW:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_now), query));
                    break;
                case BrowseMenuItems.GET_SUB:
                    ua = new UserAction(gSub, query);
                    break;
            }

            if (ua != null) bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
            return true;
        } else {
            return false;
        }

    }

    @Subscribe public void handleGenreSearchResults(GenreSearchResults results) {
        adapter = new GenreEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String genre = ((GenreEntry) getListView().getAdapter().getItem(position)).getName();

                bus.post(new MessageEvent(ProtocolEventType.UserAction,
                        new UserAction(Protocol.LibraryQueueGenre,
                                new Queue(mDefault, genre))));
            }
        });
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }
}
