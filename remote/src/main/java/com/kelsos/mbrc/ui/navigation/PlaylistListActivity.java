package com.kelsos.mbrc.ui.navigation;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistListAdapter;
import com.kelsos.mbrc.adapters.PlaylistListAdapter.OnPlaylistPlayPressedListener;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.presenters.PlaylistPresenter;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.ui.views.PlaylistListView;
import java.util.List;

public class PlaylistListActivity extends BaseActivity implements PlaylistListView, OnPlaylistPlayPressedListener {

  @Bind(R.id.playlist_recycler) RecyclerView recyclerView;

  @Inject private PlaylistListAdapter adapter;
  @Inject private PlaylistPresenter presenter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlist_list);
    initialize();
    ButterKnife.bind(this);
    presenter.bind(this);
    adapter.setOnPlaylistPlayPressedListener(this);

    RecyclerView.LayoutManager manager = new LinearLayoutManager(getBaseContext());
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.load();
  }

  @Override public void update(List<Playlist> playlists) {
    adapter.updateData(playlists);
  }

  @Override public void playlistPlayPressed(Playlist playlist, int position) {
    presenter.play(playlist.getPath());
  }
}
