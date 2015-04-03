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
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class SearchAlbumFragment extends RoboFragment
    implements AlbumEntryAdapter.MenuItemSelectedListener {
  @Inject Bus bus;
  private String mDefault;
  @InjectView(R.id.search_recycler_view) private RecyclerView mRecyclerView;

  @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
    mDefault = action.getAction();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.ui_fragment_library_search, container, false);
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLayoutManager);
  }

  @Subscribe public void handleAlbumResults(AlbumSearchResults results) {
    AlbumEntryAdapter adapter = new AlbumEntryAdapter(getActivity(), results.getList());
    adapter.setMenuItemSelectedListener(this);
    mRecyclerView.setAdapter(adapter);
  }

  @Override public void onMenuItemSelected(MenuItem menuItem, AlbumEntry entry) {

    final String qContext = Protocol.LibraryQueueAlbum;
    final String gSub = Protocol.LibraryAlbumTracks;
    String query = entry.getAlbum();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_album_queue_next:
        ua = new UserAction(qContext, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_album_queue_last:
        ua = new UserAction(qContext, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_album_play:
        ua = new UserAction(qContext, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_album_tracks:
        ua = new UserAction(gSub, query);
        break;
      default:
        break;
    }

    if (ua != null) bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
  }

  @Override public void onItemClicked(AlbumEntry album) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.LibraryQueueAlbum, new Queue(mDefault, album.getAlbum()))));
  }
}
