package com.kelsos.mbrc.ui.activities.nav;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.PlaylistAvailable;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;

public class PlaylistActivity extends BaseActivity implements PlaylistAdapter.OnPlaylistPressedListener,
    SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeLayout;
  @BindView(R.id.playlist_list) RecyclerView playlistList;

  @Inject PlaylistAdapter adapter;
  @Inject RxBus bus;
  private Scope scope;

  public PlaylistActivity() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    scope = Toothpick.openScopes(getApplication(), this);
    super.onCreate(savedInstanceState);
    Toothpick.inject(this, scope);
    setContentView(R.layout.activity_playlists);
    ButterKnife.bind(this);
    super.setup();
    adapter.setPlaylistPressedListener(this);
    playlistList.setAdapter(adapter);
    playlistList.setLayoutManager(new LinearLayoutManager(this));
    swipeLayout.setOnRefreshListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    bus.register(this, PlaylistAvailable.class, this::onPlaylistAvailable, true);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistList, true)));
  }

  @Override
  public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  private void onPlaylistAvailable(PlaylistAvailable event) {
    swipeLayout.setRefreshing(false);
    adapter.update(event.getPlaylist());
  }

  @Override
  public void playlistPressed(String path) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistPlay, path)));
  }

  @Override
  protected int active() {
    return R.id.nav_playlists;
  }

  @Override
  protected void onDestroy() {
    Toothpick.closeScope(this);
    super.onDestroy();
  }

  @Override
  public void onRefresh() {
    if (!swipeLayout.isRefreshing()) {
      swipeLayout.setRefreshing(true);
    }
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistList, true)));
  }
}
