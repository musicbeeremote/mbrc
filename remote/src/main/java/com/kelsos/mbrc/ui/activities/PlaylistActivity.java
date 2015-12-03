package com.kelsos.mbrc.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistAdapter;
import com.kelsos.mbrc.ui.fragments.MiniControlFragment;

public class PlaylistActivity extends RoboAppCompatActivity {

  public static final String NAME = "name";
  public static final String PATH = "path";

  @Bind(R.id.playlist) RecyclerView playlist;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Inject private PlaylistAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlist);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    playlist.setLayoutManager(new LinearLayoutManager(this));

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.playlist_mini_control, MiniControlFragment.newInstance())
        .commit();
  }

  @Override public void onStart() {
    super.onStart();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
