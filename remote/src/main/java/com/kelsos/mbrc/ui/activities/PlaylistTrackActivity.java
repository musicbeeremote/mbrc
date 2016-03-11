package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.PlaylistTrack;
import com.kelsos.mbrc.presenters.PlaylistTrackPresenter;
import com.kelsos.mbrc.ui.views.PlaylistTrackView;
import java.util.List;
import roboguice.RoboGuice;

public class PlaylistTrackActivity extends AppCompatActivity
    implements PlaylistTrackView, PlaylistAdapter.MenuItemSelectedListener {

  public static final String NAME = "name";
  public static final String ID = "path";

  @Bind(R.id.playlist_recycler) RecyclerView playlist;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Inject private PlaylistAdapter adapter;
  @Inject private PlaylistTrackPresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlist);
    ButterKnife.bind(this);
    RoboGuice.getInjector(this).injectMembers(this);
    presenter.bind(this);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    Intent intent = getIntent();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);

      String name = intent.getStringExtra(NAME);
      actionBar.setTitle(name);
    }

    playlist.setLayoutManager(new LinearLayoutManager(this));
    playlist.setAdapter(adapter);
    adapter.setMenuItemSelectedListener(this);
    presenter.load(intent.getLongExtra(ID, 0));
  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void showErrorWhileLoading() {

  }

  @Override public void update(List<PlaylistTrack> data) {
    adapter.update(data);
  }

  @Override public void onMenuItemSelected(MenuItem item, PlaylistTrack track) {
    switch (item.getItemId()) {
      case R.id.popup_track_play:
        presenter.queue(track, Queue.NOW);
        break;
      case R.id.popup_track_playlist:
        break;
      case R.id.popup_track_queue_next:
        presenter.queue(track, Queue.NEXT);
        break;
      case R.id.popup_track_queue_last:
        presenter.queue(track, Queue.LAST);
        break;
      default:
        break;
    }
  }

  @Override public void onItemClicked(PlaylistTrack track) {
    presenter.queue(track, Queue.NOW);
  }
}
