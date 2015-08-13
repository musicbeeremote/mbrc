package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.TrackEntry;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.TrackSearchResults;
import com.kelsos.mbrc.utilities.ScrollListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class SearchTrackFragment extends RoboFragment
    implements TrackEntryAdapter.MenuItemSelectedListener {

  @Inject Bus bus;
  @Inject private ScrollListener scrollListener;
  private String mDefault;
  @InjectView(R.id.search_recycler_view) private RecyclerView mRecyclerView;

  @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
    mDefault = action.getAction();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.ui_fragment_library_search, container, false);
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
    mRecyclerView.addOnScrollListener(scrollListener);
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
    mRecyclerView.removeOnScrollListener(scrollListener);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(layoutManager);
  }

  @Subscribe public void handleTrackResults(TrackSearchResults results) {
    TrackEntryAdapter adapter = new TrackEntryAdapter(getActivity(), results.getList());
    adapter.setMenuItemSelectedListener(this);
    mRecyclerView.setAdapter(adapter);
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, TrackEntry entry) {
    final String qContext = Protocol.LibraryQueueTrack;
    final String query = entry.getSrc();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_track_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_track_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_track_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  @Override public void onItemClicked(TrackEntry track) {
    if (mDefault.equals(Const.SUB)) {
      mDefault = Queue.NOW;
    }

    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.LibraryQueueTrack, new Queue(mDefault, track.getSrc()))));
  }
}
