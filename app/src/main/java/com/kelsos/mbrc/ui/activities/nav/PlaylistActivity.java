package com.kelsos.mbrc.ui.activities.nav;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.PlaylistAvailable;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.RoboGuice;

public class PlaylistActivity extends BaseActivity implements PlaylistAdapter.OnPlaylistPressedListener {

  @BindView(R.id.playlist_list) RecyclerView playlistList;

  @Inject private PlaylistAdapter adapter;
  @Inject private Bus bus;

  public PlaylistActivity() {
    // Required empty public constructor
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlists);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    super.setup();
    adapter.setPlaylistPressedListener(this);
    playlistList.setAdapter(adapter);
    playlistList.setLayoutManager(new LinearLayoutManager(this));
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

  @Subscribe public void onPlaylistAvailable(PlaylistAvailable event) {
    adapter.update(event.getPlaylist());
  }

  @Override public void playlistPressed(String path) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistPlay, path)));
  }
}
