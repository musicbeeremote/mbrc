package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.NowPlayingAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.NowPlayingListAvailable;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.ui.drag.SimpleItenTouchHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.HashMap;
import java.util.Map;
import roboguice.RoboGuice;

public class NowPlayingFragment extends Fragment
    implements SearchView.OnQueryTextListener, NowPlayingAdapter.NowPlayingListener {

  @BindView(R.id.now_playing_list) RecyclerView nowPlayingList;
  @Inject private Bus bus;
  @Inject private NowPlayingAdapter adapter;
  private SearchView mSearchView;
  private MenuItem mSearchItem;

  @Subscribe public void handleNowPlayingListAvailable(NowPlayingListAvailable event) {
    adapter.update(event.getList());
    adapter.setPlayingTrackIndex(event.getIndex());
  }

  @Subscribe public void handlePlayingTrackChange(TrackInfoChange event) {
    if (adapter == null || !adapter.getClass().equals(NowPlayingAdapter.class)) {
      return;
    }
    adapter.setPlayingTrackIndex(new MusicTrack(event.getArtist(), event.getTitle()));
    adapter.notifyDataSetChanged();
  }

  public boolean onQueryTextSubmit(String query) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction,
        new UserAction(Protocol.NowPlayingListSearch, query.trim())));
    mSearchView.setIconified(true);
    MenuItemCompat.collapseActionView(mSearchItem);
    return false;
  }

  public boolean onQueryTextChange(String newText) {
    return true;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_now_playing, menu);
    mSearchItem = menu.findItem(R.id.now_playing_search_item);
    mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
    mSearchView.setIconifiedByDefault(true);
    mSearchView.setOnQueryTextListener(this);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    setHasOptionsMenu(true);
  }

  @Override public void onStart() {
    super.onStart();
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingList, true)));
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.ui_fragment_nowplaying, container, false);
    ButterKnife.bind(this, view);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
    nowPlayingList.setLayoutManager(manager);
    nowPlayingList.setAdapter(adapter);
    ItemTouchHelper.Callback callback = new SimpleItenTouchHelper(adapter);
    ItemTouchHelper helper = new ItemTouchHelper(callback);
    helper.attachToRecyclerView(nowPlayingList);
    adapter.setListener(this);
    return view;
  }

  private int calculateNewIndex(int from, int to, int index) {
    int dist = Math.abs(from - to);
    if (dist == 1 && index == from
        || dist > 1 && from > to && index == from
        || dist > 1 && from < to && index == from) {
      index = to;
    } else if (dist == 1 && index == to) {
      index = from;
    } else if (dist > 1 && from > to && index == to || from > index && to < index) {
      index += 1;
    } else if (dist > 1 && from < to && index == to || from < index && to > index) {
      index -= 1;
    }
    return index;
  }

  @Subscribe public void handleTrackMoved(TrackMoved event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      //Revert
    }
  }

  @Subscribe public void handleTrackRemoval(TrackRemoval event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      ///Revert
    }
  }

  @Override public void onPress(int position) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingListPlay, position + 1)));
  }

  @Override public void onMove(int from, int to) {
    adapter.setPlayingTrackIndex(calculateNewIndex(from, to, adapter.getPlayingTrackIndex()));

    Map<String, Integer> move = new HashMap<>();
    move.put("from", from);
    move.put("to", to);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingListMove, move)));
  }

  @Override public void onDismiss(int position) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.NowPlayingListRemove, position)));
  }
}
