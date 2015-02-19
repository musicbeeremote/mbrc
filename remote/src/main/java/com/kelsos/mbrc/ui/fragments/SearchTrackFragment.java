package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.TrackEntry;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.TrackSearchResults;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.fragment.RoboListFragment;

public class SearchTrackFragment extends RoboListFragment implements TrackEntryAdapter.MenuItemSelectedListener {
    private String mDefault;

    private TrackEntryAdapter adapter;
    @Inject Bus bus;

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = ((TrackEntry) getListView().getAdapter().getItem(position)).getSrc();

                bus.post(new MessageEvent(ProtocolEventType.UserAction,
                        new UserAction(Protocol.LibraryQueueTrack,
                                new Queue(mDefault, path))));
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

    @Subscribe public void handleTrackResults(TrackSearchResults results) {
        adapter = new TrackEntryAdapter(getActivity(), R.layout.ui_list_dual, results.getList());
        adapter.setMenuItemSelectedListener(this);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override public void OnMenuItemSelected(MenuItem menuItem, TrackEntry entry) {
        final String qContext = Protocol.LibraryQueueTrack;
        final String query = entry.getSrc();

        UserAction ua = null;
        switch (menuItem.getItemId()) {
            case R.id.popup_track_queue_next:
                ua = new UserAction(qContext, new Queue("next", query));
                break;
            case R.id.popup_track_queue_last:
                ua = new UserAction(qContext, new Queue("last", query));
                break;
            case R.id.popup_track_play:
                ua = new UserAction(qContext, new Queue("now", query));
                break;
        }

        if (ua != null) bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
}
