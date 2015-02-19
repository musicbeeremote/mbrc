package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistEntryAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.fragment.RoboListFragment;

public class SearchArtistFragment extends RoboListFragment
    implements ArtistEntryAdapter.MenuItemSelectedListener {
    private ArtistEntryAdapter adapter;
    private String mDefault;
    @Inject Bus bus;

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artist = ((ArtistEntry) getListView().getAdapter().getItem(position)).getArtist();

                bus.post(new MessageEvent(ProtocolEventType.UserAction,
                        new UserAction(Protocol.LibraryQueueArtist,
                                new Queue(mDefault, artist))));
            }
        });
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
        mDefault = action.getAction();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_library_simpl, container, false);
    }


    @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
        adapter = new ArtistEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        adapter.setMenuItemSelectedListener(this);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override public void OnMenuItemSelected(MenuItem menuItem, ArtistEntry entry) {
        final String qContext = Protocol.LibraryQueueArtist;
        final String gSub = Protocol.LibraryArtistAlbums;
        String query = entry.getArtist();

        UserAction ua = null;
        switch (menuItem.getItemId()) {
            case R.id.popup_artist_queue_next:
                ua = new UserAction(qContext, new Queue("next", query));
                break;
            case R.id.popup_artist_queue_last:
                ua = new UserAction(qContext, new Queue("last", query));
                break;
            case R.id.popup_artist_play:
                ua = new UserAction(qContext, new Queue("now", query));
                break;
            case R.id.popup_artist_album:
                ua = new UserAction(gSub, query);
                break;
        }

        if (ua != null) bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
}
