package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistTrackAdapter;
import com.kelsos.mbrc.ui.fragments.MiniControlFragment;

public class PlaylistActivity extends RoboAppCompatActivity {
  public static final String NAME = "name";
  public static final String PATH = "path";
  @Bind(R.id.now_playing_list) RecyclerView nowPlayingList;
  private PlaylistTrackAdapter mAdapter;
  private String mTitle;
  private String path;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlist);
    ButterKnife.bind(this);
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
    Intent intent = getIntent();
    mTitle = intent.getStringExtra(NAME);
    path = intent.getStringExtra(PATH);

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.playlist_mini_control, MiniControlFragment.newInstance())
        .commit();
  }

  @Override public void onStart() {
    super.onStart();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(mTitle);
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
