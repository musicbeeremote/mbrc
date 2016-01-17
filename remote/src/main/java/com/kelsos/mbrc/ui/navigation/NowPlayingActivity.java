package com.kelsos.mbrc.ui.navigation;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.NowPlayingAdapter;
import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.presenters.NowPlayingPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.views.NowPlayingView;
import com.squareup.otto.Subscribe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NowPlayingActivity extends BaseActivity
    implements SearchView.OnQueryTextListener, NowPlayingAdapter.OnUserActionListener, NowPlayingView {

  @Bind(R.id.now_playing_recycler) RecyclerView recyclerView;
  @Inject private NowPlayingAdapter adapter;
  @Inject private NowPlayingPresenter presenter;
  private LinearLayoutManager layoutManager;
  private SearchView mSearchView;
  private MenuItem mSearchItem;

  @Subscribe public void handlePlayingTrackChange(TrackInfoChangeEvent event) {
    if (adapter == null || !adapter.getClass().equals(NowPlayingAdapter.class)) {
      return;
    }
    final TrackInfo info = event.getTrackInfo();
    final QueueTrack track = new QueueTrack();

    track.setArtist(info.getArtist());
    track.setTitle(info.getTitle());
    adapter.setPlayingTrack(track);
  }

  public boolean onQueryTextSubmit(String query) {
    mSearchView.setIconified(true);
    MenuItemCompat.collapseActionView(mSearchItem);
    return false;
  }

  public boolean onQueryTextChange(String newText) {
    return true;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_now_playing, menu);
    mSearchItem = menu.findItem(R.id.now_playing_search_item);
    mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
    mSearchView.setQueryHint(getString(R.string.now_playing_search_hint));
    mSearchView.setIconifiedByDefault(true);
    mSearchView.setOnQueryTextListener(this);
    return true;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_now_playing);
    ButterKnife.bind(this);
    initialize();
    presenter.bind(this);

    layoutManager = new LinearLayoutManager(getBaseContext());
    recyclerView.setLayoutManager(layoutManager);
    adapter.setOnUserActionListener(this);
    recyclerView.setAdapter(adapter);

    presenter.loadData();
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
      adapter.restorePositions(event.getFrom(), event.getTo());
    }
  }

  @Subscribe public void handleTrackRemoval(TrackRemoval event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      // TODO: 8/18/15 fix caching of track until successful removal
      //adapter.insert(track, event.getIndex());
    }
  }

  @Override public void onTrackRemoved(int position) {

  }

  @Override public void onTrackMoved(int from, int to) {

  }

  @Override public void updatePlayingTrack(QueueTrack track) {
    adapter.setPlayingTrack(track);
  }

  @Override public void removeTrack(int position) {

  }

  @Override public void moveTrack(int from, int to) {
    adapter.setPlayingTrackIndex(calculateNewIndex(from, to, adapter.getPlayingTrackIndex()));

    Map<String, Integer> move = new HashMap<>();
    move.put("from", from);
    move.put("to", to);
  }

  @Override public void updateAdapter(List<QueueTrack> data) {
    adapter.updateData(data);
  }

  @Override public void onItemClicked(int position, QueueTrack track) {
    presenter.playTrack(track);
  }
}
