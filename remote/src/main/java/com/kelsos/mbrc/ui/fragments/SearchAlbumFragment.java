package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumEntryAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.AlbumEntry;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.fragment.RoboListFragment;

public class SearchAlbumFragment extends RoboListFragment
    implements AlbumEntryAdapter.MenuItemSelectedListener{
    private String mDefault;
    private AlbumEntryAdapter adapter;

    @Inject Bus bus;

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String album = ((AlbumEntry) getListView().getAdapter().getItem(position)).getAlbum();

                bus.post(new MessageEvent(ProtocolEventType.UserAction,
                        new UserAction(Protocol.LibraryQueueAlbum,
                                new Queue(mDefault, album))));
            }
        });
    }

    @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
        mDefault = action.getAction();
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_library_simpl, container, false);
    }

    @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
        adapter = new AlbumEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
        adapter.setMenuItemSelectedListener(this);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override public void OnMenuItemSelected(MenuItem menuItem, AlbumEntry entry) {

        final String qContext = Protocol.LibraryQueueAlbum;
        final String gSub = Protocol.LibraryAlbumTracks;
        String query = entry.getAlbum();

        UserAction ua = null;
        switch (menuItem.getItemId()) {
            case R.id.popup_album_queue_next:
                ua = new UserAction(qContext, new Queue("next", query));
                break;
            case R.id.popup_album_queue_last:
                ua = new UserAction(qContext, new Queue("last", query));
                break;
            case R.id.popup_album_play:
                ua = new UserAction(qContext, new Queue("now", query));
                break;
            case R.id.popup_album_tracks:
                ua = new UserAction(gSub, query);
                break;
        }

        if (ua != null) bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
}
