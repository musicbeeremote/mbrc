package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Subscribe;

public class SinglePlaylistFragment extends RoboSherlockListFragment {
    @Inject private MainThreadBusWrapper bus;
    private TrackEntryAdapter adapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_list_track_item, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        bus.register(this);
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistList, true)));
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe public void handlePlaylistData() {
        //adapter = new PlaylistAdapter(getActivity(), R.layout.ui_list_dual, );
        //setListAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }
}
