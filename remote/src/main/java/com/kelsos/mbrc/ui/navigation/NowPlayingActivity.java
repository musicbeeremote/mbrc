package com.kelsos.mbrc.ui.navigation;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.NowPlayingAdapter;
import com.kelsos.mbrc.adapters.SimpleItemTouchHelperCallback;
import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.presenters.NowPlayingPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.views.NowPlayingView;
import java.util.List;
import roboguice.RoboGuice;

public class NowPlayingActivity extends BaseActivity
    implements SearchView.OnQueryTextListener, NowPlayingAdapter.OnUserActionListener, NowPlayingView {

  @Bind(R.id.now_playing_recycler) RecyclerView recyclerView;
  @Inject private NowPlayingAdapter adapter;
  @Inject private NowPlayingPresenter presenter;
  private LinearLayoutManager layoutManager;
  private SearchView mSearchView;
  private MenuItem mSearchItem;

  @Override protected void onStart() {
    super.onStart();

  }

  public void handlePlayingTrackChange(TrackInfoChangeEvent event) {
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
    RoboGuice.getInjector(this).injectMembers(this);
    setContentView(R.layout.activity_now_playing);
    ButterKnife.bind(this);
    initialize();
    setCurrentSelection(R.id.drawer_menu_now_playing);
    presenter.bind(this);

    layoutManager = new LinearLayoutManager(getBaseContext());
    recyclerView.setLayoutManager(layoutManager);
    adapter.setOnUserActionListener(this);
    recyclerView.setAdapter(adapter);
    SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerView);

    presenter.loadData();
  }

  public void handleTrackMoved(TrackMoved event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      adapter.restorePositions(event.getFrom(), event.getTo());
    }
  }

  public void handleTrackRemoval(TrackRemoval event) {
    // In case the action failed revert the change
    if (!event.isSuccess()) {
      // TODO: 8/18/15 fix caching of track until successful removal
      //adapter.insert(track, event.getIndex());
    }
  }

  @Override public void onItemRemoved(int position) {
    presenter.removeItem(position);
  }

  @Override public void onItemMoved(int from, int to) {
    presenter.moveItem(from, to);
  }

  @Override public void updatePlayingTrack(QueueTrack track) {
    adapter.setPlayingTrack(track);
  }

  @Override public void updateAdapter(List<QueueTrack> data) {
    adapter.updateData(data);
  }

  @Override public void onItemClicked(int position, QueueTrack track) {
    presenter.playTrack(track);
  }

  @Override public void onBackPressed() {
    ActivityCompat.finishAfterTransition(this);
  }
}
